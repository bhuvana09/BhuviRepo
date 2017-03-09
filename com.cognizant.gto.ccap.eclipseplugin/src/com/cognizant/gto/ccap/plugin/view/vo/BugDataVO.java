/*
 * BugDataVO.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author GTO - BugDataVO
 * @version 1.0
 */
public class BugDataVO {
	private static final long serialVersionUID = 1L;
	private List<ViolationsVO> violationsList = new ArrayList<ViolationsVO>();
	private List<MetricsVO> metricsList = new ArrayList<MetricsVO>();

	/**
	 * @return the metricsList
	 */
	public List<MetricsVO> getMetricsList() {
		return metricsList;
	}

	/**
	 * @param metricsList
	 *            the metricsList to set
	 */
	public void setMetricsList(List<MetricsVO> metricsList) {
		this.metricsList = metricsList;
	}

	/**
	 * @return the violationsList
	 */
	public List<ViolationsVO> getViolationsList() {
		return violationsList;
	}

	/**
	 * @param violationsList
	 *            the violationsList to set
	 */
	public void setViolationsList(List<ViolationsVO> violationsList) {
		this.violationsList = violationsList;
	}
}
