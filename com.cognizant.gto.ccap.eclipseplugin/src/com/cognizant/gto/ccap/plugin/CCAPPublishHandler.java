/*
 * CCAPPluginHandler.java
 *
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.plugin;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.sonar.runner.api.EmbeddedRunner;

import com.ccap.deltaanalysis.internal.impl.CCAPPublish;
import com.cognizant.gto.ccap.eclipseplugin.Activator;
import com.cognizant.gto.ccap.exceptionhandler.CCAPException;
import com.cognizant.gto.ccap.plugin.exception.CCAPExceptionHandler;
import com.cognizant.gto.ccap.plugin.ui.Exclusions;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.CLIUtils;
import com.cognizant.gto.ccap.plugin.utils.Constants;

/**
 * @author GTO - CCAPPluginActivator - This class is handler to respond when
 *         'Code Analysis - Publishing' menu is selected.
 * @version 1.0
 */
public class CCAPPublishHandler extends AbstractHandler {
	private static final CCAPLogger LOG = CCAPLogger
			.getLogger(CCAPPublishHandler.class);

	/**
	 * Method to execute analysis in sonar-project.properties
	 *
	 * @param event
	 * @throws ExecutionException
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		try {
			LOG.info("=============== CCAP Publish Analysis =============");

			InputHandler inputHandler = null;
			inputHandler = new InputHandler();
			LOG.info("Preparing Sonar Analysis Parameters");

			final Properties runnerProperties = inputHandler
					.prepareInput(event);
			String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			File proj_home = new File (workspace);
			

			for (File folder : proj_home.listFiles()) {	
			    if(folder.isDirectory() && folder.getName().contains(".sonar_")){
					LOG.info("Deleting folder  " + folder.getName());
					FileUtils.deleteQuietly(folder);
				}
			}

			final String jobMessage = String.format(
					"Running CCAP Publish Analysis on '%s'", runnerProperties
							.getProperty(InputHandler.SONAR_PROJECT_NAME));

			final Job job = new Job("Cognizant Code Assessment Platform") {
				@Override
				protected IStatus run(IProgressMonitor pMonitor) {
					pMonitor.beginTask(jobMessage, -1);
					try {
						LOG.info("invoking Sonar Runner");
						runnerProperties.setProperty("sonar.analysis.mode", "analysis");
						//TODO java_home's executable validation
						String java_home = null;
						java_home = runnerProperties.getProperty("JAVA_HOME");

						if(java_home!=null){
							CLIUtils.execute(runnerProperties.getProperty(InputHandler.SONAR_PROJECT_BASE_DIR), runnerProperties);
						}else{
							CCAPPublish.publish(runnerProperties);
						}

						LOG.info("executed sonar runner without error");

					} catch (CCAPException exp) {
						CCAPExceptionHandler.handlerException(
								Activator.PLUGIN_ID, exp, event, null);
						return Status.CANCEL_STATUS;
					} catch (Exception exp) {
						CCAPExceptionHandler.handlerException(
								Activator.PLUGIN_ID, exp, event, null);
						return Status.CANCEL_STATUS;
					} finally {
						Exclusions.destory();
					}
					return new Status(Status.OK, Activator.PLUGIN_ID, "OK");
				}
			};

			IJobChangeListener jobChangeListener = new IJobChangeListener() {
				public void done(final IJobChangeEvent ijobchangeevent) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							try {

								if (ijobchangeevent.getResult().isOK()) {
									LOG.info("Opening Eclipse Violation View");
									new OutputHandler()
											.showViolationView(runnerProperties);
									Constants
											.showInforAlertWindow("CCAP Publish Analysis Completed. Open Show View -> CCAP -> Measures to view results. "
													+ "Measures view will be empty, if there are no violations in selected files.");
									LOG.info("================== CCAP Publish Completed Successfully ====================");
								}

							} catch (Exception e) {
								CCAPExceptionHandler.handlerException(
										Activator.PLUGIN_ID, e, event, null);
							} finally {
								InputHandler.deleteQuietly();
							}

						}
					});
				}

				public void sleeping(IJobChangeEvent ijobchangeevent) {
				}

				public void scheduled(IJobChangeEvent ijobchangeevent) {
				}

				public void running(IJobChangeEvent ijobchangeevent) {
				}

				public void awake(IJobChangeEvent ijobchangeevent) {
				}

				public void aboutToRun(IJobChangeEvent ijobchangeevent) {
				}
			};
			job.setUser(true);
			job.addJobChangeListener(jobChangeListener);
			/**
			 *  Start the background analysis
			 */
			job.schedule();

		} catch (JavaModelException e) {
			CCAPExceptionHandler.handlerException(Activator.PLUGIN_ID, e,
					event, null);
			return Status.CANCEL_STATUS;
		} catch (CCAPException exp) {
			CCAPExceptionHandler.handlerException(Activator.PLUGIN_ID, exp,
					event, null);
			return Status.CANCEL_STATUS;
		} catch (Exception exp) {
			CCAPExceptionHandler.handlerException(Activator.PLUGIN_ID, exp,
					event, null);
			return Status.CANCEL_STATUS;
		}

		return event;
	}
	


 


}