package com.cognizant.gto.ccap.plugin.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;

import com.cognizant.gto.ccap.plugin.InputHandler;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;

/**
 * Exclusions class deals which selection object which is set from UI. the class
 * seperates the excluded files and selected files from the selection object.
 * The full use of Exclusions class can remove the Local Analysis Tree from the
 * UI replacing its features.
 *
 * @author 221013
 */
public class Exclusions {

	private static final CCAPLogger LOG = CCAPLogger
			.getLogger(Exclusions.class);

	private List<String> selectedFiles = new ArrayList<String>();

	public List<String> getSelectedFiles() {

		return selectedFiles;
	}

	private void setSelectedFiles(List<String> selectedFiles) {

		this.selectedFiles = selectedFiles;
	}

	private boolean withTree = false;

	public boolean isWithTree() {

		return withTree;
	}

	public void setWithTree(boolean withTree) {

		this.withTree = withTree;
	}

	private Object selection;

	public Object getSelection() {

		return selection;
	}

	public void setSelection(Object selection) {

		this.selection = selection;
	}

	private Exclusions(Object selection) {

		super();
		this.selection = selection;
	}

	private Exclusions() {

		super();
	}

	private static Exclusions exclusions = null;

	public static Exclusions getInstance() {

		if (exclusions == null) {
			LOG.debug("Initializing Exclusion....");
			exclusions = new Exclusions();
			return exclusions;
		}
		return exclusions;
	}

	public static void destory() {
		exclusions = null;
	}

	private IJavaProject currentProject;

	public IJavaProject getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(IJavaProject currentProject) {
		this.currentProject = currentProject;
	}

	Collection<String> excludedFiles = null;

	/**
	 * @return
	 */
	public Collection<String> getExcludedFiles() {

		if (this.excludedFiles != null) {
			return this.excludedFiles;
		}
		LOG.debug("Searching for Excluded Files........");
		if (selection != null) {
			Collection<IPath> allSelected = new ArrayList<IPath>();
			try {
				Iterator itr = ((IStructuredSelection) selection).iterator();
				while (itr.hasNext()) {
					selection = itr.next();
					if (selection instanceof IPackageFragmentRoot) {
						IPackageFragmentRoot srcFldrs = (IPackageFragmentRoot) selection;
						setCurrentProject(srcFldrs.getJavaProject());
						IJavaElement[] java = srcFldrs.getChildren();
						int i = 0;
						String[] packName = new String[java.length];
						while (i < java.length) {
							packName[i] = java[i++].getElementName();
						}
						for (String p : packName) {
							allSelected.addAll(getClasses(p, srcFldrs));
						}

					} else if (selection instanceof IPackageFragment) {
						IPackageFragment pac = (IPackageFragment) selection;
						setCurrentProject(pac.getJavaProject());
						IJavaElement[] java = pac.getChildren();
						Collection<IPath> cls = new ArrayList<IPath>();
						for (IJavaElement j : java) {
							cls.add(j.getPath());
						}
						allSelected.addAll(cls);
					} else if (selection instanceof ICompilationUnit) {

						ICompilationUnit com = (ICompilationUnit) selection;
						setCurrentProject(com.getJavaProject());
						IJavaElement[] java = com.getChildren();

						Collection<IPath> cls = new HashSet<IPath>();
						for (IJavaElement j : java) {
							cls.add(j.getParent().getPath());

						}
						allSelected.addAll(cls);

					} else if (selection instanceof IJavaProject) {
						setCurrentProject((IJavaProject) selection);
						return selectProjectRoot();
					} else if (selection instanceof IProject) {
						setCurrentProject(JavaCore.create((IProject) selection));
						return selectProjectRoot();
					} else if (selection instanceof IFile) {
						IFile file = (IFile) selection;
						setCurrentProject(JavaCore.create(file.getProject()));

						Collection<IPath> cls = new HashSet<IPath>();

						cls.add(file.getFullPath());
						allSelected.addAll(cls);
					} else if (selection instanceof IFolder) {
						IFolder folder = (IFolder) selection;
						setCurrentProject(JavaCore.create(folder.getProject()));

						Collection<IPath> cls = new HashSet<IPath>();
						IResource[] resList = (IResource[]) folder.members();

						for (IResource res : resList) {
							getChildren(res, cls, "java", "xml");
						}
						allSelected.addAll(cls);
					}
					else {
						// return null;
					}
				}

				Collection<IPath> allFiles = getAllSelectableJavaFiles();
				Collection<String> excluded = new ArrayList<String>();
				Collection<String> allSelectUI = new ArrayList<String>();
				for (IPath p : allFiles) {
					IFile f = (IFile) FileBuffers.getWorkspaceFileAtLocation(p);
					excluded.add(f.getRawLocation().toOSString());
				}

				for (IPath p : allSelected) {
					IFile f = (IFile) FileBuffers.getWorkspaceFileAtLocation(p);
					allSelectUI.add(f.getRawLocation().toOSString());
				}
				excluded.removeAll(allSelectUI);

				setExcludedFiles(excluded);
				setSelectedFiles((List<String>) allSelectUI);

			} catch (JavaModelException e) {
				LOG.error("Error while Searching Excluded Files.", e);
			} catch (CoreException e) {
				LOG.error("Error while Searching Excluded Files.", e);
			}

		} else {
			throw new IllegalStateException(
					"Selection object is null. Instantiate Exclusions class with valid selection from project");
		}
		LOG.debug("Searching for Excluded Files Complete.");
		return this.excludedFiles;
	}

