/*
 * ViewTableComparator.java
 *
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.plugin.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.cognizant.gto.ccap.plugin.view.vo.MetricsVO;
import com.cognizant.gto.ccap.plugin.view.vo.ViolationsVO;

/**
 * Class to sort the columns in violation and metric view
 */
public class ViewTableComparator extends ViewerComparator {

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public static final int PROPERTY = 1;

	/**
	 *  Violation tab
	 */
	public static final int RESOURCE = 1;
	public static final int MESSAGE = 2;
	public static final int RULE = 3;
	public static final int SEVERITY = 4;
	public static final int FULLPATH = 5;
	public static final int HASRESOLUTION= 6;

	/**
	 *  Metric tab
	 */
	public static final int FILENAME = 1;
	public static final int METRICDOMAIN = 2;
	public static final int NAME = 3;
	public static final int VALUE = 4;

	public ViewTableComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			/**
			 *  Same column as last sort; toggle the direction
			 */
			direction = 1 - direction;
		} else {
			/**
			 *  New column; do an ascending sort
			 */
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		int rc = 0;

		if (obj1 instanceof ViolationsVO && obj2 instanceof ViolationsVO) {
			/**
			 *  To sort the Violation tab column
			 */
			ViolationsVO v1 = (ViolationsVO) obj1;
			ViolationsVO v2 = (ViolationsVO) obj2;
			switch (propertyIndex) {
			case RESOURCE:
				rc = v1.getPkgName().compareTo(v2.getPkgName());
				break;
			case MESSAGE:
				rc = v1.getLongMsg().compareTo(v2.getLongMsg());
				break;
			case RULE:
				rc = v1.getBugType().compareTo(v2.getBugType());
				break;
			case SEVERITY:
				rc = v1.getBugPriority().compareTo(v2.getBugPriority());
				break;

			case FULLPATH:
				rc = v1.getFullPath().compareTo(v2.getFullPath());
				break;
			case HASRESOLUTION:
				rc=v1.getHasResolution().compareTo(v2.getHasResolution());
				break;
			default:
				rc = 0;
			}
		} else if (obj1 instanceof MetricsVO && obj2 instanceof MetricsVO) {
			/**
			 *  To sort the Metric tab column
			 */
			MetricsVO m1 = (MetricsVO) obj1;
			MetricsVO m2 = (MetricsVO) obj2;
			switch (propertyIndex) {
			case FILENAME:
				rc = m1.getFileName().compareTo(m2.getFileName());
				break;
			case METRICDOMAIN:
				rc = m1.getMetricDomain().compareTo(m2.getMetricDomain());
				break;
			case NAME:
				rc = m1.getName().compareTo(m2.getName());
				break;
			case VALUE:
				rc = m1.getValue().compareTo(m2.getValue());
				break;
			default:
				rc = 0;
			}
		}
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}