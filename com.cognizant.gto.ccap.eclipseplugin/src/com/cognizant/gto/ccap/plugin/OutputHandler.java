package com.cognizant.gto.ccap.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

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
import com.cognizant.gto.ccap.plugin.refactormarker.resolution.ResolutionRegistry;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.Constants;
import com.cognizant.gto.ccap.plugin.view.ViolationsDetailView;
import com.cognizant.gto.ccap.plugin.view.vo.BugDataVO;
import com.cognizant.gto.ccap.plugin.view.vo.MetricsVO;
import com.cognizant.gto.ccap.plugin.view.vo.ViolationsVO;

import static com.ccap.deltaanalysis.internal.impl.constants.Constants.*;

public class OutputHandler {

	private static final CCAPLogger LOG = CCAPLogger
			.getLogger(OutputHandler.class);

	public static final String COM_COGNIZANT_GTO_CCAP_PLUGIN_VIEW_VIOLATIONS_DETAIL_VIEW = "com.cognizant.gto.ccap.plugin.view.ViolationsDetailView";
	private static final String SONAR_PROJECT_BASE_DIR = "sonar.projectBaseDir";
	public static IWorkbenchWindow window = null;

	static {
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Maps the CCAPResult to BugDataVO
	 * 
	 * @param result
	 * @param projectHome
	 * @return
	 */
	List<BugDataVO> mapTo(CCAPResult result, String projectHome) {

		String yes="yes";
		String no="no";
		BugDataVO totalXML = new BugDataVO();
		List<ViolationsVO> violations = new ArrayList<ViolationsVO>();
		List<MetricsVO> metrics = new ArrayList<MetricsVO>();

		List<CCAPResource> resources = result.getResourcesList();
		
		//fetch list of refactor rules from resolution registry fragment
		Set<String> resolvableRules = ResolutionRegistry.getInstance()
				.getResolvableRules();
		
		for (CCAPResource res : resources) {
			MetricsVO mvo = null;

			for (CCAPMetric deltametric : res.getMetrics()) {
				mvo = new MetricsVO();
				mvo.setFileName(res.getName());
				mvo.setMetricDomain(deltametric.getDomain());
				mvo.setName(deltametric.getName());
				mvo.setValue(String.valueOf(deltametric.getValue()));
				metrics.add(mvo);
			}
			ViolationsVO vvo = null;
			for (CCAPViolation viol : res.getViolations()) {
				vvo = new ViolationsVO();
				vvo.setHasResolution(no);
				if(!resolvableRules.isEmpty() && resolvableRules.contains(viol.getRuleName().replace(" ", ""))){
					vvo.setHasResolution(yes);
				}				
				
				vvo.setBugPriority(viol.getSeverity().name());
				vvo.setBugType(viol.getRuleName());
				/**
				 * home+relativepath
				 */
				vvo.setFullPath(String.format("%s%s%s", projectHome,
						File.separator, viol.getFileName()));
				vvo.setLinNo(String.valueOf(viol.getLineNumber()));
				vvo.setLongMsg(viol.getMessage());
				vvo.setPkgName(viol.getFileName());
				vvo.setIsNew(viol.isNewViolation());
				violations.add(vvo);
			}
		}

		totalXML.setMetricsList(metrics);
		totalXML.setViolationsList(violations);
		List<BugDataVO> bugdatavo = new ArrayList<BugDataVO>();
		bugdatavo.add(totalXML);
		return bugdatavo;
	}

	public void showViolationView(Properties runnerProperties)
			throws IOException, ParserConfigurationException, SAXException {

		/**
		 * Gets the violations.xml and convert to CCAPResult
		 */
		String loc = runnerProperties.getProperty(SONAR_WRK_DIR_KEY);

		File violationXML = null;
		String deltaViolationsLoc = loc + File.separator + DELTA_EXPORT
				+ File.separator + DELTA_VIOLATIONS_XML;
		violationXML = new File(deltaViolationsLoc);
		List<BugDataVO> totalViolations = new ArrayList<BugDataVO>() ;

		if (violationXML != null && violationXML.exists()) {
		
		String fileString = FileUtils.readFileToString(violationXML);
		fileString = "<ccap>\n" + fileString + "\n</ccap>";
		violationXML.delete();
		FileUtils.writeStringToFile(violationXML, fileString);
		CCAPResult result = null;
		CCAPOutputHandler output = new CCAPOutputHandler();
		result = output.mapTo(violationXML);

		/**
		 * Converts to CCAPResult to BugDataVO
		 */
		 totalViolations = mapTo(result,
				runnerProperties.getProperty(SONAR_PROJECT_BASE_DIR));
		}
		if (totalViolations != null && totalViolations.isEmpty()) {
			Constants
					.showInforAlertWindow("No Violation for the Selected Files");
			return;
		}

		ViolationsDetailView detailView = new ViolationsDetailView();
		detailView.setTotalViolations(totalViolations);
		refreshViolationsView(ViolationsDetailView.class);
		try {
			window.getActivePage().showView(
					COM_COGNIZANT_GTO_CCAP_PLUGIN_VIEW_VIOLATIONS_DETAIL_VIEW);
		} catch (PartInitException e) {
			LOG.error(e);
		}
	}

	public void showViolationViewIncrement(Properties runnerProperties)
			throws IOException, ParserConfigurationException, SAXException {

		/**
		 * Gets the violations.xml and convert to CCAPResult
		 */
		String loc = runnerProperties.getProperty(SONAR_WRK_DIR_KEY);

		File violationXML = null;
		String deltaViolationsLoc = loc + File.separator + DELTA_EXPORT
				+ File.separator + DELTA_VIOLATIONS_XML;
		violationXML = new File(deltaViolationsLoc);
		List<BugDataVO> totalViolations = new ArrayList<BugDataVO>() ;
		if (violationXML != null && violationXML.exists()) {

	
		String fileString = FileUtils.readFileToString(violationXML);

		fileString = "<ccap>\n" + fileString + "\n</ccap>";
		violationXML.delete();
		FileUtils.writeStringToFile(violationXML, fileString);
		CCAPResult result = null;
		CCAPOutputHandler output = new CCAPOutputHandler();
		result = output.mapTo(violationXML);

		/**
		 * Converts to CCAPResult to BugDataVO
		 */
		totalViolations = mapTo(result,
				runnerProperties.getProperty(SONAR_PROJECT_BASE_DIR));
		}
		if (totalViolations != null && totalViolations.isEmpty()) {
			Constants
					.showInforAlertWindow("No Incremented Violation for the Selected Files or Project");
			return;
		}

		ViolationsDetailView detailView = new ViolationsDetailView();
		detailView.setTotalViolations(totalViolations);
		refreshViolationsView(ViolationsDetailView.class);
		try {
			window.getActivePage().showView(
					COM_COGNIZANT_GTO_CCAP_PLUGIN_VIEW_VIOLATIONS_DETAIL_VIEW);
		} catch (PartInitException e) {
			LOG.error(e);
		}
	
	}

	/**
	 * Method to refresh the ViolationsDetailsView
	 * 
	 * @param viewType
	 */
	public static void refreshViolationsView(final Class viewType) {
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