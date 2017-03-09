package com.cognizant.gto.ccap.plugin.refactormarker.resolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolutionRegistry {
	/** the extension point ID in the plugin.xml file */
	public static final String EXTENSION_POINT = "com.cognizant.gto.ccap.eclipseplugin.markerResolution";

	// the singleton
	private static final ResolutionRegistry INSTANCE = new ResolutionRegistry();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ResolutionRegistry.class);

	public static ResolutionRegistry getInstance() {
		if (!INSTANCE.initialised) {
			INSTANCE.fetchAvailableResolutionTypes(false);
		}

		return INSTANCE;
	}

	private boolean initialised = false;

	private boolean loggedNoResolutionMessage = false;

	// the entries from the extension registry
	private List<ResolutionType> resolutions = null;

	// Set of unique resolvableRules configured in the fragment manifest
	private Set<String> resolvableRules = null;

	// Map of rule name versus resolutions indices
	private Map<String, Set<Integer>> rulewiseResolutions = null;

	private void fetchAvailableResolutionTypes(boolean refresh) {
		IConfigurationElement[] foundElements = null;

		if (!initialised || refresh) {
			// initialize fields
			resolvableRules = getResolvableRules();
			resolutions = getResolutions();
			rulewiseResolutions = getRulewiseResolutions();

			foundElements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(EXTENSION_POINT);
		}

		if (!loggedNoResolutionMessage
				&& (null == foundElements || foundElements.length <= 0)) {
			loggedNoResolutionMessage = true;

			LOGGER.info("No Resolution Contributions Found...");

			return;
		}

		for (int index = 0; index < foundElements.length; index++) {
			IConfigurationElement element = foundElements[index];
			String clazz = element.getAttribute(ResolutionType.ATTR_RULE);
			String rule = element.getAttribute(ResolutionType.ATTR_RULE);
			String info = element.getAttribute(ResolutionType.ATTR_RULE);

			// Skip elements with empty values
			if ((null == clazz || clazz.trim().isEmpty())
					|| (null == rule || rule.trim().isEmpty())
					|| (null == info || info.trim().isEmpty())) {
				continue;
			}

			Set<Integer> resolutionIndexSet;

			if (resolvableRules.add(rule)) {
				resolutionIndexSet = new HashSet<Integer>();

				LOGGER.info("Found Resolution Contribution for rule: " + rule);
			} else {
				// Handling multiple Resolutions for same Rule
				resolutionIndexSet = rulewiseResolutions.get(rule);
			}

			if (resolutionIndexSet.add(new Integer(index))) {
				ResolutionType resolutionType = ResolutionType
						.populateConfiguration(element);
				Set<ResolutionType> resolutionTypes = new HashSet<ResolutionType>();

				// Filtering out identically configured elements
				if (resolutionTypes.add(resolutionType)) {
					rulewiseResolutions.put(rule, resolutionIndexSet);
					resolutions.add(index, resolutionType);
				}
			}
		}

		if (!initialised) {
			initialised = true;
		}
	}

	/**
	 * @param updateFlag
	 * @return List of  CCAP ResolutionTypes registered with the platform
	 */
	public List<ResolutionType> getResolutions() {
		if (null == resolutions) {
			resolutions = new ArrayList<ResolutionType>();
		}

		return resolutions;
	}

	public Set<String> getResolvableRules() {
		if (null == resolvableRules) {
			resolvableRules = new HashSet<String>();
		}

		return resolvableRules;
	}

	public Map<String, Set<Integer>> getRulewiseResolutions() {
		if (null == rulewiseResolutions) {
			rulewiseResolutions = new HashMap<String, Set<Integer>>();
		}

		return rulewiseResolutions;
	}

	public void refresh() {
		fetchAvailableResolutionTypes(true);
	}
}