	public void setExcludedFiles(Collection<String> excludedFiles) {
		this.excludedFiles = excludedFiles;
	}

	/**
	 * selects all the files in the Project
	 * @return
	 * @throws CoreException
	 */
	private Collection<String> selectProjectRoot() throws CoreException {

		try {
			Collection<IPath> allFiles = getAllSelectableSourcesFiles();
			Collection<String> selectedFiles = new ArrayList<String>();
			for (IPath p : allFiles) {
				IFile f = (IFile) FileBuffers.getWorkspaceFileAtLocation(p);
				selectedFiles.add(f.getRawLocation().toOSString());
			}

			setSelectedFiles((List<String>) selectedFiles);

		} catch (JavaModelException e) {
			LOG.error("Error while internally selecting the project files", e);
		}
		return selectedFiles;
	}

	/**
	 * Gets all files from the srcfolders present in the current project
	 *
	 * @return
	 * @throws CoreException
	 */
	private Collection<IPath> getAllSelectableJavaFiles() throws CoreException {

		Collection<IPath> all = new ArrayList<IPath>();
		if (selection != null && this.currentProject != null) {
			IPackageFragmentRoot[] srcFldrs = this.currentProject
					.getAllPackageFragmentRoots();
			int sr = 0;
			for (; sr < srcFldrs.length; sr++) {
				IJavaElement[] java = srcFldrs[sr].getChildren();
				int i = 0;
				String[] packName = new String[java.length];
				while (i < java.length) {
					packName[i] = java[i++].getElementName();
				}
				for (String p : packName) {
					all.addAll(getClasses(p, srcFldrs[sr]));
				}

			}

		} else {
			throw new IllegalStateException(
					"selection object is null. instantiate Exclusions class with IStructuredSelection from a project");
		}
		return all;
	}

