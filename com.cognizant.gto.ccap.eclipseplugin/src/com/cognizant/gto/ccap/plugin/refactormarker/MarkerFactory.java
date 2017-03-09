/**
 * 
 */
package com.cognizant.gto.ccap.plugin.refactormarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognizant.gto.ccap.plugin.refactormarker.utility.EditorHandler;

public class MarkerFactory {
	// Annotation ID
	public static final String ANNOTATION = "com.cognizant.gto.ccap.plugin.refactormarker.annotationtype";

	// Annotation Specification ID
	public static final String ANNOTATION_SPECIFICATION = "com.cognizant.gto.ccap.plugin.refactormarker.annotationspecification";

	public static final int INT_MINUSONE = -1;

	public static final int INT_ZERO = 0;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MarkerFactory.class.getName());

	// Marker ID
	public static final String MARKER = "com.cognizant.gto.ccap.plugin.refactormarker";

	// MarkerDeleteFlag Attribute Name
	public static final String MARKER_DELETE_FLAG = "MarkerDeleteFlag";

	// QuickFix Processor ID
	public static final String QUICKFIX_PROCESSOR = "com.cognizant.gto.ccap.plugin.refactormarker.quickfixprocessor";

	// ResolutionExists Attribute Name
	public static final String RESOLUTION_EXISTS = "ResolutionExists";

	// SelectedItemText Attribute Name
	public static final String SELECTED_ITEM_TEXT = "SelectedItemText";

	public static final String STRING_NA = "NA";

	// ViolatedRuleInfo Attribute Name
	public static final String VIOLATED_RULE_INFO = "ViolatedRuleInfo";

	// ViolatedRuleName Attribute Name
	public static final String VIOLATED_RULE_NAME = "ViolatedRuleName";

	public static void addAnnotation(IMarker marker, Position position,
			ITextEditor editor) {
		// The DocumentProvider enables to get the document currently loaded in
		// the editor
		IDocumentProvider idp = editor.getDocumentProvider();

		// This is the document we want to connect to. This is taken from the
		// current editor input.
		IDocument document = idp.getDocument(editor.getEditorInput());

		// The IannotationModel enables to add/remove/change annotation to a
		// Document loaded in an Editor
		IAnnotationModel iamf = idp.getAnnotationModel(editor.getEditorInput());

		// Note: The annotation type id specify that you want to create one of
		// your annotations
		SimpleMarkerAnnotation ma = new SimpleMarkerAnnotation(ANNOTATION,
				marker);

		// Finally add the new annotation to the model
		iamf.connect(document);
		iamf.addAnnotation(ma, position);
		iamf.disconnect(document);
	}

	public static void cleanup(IMarker marker) {
		if (!marker.exists()) {
			LOGGER.debug("Invalid marker handle!");

			return;
		}

		// Remove the marker
		try {
			LOGGER.debug("Removing Marker [" + marker.getId() + " - "
					+ marker.getType() + " for file: "
					+ marker.getResource().getFullPath());
			marker.delete();
			// removeAnnotation(marker,
			// getActiveEditor());
		} catch (CoreException ce) {
			LOGGER.error("Removing marker failed for file: "
					+ marker.getResource().getFullPath() + " due to "
					+ ce.getClass().getSimpleName(), ce);
		}
	}

	public static void cleanupAll(IMarker[] markers) {
		for (IMarker marker : markers) {
			cleanup(marker);
		}
	}

	/*
	 * Creates a Marker
	 */
	public static IMarker createRefactorMarker(MarkerPayload payload) {
		if (null == payload || null == payload.getFilePath()) {
			LOGGER.debug("No payload data found or invalid file handle!");

			return null;
		}

		IPath filePath = new Path(payload.getFilePath());
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(filePath);
		IMarker aiMarker = null;

		try {
			// note: you use the id that is defined in your plugin.xml
			aiMarker = file.createMarker(MARKER);
		} catch (CoreException ce) {
			LOGGER.error("Creating marker failed for file: " + filePath
					+ " due to " + ce.getClass().getSimpleName(), ce);

			return null;
		}

		try {
			int lineNumber = payload.getStartLine();
			String erringElement = payload.getElementName();
			String ruleName = payload.getRuleName();
			String ruleInfo = payload.getRuleInfo();

			// setting transient attribute for parent type is persistent
			aiMarker.setAttribute(IMarker.TRANSIENT, true);
			aiMarker.setAttribute(IMarker.MESSAGE,
					(((new StringBuilder(ruleName))
							.append(" (violation/pattern) with message \""))
							.append(ruleInfo).append("\" found on element '")
							.append(erringElement).append("' at line number ")
							.append(lineNumber)).toString());
			aiMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			//aiMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			aiMarker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			aiMarker.setAttribute(IMarker.LOCATION, "line " + lineNumber);
			aiMarker.setAttribute(VIOLATED_RULE_NAME, ruleName.replace(" ", ""));
			aiMarker.setAttribute(VIOLATED_RULE_INFO, ruleInfo);
			aiMarker.setAttribute(RESOLUTION_EXISTS,
					payload.isResolutionFound());

			IDocument document = fetchDocumentHandleForFile(file);

			if (null == document) {
				LOGGER.debug("Could not create IDocument handle for file: "
						+ filePath);

				// remove marker since the document handle is missing
				cleanup(aiMarker);

				return null;
			}

			// compute and set char start and char end
			int lineOffset = INT_MINUSONE, linelength = INT_MINUSONE, slctnOffset = INT_MINUSONE, slctnlength = INT_MINUSONE;
			String lineText = STRING_NA;

			try {
				lineOffset = document.getLineOffset(lineNumber - 1);
				linelength = document.getLineLength(lineNumber - 1);
				lineText = document.get(lineOffset, linelength).trim();
			} catch (BadLocationException ble) {
				LOGGER.error("Marking failed for " + payload + " due to "
						+ ble.getClass().getSimpleName() + " with message: "
						+ ble.getMessage(), ble);

				// remove marker since the text region isn't located
				cleanup(aiMarker);

				return null;
			}

			if (MarkerFactory.STRING_NA.equals(lineText)
					|| MarkerFactory.INT_MINUSONE == linelength) {
				LOGGER.debug("Could not find line data for marking!");

				// remove marker since the line data is missing
				cleanup(aiMarker);

				return null;
			}

			aiMarker.setAttribute(SELECTED_ITEM_TEXT, lineText);

			// fetch the initial three characters for computing the
			// start offset
			String searchText = ((lineText.length() >= 4) ? lineText.substring(
					0, 3) : lineText);
			IRegion searchRegion = null;

			try {
				searchRegion = searchDocumentRegionForText(document,
						lineOffset, searchText);
			} catch (PatternSyntaxException pse) {
				LOGGER.error("Placing markers failed due to "
						+ pse.getClass().getSimpleName() + " caught in file: "
						+ filePath, pse);

				// remove marker since the text region isn't located
				cleanup(aiMarker);

				return null;
			}

			if (null == searchRegion) {
				LOGGER.debug("Could not find Search Region for text: "
						+ searchText);

				// remove marker since the search string is missing
				cleanup(aiMarker);

				return null;
			}

			slctnOffset = searchRegion.getOffset();
			slctnlength = lineText.length();

			aiMarker.setAttribute(IMarker.CHAR_START, slctnOffset);
			aiMarker.setAttribute(IMarker.CHAR_END, (slctnOffset + slctnlength));
			// addAnnotation(marker, (new Position(searchRegion.getLength(),
			// lineText.length())), getActiveEditor());
		} catch (CoreException ce) {
			LOGGER.error("Setting marker attribute failed for file: "
					+ filePath + " due to " + ce.getClass().getSimpleName(), ce);

			return null;
		}

		return aiMarker;
	}

	public static IDocument fetchDocumentHandleForFile(IFile file) {
		IDocument document = null;

		if (null == file || !file.exists()) {
			LOGGER.debug("Invalid File handle!");
		} else {
			ITextFileBufferManager bufferManager = FileBuffers
					.getTextFileBufferManager();

			if (null == bufferManager) {
				LOGGER.debug("Missing TextFileBufferManager!");
			} else {
				IPath filePath = file.getFullPath();

				try {
					bufferManager.connect(filePath, LocationKind.IFILE,
							new NullProgressMonitor());
				} catch (CoreException ce) {
					LOGGER.error(
							"Connecting TextFileBufferManager failed for file: "
									+ filePath + " due to "
									+ ce.getClass().getSimpleName(), ce);

					return document;
				}

				ITextFileBuffer textFileBuffer = bufferManager
						.getTextFileBuffer(file.getFullPath(),
								LocationKind.IFILE);

				if (null == textFileBuffer) {
					LOGGER.debug("Missing TextFileBuffer!");
				} else {
					document = textFileBuffer.getDocument();

					try {
						bufferManager.disconnect(filePath, LocationKind.IFILE,
								new NullProgressMonitor());
					} catch (CoreException ce) {
						LOGGER.error(
								"Disconnecting TextFileBufferManager failed for file: "
										+ filePath + " due to "
										+ ce.getClass().getSimpleName(), ce);
					}
				}
			}
		}

		return document;
	}

	/*
	 * Returns a list of markers that are linked to the resource or any sub
	 * resource of the resource
	 */
	public static List<IMarker> findAllMarkers(IResource resource) {
		try {
			return Arrays.asList(resource.findMarkers(MARKER, true,
					IResource.DEPTH_INFINITE));
		} catch (CoreException ce) {
			LOGGER.error("Finding markers failed for " + resource.getName()
					+ " due to " + ce.getClass().getSimpleName(), ce);

			return new ArrayList<IMarker>();
		}
	}

	/*
	 * returns a list of a resources markers
	 */
	public static List<IMarker> findMarkers(IFile file) {
		try {
			return Arrays.asList(file.findMarkers(MARKER, false,
					IResource.DEPTH_ZERO));
		} catch (CoreException ce) {
			LOGGER.error("Finding markers failed for " + file.getName()
					+ " due to " + ce.getClass().getSimpleName(), ce);
			return new ArrayList<IMarker>();
		}
	}

	public static ICompilationUnit getCompilationUnit(IMarker marker) {
		IResource resource = marker.getResource();

		if ((resource instanceof IFile) && (resource.isAccessible())) {
			return JavaCore.createCompilationUnitFrom((IFile) resource);
		}

		return null;
	}

	public static Shell getShell() {
		IWorkbenchWindow workbenchWindow = EditorHandler
				.getActiveWorkbenchWindow();

		return ((null != workbenchWindow) ? workbenchWindow.getShell() : null);
	}

	/*
	 * Confirms whether marker already exists at a line and optionally deletes
	 * it
	 */
	public static boolean markerExistsAlready(IFile file, int lineNumber,
			boolean removeOption) {
		List<IMarker> markers = findMarkers(file);

		if (!markers.isEmpty()) {
			for (IMarker marker : markers) {
				if (marker.exists()
						&& marker.getAttribute(IMarker.LINE_NUMBER,
								INT_MINUSONE) == lineNumber) {
					if (removeOption) {
						cleanup(marker);

						break;
					} else {
						return true;
					}
				}
			}
		}

		return false;
	}

	/*
	 * Removes All Markers
	 */
	public static void removeAllMarkers(IResource resource)
			throws CoreException {
		// delete markers of this type alone from all children
		if (resource instanceof IFile) {
			removeMarkerFromFile(((IFile) resource.getAdapter(IFile.class)),
					INT_ZERO, true);
		} else {
			cleanupAll(resource.findMarkers(MARKER, false,
					IResource.DEPTH_INFINITE));
		}
	}

	public static void removeAnnotation(IMarker marker, ITextEditor editor) {
		// The DocumentProvider enables to get the document currently loaded in
		// the editor
		IDocumentProvider idp = editor.getDocumentProvider();

		// This is the document we want to connect to. This is taken from the
		// current editor input.
		IDocument document = idp.getDocument(editor.getEditorInput());

		// The IannotationModel enables to add/remove/change annotation to a
		// Document loaded in an Editor
		IAnnotationModel iamf = idp.getAnnotationModel(editor.getEditorInput());

		// Note: The annotation type id specify that you want to create one of
		// your annotations
		SimpleMarkerAnnotation ma = new SimpleMarkerAnnotation(ANNOTATION,
				marker);

		// Finally remove the new annotation from the model
		iamf.connect(document);
		iamf.removeAnnotation(ma);
		iamf.disconnect(document);
	}

	/*
	 * Removes a Marker
	 */
	public static void removeMarkerFromFile(IFile file, int lineNumber,
			boolean removeAll) throws CoreException {
		if (null != file && file.exists()) {
			if (removeAll) {
				cleanupAll(file
						.findMarkers(MARKER, false, IResource.DEPTH_ZERO));
			} else {
				markerExistsAlready(file, lineNumber, true);
			}
		}
	}

	public static IRegion searchDocumentRegionForText(IDocument document,
			int textOffset, String findString) {
		if (null == document) {
			LOGGER.debug("Invalid IDocument handle!");

			return null;
		}

		try {
			return ((new FindReplaceDocumentAdapter(document)).find(textOffset,
					findString, true, true, false, false));
		} catch (BadLocationException ble) {
			LOGGER.error("Placing markers failed due to "
					+ ble.getClass().getSimpleName(), ble);

			return null;
		}
	}

	public static void showInformationMessageDialog(String dialogTitle,
			String dialogMessage) {
		(new MessageDialog(getShell(), dialogTitle, null, dialogMessage,
				MessageDialog.INFORMATION, new String[] { "OK" }, INT_ZERO))
				.open();
	}

	public MarkerFactory() {
		super();
	}

}
