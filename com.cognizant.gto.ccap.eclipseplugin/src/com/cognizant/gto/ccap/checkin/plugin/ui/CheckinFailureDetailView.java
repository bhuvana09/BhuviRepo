/*
 * CheckinFailureDetailView.java
 *
 * Copyright (c) 2013-2014 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.cognizant.gto.ccap.checkin.plugin.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.cognizant.gto.ccap.checkin.plugin.vo.CheckinFailureVO;
import com.cognizant.gto.ccap.plugin.utils.CCAPLogger;
import com.cognizant.gto.ccap.plugin.utils.Constants;
import com.cognizant.gto.ccap.plugin.view.vo.ViolationsVO;

/**
 * @author GTO - CheckinFailureDetailView - Detail view will be created
 * @version 1.0
 */
public class CheckinFailureDetailView extends ViewPart {

	private static final CCAPLogger LOGGER = CCAPLogger
			.getLogger(CheckinFailureDetailView.class);

	private TableComparator comparator;
	private static List<CheckinFailureVO> checkinFailureList = new ArrayList<CheckinFailureVO>();
	private TableViewer viewer;
	private ViewLabelProvider labelProvider;
	private int pCount = 0;
	private Table violationTable;

	/**
	 * Method to get the list of CheckInFailureVO
	 *
	 * @return the checkinFailureList
	 */
	public static List<CheckinFailureVO> getCheckinFailureList() {
		return checkinFailureList;
	}

	/**
	 * Method to set the list of CheckInFailureVO
	 *
	 * @param checkinFailureList
	 *            the checkinFailureList to set
	 */
	public static void setCheckinFailureList(
			List<CheckinFailureVO> checkinFailureList) {
		CheckinFailureDetailView.checkinFailureList = checkinFailureList;
	}

	/**
	 * Method to set the list of CheckInFailureDetails
	 *
	 * @param totalViolations
	 */
	public void setCheckinFailureDetails(List<CheckinFailureVO> totalViolations) {
		CheckinFailureDetailView.setCheckinFailureList(totalViolations);
	}

	/**
	 * Method to get count
	 *
	 * @return the pCount
	 */
	public int getpCount() {
		return pCount;
	}

	/**
	 * Method to set count
	 *
	 * @param the
	 *            pCount
	 */
	public void setpCount(int pCount) {
		this.pCount = pCount;
	}

	/**
	 * Method to call Viewpart
	 */
	public CheckinFailureDetailView() {
		super();
		pCount = 0;
	}

