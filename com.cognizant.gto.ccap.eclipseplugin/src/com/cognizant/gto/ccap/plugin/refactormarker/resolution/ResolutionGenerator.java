package com.cognizant.gto.ccap.plugin.refactormarker.resolution;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognizant.gto.ccap.plugin.refactormarker.MarkerFactory;

public class ResolutionGenerator implements IMarkerResolutionGenerator2 {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ResolutionGenerator.class);
	private final String STRING_ELIPSES = "...";
	private final ResolutionRegistry registry = ResolutionRegistry
			.getInstance();

	public ResolutionGenerator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.
	 * core.resources.IMarker)
	 */
	@Override
	public IMarkerResolution[] getResolutions(IMarker paramIMarker) {
		LOGGER.debug("Inside getResolutions");
		IMarkerResolution2[] markerResolutions = null;
		String markerRuleName = paramIMarker.getAttribute(
				MarkerFactory.VIOLATED_RULE_NAME, MarkerFactory.STRING_NA);
		ArrayList<IMarkerResolution2> markerResolutionsList = new ArrayList<IMarkerResolution2>();
		List<ResolutionType> resolutionTypes = registry.getResolutions();
		Map<String, Set<Integer>> resolutionIndicesMap = registry
				.getRulewiseResolutions();
		LOGGER.debug("resolutionIndicesMap: " + resolutionIndicesMap.isEmpty());
		// Handling multiple resolutions for same Rule
		for (Integer resolutionIndex : resolutionIndicesMap.get(markerRuleName)) {
			IConfigurationElement resolutionElement = resolutionTypes.get(
					resolutionIndex).getResolutionElement();
			String resolutionClassName = resolutionElement
					.getAttribute(ResolutionType.ATTR_CLASS);
			String resolutionRuleInfo = resolutionElement
					.getAttribute(ResolutionType.ATTR_INFO);
			String markerRuleInfo = paramIMarker.getAttribute(
					MarkerFactory.VIOLATED_RULE_INFO, MarkerFactory.STRING_NA);
			String markerTextData = paramIMarker.getAttribute(
					MarkerFactory.SELECTED_ITEM_TEXT, MarkerFactory.STRING_NA);
			LOGGER.debug("resolutionClassName: " + resolutionClassName);
			LOGGER.debug("markerRuleInfo: " + markerRuleInfo);
			LOGGER.debug("markerTextData: " + markerTextData);

			if (MarkerFactory.STRING_NA.equals(resolutionClassName)
					|| MarkerFactory.STRING_NA.equals(resolutionRuleInfo)) {
				continue;
			}

			try {
				Constructor<?> constructor = (Constructor<?>) Class.forName(
						resolutionClassName).getDeclaredConstructor(
						String.class, String.class);

				if (null == constructor) {
					continue;
				}

				StringBuilder resolutionDescription = new StringBuilder();

				if (!MarkerFactory.STRING_NA.equals(markerRuleInfo)) {
					resolutionDescription.append(markerRuleInfo);
				}

				if (!MarkerFactory.STRING_NA.equals(markerTextData)) {
					resolutionDescription.append(STRING_ELIPSES)
							.append(markerTextData).append(STRING_ELIPSES);
				}

				Object resolutionClass = constructor.newInstance(
						resolutionRuleInfo, resolutionDescription.toString());

				if (null == resolutionClass
						|| !(resolutionClass instanceof IMarkerResolution2)) {
					continue;
				}

				markerResolutionsList.add((IMarkerResolution2) resolutionClass);
			} catch (Exception e) {
				Throwable t = e.getCause();
				String causalException = (null != t) ? t.getClass()
						.getSimpleName() : e.getClass().getSimpleName();

				LOGGER.error("No resolutions found for " + markerRuleName
						+ " due to " + causalException, ((null != t) ? t : e));
			}
		}
		LOGGER.debug("markerResolutionsList empty: " + markerResolutionsList.isEmpty());

		if (!markerResolutionsList.isEmpty()) {
			// Set the fetched results
			markerResolutions = markerResolutionsList
					.toArray(new IMarkerResolution2[markerResolutionsList
							.size()]);
			LOGGER.debug("markerResolutionsList size: " + markerResolutionsList.size());

		}

		return markerResolutions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IMarkerResolutionGenerator2#hasResolutions(org.eclipse
	 * .core.resources.IMarker)
	 */
	@Override
	public boolean hasResolutions(IMarker paramIMarker) {
		LOGGER.debug("inside  hasResolutions ");
		if (!paramIMarker.exists()) {
			return false;
		}

		String markerRuleName = paramIMarker.getAttribute(
				MarkerFactory.VIOLATED_RULE_NAME, MarkerFactory.STRING_NA);

		if (MarkerFactory.STRING_NA.equals(markerRuleName)) {
			return false;
		}
		LOGGER.debug("markerRuleName:: " + markerRuleName);

		boolean resolutionExists = paramIMarker.getAttribute(
				MarkerFactory.RESOLUTION_EXISTS, false);
		boolean foundResolutions = resolutionRegistryHasRule(markerRuleName);
		LOGGER.debug("foundResolutions:: " + foundResolutions);

		// Sync up the flag on the marker
		if ((!resolutionExists && foundResolutions)
				|| (resolutionExists && !foundResolutions)) {
			resolutionExists = foundResolutions;

			try {
				paramIMarker.setAttribute(MarkerFactory.RESOLUTION_EXISTS,
						resolutionExists);

				return foundResolutions;
			} catch (CoreException ce) {
				LOGGER.warn("Caught " + ce.getClass().getSimpleName()
						+ " with message: " + ce.getMessage(), ce);
			}
		}
		LOGGER.debug("resolutionExists:: " + resolutionExists);

		return resolutionExists;
	}

	private boolean resolutionRegistryHasRule(String markerRuleName) {
		// Refresh the resolutions registry
		registry.refresh();

		return (!registry.getResolvableRules().isEmpty() && registry
				.getResolvableRules().contains(markerRuleName));
	}

}
