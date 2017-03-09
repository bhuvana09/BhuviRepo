/*
 * ViolationsDetailView.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.cognizant.gto.ccap.eclipseplugin.Activator;
import com.cognizant.gto.ccap.plugin.refactormarker.MarkerFactory;
import com.cognizant.gto.ccap.plugin.refactormarker.MarkerPayload;
import com.cognizant.gto.ccap.plugin.refactormarker.resolution.ResolutionRegistry;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.Constants;
import com.cognizant.gto.ccap.plugin.view.vo.BugDataVO;
import com.cognizant.gto.ccap.plugin.view.vo.MetricsVO;
import com.cognizant.gto.ccap.plugin.view.vo.ViolationsVO;

/**
 * @author GTO - ViolationsDetailView - Class to create view with different tabs
 *         to show all the metrics
 * @version 1.0
 */
public class ViolationsDetailView extends ViewPart {

	private static CCAPLogger logger = CCAPLogger
			.getLogger(ViolationsDetailView.class);
	private static List<BugDataVO> totalViolations = new ArrayList<BugDataVO>();
	private TableViewer violationsViewer, metricsViewer = null;

	private ViewLabelProvider labelProvider = null;

	private int violationsCount = 0;
	private int metricsCount = 0;
	private Table violationTable;
	private ISelection selection;

	private ViolationTableFilter violationFilter = null;
	private ViewTableComparator viewerComparator = null;

	private List<ViolationsVO> initialBaseContent_ViolationVOS = new ArrayList<ViolationsVO>();

	/**
	 * @return the totalViolations
	 */
	public static List<BugDataVO> getTotalViolations() {
		return totalViolations;
	}

	/**
	 * @param totalViolations
	 *            the totalViolations to set
	 */
	public static void setTotalViolations(List<BugDataVO> totalViolations) {
		ViolationsDetailView.totalViolations = totalViolations;
	}

	/**
	 * @return the violationsViewer
	 */
	public TableViewer getViolationsViewer() {
		return violationsViewer;
	}

	/**
	 * @param violationsViewer
	 *            the violationsViewer to set
	 */
	public void setViolationsViewer(TableViewer violationsViewer) {
		this.violationsViewer = violationsViewer;
	}

	public ViolationsDetailView() {
		super();

		violationsCount = 0;
		metricsCount = 0;
		violationFilter = new ViolationTableFilter();
	}

	// private void createMenu() {
	// IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
	// mgr.add(selectAllAction);
	// }

	// private void refreshToggleButtonState(String commandID, String
	// constantID) {
	//
	// ICommandService commandService = (ICommandService)
	// PlatformUI.getWorkbench().getService(ICommandService.class);
	// Command command = commandService.getCommand(commandID);
	//
	// State state = command.getState(RegistryToggleState.STATE_ID);
	// if (Activator.getDefault().getPreferenceStore().getBoolean(constantID)) {
	// state.setValue(Boolean.TRUE);
	// } else {
	// state.setValue(Boolean.FALSE);
	// }
	// }

	Action actionToggle;

	private void createAction() {

		ImageDescriptor icon = Activator
				.getImageDescriptor("icons/new-bug-icon.png");
		actionToggle = new Action("Show only new Violations", SWT.TOGGLE) {

			@Override
			public void runWithEvent(Event event) {
				ToolItem item = (ToolItem) event.widget;
				System.out.println(item.getSelection());
				if (item.getSelection()) {
					loadOnlyNewViolations();
				} else {
					loadAllViolations();
				}
				super.runWithEvent(event);
			}
		};

		actionToggle.setImageDescriptor(icon);
	}

	@SuppressWarnings("unchecked")
	private final void loadOnlyNewViolations(){
		List<ViolationsVO> currentState = (List<ViolationsVO>) this.violationsViewer.getInput();
		currentState.clear();
		currentState.addAll(getNewViolations());
		this.violationsViewer.refresh();
	}

