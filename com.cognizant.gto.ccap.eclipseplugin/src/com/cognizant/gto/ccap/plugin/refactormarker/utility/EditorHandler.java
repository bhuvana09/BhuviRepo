package com.cognizant.gto.ccap.plugin.refactormarker.utility;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EditorHandler.class.getName());

	private EditorHandler() {
		super();
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}

	public static ITypeRoot getJavaInput(IEditorPart part) {
		IEditorInput editorInput = part.getEditorInput();
		if (editorInput != null) {
			IJavaElement input = JavaUI.getEditorInputJavaElement(editorInput);
			if (input instanceof ITypeRoot) {
				return (ITypeRoot) input;
			}
		}
		return null;
	}

	public static void selectInEditor(ITextEditor editor, int offset, int length) {
		IEditorPart active = getActiveEditor();
		if (active != editor) {
			editor.getSite().getPage().activate(editor);
		}
		editor.selectAndReveal(offset, length);
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/*
	 * Returns the selection of the text editor
	 */
	public static TextSelection getTextSelection() {

		ISelection selection = getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (selection instanceof TextSelection) {
			return (TextSelection) selection;
		}

		return null;
	}

	public static IDocument getActiveDocument() {
		IEditorPart editorPart = getActiveEditor();

		if (null != editorPart) {
			ITextEditor textEditor = (ITextEditor) editorPart;

			if (null != textEditor) {
				IDocumentProvider documentProvider = textEditor
						.getDocumentProvider();
				IEditorInput editorInput = textEditor.getEditorInput();

				if (null != documentProvider && null != editorInput) {
					return documentProvider.getDocument(editorInput);
				}
			}
		}

		return null;
	}

	public static ITextSelection selectLineTextIfNotAlready(ITextSelection sel,
			JavaEditor editor) {
		try {
			if (!sel.getText().trim().isEmpty()) {
				return sel;
			}
			IDocument document = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());
			editor.selectAndReveal(sel.getOffset(),
					document.getLineLength(sel.getStartLine()));

			sel = (ITextSelection) editor.getSelectionProvider().getSelection();
			LOGGER.debug("new sel is " + sel);
			return sel;
		} catch (BadLocationException ble) {
			LOGGER.error("Text Selection Failed due to"+ ble.getClass().getSimpleName(), ble);
			return sel;
		}
	}

	public static IMethod getSingleSelectedMethod(ITextSelection selection,
			JavaEditor editor) throws JavaModelException, BadLocationException {
		// - when caret/selection on method name (call or declaration) -> that
		// method
		// - otherwise: caret position's enclosing method declaration
		// - when caret inside argument list of method declaration -> enclosing
		// method declaration
		// - when caret inside argument list of method call -> enclosing method
		// declaration (and NOT method call)

		// selection.get
		IJavaElement[] elements = SelectionConverter.codeResolve(editor);
		if (elements.length > 1)
			return null;

		if (elements.length == 1 && elements[0] instanceof IMethod) {
			return (IMethod) elements[0];
		} else {
			// selection' offset returns beginning of the line selection that
			// can't help detect method.
			// so calculate offset of something which is in the middle of the
			// method declaration
			LOGGER.debug("selection offset " + selection.getOffset());
			LOGGER.debug("selection text " + selection.getText());
			int offset = selection.getOffset()
					+ selection.getText().indexOf('(');
			IJavaElement elementAt = SelectionConverter
					.getInputAsCompilationUnit(editor).getElementAt(offset);
			if (!(elementAt instanceof IMethod)) {
				return null;
			}

			return (IMethod) elementAt;
		}
	}
}
