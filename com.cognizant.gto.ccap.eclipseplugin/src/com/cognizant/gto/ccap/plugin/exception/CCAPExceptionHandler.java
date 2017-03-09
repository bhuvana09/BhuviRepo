package com.cognizant.gto.ccap.plugin.exception;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import com.cognizant.gto.ccap.exceptionhandler.CCAPException;
import com.cognizant.gto.ccap.exceptionhandler.ErrorMessage;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;

/**
 * Class handles all the exception passed and Popups the Message in the UI
 * screen and Logs the error stack to the Error Log view
 *
 * @author 221013
 *
 */
public class CCAPExceptionHandler {

	private static final String CQ_ANALYSIS_FAILED_PLEASE_CHECK_LOG_FOR_MORE_INFORMATION = "CCAP Analysis Failed, please check log for more information";

	private static final CCAPLogger LOG = CCAPLogger
			.getLogger(CCAPExceptionHandler.class);

	public static void handlerException(final String pluginId,
			final Exception exp, ExecutionEvent event, final IWorkbenchPage page) {

		final Display display = Display.getDefault();

		display.syncExec(new Runnable() {

			public void run() {

				IStatus errorStatus = null;
				if (exp instanceof CCAPException) {

					CCAPException e = (CCAPException) exp;
					Status s = new Status(IStatus.INFO, pluginId, e
							.getGenericMessage());

					errorStatus = new MultiStatus(pluginId, IStatus.INFO,
							new Status[] { s }, e.getLocalizedMessage(), e
									.getCause());
					LOG.info("Error Occured in CCAP Analysis", e);
					StatusAdapter adapter = new StatusAdapter(errorStatus);
					StatusManager.getManager().handle(adapter,
							StatusManager.LOG | StatusManager.SHOW);
					WorkbenchPlugin.getDefault().getLog()
							.log(adapter.getStatus());
				} else {
					errorStatus = new Status(
							IStatus.ERROR,
							pluginId,
							CQ_ANALYSIS_FAILED_PLEASE_CHECK_LOG_FOR_MORE_INFORMATION,
							exp);
					LOG.error("Error Occured in CCAP Analysis", exp);
					StatusAdapter adapter = new StatusAdapter(errorStatus);
					StatusManager.getManager().handle(adapter,
							StatusManager.LOG | StatusManager.SHOW);
					WorkbenchPlugin.getDefault().getLog()
							.log(adapter.getStatus());

				}
			}
		});

	}

	public static void handlerException(final String pluginId,
			final String msg, final ExecutionEvent event,
			final IWorkbenchPage page) {

		final Display display = Display.getDefault();

		display.syncExec(new Runnable() {

			public void run() {

				String errorMsgToUser = ErrorMessage.getUserErrorMessage(msg);
				if (errorMsgToUser != null) {

					IStatus errorStatus = new Status(IStatus.INFO, pluginId,
							errorMsgToUser, null);
					StatusAdapter adapter = new StatusAdapter(errorStatus);
					StatusManager.getManager().handle(adapter,
							StatusManager.LOG | StatusManager.SHOW);
					WorkbenchPlugin.getDefault().getLog()
							.log(adapter.getStatus());
					LOG.info(msg);
				} else {
					IStatus errorStatus = new Status(
							IStatus.ERROR,
							pluginId,
							CQ_ANALYSIS_FAILED_PLEASE_CHECK_LOG_FOR_MORE_INFORMATION,
							null);
					StatusAdapter adapter = new StatusAdapter(errorStatus);
					StatusManager.getManager().handle(adapter,
							StatusManager.LOG | StatusManager.SHOW);
					WorkbenchPlugin.getDefault().getLog()
							.log(adapter.getStatus());
					LOG.error(msg);
				}

			}
		});

	}

}
