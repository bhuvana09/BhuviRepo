package com.cognizant.gto.ccap.plugin;

import static com.ccap.deltaanalysis.internal.impl.constants.Constants.ANALYSIS_TEMP_FOLDER;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.CCAP_URL_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.PROJECTKEY_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.PROJECT_NAME_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.PROJECT_VERSION_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.SONAR_HOST_URL_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.SONAR_LOGIN_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.SONAR_WRK_DIR_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.SOURCE_FOLDER_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.USER_HOME_FOLDER;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.sonar.runner.api.EmbeddedRunner;

import com.ccap.deltaanalysis.internal.impl.CCAPLocal;
import com.cognizant.gto.ccap.eclipseplugin.Activator;
import com.cognizant.gto.ccap.exceptionhandler.CCAPException;
import com.cognizant.gto.ccap.plugin.ui.Exclusions;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.Constants;

public class InputHandler {

	private static final String FORWARD_SLASH = "/";
	private static final String BACK_SLASH = "\\";
	private static final String DOT = ".";
	public static final String SONAR_INCLUSIONS = "sonar.inclusions";
	public static final String SONAR_PROJECT_NAME = PROJECT_NAME_KEY;
	private static final String SONAR_LANGUAGE = "sonar.language";
	private static final String SONAR_PROJECT_PROPERTIES = "sonar-project.properties";
	private static final String SONAR_VERBOSE = "sonar.verbose";
	private static final String SONAR_PROFILE = "sonar.profile";
	public static final String SONAR_PROJECT_BASE_DIR = "sonar.projectBaseDir";
	private static final String SONAR_PASSWORD = "sonar.password";
	public static final String WORKSPACE = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	static IWorkbenchWindow window = null;
	private File propFile = null;

