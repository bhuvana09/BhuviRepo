package com.cognizant.gto.ccap.checkin.plugin.ui;

import static com.ccap.deltaanalysis.internal.impl.constants.Constants.DELTA_EXPORT;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.DELTA_VIOLATIONS_XML;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.SONAR_WRK_DIR_KEY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

import com.ccap.deltaanalysis.api.vo.ccap.CCAPMetric;
import com.ccap.deltaanalysis.api.vo.ccap.CCAPResource;
import com.ccap.deltaanalysis.api.vo.ccap.CCAPResult;
import com.ccap.deltaanalysis.api.vo.ccap.CCAPViolation;
import com.ccap.deltaanalysis.internal.impl.CCAPOutputHandler;
import com.cognizant.gto.ccap.checkin.plugin.vo.CheckinFailureVO;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.Constants;

public class CheckinOutputHandler {

	private static final String SONAR_PROJECT_BASE_DIR = "sonar.projectBaseDir";
	private static final String COM_COGNIZANT_GTO_CCAP_CHECKIN_PLUGIN_UI_CHECKIN_FAILURE_DETAIL_VIEW = "com.cognizant.gto.ccap.checkin.plugin.ui.CheckinFailureDetailView";
	private static IWorkbenchWindow window = null;

	static {
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	private static final CCAPLogger LOG = CCAPLogger
			.getLogger(CheckinOutputHandler.class);

	/**
	 * show checkin failure details
	 *
	 * @param analysisFiles
	 * @param reportXmlLocation
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws PartInitException
	 * @throws XPathExpressionException
	 */
	public boolean showCheckinFailureResult(Properties runnerProperties)
			throws ParserConfigurationException, IOException, SAXException,
			PartInitException, XPathExpressionException {

		String loc = runnerProperties.getProperty(SONAR_WRK_DIR_KEY);

		File violationXML = null;
		String deltaViolationsLoc = loc + File.separator + DELTA_EXPORT
				+ File.separator + DELTA_VIOLATIONS_XML;
		violationXML = new File(deltaViolationsLoc);
		CCAPResult result = null;
		if (violationXML != null && violationXML.exists()) {
		String fileString = FileUtils.readFileToString(violationXML);
		fileString = "<ccap>\n" + fileString + "\n</ccap>";
		violationXML.delete();
		FileUtils.writeStringToFile(violationXML, fileString);

		CCAPOutputHandler output = new CCAPOutputHandler();
		result = output.mapTo(violationXML);
		if (result.isExceededThreshold()) {
			LOG.info("Opening Eclipse Checkin Policy View");
			Constants
					.showErrorAlertWindow("Check-in policy violations detected. See 'Check-in Policy Violations' view for more information.");
			List<CheckinFailureVO> list = null;
			list = mapTo(result,runnerProperties.getProperty(SONAR_PROJECT_BASE_DIR));
			CheckinFailureDetailView.setCheckinFailureList(list);
			refreshCheckinView(CheckinFailureDetailView.class);
			window.getActivePage()
					.showView(
							COM_COGNIZANT_GTO_CCAP_CHECKIN_PLUGIN_UI_CHECKIN_FAILURE_DETAIL_VIEW);
			return result.isExceededThreshold();
		} else {
			return result.isExceededThreshold();
		}
		}
		return false;

	}

	private static List<CheckinFailureVO> mapTo(CCAPResult result, String projectHome) {
		List<CheckinFailureVO> checkinlist = null;
		checkinlist = new ArrayList<CheckinFailureVO>();
		CheckinFailureVO vo = null;
		List<CCAPResource> resources = result.getResourcesList();
		for (CCAPResource res : resources) {
			String resourceName = null;
			for(CCAPViolation r: res.getViolations()){
				resourceName = r.getFileName();
				break;
			}

			List<CCAPMetric> exceeds = res.getExceededMetrics();
			for (CCAPMetric ex : exceeds) {
				vo = new CheckinFailureVO();
				vo.setExpectedMaxvalue(Double.valueOf(ex.getExpectedMaxValue()));
				vo.setExpectedMinValue(Double.valueOf(ex.getExpectedMinValue()));
				vo.setResource(res.getName());
				vo.setRuleDomain(ex.getDomain());
				vo.setRuleName(ex.getName());
				vo.setRuleValue(Double.valueOf(ex.getValue()));
				vo.setOperator(ex.getOperator());
				/**home+relativepath
				 */
				vo.setFullPath(String.format("%s%s%s", projectHome,
						File.separator, resourceName));
				checkinlist.add(vo);
			}
		}
		return checkinlist;
	}

	/**
	 * Method to refresh the ViolationsDetailsView
	 *
	 * @param viewType
	 */
	public static void refreshCheckinView(final Class viewType) {
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		IWorkbenchPage[] pages = null;
		for (int w = 0; w < windows.length; w++) {
			pages = windows[w].getPages();
			IViewReference[] viewRefs = null;
			for (int p = 0; p < pages.length; p++) {
				viewRefs = pages[p].getViewReferences();
				IWorkbenchPart viewPart = null;
				Class<?> partType = null;
				for (int v = 0; v < viewRefs.length; v++) {
					viewPart = viewRefs[v].getPart(false);
					partType = (viewPart != null) ? viewPart.getClass() : null;
					if (viewType == null || viewType.equals(partType)) {
						pages[p].hideView(viewRefs[v]);
					}
				}
			}
		}
	}

}