	/**
	 * Gets all files from the srcfolders present in the current project
	 *
	 * @return
	 * @throws CoreException
	 */
	private Collection<IPath> getAllSelectableSourcesFiles()
			throws CoreException {

		Collection<IPath> all = new ArrayList<IPath>();
		if (selection != null && this.currentProject != null) {
			/**
			 *  STEP1 : all source folders
			 */
			IPackageFragmentRoot[] srcFldrs = this.currentProject
					.getAllPackageFragmentRoots();
			int sr = 0;
			for (; sr < srcFldrs.length; sr++) {
				IJavaElement[] java = srcFldrs[sr].getChildren();
				int i = 0;
				String[] packName = new String[java.length];
				while (i < java.length) {
					packName[i] = java[i++].getElementName();
				}
				for (String p : packName) {
					all.addAll(getClasses(p, srcFldrs[sr]));
				}

			}

			/**
			 *  STEP2 : all non-sources folders
			 */
			Object[] nonResAll = this.currentProject.getNonJavaResources();
			String[] sourceFolders = null;

			for (Object o : nonResAll) {
				if (o instanceof IFolder) {
					IFolder folder = (IFolder) o;
					setCurrentProject(JavaCore.create(folder.getProject()));

					/**
					 * filters the sources mentioned in sonar.sources
					 */
					if (sourceFolders == null) {
						sourceFolders = InputHandler
								.getSonarSources(getCurrentProject());
					}
					// END
					if (sourceFolders != null) {
						for (String s : sourceFolders) {
							if (folder.getName().contains(s)) {
								Collection<IPath> cls = new HashSet<IPath>();
								IResource[] resList = (IResource[]) folder
										.members();

								for (IResource res : resList) {
									getChildren(res, cls, "java", "xml");
								}
								all.addAll(cls);
							}
						}
					} else {
						Collection<IPath> cls = new HashSet<IPath>();
						IResource[] resList = (IResource[]) folder.members();

						for (IResource res : resList) {
							getChildren(res, cls, "java", "xml");
						}
						all.addAll(cls);
					}

				}
			}
			// END
		} else {
			throw new IllegalStateException(
					"selection object is null. instantiate Exclusions class with IStructuredSelection from a project");
		}
		return all;
	}

	/**
	 * Gets all java classes from a package
	 *
	 * @param p
	 * @param srcFldrs
	 * @return
	 * @throws JavaModelException
	 */
	private Collection<IPath> getClasses(String p, IPackageFragmentRoot srcFldrs)
			throws JavaModelException {

		IPackageFragment pf = srcFldrs.getPackageFragment(p);
		ICompilationUnit[] coms = pf.getCompilationUnits();
		Collection<IPath> cls = new ArrayList<IPath>();
		for (ICompilationUnit c : coms) {
			cls.add(c.getPath());
		}
		return cls;
	}

	public List<String> getAllSelectableJavaFilesAsString()
			throws CoreException {

		List<String> allList = new ArrayList<String>();
		try {
			Collection<IPath> all = getAllSelectableJavaFiles();
			for (IPath p : all) {
				IFile f = (IFile) FileBuffers.getWorkspaceFileAtLocation(p);
				allList.add(f.getRawLocation().toOSString());
			}

		} catch (JavaModelException e) {
			LOG.warn(e);
		}
		return allList;
	}

	/**
	 * Gets all java classes from a package
	 *
	 * @param p
	 * @param srcFldrs
	 * @return
	 * @throws JavaModelException
	 */
	private Collection<String> getClassesAsString(String p,
			IPackageFragmentRoot srcFldrs) {

		IPackageFragment pf = srcFldrs.getPackageFragment(p);
		ICompilationUnit[] coms = null;
		try {
			coms = pf.getCompilationUnits();
		} catch (JavaModelException e) {
			LOG.warn(e);
		}
		Collection<String> cls = new HashSet<String>();
		if (coms != null) {
			for (ICompilationUnit c : coms) {
				try {
					String pack = null;
					IPackageDeclaration[] pc = c.getPackageDeclarations();
					for (IPackageDeclaration p1 : pc) {
						pack = p1.getElementName().replace(".", "/");
					}
					cls.add(pack + "/" + c.getElementName());
				} catch (JavaModelException e) {
					LOG.warn(e);
				}

			}
		}
		return cls;
	}

