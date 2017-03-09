/*
 * Constants.java
 *
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.plugin.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Constants in this files are lesser used. Since the Constants file is reused from the old code base.
 * For any code review or understanding for newer implementation please change or take it into your control.
 *
 * @author GTO - Constants - Constants used in this plugin.
 * @version 1.0
 */
public class Constants {

	/**
	 * Default constructor
	 */
	public Constants() {

	}

	public static final String NEW_VIOLATION_ICON = "image.cap.new-violation.icon";

	public static final String DB_NAME = "sonar";
	public static final String MARKER_ICON = "image.cap.marker.icon";
	public static final String CAP_VIEW_ICON = "image.cap.icon";
	public static final String FOLDER_ICON = "image.cap.folder.icon";
	public static final String JAVA_FILE_ICON = "image.cap.java.file.icon";
	public static final String INFO_ICON = "image.cap.info.icon";
	public static final String WARN_ICON = "image.cap.warn.icon";
	public static final String CAP_LOGO = "image.cap.logo";
	public static final String PACKAGE_ICON = "image.cap.pkg.icon";
	public static final String SRC_FOLDER_ICON = "image.cap.src.folder.icon";
	public static final String CLASS_ICON = "image.cap.class.icon";
	public static final String METHOD_ICON = "image.cap.method.icon";
	public static final String LINE_ICON = "image.cap.line.icon";
	public static final String PROP_CCAP_URL = "CCAP_URL";
	public static final String PROP_CCAPWEB_URL = "CCAPWEB_URL";
	public static final String PROP_PROJECT_KEY = "PROJECT_KEY";
	public static final String PROP_CCAP_USER_LOGIN_ID = "USER_LOGINID";
	public static final String PROP_CCAP_USER_PASSWORD = "USER_PASSWORD";
	public static final String PROP_SONAR_RUNNER_OPTS = "SONAR_RUNNER_OPTS";
	public static final String PROP_SONAR_PROFILE = "SONAR_PROFILE";
	public static final String PROP_ENABLE_LOG = "ENABLE_LOG";
	public static final String DEFAULT_CCAP_SONAR_URL = "http://localhost:8080";
	public static final String DEFAULT_PROJECT_KEY = "com.cognizant.default:projectkey";
	public static final String PLUGIN_SOURCE_FILEANALYSIS = "fileanalysis";
	public static final String PLUGIN_SOURCE_SVN_CHECKIN = "svncheckin";
	public static final String PLUGIN_SOURCE_CVS_CHECKIN = "cvscheckin";
	public static final String IMG_BLOCKER = "image.ccap.blocker.icon";
	public static final String IMG_CRITICAL = "image.cap.critical.icon";
	public static final String IMG_MAJOR = "image.ccap.major.icon";
	public static final String IMG_MINOR = "image.ccap.minor.icon";
	public static final String IMG_INFO = "image.ccap.info.icon";
	public static final String CONSOLE_TITLE = "CCAP Analysis Output";
	public static final String SERVER_URL = "http://ccap.cognizant.com/CCAPWeb/CcapInfoServlet";
	public static final String CCAP_ANALYSIS = "CCAP Analysis";
	public static final String PROJECT_TYPE_ANT = "ant";
	public static final String PROJECT_TYPE_MAVEN = "maven";
	public static final String LINUX = "linux";
	public static final String ITEM_TYPE = "TYPE";
	public static final String PROJECT = "PROJECT";
	public static final String PACKAGE = "PACKAGE";
	public static final String SOURCE = "SOURCE";
	public static final String LOCATION = "LOCATION";
	public static final String OUTPUT_LOCATION = "OUTPUT_LOCATION";
	public static final String SOURCE_LOCATION = "SOURCE_LOCATION";
	public static final int EDITABLECOLUMN = 1;
	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String COMPONENT_NAME = "Enter component name";
	public static final String IMG_FIXIT="image.ccap.fixit.icon";
	public static final String IMG_FIXITT="image.ccap.fixitt.icon";
	public static final String IMG_NOFIX="image.ccap.nofix.icon";
	
	// Error Message
	public static final String ERROR_VIOLATION_VIEW = "Error in ViolationDetailsView";
	public static final String ANALYSIS_IN_PROGRESS = "Another code analysis is in progress. Please wait until it completes.";
	public static final String ANALYSIS_EXCEPTION = "Exception occured during Analysis.";

	// NUMBERS
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int HUNDERED = 100;
	public static final int EIGHTY = 80;
	public static final int EIGHTEEN = 18;

	public static void showInforAlertWindow(String infoMessage) {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		MessageDialog.openInformation(shell,
				"Cognizant Code Assessment Platform", infoMessage);
	}

	public static void showErrorAlertWindow(String errorMessage) {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		MessageDialog.openError(shell, "Cognizant Code Assessment Platform",
				errorMessage);
	}

}
