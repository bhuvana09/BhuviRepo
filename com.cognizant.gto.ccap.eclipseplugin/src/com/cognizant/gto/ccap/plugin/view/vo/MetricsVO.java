/*
 * MetricsVO.java
 * 
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 * 
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.plugin.view.vo;

/**
 * @author GTO - MetricsVO
 * @version 1.0
 */
public class MetricsVO {
	private String fileName = null;
	private String metricDomain = null;
	private String name = null;
	private String value = null;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the metricDomain
	 */
	public String getMetricDomain() {
		return metricDomain;
	}

	/**
	 * @param metricDomain
	 *            the metricDomain to set
	 */
	public void setMetricDomain(String metricDomain) {
		this.metricDomain = metricDomain;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