	/**
	 * Return exclusion can be directly added to the Sonar Properties file
	 *
	 * @param excluePackage
	 * @return
	 */
	public Collection<String> getExcludedFiles(String excluePackage) {

		LOG.debug("Searching for Excluded Files in Package level...");
		Collection<String> excluList = new HashSet<String>();
		try {

			if (excluePackage != null) {
				String[] packs = excluePackage.split("\n");
				String[] pkgs = new String[packs.length];
				for (int itr = 0; itr < packs.length; itr++) {
					pkgs[itr] = packs[itr].trim();
				}
				List<String> pckgs = new ArrayList<String>();
				for (String s : pkgs) {
					String temp[] = s.split(",");
					for (String t : temp) {
						pckgs.add(t);
					}
				}
				if (this.currentProject != null) {
					for (String p : pckgs) {
						if (p.endsWith("*")) {
							IPackageFragmentRoot[] srcFldrs = this.currentProject
									.getPackageFragmentRoots();
							int sr = 0;
							Collection<String> allpacksts = new ArrayList<String>();
							for (; sr < srcFldrs.length; sr++) {

								IJavaElement[] java = srcFldrs[sr]
										.getChildren();
								int i = 0;
								String[] packName = new String[java.length];
								while (i < java.length) {
									packName[i] = java[i++].getElementName();
								}
								/**
								 * allpacksts gets all package name start with
								 * p.*
								 */
								String start = p.substring(0, p.length() - 2);
								for (String s : packName) {
									if (s.startsWith(start)) {
										allpacksts.add(s);
									}
								}
								/**
								 * get all classes in the package
								 */
								for (String s : allpacksts) {
									excluList.addAll(getClassesAsString(s,
											srcFldrs[sr]));
								}

							}
						} else {
							IPackageFragmentRoot[] srcFldrs = this.currentProject
									.getPackageFragmentRoots();
							int i = 0;
							for (; i < srcFldrs.length; i++) {
								excluList.addAll(getClassesAsString(p,
										srcFldrs[i]));
							}
						}
						if (p.endsWith(".java")) {
							LOG.debug("interpreting java file");
							String r = p.replace(".java", "");
							String st = r.replace(".", "/");
							String st1 = st + ".java";
							excluList.add(st1);
						}
					}
				} else {
					throw new IllegalStateException("Current Project is null");
				}
			} else {
				LOG.debug("Excluded packages are not avilable from UI to process");
				return excluList;
			}
		} catch (JavaModelException e) {
			LOG.error(e);
		}
		LOG.debug("returning excluded files:" + excluList.toString());
		LOG.debug("Searching Excluded files in Package level completed.");
		return excluList;
	}

	/**
	 * Not working method
	 *
	 * @param path
	 * @return
	 */
	@Deprecated
	public String getFullClassName(String path) {

		IPath filePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);

		ICompilationUnit element = (ICompilationUnit) JavaCore.create(file);
		String packageName = null;
		IPackageFragment pack = (IPackageFragment) element.getParent();

		packageName = pack.getElementName();

