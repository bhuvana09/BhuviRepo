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
import org.eclipse.swt.widgets.Display;

import com.ccap.deltaanalysis.internal.impl.CCAPLocal;
import com.cognizant.gto.ccap.eclipseplugin.Activator;
import com.cognizant.gto.ccap.exceptionhandler.CCAPException;
import com.cognizant.gto.ccap.plugin.exception.CCAPExceptionHandler;
import com.cognizant.gto.ccap.plugin.ui.Exclusions;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.CLIUtils;
import com.cognizant.gto.ccap.plugin.utils.Constants;

public class CCAPLAIncrementalHandler extends AbstractHandler {

	private static final CCAPLogger LOG = CCAPLogger
			.getLogger(CCAPLAIncrementalHandler.class);

	private boolean isIncrementAnalysis = true;
	/**
	 * Method to execute analysis in sonar-project.properties
	 *
	 * @param event
	 * @throws ExecutionException
	 */
	
	
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		try {
			LOG.info("=============== CCAP Local Analysis =============");
			LOG.info("=============== Incremental mode =============");
			InputHandler inputHandler = null;
			inputHandler = new InputHandler();
			LOG.info("Preparing Sonar Analysis Parameters");
			final Properties runnerProperties = inputHandler
					.prepareInputForLocalAnalysis(event);
			String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			File proj_home = new File (workspace);
			
			
			for(File folder: proj_home.listFiles()){
				LOG.info(".sonar folders in project home " + folder.getAbsolutePath());
			    if(folder.isDirectory() && folder.getName().contains(".sonar_")){
					LOG.info("Deleting folder  " + folder.getName());
					FileUtils.deleteQuietly(folder);
			    }
			}
			final String jobMessage = String.format(
					"Running CCAP Local Analysis on '%s'", runnerProperties
							.getProperty(InputHandler.SONAR_PROJECT_NAME));

			final Job incrementalJob = new Job("Cognizant Code Assessment Platform") {
				@Override
				protected IStatus run(IProgressMonitor pMonitor) {
					pMonitor.beginTask(jobMessage, -1);
					try {
						CCAPLocal local = null;
						local = new CCAPLocal();
						LOG.info("invoking Sonar Runner");
						runnerProperties.setProperty("sonar.analysis.mode", "incremental");
						String java_home = null;
						java_home = runnerProperties.getProperty("JAVA_HOME");

						if(java_home!=null){
							InputHandler.addLocalDBProperties(local, runnerProperties, false);
							CLIUtils.execute(runnerProperties.getProperty(InputHandler.SONAR_PROJECT_BASE_DIR), runnerProperties);
						}else{
							local.localAnalysis(runnerProperties, false);
						}

//						local.localAnalysis(runnerProperties, false);

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
										new OutputHandler()
										.showViolationViewIncrement(runnerProperties);
								Constants.showInforAlertWindow("CCAP Local Analysis Completed. "
												+ "Violations tab view will be empty, if there are no incremented violations in selected files.");
										LOG.info("================== CCAP Local Incremented Analysis Completed Successfully ====================");
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
			incrementalJob.setUser(true);
			incrementalJob.addJobChangeListener(jobChangeListener);
			incrementalJob.schedule();

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
	public boolean isIncrementAnalysis() {
		return isIncrementAnalysis;
	}
	public void setIncrementAnalysis(boolean isIncrementAnalysis) {
		this.isIncrementAnalysis = isIncrementAnalysis;
	}
}