	@SuppressWarnings("unchecked")
	private final void loadAllViolations(){
		List<ViolationsVO> currentState = (List<ViolationsVO>) this.violationsViewer.getInput();
		currentState.clear();
		currentState.addAll(getAllViolations());
		this.violationsViewer.refresh();
	}

	private Collection<? extends ViolationsVO> getAllViolations() {
		List<ViolationsVO> copyAll = null;
		copyAll = new ArrayList<ViolationsVO>();
		copyAll.addAll(this.initialBaseContent_ViolationVOS);
		return copyAll;
	}

	private List<ViolationsVO> getNewViolations() {
		List<ViolationsVO> copyNew =  null;
		copyNew = new ArrayList<ViolationsVO>();
		for(ViolationsVO c : this.initialBaseContent_ViolationVOS){
			if(c.getIsNew()){
				copyNew.add(c);
			}
		}
		return copyNew;
	}

	private void createToolBar() {
		IToolBarManager toolbarMGR = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarMGR.add(actionToggle);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		ViolationsContentProvider violationsContentProvider = null;
		List<ViolationsVO> violationsVOList = new ArrayList<ViolationsVO>();
		List<MetricsVO> metricsVOList = new ArrayList<MetricsVO>();
		totalViolations = ViolationsDetailView.getTotalViolations();

		for (BugDataVO compVO : totalViolations) {
			violationsVOList = compVO.getViolationsList();
			//caching
			initialBaseContent_ViolationVOS.addAll(violationsVOList);

			metricsVOList = compVO.getMetricsList();
		}

		Composite container = new Composite(parent, SWT.NONE);
		/**
		 * Options for filtering new violations
		 */
		// createToolbar(parent);
		createAction();
		createToolBar();

		FillLayout layout = new FillLayout();
		container.setLayout(new GridLayout(1, true));

		/**
		 * Search Composite for search label and testbox
		 */
		final Composite searchComposite = new Composite(container, SWT.NONE);
		GridData gd = null;
		TabItem violationsTab, metricsTab = null;
		TabFolder tabFolder = null;
		searchComposite.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		searchComposite.setLayoutData(gd);
		Label searchLabel = new Label(searchComposite, SWT.NONE);
		searchLabel.setText("Filter by Resource: ");
		final Text searchText = new Text(searchComposite, SWT.BORDER);
		gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		searchText.setLayoutData(gd);

		tabFolder = new TabFolder(container, SWT.BORDER);

		gd = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gd);

		/**
		 * Create Violations tab
		 */
		violationsTab = new TabItem(tabFolder, SWT.BORDER);
		violationsTab.setText("Violations Details");
		Composite violationsComposite = new Composite(tabFolder,
				SWT.COLOR_DARK_GREEN);
		violationsComposite.setLayout(layout);
		violationsTab.setControl(violationsComposite);

		/**
		 * Create Metrics tab
		 */
		metricsTab = new TabItem(tabFolder, SWT.NULL);
		metricsTab.setText("Metrics Details");
		Composite metricsComposite = new Composite(tabFolder,
				SWT.COLOR_DARK_BLUE);
		metricsComposite.setLayout(layout);
		metricsTab.setControl(metricsComposite);