		String className = element.getElementName();
		className = className.substring(0, className.length() - 6);
		className = packageName + "." + className;
		return className;
	}

	/**
	 * Returns true if the selection contains atleast one java file
	 *
	 * @return
	 */
	public boolean containsJavaFiles() {

		boolean flag = false;
		if (selectedFiles != null) {
			for (String s : selectedFiles) {
				if (s.endsWith(".java")) {
					flag = true;
				}
			}
		} else {
			LOG.warn("selectedFiles is NULL");
		}
		return flag;
	}

	private static void getChildren(IResource res, Collection<IPath> files,
			String java, String xml) throws CoreException {

		if (res instanceof IFile) {
			if ((res.getFileExtension() != null && res.getFileExtension()
					.endsWith(java))
					|| (res.getFileExtension() != null && res
							.getFileExtension().endsWith(xml)))
				files.add(res.getFullPath());
		}

		if (res instanceof IFolder) {

			IResource[] resources = ((IFolder) res).members();
			for (int j = 0; j < resources.length; j++) {
				getChildren(resources[j], files, java, xml);
			}
		}
	}

	public Collection<String> getSelectedFiles(String fileFormat1, String fileFormat2)
			throws JavaModelException {

		String[] sourceFolders = null;

		if (this.selection != null) {
			Collection<IPath> allSelected = new ArrayList<IPath>();
			try {
				Iterator itr = ((IStructuredSelection) this.selection)
						.iterator();
				while (itr.hasNext()) {
					this.selection = itr.next();
					if (this.selection instanceof IPackageFragmentRoot) {
						IPackageFragmentRoot srcFldrs = (IPackageFragmentRoot) this.selection;
						setCurrentProject(srcFldrs.getJavaProject());
						IJavaElement[] java = srcFldrs.getChildren();
						int i = 0;
						String[] packName = new String[java.length];
						while (i < java.length) {
							packName[i] = java[i++].getElementName();
						}
						for (String p : packName) {
							allSelected.addAll(getClasses(p, srcFldrs));
						}

					} else if (this.selection instanceof IPackageFragment) {
						IPackageFragment pac = (IPackageFragment) this.selection;
						setCurrentProject(pac.getJavaProject());
						IJavaElement[] java = pac.getChildren();
						Collection<IPath> cls = new ArrayList<IPath>();
						for (IJavaElement j : java) {
							cls.add(j.getPath());
						}
						allSelected.addAll(cls);
					} else if (this.selection instanceof ICompilationUnit) {

						ICompilationUnit com = (ICompilationUnit) this.selection;
						setCurrentProject(com.getJavaProject());
						IJavaElement[] java = com.getChildren();

						Collection<IPath> cls = new HashSet<IPath>();
						for (IJavaElement j : java) {
							cls.add(j.getParent().getPath());

						}
						allSelected.addAll(cls);

					} else if (this.selection instanceof IJavaProject) {
						setCurrentProject((IJavaProject) this.selection);
						return selectProjectRoot();
					} else if (this.selection instanceof IProject) {
						setCurrentProject(JavaCore
								.create((IProject) this.selection));
						return selectProjectRoot();
					} else if (this.selection instanceof IFile) {
						IFile file = (IFile) this.selection;
						setCurrentProject(JavaCore.create(file.getProject()));
						Collection<IPath> cls = new HashSet<IPath>();

						/**
						 *  filters the sources mentioned in sonar.sources
						 */
						if (sourceFolders == null) {
							sourceFolders = InputHandler
									.getSonarSources(getCurrentProject());
						}

						if(sourceFolders != null){
							for(String s : sourceFolders){
								if(file.getFullPath().toOSString().contains(s)){
									cls.add(file.getFullPath());
									allSelected.addAll(cls);
								}
							}
						}

					} else if (this.selection instanceof IFolder) {
						IFolder folder = (IFolder) this.selection;
						setCurrentProject(JavaCore.create(folder.getProject()));
						/**
						 *  filters the sources mentioned in sonar.sources
						 */
						if (sourceFolders == null) {
							sourceFolders = InputHandler
									.getSonarSources(getCurrentProject());
						}
						// END
						if (sourceFolders != null) {
							for (String s : sourceFolders) {
								if (folder.getName().contains(s)) {
									Collection<IPath> cls = new HashSet<IPath>();
									IResource[] resList = (IResource[]) folder
											.members();

									for (IResource res : resList) {
										getChildren(res, cls, fileFormat1, fileFormat2);
									}
									allSelected.addAll(cls);
								}
							}
						} else {
							Collection<IPath> cls = new HashSet<IPath>();
							IResource[] resList = (IResource[]) folder
									.members();

							for (IResource res : resList) {
								getChildren(res, cls, fileFormat1, fileFormat2);
							}
							allSelected.addAll(cls);
						}

					} else {
						// return null;
					}
				}

				Collection<String> allSelectUI = new ArrayList<String>();
				for (IPath p : allSelected) {
					IFile f = (IFile) FileBuffers.getWorkspaceFileAtLocation(p);
					allSelectUI.add(f.getRawLocation().toOSString());
				}
				setSelectedFiles((List<String>) allSelectUI);

			} catch (JavaModelException e) {
				LOG.error("Error while Searching Included Files.", e);
			} catch (CoreException e) {
				LOG.error("Error while Searching Included Files.", e);
			}

		}
		return this.selectedFiles;
	}

}
