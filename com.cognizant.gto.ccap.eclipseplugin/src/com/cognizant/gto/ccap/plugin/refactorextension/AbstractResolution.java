package com.cognizant.gto.ccap.plugin.refactorextension;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IMarkerResolution2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognizant.gto.ccap.plugin.refactormarker.MarkerFactory;
import com.cognizant.gto.ccap.plugin.refactormarker.utility.EditorHandler;


public abstract class AbstractResolution implements IMarkerResolution2 {
	private CompilationUnit fCompilationUnit = null;
	private String fDescription = MarkerFactory.STRING_NA;
	private IDocument fDocument = null;
	private IDocumentListener fDocumentListener = null;
	private boolean fDocumentTagged;
	private AST fJdtAst = null;
	private String fLabel = MarkerFactory.STRING_NA;
	private int fLineNumber = MarkerFactory.INT_MINUSONE;
	private Logger fLogger = null;
	private IMarker fMarker = null;
	private boolean fProcessTextEdits = true;
	private Shell fShell = null;
	private ICompilationUnit fWorkingCopy = null;
	private String ruleName = null;

	/**
	 * @param pLabel
	 * @param pDescription
	 */
	public AbstractResolution(String pLabel, String pDescription) {
		fLabel = pLabel;
		fDescription = pDescription;
	}

	/**
	 * @param pCompilationUnit
	 * @return
	 */
	private CompilationUnit createAST(ICompilationUnit pCompilationUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setSource(pCompilationUnit);

		return (CompilationUnit) parser.createAST(null);
	}

	private void doHousekeepingJob() {
		if (fProcessTextEdits) {
			editAndSave();
		}

		if (fDocumentTagged && null != fDocumentListener) {
			fDocument.removeDocumentListener(fDocumentListener);
		}

		if (fMarker.getAttribute(MarkerFactory.MARKER_DELETE_FLAG, false)) {
			MarkerFactory.cleanup(fMarker);
		}
	}

	private void editAndSave() {
		try {
			(fCompilationUnit.rewrite(fDocument, fWorkingCopy.getJavaProject()
					.getOptions(true))).apply(fDocument);
		} catch (MalformedTreeException mte) {
			fLogger.warn("Caught " + mte.getClass().getSimpleName()
					+ " with Message " + mte.getMessage(), mte);
		} catch (BadLocationException ble) {
			fLogger.warn("Caught " + ble.getClass().getSimpleName()
					+ " with Message " + ble.getMessage(), ble);
		}

		EditorHandler.getActiveEditor().doSave(new NullProgressMonitor());
	}

	public abstract void executeResolution();

	/**
	 * @return the JDT AST (root/parent of source tree)
	 */
	protected AST getAST() {
		return fJdtAst;
	}

	/**
	 * @return the JDT AST Compilation Unit
	 */
	protected CompilationUnit getCompilationUnit() {
		return fCompilationUnit;
	}

	@Override
	public String getDescription() {
		if (null != fDescription && !fDescription.isEmpty()
				&& !MarkerFactory.STRING_NA.equalsIgnoreCase(fDescription)) {
			return fDescription;
		}

		return null;
	}

	/**
	 * @return the IDocument associated with the source file
	 */
	protected IDocument getDocument() {
		return fDocument;
	}

	@Override
	public String getLabel() {
		if (null != fLabel && !fLabel.isEmpty()
				&& !MarkerFactory.STRING_NA.equalsIgnoreCase(fLabel)) {
			return fLabel;
		}

		return null;
	}

	/**
	 * @return the logger initialised for current instance
	 */
	protected Logger getLogger() {
		return fLogger;
	}

	/**
	 * @return the line number the current marker is placed on
	 */
	protected int getMarkerLineNumber() {
		return fLineNumber;
	}

	/**
	 * @return Shell for Eclipse UI dialogs
	 */
	protected Shell getShell() {
		return fShell;
	}

	/**
	 * @return
	 */
	protected ICompilationUnit getWorkingCopy() {
		return fWorkingCopy;
	}

	/**
	 * @param loggerClass
	 * @return
	 */
	private Logger initLogger(Object loggerClass) {
		if (null != loggerClass && loggerClass instanceof AbstractResolution) {
			return LoggerFactory.getLogger(loggerClass.getClass()
					.getSimpleName());
		}

		return initLogger(this);
	}

