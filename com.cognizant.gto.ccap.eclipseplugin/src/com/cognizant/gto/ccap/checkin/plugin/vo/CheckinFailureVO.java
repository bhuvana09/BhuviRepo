/*
 * CheckinFailureVO.java
 *
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.checkin.plugin.vo;

import java.io.Serializable;

/**
 * @author GTO - CheckinFailureVO
 * @version 1.0
 */
public class CheckinFailureVO implements Serializable {
	private String resource;
	private String ruleDomain;
	private String ruleName;
	private String lineNo;
	private Double ruleValue;
	private Double expectedMinValue;
	private Double expectedMaxvalue;
	private String operator;
	private String fullPath;
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	/**
	 * @return the resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * @return the ruleDomain
	 */
	public String getRuleDomain() {
		return ruleDomain;
	}

	/**
	 * @param ruleDomain
	 *            the ruleDomain to set
	 */
	public void setRuleDomain(String ruleDomain) {
		this.ruleDomain = ruleDomain;
	}

	/**
	 * @return the ruleName
	 */
	public String getRuleName() {
		return ruleName;
	}

	/**
	 * @param ruleName
	 *            the ruleName to set
	 */
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	/**
	 * @return the lineNo
	 */
	public String getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo
	 *            the lineNo to set
	 */
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return the ruleValue
	 */
	public Double getRuleValue() {
		return ruleValue;
	}

	/**
	 * @param ruleValue
	 *            the ruleValue to set
	 */
	public void setRuleValue(Double ruleValue) {
		this.ruleValue = ruleValue;
	}

	/**
	 * @return the expectedMinValue
	 */
	public Double getExpectedMinValue() {
		return expectedMinValue;
	}

	/**
	 * @param expectedMinValue
	 *            the expectedMinValue to set
	 */
	public void setExpectedMinValue(Double expectedMinValue) {
		this.expectedMinValue = expectedMinValue;
	}

	/**
	 * @return the expectedMaxvalue
	 */
	public Double getExpectedMaxvalue() {
		return expectedMaxvalue;
	}

	/**
	 * @param expectedMaxvalue
	 *            the expectedMaxvalue to set
	 */
	public void setExpectedMaxvalue(Double expectedMaxvalue) {
		this.expectedMaxvalue = expectedMaxvalue;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}
}