	static {
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	private static final CCAPLogger LOG = CCAPLogger.getLogger(InputHandler.class);

	/**
	 * Prepares input parameter when publish is called
	 * @param event
	 * @return
	 * @throws JavaModelException
	 */
	public Properties prepareInput(ExecutionEvent event)
			throws JavaModelException {

		Properties runnerProperties = null;
		runnerProperties = new Properties();

		IJavaProject currentProject = null;

		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		if (selection == null) {
			throw new CCAPException("No menu item selected");
		}

		if (selection != null) {
			Object selectedObject = ((IStructuredSelection) selection)
					.getFirstElement();

			if (selectedObject instanceof IProject) {
				currentProject = JavaCore.create((IProject) selectedObject);
			} else if (selectedObject instanceof IJavaProject) {
				currentProject = (IJavaProject) selectedObject;
			}

		}

		assignProjectParameters(currentProject, runnerProperties);

		/**
		 *  Override by sonar-project.properties
		 */
		try {
			overrideByRunnerLevelProperties(runnerProperties);
		} catch (CCAPException e) {
			refreshWorkspaceProject(currentProject);
			openFile(this.propFile, event);
			throw e;
		}
		LOG.debug(runnerProperties.toString());
		assignPreferenceParameters(runnerProperties);
		assignCredentials(runnerProperties);
		assignLicenseParameters(runnerProperties);
		return runnerProperties;

	}

	/**
	 * Assigns Project Parameters like, projecthome, projectname, source folders
	 * @param project
	 * @param runnerProperties
	 * @throws JavaModelException
	 */
	private void assignProjectParameters(IJavaProject project,
			Properties runnerProperties) throws JavaModelException {

		String projectName = getProjectName(project);

		runnerProperties.setProperty(PROJECT_NAME_KEY, projectName);

		runnerProperties
				.setProperty(
						PROJECT_VERSION_KEY,
						String.format("%s+%d", projectName,
								System.currentTimeMillis()));
		runnerProperties.setProperty(SOURCE_FOLDER_KEY, String.format("%s",
				getCommaSeperatedString(getSourceFolderList(project))));

		String projectHome = getProjectHome(project);
		runnerProperties.setProperty(SONAR_PROJECT_BASE_DIR, projectHome);

		 long millisStart = Calendar.getInstance().getTimeInMillis();
		runnerProperties.setProperty(SONAR_WRK_DIR_KEY,
				String.format("%s%s.sonar"+ String.valueOf(millisStart), projectHome, File.separator));
	}

	private void assignPreferenceParameters(Properties runnerProperties) {
		if (Activator.getDefault().getPreferenceStore()
				.getString(Constants.PROP_CCAPWEB_URL) != null
				&& !Activator.getDefault().getPreferenceStore()
						.getString(Constants.PROP_CCAPWEB_URL).isEmpty()) {
			runnerProperties
					.setProperty(CCAP_URL_KEY,
							Activator.getDefault().getPreferenceStore()
									.getString(Constants.PROP_CCAPWEB_URL));
		} else {
			throw new CCAPException(
					String.format(
							"Configuration Error: '%s' is not configured in the Preference Page",
							Constants.PROP_CCAPWEB_URL));
		}
		if (Activator.getDefault().getPreferenceStore()
				.getString(Constants.PROP_CCAP_URL) != null
				&& !Activator.getDefault().getPreferenceStore()
						.getString(Constants.PROP_CCAP_URL).isEmpty()) {
			runnerProperties.setProperty(
					SONAR_HOST_URL_KEY,
					Activator.getDefault().getPreferenceStore()
							.getString(Constants.PROP_CCAP_URL));
		} else {
			throw new CCAPException(
					String.format(
							"Configuration Error: '%s' is not configured in the Preference Page",
							Constants.PROP_CCAP_URL));
		}

		if (Activator.getDefault().getPreferenceStore()
				.getString(Constants.PROP_ENABLE_LOG) != null
				&& !Activator.getDefault().getPreferenceStore()
						.getString(Constants.PROP_ENABLE_LOG).isEmpty()) {
			runnerProperties.setProperty(SONAR_VERBOSE, Activator.getDefault()
					.getPreferenceStore().getString(Constants.PROP_ENABLE_LOG));
			addSonarLogToFile();
		}
	}

	/**
	 * Adds sonar logs to a File
	 */
	private void addSonarLogToFile() {
		FileOutputStream infoOut = null;
		FileOutputStream errOut = null;
		try {
			infoOut = new FileOutputStream("sonar.info.log");
			PrintStream infoPS = new PrintStream(infoOut);
			System.setOut(infoPS);
			errOut = new FileOutputStream("sonar.err.log");
			PrintStream errPS = new PrintStream(errOut);
			System.setErr(errPS);
		} catch (FileNotFoundException e) {
			LOG.error("while creating sonar logs");
		}

	}

	/**
	 * Assigns Sonar Credentials to runner properties
	 * @param runnerProperties
	 */
	private void assignCredentials(Properties runnerProperties) {
		if (Activator.getDefault().getPreferenceStore()
				.getString(Constants.PROP_CCAP_USER_LOGIN_ID) != null
				&& !Activator.getDefault().getPreferenceStore()
						.getString(Constants.PROP_CCAP_USER_LOGIN_ID).isEmpty()) {
			runnerProperties.setProperty(
					SONAR_LOGIN_KEY,
					Activator.getDefault().getPreferenceStore()
							.getString(Constants.PROP_CCAP_USER_LOGIN_ID));
		} else {
			throw new CCAPException(
					String.format(
							"Configuration Error: '%s' is not configured in the Preference Page",
							Constants.PROP_CCAP_USER_LOGIN_ID));
		}
		if (Activator.getDefault().getPreferenceStore()
				.getString(Constants.PROP_CCAP_USER_PASSWORD) != null
				&& !Activator.getDefault().getPreferenceStore()
						.getString(Constants.PROP_CCAP_USER_PASSWORD).isEmpty()) {
			runnerProperties.setProperty(
					SONAR_PASSWORD,
					Activator.getDefault().getPreferenceStore()
							.getString(Constants.PROP_CCAP_USER_PASSWORD));
		} else {
			throw new CCAPException(
					String.format(
							"Configuration Error: '%s' is not configured in the Preference Page",
							Constants.PROP_CCAP_USER_LOGIN_ID));
		}
	}

	/**
	 * Gets the sonar.sources values as Array from the sonar-project.properties
	 * @param project
	 * @return
	 */
	public static String[] getSonarSources(IJavaProject project) {
		Properties prop = new Properties();
		String projectHome = null;

		IPath projHome = null;

		projHome = project.getResource().getRawLocation();
		if (projHome != null) {
			projectHome = projHome.toOSString();
		} else {
			projHome = project.getResource().getLocation();
			projectHome = projHome.toOSString();
		}

		if (projectHome == null) {
			throw new IllegalStateException(
					"Project Home location cannot be retrived");
		}

		File propFile = new File(projectHome + File.separator
				+ SONAR_PROJECT_PROPERTIES);

		if (propFile.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(propFile);
				prop.load(reader);
				String folds = prop.getProperty(SOURCE_FOLDER_KEY);
				return folds.split(",");
			} catch (FileNotFoundException e) {
				LOG.error(e);
			} catch (IOException e) {
				LOG.error(e);
			}
		}

		return null;
	}