		/**
		 * Create Violations tab view
		 */
		violationsViewer = new TableViewer(violationsComposite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		violationTable = violationsViewer.getTable();
		violationTable.setLinesVisible(true);
		violationTable.setHeaderVisible(true);
		violationsContentProvider = new ViolationsContentProvider();
		labelProvider = new ViewLabelProvider();
		violationsViewer.setContentProvider(violationsContentProvider);
		violationsViewer.setLabelProvider(labelProvider);
		drawViolationsTableColumns(violationsViewer);
		violationsViewer.setInput(violationsVOList);
		viewerComparator = new ViewTableComparator();
		violationsViewer.setComparator(viewerComparator);

		violationsViewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				try {
					ISelection selection = violationsViewer.getSelection();
					TableItem[] seItems = null;
					ViolationsVO bugDataVO = null;
					int beginLine = 1;
					IWorkspaceRoot workspaceRoot = ResourcesPlugin
							.getWorkspace().getRoot();
					cleanupAllRefactorMarkers(workspaceRoot);
					
					if (selection.isEmpty()) {
						seItems = violationTable.getSelection();
						bugDataVO = new ViolationsVO();
						for (TableItem s : seItems) {
							bugDataVO.setPkgName(s.getText(1));
							bugDataVO.setBugType(s.getText(2));
							bugDataVO.setLongMsg(s.getText(3));
							bugDataVO.setBugPriority(s.getText(4));
							bugDataVO.setLinNo(s.getText(5));
							bugDataVO.setFullPath(s.getText(6));
							bugDataVO.setHasResolution(s.getText(7));
						}
					}
					if (!selection.isEmpty()) {
						Object obj = ((IStructuredSelection) selection)
								.getFirstElement();
						bugDataVO = (ViolationsVO) obj;
					}
					if (!StringUtils.isEmpty(bugDataVO.getLinNo())) {

						beginLine = Integer.valueOf(bugDataVO.getLinNo())
								.intValue();
					}
					IFile file = workspaceRoot.getFileForLocation(
									new Path(bugDataVO.getFullPath()));
					logger.info("IFILE getLocation: " + file.getLocation().toString());
					
					logger.info("IFILE PROJECTNAME: " + file.getProject().getName());
					IType iType = JavaCore.createCompilationUnitFrom(file).getTypes()[0];
					
						logger.info("itype getFullyQualifiedName:" + iType.getFullyQualifiedName());
						logger.info("itype getElementName:" + iType.getElementName());
						logger.info("itype getElementType:" + iType.getElementType());

						Set<String> resolvableRules = ResolutionRegistry.getInstance()
								.getResolvableRules();
					//IMarker aiMarker = file.createMarker("com.cognizant.gto.ccap.plugin.refactormarker");
					boolean resolutionExists = (!resolvableRules.isEmpty() && resolvableRules
								.contains(bugDataVO.getBugType().replace(" ", "")));
					logger.info("resolutionExists:" + resolutionExists);

					IMarker aiMarker = MarkerFactory
							.createRefactorMarker(prepareMarkerPayload(iType.getFullyQualifiedName(),bugDataVO.getFullPath(),
									beginLine,file.getProject().getName(), 
									bugDataVO.getBugType(),bugDataVO.getLongMsg(), resolutionExists));
					
					showLineReference(aiMarker);
				} catch (Exception exc) {
					exc.printStackTrace();
					logger.error("ViolationsDetailView: hookMouseListener()"
							+ exc);
				}
			}

