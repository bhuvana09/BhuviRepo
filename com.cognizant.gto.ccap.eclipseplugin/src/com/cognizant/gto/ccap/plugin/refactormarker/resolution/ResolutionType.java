package com.cognizant.gto.ccap.plugin.refactormarker.resolution;

import org.eclipse.core.runtime.IConfigurationElement;

public class ResolutionType {
	// class attribute
	public final static String ATTR_CLASS = "class";
	// group attribute
	public final static String ATTR_GROUP = "group";
	// info attribute
	public final static String ATTR_INFO = "info";
	// rule attribute
	public final static String ATTR_RULE = "rule";

	// element of the entry
	private IConfigurationElement resolutionElement;

	private ResolutionType(IConfigurationElement element) {
		if (null != element) {
			this.resolutionElement = element;
		}
	}

	/**
	 * Returns ResolutionType Wrapper for IConfigurationElement
	 * 
	 * @param element
	 *            -- non-null config element for Resolution
	 * @return -- non-null resolution type having input element
	 */
	public static ResolutionType populateConfiguration(
			IConfigurationElement element) {
		return (new ResolutionType(element));
	}

	public IConfigurationElement getResolutionElement() {
		return resolutionElement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((resolutionElement == null) ? 0 : resolutionElement
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		ResolutionType other = (ResolutionType) obj;

		if (resolutionElement == null) {
			if (other.resolutionElement != null) {
				return false;
			}
		} else if (other.resolutionElement == null) {
			return false;
		} else if (null == resolutionElement.getAttribute(ATTR_RULE)) {
			if (null != other.resolutionElement.getAttribute(ATTR_RULE)) {
				return false;
			}
		} else if (!(resolutionElement.getAttribute(ATTR_RULE).trim())
				.equals(other.resolutionElement.getAttribute(ATTR_RULE).trim())) {
			return false;
		} else if (null == resolutionElement.getAttribute(ATTR_CLASS)) {
			if (null != other.resolutionElement.getAttribute(ATTR_CLASS)) {
				return false;
			}
		} else if (!(resolutionElement.getAttribute(ATTR_CLASS).trim())
				.equals(other.resolutionElement.getAttribute(ATTR_CLASS).trim())) {
			return false;
		}

		// Rule and Class attributes are identical
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ResolutionType:{");

		if (null != resolutionElement) {
			builder.append("(" + ATTR_RULE + ":"
					+ resolutionElement.getAttribute(ATTR_RULE) + "),");
			builder.append("(" + ATTR_INFO + ":"
					+ resolutionElement.getAttribute(ATTR_INFO) + "),");
			builder.append("(" + ATTR_CLASS + ":"
					+ resolutionElement.getAttribute(ATTR_CLASS) + "),");
			builder.append("(" + ATTR_GROUP + ":"
					+ resolutionElement.getAttribute(ATTR_GROUP) + ")");
		}

		return builder.append("}]").toString();
	}

}
