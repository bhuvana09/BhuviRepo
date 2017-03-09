/*
 * ViolationsVO.java
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
 * @author GTO - ViolationsVO
 * @version 1.0
 */
public class ViolationsVO {

	private String pkgName = null;
	private String longMsg = null;
	private String bugType = null;
	private String bugPriority = null;
	private String linNo = null;
	private String fullPath = null;
	private Boolean isNew = null;
	private String hasResolution= null;

	/**
	 * @return the fullPath
	 */
	public String getFullPath() {
		return fullPath;
	}

	/**
	 * @param fullPath
	 *            the fullPath to set
	 */
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	/**
	 * @return the pkgName
	 */
	public String getPkgName() {
		return pkgName;
	}

	/**
	 * @param pkgName
	 *            the pkgName to set
	 */
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	/**
	 * @return the longMsg
	 */
	public String getLongMsg() {
		return longMsg;
	}

	/**
	 * @param longMsg
	 *            the longMsg to set
	 */
	public void setLongMsg(String longMsg) {
		this.longMsg = longMsg;
	}

	/**
	 * @return the bugType
	 */
	public String getBugType() {
		return bugType;
	}

	/**
	 * @param bugType
	 *            the bugType to set
	 */
	public void setBugType(String bugType) {
		this.bugType = bugType;
	}

	/**
	 * @return the bugPriority
	 */
	public String getBugPriority() {
		return bugPriority;
	}

	/**
	 * @param bugPriority
	 *            the bugPriority to set
	 */
	public void setBugPriority(String bugPriority) {
		this.bugPriority = bugPriority;
	}

	/**
	 * @return the linNo
	 */
	public String getLinNo() {
		return linNo;
	}

	/**
	 * @param lineNo
	 *            the linNo to set
	 */
	public void setLinNo(String lineNo) {
		this.linNo = lineNo;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public String getHasResolution() {
		return hasResolution;
	}

	public void setHasResolution(String hasResolution) {
		this.hasResolution = hasResolution;
	}

}