	/**
	 * @param pLineNumber
	 * @return
	 */
	protected boolean isValidLine(int pLineNumber) {
		return (MarkerFactory.INT_MINUSONE != pLineNumber || MarkerFactory.INT_ZERO != pLineNumber);
	}

	private void prepare4Resolving() {
		fLogger = initLogger(this);
		fLogger.debug("inside prepare4Resolving");
		
		try {
			ruleName = (String) fMarker.getAttribute(MarkerFactory.VIOLATED_RULE_NAME);
			setRuleName(ruleName);

		} catch (CoreException e) {
			
			fLogger.error(
					"Error while getting Violation Rule Name: "
							+ e.getMessage(), e);
		}

		
		fLineNumber = fMarker.getAttribute(IMarker.LINE_NUMBER,
				MarkerFactory.INT_MINUSONE);

		ICompilationUnit compilationUnit = MarkerFactory
				.getCompilationUnit(fMarker);

		if (null == compilationUnit) {
			ITypeRoot typeRoot = EditorHandler.getJavaInput(EditorHandler
					.getActiveEditor());

			if (null != typeRoot && typeRoot instanceof ICompilationUnit) {
				compilationUnit = ((ICompilationUnit) typeRoot);
			}
		}

		if (null != compilationUnit) {
			try {
				fWorkingCopy = compilationUnit
						.getWorkingCopy(new NullProgressMonitor());
			} catch (JavaModelException jme) {
				fLogger.error(
						"Working Copy creation failed due to: "
								+ jme.getMessage(), jme);
			}

			if (null == fWorkingCopy) {
				fWorkingCopy = compilationUnit;
			}
		}

		fCompilationUnit = createAST(fWorkingCopy);
		fJdtAst = fCompilationUnit.getAST();
		fShell = MarkerFactory.getShell();
		fDocument = EditorHandler.getActiveDocument();

		if (null == fDocumentListener) {
			fDocumentListener = new IDocumentListener() {
				@Override
				public void documentAboutToBeChanged(DocumentEvent arg0) {
					// Do nothing, as of now
				}

				@Override
				public void documentChanged(DocumentEvent docEvent) {
					if (null != fDocument && null != docEvent.getDocument()
							&& docEvent.getDocument().equals(fDocument)) {
						setMarkerAttributeFlag(
								MarkerFactory.MARKER_DELETE_FLAG, true);
					}
				}
			};

			if (!fDocumentTagged) {
				fDocument.addDocumentListener(fDocumentListener);

				fDocumentTagged = true;
			}
		}

		// Needed to use Text Edits and AST ReWrite while saving
		fCompilationUnit.recordModifications();
	}

	@Override
	public void run(IMarker pMarker) {
		if (null != pMarker && pMarker.exists()) {
			fMarker = pMarker;
			prepare4Resolving();
			fLogger.debug("abstractresolution - inside run");

			executeResolution();
			fLogger.debug("abstractresolution - after executeresolution");

			doHousekeepingJob();
		} else {
			fLogger.error("Invalid Marker Handle encountered!!");
		}
	}

	/**
	 * @param flag
	 *            indicating whether to use built-in Document Saving framework
	 */
	protected void setEditAndSaveFlag(boolean flag) {
		fProcessTextEdits = flag;
	}

	/**
	 * @param loggerClass
	 *            that needs to be used for initialising the log appender
	 */
	protected void setLogger(Object loggerClass) {
		if (null == loggerClass) {
			setLogger(initLogger(this));
		}

		fLogger = initLogger(loggerClass);
	}

	/**
	 * @param attribute
	 * @param flag
	 */
	private void setMarkerAttributeFlag(String attribute, boolean flag) {
		try {
			if(fMarker.exists()) {
				fMarker.setAttribute(attribute, flag);
			}
			
		} catch (CoreException ce) {
			fLogger.debug(((((new StringBuilder("Caught ")).append(ce
					.getClass().getSimpleName())).append(" while setting ")
					.append(attribute)).append(" attribute on marker: ")
					.append(fMarker.getId())).toString(), ce);
		}
	}
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