	/**
	 * Method to get Viewer
	 */
	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Method to set Viewer
	 *
	 * @param viewer
	 */
	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Method to create Part Control
	 *
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, 0x10302);
		violationTable = viewer.getTable();
		violationTable.setLinesVisible(true);
		violationTable.setHeaderVisible(true);
		ViewContentProvider contentProvider = new ViewContentProvider();
		labelProvider = new ViewLabelProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		drawTableColumns(violationTable, getCheckinFailureList());
		viewer.setInput(checkinFailureList);

		viewer.getTable().addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				CheckinFailureVO vo = null;
				ISelection selection = viewer.getSelection();

				TableItem[] seItems = null;

				if (selection.isEmpty()) {
					seItems = violationTable.getSelection();
					for (TableItem s : seItems) {

					}
				}else if (!selection.isEmpty()) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					vo = (CheckinFailureVO) obj;

					IFile file = ResourcesPlugin
							.getWorkspace()
							.getRoot()
							.getFileForLocation(
									new Path(vo.getFullPath()));

					IWorkbenchPage page = getSite().getPage();
					IEditorPart editor = getSite().getPage().getActiveEditor();

					try {
						editor = IDE.openEditor(page, file, true);
					}catch(Exception e){
						LOGGER.error("Error while opening the file in editor", e);
					}

				}
			}
		});
	}

	/**
	 * Method to draw table columns
	 *
	 * @param violationTable
	 *            , checkinFailureList
	 * @param vTypeList
	 */
	private void drawTableColumns(final Table violationTable,
			final List<CheckinFailureVO> checkinFailureList) {
		comparator = new TableComparator();
		comparator.setColumn(TableComparator.RESOURCE);
		comparator.setDirection(TableComparator.ASCENDING);
		TableColumn[] columns = new TableColumn[8];
		columns[Constants.ZERO] = new TableColumn(violationTable, SWT.NONE);
		columns[Constants.ZERO].setResizable(true);
		columns[Constants.ZERO].setText("S.No");
		columns[Constants.ZERO].setWidth(35);
		columns[Constants.ONE] = new TableColumn(violationTable, SWT.NONE);
		columns[Constants.ONE].setResizable(true);
		columns[Constants.ONE].setText("Resource");
		columns[Constants.ONE].setWidth(250);
		columns[1].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				comparator.setColumn(TableComparator.RESOURCE);
				comparator.reverseDirection();
				violationTable.setRedraw(false);
				violationTable.removeAll();
				Collections.sort(checkinFailureList, comparator);
				int index1 = 1;
				for (Iterator<CheckinFailureVO> itr = checkinFailureList
						.iterator(); itr.hasNext();) {
					CheckinFailureVO checkinFailureVO = itr.next();
					TableItem item = new TableItem(violationTable, SWT.NONE);
					item.setText(Constants.ZERO, String.valueOf(index1++));
					item.setText(Constants.ONE, checkinFailureVO.getResource());
					item.setText(Constants.TWO,
							checkinFailureVO.getRuleDomain());
					item.setText(Constants.THREE,
							checkinFailureVO.getRuleName());
					item.setText(Constants.FOUR, checkinFailureVO
							.getRuleValue().toString());
					item.setText(Constants.FIVE, checkinFailureVO
							.getExpectedMinValue().toString());
					item.setText(Constants.SIX, checkinFailureVO
							.getExpectedMaxvalue().toString());
					// item.setText(Constants.SEVEN,
					// checkinFailureVO.getLineNo());
				}
				violationTable.setRedraw(true);
			}
		});
		columns[Constants.TWO] = new TableColumn(violationTable, SWT.NONE);
		columns[Constants.TWO].setResizable(true);
		columns[Constants.TWO].setText("Rule Domain");
		columns[Constants.TWO].setWidth(Constants.HUNDERED);
		columns[Constants.TWO].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				comparator.setColumn(TableComparator.DOMAIN);
				comparator.reverseDirection();
				violationTable.setRedraw(false);
				violationTable.removeAll();
				Collections.sort(checkinFailureList, comparator);
				int index3 = 1;
				for (Iterator<CheckinFailureVO> itr = checkinFailureList
						.iterator(); itr.hasNext();) {
					CheckinFailureVO checkinFailureVO = itr.next();
					TableItem item = new TableItem(violationTable, SWT.NONE);
					item.setText(0, String.valueOf(index3++));
					item.setText(Constants.ONE, checkinFailureVO.getResource());
					item.setText(Constants.TWO,
							checkinFailureVO.getRuleDomain());
					item.setText(Constants.THREE,
							checkinFailureVO.getRuleName());
					item.setText(Constants.FOUR, checkinFailureVO
							.getRuleValue().toString());
					item.setText(Constants.FIVE, checkinFailureVO
							.getExpectedMaxvalue().toString());
					item.setText(Constants.SIX, checkinFailureVO
							.getOperator().toString());
					// item.setText(Constants.SEVEN,
					// checkinFailureVO.getLineNo());
				}
				violationTable.setRedraw(true);
			}
		});

		columns[Constants.THREE] = new TableColumn(violationTable, SWT.NONE);
		columns[Constants.THREE].setResizable(true);
		columns[Constants.THREE].setText("Rule");
		columns[Constants.THREE].setWidth(Constants.HUNDERED);
		columns[Constants.THREE].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				comparator.setColumn(TableComparator.RULE);
				comparator.reverseDirection();
				violationTable.setRedraw(false);
				violationTable.removeAll();
				Collections.sort(checkinFailureList, comparator);
				int index4 = 1;
				for (Iterator<CheckinFailureVO> itr = checkinFailureList
						.iterator(); itr.hasNext();) {
					CheckinFailureVO checkinFailureVO = itr.next();
					TableItem item = new TableItem(violationTable, SWT.NONE);
					item.setText(Constants.ZERO, String.valueOf(index4++));
					item.setText(Constants.ONE, checkinFailureVO.getResource());
					item.setText(Constants.TWO,
							checkinFailureVO.getRuleDomain());
					item.setText(Constants.THREE,
							checkinFailureVO.getRuleName());
					item.setText(Constants.FOUR, checkinFailureVO
							.getRuleValue().toString());
					item.setText(Constants.FIVE, checkinFailureVO
							.getExpectedMaxvalue().toString());
					item.setText(Constants.SIX, checkinFailureVO
							.getOperator().toString());
					// item.setText(Constants.SEVEN,
					// checkinFailureVO.getLineNo());
				}
				violationTable.setRedraw(true);
			}
		});

		columns[Constants.FOUR] = new TableColumn(violationTable, SWT.CENTER);
		columns[Constants.FOUR].setResizable(true);
		columns[Constants.FOUR].setText("Metrics Value");
		columns[Constants.FOUR].setWidth(Constants.EIGHTY);
		columns[Constants.FIVE] = new TableColumn(violationTable, SWT.CENTER);
		columns[Constants.FIVE].setResizable(true);
		columns[Constants.FIVE].setText("Check-in Limit");
		columns[Constants.FIVE].setWidth(Constants.EIGHTY);
		columns[Constants.SIX] = new TableColumn(violationTable, SWT.CENTER);
		columns[Constants.SIX].setResizable(true);
		columns[Constants.SIX].setText("Operator");
		columns[Constants.SIX].setWidth(Constants.EIGHTY);

		// columns[Constants.SEVEN] = new TableColumn(violationTable,
		// SWT.CENTER);
		// columns[Constants.SEVEN].setResizable(true);
		// columns[Constants.SEVEN].setText("Line No");
		// columns[Constants.SEVEN].setWidth(Constants.EIGHTY);
	}

	class ViewContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			List<CheckinFailureVO> checkinFailureVO = (List<CheckinFailureVO>) parent;
			return checkinFailureVO.toArray();
		}

		public void dispose() {
		}

		public void show() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			String result = null;
			try {
				if (obj instanceof CheckinFailureVO) {
					CheckinFailureVO checkinFailureVO = (CheckinFailureVO) obj;
					if (index == 0) {
						pCount++;
						result = String.valueOf(pCount);
					} else if (index == 1) {
						result = checkinFailureVO.getResource();
					} else if (index == Constants.TWO) {
						result = checkinFailureVO.getRuleDomain();
					} else if (index == Constants.THREE) {
						result = checkinFailureVO.getRuleName();
					} else if (index == Constants.FOUR) {
						result = ((checkinFailureVO.getRuleValue() != null) ? checkinFailureVO
								.getRuleValue().toString() : "");
					} else if (index == 5) {
						result = ((checkinFailureVO.getExpectedMinValue() != null) ? checkinFailureVO
								.getExpectedMaxvalue().toString() : "");
					} else if (index == Constants.SIX) {
						result = ((checkinFailureVO.getOperator() != null) ? checkinFailureVO
								.getOperator().toString() : "");
					}
					// else if (index == Constants.SEVEN) {
					// result = ((checkinFailureVO.getLineNo() != null) ?
					// checkinFailureVO
					// .getLineNo().toString() : "");
					// }
				} else {
					result = obj.getClass().getName();
				}
			} catch (Exception exc) {
				LOGGER.error("CheckinFailureDetailView: getColumnText()", exc);
			}
			return result;
		}

		/**
		 * Method to get the column image
		 *
		 * @param obj
		 *            , index
		 * @return image
		 */
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
	}

	@Override
	public void setFocus() {
	}

	/**
	 * Method to set Icon image
	 *
	 * @param image
	 * @return image
	 */
	public Image setIconImage(java.awt.Image image) {
		return null;
	}

	class TableComparator implements Comparator<Object> {
		public static final int RESOURCE = 1;
		public static final int DOMAIN = 2;
		public static final int RULE = Constants.THREE;
		public static final int ASCENDING = 0;
		public static final int DESCENDING = 1;
		private int column;
		private int direction;

		public int compare(Object obj1, Object obj2) {
			int rc = 0;
			CheckinFailureVO p1 = (CheckinFailureVO) obj1;
			CheckinFailureVO p2 = (CheckinFailureVO) obj2;
			switch (column) {
			case RESOURCE:
				rc = p1.getResource().compareTo(p2.getResource());
				break;
			case DOMAIN:
				rc = p1.getRuleDomain().compareTo(p2.getRuleDomain());
				break;
			case RULE:
				rc = p1.getRuleName().compareTo(p2.getRuleName());
				break;
			}
			if (direction == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}

		/**
		 * Method to set column
		 *
		 * @param column
		 */
		public void setColumn(int column) {
			this.column = column;
		}

		/**
		 * Method to set direction
		 *
		 * @param direction
		 */
		public void setDirection(int direction) {
			this.direction = direction;
		}

		/**
		 * Method to reverse direction
		 *
		 */
		public void reverseDirection() {
			direction = 1 - direction;
		}
	}
}