			private void showLineReference(IMarker marker) {
				IEditorPart editor = getSite().getPage().getActiveEditor();
				if (editor != null) {
					org.eclipse.ui.IEditorInput input = editor.getEditorInput();
					if (input instanceof IFileEditorInput) {
						IFile file = ((IFileEditorInput) input).getFile();
						if (marker.getResource().equals(file)) {
							IDE.gotoMarker(editor, marker);
						} else if (marker.getResource() instanceof IFile) {
							IWorkbenchPage page = getSite().getPage();

							try {
								editor = IDE.openEditor(page,
										(IFile) marker.getResource(), true);
								IDE.gotoMarker(editor, marker);
							} catch (PartInitException partException) {
								logger.error("ViolationsDetailView: showLineReference()"
										+ partException);
							}
						}
					}
				} else {
					/**
					 * No file is opened and so no active editor
					 */
					if (marker.getResource() instanceof IFile) {
						IWorkbenchPage page = getSite().getPage();

						try {
							editor = IDE.openEditor(page,
									(IFile) marker.getResource(), true);
							IDE.gotoMarker(editor, marker);
						} catch (PartInitException partException) {
							logger.error("ViolationsDetailView: showLineReference()"
									+ partException);
						}
					}
				}
			}
		});
		violationsViewer.setSelection(selection);

		/**
		 * Create metrics tab view
		 */
		MetricsContentProvider metricsContentProvider = null;
		metricsViewer = new TableViewer(metricsComposite, 0x10302);
		Table metricsTable = metricsViewer.getTable();
		metricsTable.setLinesVisible(true);
		metricsTable.setHeaderVisible(true);
		metricsContentProvider = new MetricsContentProvider();
		labelProvider = new ViewLabelProvider();
		metricsViewer.setContentProvider(metricsContentProvider);
		metricsViewer.setLabelProvider(labelProvider);
		drawMetricsTableColumns(metricsViewer);
		metricsViewer.setInput(metricsVOList);
		metricsViewer.setComparator(viewerComparator);

		/**
		 * Adding Violation and Metric filter in Search Text box
		 */
		searchText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;
			//TODO new violations changes
				//violationFilter.setFilterMethod(ViolationTableFilter.FILE_NAME_METHOD);
			//END
				violationFilter.setSearchText(text.getText());
				violationsViewer.refresh();
				metricsViewer.refresh();
			}
		});
		violationsViewer.addFilter(violationFilter);
		metricsViewer.addFilter(violationFilter);
	}

	/**
	 * method to draw violations details tab
	 *
	 * @param violationTable
	 * @param violationsViewer2
	 * @param vTypeList
	 */
	private void drawViolationsTableColumns(final TableViewer violationViewer) {
		TableViewerColumn[] columns = new TableViewerColumn[8];
		columns[0] = createTableViewerColumn(violationViewer, "S.No", 0, true,
				35);
		columns[0].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText((violationViewer.getTable().indexOf(
						(TableItem) cell.getItem()) + 1)
						+ "");
			}
		});
		
		columns[1] = createTableViewerColumn(violationViewer, "Resolution Available",
				1, true, 70);
		columns[1].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				if(violationsVO.getHasResolution().toString().equals("yes")){
			cell.setImage(Activator.getImage(Constants.IMG_FIXIT));
				}
				else{
					cell.setImage(Activator.getImage(Constants.IMG_NOFIX));
				}
			}
		});
		
		columns[2] = createTableViewerColumn(violationViewer, "Resource", 2,
				true, 150);
		columns[2].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				cell.setText(violationsVO.getPkgName());
			}
		});
		columns[3] = createTableViewerColumn(violationViewer, "Message", 3,
				true, 220);
		columns[3].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				cell.setText(violationsVO.getLongMsg());
			}
		});
		columns[4] = createTableViewerColumn(violationViewer, "Rule", 4, true,
				150);
		columns[4].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				cell.setText(violationsVO.getBugType());
			}
		});
		columns[5] = createTableViewerColumn(violationViewer, "Severity", 5,
				true, 50);
		columns[5].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				cell.setText(violationsVO.getBugPriority());
			}
		});
		columns[6] = createTableViewerColumn(violationViewer, "Line No", 6,
				true, 50);
		columns[6].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				cell.setText(violationsVO.getLinNo());
			}
		});
		columns[7] = createTableViewerColumn(violationViewer, "Resource Path",
				7, false, 0);
		columns[7].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ViolationsVO violationsVO = (ViolationsVO) cell.getElement();
				cell.setText(violationsVO.getFullPath());
			}
		});
		
	}


	/**
	 * method to draw size metics details tab
	 *
	 * @param violationTable
	 * @param vTypeList
	 */
	private void drawMetricsTableColumns(final TableViewer metricViewer) {
		TableViewerColumn[] columns = new TableViewerColumn[8];
		columns[0] = createTableViewerColumn(metricViewer, "S.No", 0, true, 35);
		columns[0].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText((metricViewer.getTable().indexOf(
						(TableItem) cell.getItem()) + 1)
						+ "");
			}
		});
		columns[1] = createTableViewerColumn(metricViewer, "Resource", 1, true,
				150);
		columns[1].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				MetricsVO metricVO = (MetricsVO) cell.getElement();
				cell.setText(metricVO.getFileName());
			}
		});
		columns[2] = createTableViewerColumn(metricViewer, "Metric_Domain", 2,
				true, 220);
		columns[2].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				MetricsVO metricVO = (MetricsVO) cell.getElement();
				cell.setText(metricVO.getMetricDomain());
			}
		});
		columns[3] = createTableViewerColumn(metricViewer, "Name", 3, true, 150);
		columns[3].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				MetricsVO metricVO = (MetricsVO) cell.getElement();
				cell.setText(metricVO.getName());
			}
		});
		columns[4] = createTableViewerColumn(metricViewer, "Value", 4, true, 50);
		columns[4].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				MetricsVO metricVO = (MetricsVO) cell.getElement();
				cell.setText(metricVO.getValue());
			}
		});
	}

	/**
	 * Method to set the fields for violation and metric table columns.
	 *
	 * @param impactTable
	 * @param name
	 * @param columnId
	 * @param resizable
	 * @param width
	 * @param isSourceCode
	 * @return TableColumn
	 */
	private TableViewerColumn createTableViewerColumn(
			final TableViewer violationViewer, final String name,
			final int columnId, boolean resizable, int width) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(
				violationViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();

		if (0 == columnId) {
			column.setResizable(resizable);
			column.setText(name);
			column.setWidth(width);
		} else {
			column.setResizable(resizable);
			column.setText(name);
			column.setWidth(width);
			column.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					viewerComparator.setColumn(columnId);
					int dir = viewerComparator.getDirection();
					if (violationViewer.getTable().getSortColumn() == column) {
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					} else {
						dir = SWT.DOWN;
					}
					violationViewer.getTable().setSortDirection(dir);
					violationViewer.getTable().setSortColumn(column);
					violationViewer.refresh();
				}
			});
		}
		return viewerColumn;
	}

	/**
	 * Inner class to Violation Content Provider.
	 *
	 */
	class ViolationsContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object parent) {
			List<ViolationsVO> violList = (List<ViolationsVO>) parent;
			return violList.toArray();
		}

		public void dispose() {
		}

		public void show() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * Inner class to Metric Container Provider.
	 *
	 */
	class MetricsContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object parent) {
			List<MetricsVO> metricsList = (List<MetricsVO>) parent;
			return metricsList.toArray();
		}

		public void dispose() {
		}

		public void show() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	/**
	 * Inner class to View Label Provider.
	 *
	 */
	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			String result = null;

			try {
				if (obj instanceof ViolationsVO) {
					ViolationsVO violationsVO = (ViolationsVO) obj;
					if (index == 0) {
						violationsCount++;
						result = String.valueOf(violationsCount);
					} else if (index == 1) {
						result = violationsVO.getPkgName();
					} else if (index == 2) {
						result = violationsVO.getLongMsg();
					} else if (index == 3) {
						result = violationsVO.getBugType();
					} else if (index == 4) {
						result = violationsVO.getBugPriority();
					} else if (index == 5) {
						result = violationsVO.getLinNo();
					} else if (index == 5) {
						result = violationsVO.getFullPath();
					} else if(index==6){
						result= violationsVO.getHasResolution();
					}
				}

				else if (obj instanceof MetricsVO) {

					MetricsVO metricsVO = (MetricsVO) obj;
					if (index == 0) {
						metricsCount++;
						result = String.valueOf(metricsCount);
					} else if (index == 1) {
						result = metricsVO.getFileName();
					} else if (index == 2) {
						result = metricsVO.getMetricDomain();
					} else if (index == 3) {
						result = metricsVO.getName();
					} else if (index == 4) {
						result = metricsVO.getValue();
					}
				} else {
					result = obj.getClass().getName();
				}
			} catch (Exception exc) {
				logger.error("ViolationsDetailView: getColumnText()", exc);
			}
			return result;
		}

		public Image getColumnImage(Object obj, int index) {
			Image image = null;
			String result = null;

			if (obj instanceof ViolationsVO) {
				ViolationsVO bugDataVO = (ViolationsVO) obj;
				if (index == 4) {
					result = bugDataVO.getBugPriority();
					if (result.equalsIgnoreCase("BLOCKER")) {
						image = Activator.getImage(Constants.IMG_BLOCKER);
					} else if (result.equalsIgnoreCase("CRITICAL")) {
						image = Activator.getImage(Constants.IMG_CRITICAL);
					} else if (result.equalsIgnoreCase("MAJOR")) {
						image = Activator.getImage(Constants.IMG_MAJOR);

					} else if (result.equalsIgnoreCase("MINOR")) {
						image = Activator.getImage(Constants.IMG_MINOR);
					} else if (result.equalsIgnoreCase("INFO")) {
						image = Activator.getImage(Constants.IMG_INFO);
					}
				}
			}
			return image;
		}
	}

	@Override
	public void setFocus() {
		violationsViewer.getTable().setFocus();
	}

	public Image setIconImage(java.awt.Image image) {
		return null;
	}

	/**
	 * Inner class to filter the Violation & Metric view.
	 *
	 */
	private final class ViolationTableFilter extends ViewerFilter {
	//	public static final String FILE_NAME_METHOD = "FILE_NAME_METHOD";
//		public static final String NEW_VIOLATION_METHOD = "NEW_VIOLATION_METHOD";
//		private String filterMethod;
		private String searchString;
		private boolean result = true;

//		public void setFilterMethod(String filterMethod) {
//			this.filterMethod = filterMethod;
//		}

		public void setSearchText(String s) {
			/**
			 * Search must be a substring of the existing value
			 */
			this.searchString = ".*" + s + ".*";
		}

		@Override
		public boolean select(Viewer arg0, Object parentElement, Object element) {

//			if(this.filterMethod.equals(NEW_VIOLATION_METHOD)){
//
//			}else if(this.filterMethod.equals(FILE_NAME_METHOD)){
//				if (searchString != null && searchString.length() > 0) {
//					String txt = labelProvider.getColumnText(element, 1);
//					result = txt.toLowerCase().matches(searchString.toLowerCase());
//				}
//			}

			if (searchString != null && searchString.length() > 0) {
				String txt = labelProvider.getColumnText(element, 1);
				result = txt.toLowerCase().matches(searchString.toLowerCase());
			}

			return result;
		}

		@Override
		public boolean isFilterProperty(final Object element, final String prop) {
			return true;
		}
	}
	
	
	
	
	private static MarkerPayload prepareMarkerPayload(
			String qualifiedName, String javaFilePath, int lineNo, 
			String projectName, String ruleName, String ruleDescription,
			boolean resolutionFound) {
		final short priority = 3, columns = ((short) MarkerFactory.INT_MINUSONE);
		String elementName = fetchElementName(qualifiedName);

		return new MarkerPayload(elementName, columns,
				lineNo, javaFilePath,
				priority, projectName,
				qualifiedName, resolutionFound,
				ruleDescription, ruleName, columns,
				lineNo);
	}

	
	private static String fetchElementName(String qualifiedName) {
		if (qualifiedName.contains(".")) {
			return (qualifiedName.substring(qualifiedName
					.lastIndexOf(".") + 1));
		}

		return qualifiedName;
	}
	
	private boolean cleanupAllRefactorMarkers(
			IWorkspaceRoot workspaceRoot) throws CoreException {	
			if (workspaceRoot.exists()) {
				MarkerFactory.removeAllMarkers(workspaceRoot);
				logger.info("Cleaned up previous refactor markers!!");

				return true;
			}

		return false;
	}

}