	/**
	 * Overrides the Runner level configurations of the User from
	 * sonar-project.properties
	 *
	 * Assume: Second Level call assume Project Home available in the Properties
	 * Passed
	 *
	 * @param runnerProperties
	 * @return
	 */
	private void overrideByRunnerLevelProperties(Properties runnerProperties) {

		String projectHome = runnerProperties
				.getProperty(SONAR_PROJECT_BASE_DIR);
		this.propFile = new File(projectHome + File.separator
				+ SONAR_PROJECT_PROPERTIES);

		FileWriter fw = null;
		PrintWriter pw = null;
		FileOutputStream fout = null;
		try {

			if (this.propFile.exists()) {
				FileReader reader = null;
				reader = new FileReader(this.propFile);
				runnerProperties.load(reader);
			} else {
				fout = new FileOutputStream(this.propFile);
				runnerProperties.remove(SONAR_INCLUSIONS);
				runnerProperties.put(PROJECTKEY_KEY, "");
				runnerProperties.put(SONAR_PROFILE, "Sonar way");
				runnerProperties.store(fout, SONAR_PROJECT_PROPERTIES);
				fout.flush();

				fw = new FileWriter(this.propFile, true);
				pw = new PrintWriter(fw);

				StringBuilder propWriter = new StringBuilder();
				propWriter
						.append("\n\n\n")
						.append("#For multi module projects follow the below configuration.\n\n")
						.append("# List of the module identifiers\n")
						.append("# sonar.modules=module1,module2 \n\n")
						.append("# Properties can obviously be overriden for\n")
						.append("# each module - just prefix them with the module ID\n")
						.append("# module1.sonar.projectName=Module 1\n")
						.append("# module2.sonar.projectName=Module 2 \n\n\n")
						.append("#For the additional configuration please check sonar website \n")
						.append("#http://docs.codehaus.org/display/SONAR/Analyzing+with+Sonar+Runner \n\n\n")
						.append("#Tells Sonar to reuse existing reports for Unit Tests execution and coverage reports \n")
						.append("#sonar.dynamicAnalysis=reuseReports \n\n\n")
						.append("#Tells Sonar where the Unit Tests execution reports are \n")
						.append("#sonar.surefire.reportsPath=target/surefire-reports \n\n\n")
						.append("#Tells Sonar that the code coverage tool used by Unit Tests are cobertura \n")
						.append("#sonar.java.coveragePlugin=cobertura \n\n\n")
						.append("#Tells Sonar where the Unit Tests code coverage report is \n")
						.append("#sonar.cobertura.reportPath=target/site/cobertura/coverage.xml");
				pw.write(propWriter.toString());
				throw new CCAPException(String.format(
						"Configure '%s' in the '%s' file at Project Home",
						PROJECTKEY_KEY, SONAR_PROJECT_PROPERTIES));
			}

			 long millisStart = Calendar.getInstance().getTimeInMillis();
				runnerProperties.remove(SONAR_WRK_DIR_KEY);
				runnerProperties.setProperty(SONAR_WRK_DIR_KEY,
				String.format("%s%s.sonar_"+ String.valueOf(millisStart), WORKSPACE, File.separator));
				LOG.info("########################### SONAR_WRK_DIR_KEY : "  + runnerProperties.getProperty(SONAR_WRK_DIR_KEY));

		} catch (IOException ex) {
			throw new CCAPException(String.format(
					"Exception occured while creating '%s' at Project Home",
					SONAR_PROJECT_PROPERTIES), ex);
		} finally {
			if (null != pw) {
				try {
					pw.close();
				} catch (Exception e) {
				}
			}
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {

				}
			}
			if (null != fout) {
				try {
					fout.close();
				} catch (IOException e) {

				}
			}
		}

	}

	/**
	 * Returns the ProjectName of the selected
	 *
	 * @param event
	 * @return
	 */
	public String getProjectName(IJavaProject project) {
		return project.getProject().getName();
	}

	/**
	 * Returns the Project Home of the selected
	 * @param javaProject
	 * @return
	 */
	public String getProjectHome(IJavaProject javaProject) {

		IPath projHome = null;
		String home = null;

		projHome = javaProject.getResource().getRawLocation();
		if (projHome != null) {
			home = projHome.toOSString();
		} else {
			projHome = javaProject.getResource().getLocation();
			home = projHome.toOSString();
		}

		if (home == null) {
			throw new IllegalStateException(
					"Project Home location cannot be retrived");
		}
		return home;
	}

	/**
	 * Gets the source folders of the IProject selected
	 *
	 * @param javaProject
	 * @return List
	 * @throws JavaModelException
	 */
	public Set<String> getSourceFolderList(IJavaProject javaProject)
			throws JavaModelException {
		Set<String> sourceFolders = new HashSet<String>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath projHome = null;
		String home = null;
		String sourceFolderPath = null;

		projHome = javaProject.getResource().getRawLocation();
		if (projHome != null) {
			home = projHome.toOSString();
		} else {
			projHome = javaProject.getResource().getLocation();
			home = projHome.toOSString();
		}
		if (home == null || home.isEmpty()) {
			throw new CCAPException("Fail to get source folder from project");
		}
		String[] temp = home.split("\\\\");
		int len = temp[temp.length - 1].length();
		len++;
		home = home.substring(0, home.length() - len);

		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		IPath path = null;
		IFolder sourceFolder = null;
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				path = entry.getPath();
				sourceFolder = root.getFolder(path);
				sourceFolderPath = sourceFolder.getFullPath().toOSString();
				String src = StringUtils.remove(sourceFolderPath, javaProject
						.getProject().getName());
				/**
				 * CCAP version
				 */
//				src = StringUtils.remove(src, "\\");
				/**
				 * end of ccap version
				 */
				/**
				 * kbc version
				 */
				src = StringUtils.removeStart(src, BACK_SLASH);
				src = StringUtils.replace(src, BACK_SLASH, FORWARD_SLASH);
				/**
				 * end of kbc version
				 */
				sourceFolders.add(src);
			}
		}

		return sourceFolders;
	}
	/**
	 * Concatenate the set of values to a String
	 * @param values
	 * @return
	 */
	public String getCommaSeperatedString(Set<String> values) {
		final String COMMA = ",";
		StringBuffer bufString = new StringBuffer();
		for (String value : values) {
			bufString.append(value).append(COMMA);
		}
		return bufString.toString();
	}

	/**
	 * Prepares Sonar Parameters for the Local Analysis
	 *
	 * @param event
	 * @return
	 * @throws JavaModelException
	 */
	public Properties prepareInputForLocalAnalysis(ExecutionEvent event)
			throws JavaModelException {
		Properties runnerProperties = null;
		runnerProperties = new Properties();

		IJavaProject currentProject = null;

		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		currentProject = getJavaProject(selection);

		assignProjectParameters(currentProject, runnerProperties);

		/**
		 * Override by sonar-project.properties
		 */
		try {
			overrideByRunnerLevelProperties(runnerProperties);
		} catch (CCAPException e) {
			refreshWorkspaceProject(currentProject);
			openFile(this.propFile, event);
			throw e;
		}

		addSelectedFiles(event, runnerProperties);
		LOG.info(runnerProperties.toString());
		assignPreferenceParameters(runnerProperties);
		assignCredentials(runnerProperties);
		assignLicenseParameters(runnerProperties);
		return runnerProperties;
	}

	/**
	 * Prepares Sonar Parameters for the Check-in policy Analysis
	 * Method call used in case of Check-in policy
	 *
	 * @param currentProject
	 * @return
	 * @throws JavaModelException
	 */
	public Properties prepareInputForLocalAnalysis(IJavaProject currentProject)
			throws JavaModelException {

		Properties runnerProperties = null;
		runnerProperties = new Properties();

		assignProjectParameters(currentProject, runnerProperties);

		/**
		 * Override by sonar-project.properties
		 */
		try {
			overrideByRunnerLevelProperties(runnerProperties);
		} catch (CCAPException e) {
			refreshWorkspaceProject(currentProject);
			throw e;
		}
		LOG.info(runnerProperties.toString());
		assignPreferenceParameters(runnerProperties);
		assignCredentials(runnerProperties);
		assignLicenseParameters(runnerProperties);
		return runnerProperties;
	}

	/**
	 * Assigns all selected files to the sonar.inclusions property
	 *
	 * @param event
	 * @param runnerProperties
	 * @return
	 * @throws JavaModelException
	 */
	private IJavaProject addSelectedFiles(ExecutionEvent event,
			Properties runnerProperties) throws JavaModelException {
		Exclusions exclusion = Exclusions.getInstance();

		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		if (selection == null) {
			throw new CCAPException("No menu item selected");
		}

		exclusion.setSelection(selection); // mandatory
		IJavaProject javaProject = null;

	//	exclusion.getSelectedFiles("java", "xml");
//		javaProject = exclusion.getCurrentProject();
		javaProject = getJavaProject(selection);

		Set<String> selectedFiles = removeProjectHome(exclusion.getSelectedFiles("java", "xml"), getProjectHome(javaProject));
		if(selectedFiles!=null && selectedFiles.isEmpty()){
			throw new CCAPException("No valid files are selected. Check your file selection and 'sonar.sources' property configured in sonar-project.properties file");
		}

		/**
		 * KBC version
		 */
		Set<String> modified = new HashSet<String>();
		for(String ss : selectedFiles){
			String temp = StringUtils.replace(ss, BACK_SLASH, FORWARD_SLASH);
//			modified.add(DOT+temp);
			modified.add(temp);
		}

		String inclusions = getCommaSeperatedString(modified);
		/**
		 * end of KBC version
		 */
		/**
		 * ccap version
		 */
//		String inclusions = getCommaSeperatedString(selectedFiles);
		/**
		 * end of ccap version
		 */
		runnerProperties.setProperty(SONAR_INCLUSIONS, inclusions);

		return javaProject;
	}

	/**
	 * Removes the Project Home string from the beginning of all the strings in the List
	 * @param selectedFiles
	 * @param projectHome
	 * @return
	 */
	public Set<String> removeProjectHome(Collection<String> selectedFiles,
			String projectHome) {
		Set<String> inclusionList = new HashSet<String>();
		for (String s : selectedFiles) {
			inclusionList.add(StringUtils.removeStart(s, projectHome));
		}
		return inclusionList;
	}

	/**
	 * Gets the Selected Java Project from the Selected Objects
	 * @param selection
	 * @return
	 */
	public IJavaProject getJavaProject(IStructuredSelection selection) {
		IProject iProjectt = null;

		final Object selectedObject = ((IStructuredSelection) selection)
				.getFirstElement();

		if (selectedObject instanceof IProject) {
			IProject proj = ((IProject) selectedObject);
			iProjectt = proj;
		} else if (selectedObject instanceof IJavaProject) {
			IJavaProject javaProject = (IJavaProject) selectedObject;
			iProjectt = javaProject.getProject();
		} else if (selectedObject instanceof IFolder) {
			IFolder folder = (IFolder) selectedObject;
			iProjectt = folder.getProject();
		} else if (selectedObject instanceof IFile) {
			IFile file = (IFile) selectedObject;
			iProjectt = file.getProject();
		} else if (selectedObject instanceof ICompilationUnit) {
			ICompilationUnit unit = (ICompilationUnit) selectedObject;
			iProjectt = unit.getJavaProject().getProject();
		} else if (selectedObject instanceof IType) {
			IType unit = (IType) selectedObject;
			iProjectt = unit.getJavaProject().getProject();
		} else if (selectedObject instanceof IPackageFragment) {
			IPackageFragment unit = (IPackageFragment) selectedObject;
			iProjectt = unit.getJavaProject().getProject();
		} else if (selectedObject instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot unit = (IPackageFragmentRoot) selectedObject;
			iProjectt = unit.getJavaProject().getProject();
		} else if (selectedObject instanceof IResource) {
			IResource file = (IResource) selectedObject;
			iProjectt = file.getProject();
		} else {
			LOG.error("Could not cast to a IProject from the selection");
		}
		return JavaCore.create(iProjectt);
	}

	/**
	 * Opens the sonar-project.properties file in the Editor
	 * @param fileToOpen
	 * @param event
	 */
	private void openFile(File fileToOpen, final ExecutionEvent event) {
		IWorkbenchPage page = null;
		if (fileToOpen != null) {
			if (fileToOpen.exists() && fileToOpen.isFile()) {
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						fileToOpen.toURI());
				if (page == null) {
					page = getActivePage(event);
				}
				try {
					IDE.openEditorOnFileStore(page, fileStore);
				} catch (PartInitException e) {
					// ignore
				}
			} else {
				// ignore
			}
		}
	}

	/**
	 *	Gets the Active Page in the Workbench
	 * @param event
	 * @return
	 */
	public static IWorkbenchPage getActivePage(ExecutionEvent event) {

		IWorkbenchPage page = null;

		if (window == null) {
			try {
				window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			} catch (ExecutionException e) {
				LOG.error(e);
			}
		}
		if (event != null) {
			if (HandlerUtil.getActiveWorkbenchWindow(event) != null) {
				page = HandlerUtil.getActiveWorkbenchWindow(event)
						.getActivePage();
			} else {
				if (window != null) {
					page = window.getActivePage();
				}
			}
		} else if (window != null) {
			page = window.getActivePage();
		} else {
			LOG.warn("Internal error : could not get the active page through api calls");
		}
		return page;
	}

	/**
	 * Refreshes selected project in workspace
	 *
	 * @param project
	 */
	private void refreshWorkspaceProject(IJavaProject project) {

		try {
			HaltProgressMonitor m = new HaltProgressMonitor();
			project.getProject().refreshLocal(IResource.DEPTH_INFINITE, m);
			try {
				while (!m.isDone()) {
					Thread.sleep(300);
				}
			} catch (InterruptedException e) {
				 LOG.error(e.getMessage());
			}

		} catch (CoreException e) {
			 LOG.error(e.getMessage());
		}

	}

	class HaltProgressMonitor extends NullProgressMonitor {

		@Override
		public void setCanceled(boolean cancelled) {
			isDone = true;
			super.setCanceled(cancelled);
		}

		private boolean isDone = false;

		public synchronized boolean isDone() {
			return isDone;
		}

		@Override
		public void done() {
			isDone = true;
			super.done();
		}
	}

	/**
	 * Delete the working directory
	 * @param runnerProperties 
	 */
	public static void deleteQuietly() {
		FileUtils.deleteQuietly(ANALYSIS_TEMP_FOLDER);
		FileUtils.deleteQuietly(USER_HOME_FOLDER);
		File tempFolder = ANALYSIS_TEMP_FOLDER;
		/**
		 * supported
		 */
		File tempProjectHome = new File(tempFolder.getParent() + File.separator
				+ "Project");
		if (tempProjectHome.exists()) {
			FileUtils.deleteQuietly(tempProjectHome);
		}
		File deltaFileFolder = new File(tempFolder.getParent() + File.separator
				+ "deltatxt");
		if (deltaFileFolder.exists()) {
			FileUtils.deleteQuietly(deltaFileFolder);
		}
		
	
	}

	/**
	 * Adds LocalDB Properties in case of Bat file invocation
	 * @param local
	 * @param runnerProperties
	 * @param dosync
	 * @throws IOException
	 */
	public static void addLocalDBProperties(CCAPLocal local, Properties runnerProperties, boolean dosync) throws IOException{
		String jdbc_url = null;
		jdbc_url = local.copyh2DB(runnerProperties.getProperty(CCAP_URL_KEY), dosync);
		local.addDBProperties(runnerProperties, jdbc_url);
	}


	private static void assignLicenseParameters(Properties runnerProperties){
		InputStream is = null;
		final String PRODUCT_NAME = "ccap.license.product.name";
		final String PRODUCT_TYPE = "ccap.license.product.type";
		final String plugin_license = "License.cert";
		is = InputHandler.class.getClassLoader().getResourceAsStream(plugin_license);

		StringBuffer buffer = new StringBuffer();
		if(is!=null){
			try{
				List list = IOUtils.readLines(is);
				for(Object o : list){
					String line = (String) o;
					buffer.append(line);
				}
			}catch(FileNotFoundException e){
				LOG.error("License.cert file is not available in plugin bundle",e);
				throw new IllegalStateException("License.cert file is not available in plugin bundle", e);
			}catch (IOException e) {
				LOG.error("License.cert file issue",e);
			}finally {
				if(is!=null){
					try{
						is.close();
					}catch(Exception e){

					}
				}
			}
		}else{
			LOG.error("License.cert file is not available in plugin bundle");
			throw new IllegalStateException("License.cert file is not available in plugin bundle");
		}

		//ccap.license.product.type = [VS] [ECLIPSE]
		//ccap.license.product.name = from IDE
		runnerProperties.setProperty(PRODUCT_TYPE, "Eclipse");
		runnerProperties.setProperty(PRODUCT_NAME, buffer.toString());

	}


}