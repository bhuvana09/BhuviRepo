/*
 * CCAPPreferences.java
 *
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cognizant.gto.ccap.eclipseplugin.Activator;
import com.cognizant.gto.ccap.plugin.utils.Constants;

/**
 * @author GTO - CCAPPreferences - CCAP menu with input will be created in
 *         Preference
 * @version 1.0
 */
public class CCAPPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor ccapSonarUrlEditor = null;
	private StringFieldEditor ccapWebUrlEditor = null;
	private BooleanFieldEditor debugFlagEditor = null;
	private StringFieldEditor ccapUserIdEditor = null;
	private StringFieldEditor ccapUserPasswordEditor = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench arg0) {
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * Method to create filed editors for giving CCAP inputs in preference.
	 *
	 */
	@Override
	protected void createFieldEditors() {
		String ccapSonarUrl = this.getPreferenceStore().getString(
				Constants.PROP_CCAP_URL);
		String ccapWebUrl = this.getPreferenceStore().getString(
				Constants.PROP_CCAPWEB_URL);
		String ccapUserId = this.getPreferenceStore().getString(
				Constants.PROP_CCAP_USER_LOGIN_ID);

		ccapSonarUrlEditor = new StringFieldEditor(Constants.PROP_CCAP_URL,
				"CCAP URL:        ", this.getFieldEditorParent());
		ccapSonarUrlEditor.loadDefault();
		ccapSonarUrlEditor.setEmptyStringAllowed(false);
		ccapSonarUrlEditor.setStringValue(ccapSonarUrl);
		ccapWebUrlEditor = new StringFieldEditor(Constants.PROP_CCAPWEB_URL,
				"CCAPWeb URL:", this.getFieldEditorParent());
		ccapWebUrlEditor.loadDefault();
		ccapWebUrlEditor.setEmptyStringAllowed(false);
		ccapWebUrlEditor.setStringValue(ccapWebUrl);

		ccapUserIdEditor = new StringFieldEditor(
				Constants.PROP_CCAP_USER_LOGIN_ID, "User Id:              ",
				this.getFieldEditorParent());
		ccapUserIdEditor.loadDefault();
		ccapUserIdEditor.setEmptyStringAllowed(false);
		ccapUserIdEditor.setStringValue(ccapUserId);

		Composite fieldEditorParent = getFieldEditorParent();
		ccapUserPasswordEditor = new StringFieldEditor(
				Constants.PROP_CCAP_USER_PASSWORD, "Password:         ",
				fieldEditorParent);
		final Label label = ccapUserPasswordEditor
				.getLabelControl(fieldEditorParent);
		label.setToolTipText("User Network Password");
		final Text txt = ccapUserPasswordEditor
				.getTextControl(fieldEditorParent);
		txt.setEchoChar('*');

		debugFlagEditor = new BooleanFieldEditor(Constants.PROP_ENABLE_LOG,
				"Enable debug log for analysis", this.getFieldEditorParent());
		this.addField(ccapSonarUrlEditor);
		this.addField(ccapWebUrlEditor);
		this.addField(ccapUserIdEditor);
		this.addField(ccapUserPasswordEditor);

		this.addField(debugFlagEditor);
	}

	/**
	 * Method to set the preferance values in the preference store.
	 *
	 */
	@Override
	public boolean performOk() {
		String ccapSonarUrl = ccapSonarUrlEditor.getStringValue();
		String ccapWebUrl = ccapWebUrlEditor.getStringValue();
		String ccapUserPassword = ccapUserPasswordEditor.getStringValue();
		String ccapUserId = ccapUserIdEditor.getStringValue();

		this.getPreferenceStore().putValue(Constants.PROP_CCAP_URL,
				ccapSonarUrl);
		this.getPreferenceStore().putValue(Constants.PROP_CCAPWEB_URL,
				ccapWebUrl);
		this.getPreferenceStore().putValue(Constants.PROP_CCAP_USER_LOGIN_ID,
				ccapUserId);
		this.getPreferenceStore().putValue(Constants.PROP_CCAP_USER_PASSWORD,
				ccapUserPassword);

		this.getPreferenceStore().putValue(Constants.PROP_ENABLE_LOG,
				debugFlagEditor.getBooleanValue() + "");

		return super.performOk();
	}
}
