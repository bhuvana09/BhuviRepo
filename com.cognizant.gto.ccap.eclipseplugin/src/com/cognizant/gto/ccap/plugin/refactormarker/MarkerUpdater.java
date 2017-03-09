/**
 * 
 */
package com.cognizant.gto.ccap.plugin.refactormarker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkerUpdater implements IMarkerUpdater {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MarkerUpdater.class.getName());

	/*
	 * Returns the attributes for which this updater is responsible.If the
	 * result is null, the updater assumes responsibility for any attributes.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IMarkerUpdater#getAttribute()
	 */
	@Override
	public String[] getAttribute() {
		// TODO Refine the attribute responsibility details here
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IMarkerUpdater#getMarkerType()
	 */
	@Override
	public String getMarkerType() {
		// returns the marker type that we are interested in updating
		return MarkerFactory.MARKER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.IMarkerUpdater#updateMarker(org.eclipse.core
	 * .resources.IMarker, org.eclipse.jface.text.IDocument,
	 * org.eclipse.jface.text.Position)
	 */
	@Override
	public boolean updateMarker(IMarker marker, IDocument document,
			Position position) {
		try {
			if (null != marker && marker.exists()) {
				String markerData = marker.getAttribute(
						MarkerFactory.SELECTED_ITEM_TEXT,
						MarkerFactory.STRING_NA);

				if (null != document && null != position
						&& !MarkerFactory.STRING_NA.equals(markerData)) {
					try {
						int start = position.getOffset();
						int end = start
								+ ((position.getLength() <= markerData.length()) ? position
										.getLength() : markerData.length());

						marker.setAttribute(IMarker.LINE_NUMBER,
								(document.getLineOfOffset(start) + 1));
						marker.setAttribute(IMarker.LOCATION, "line "
								+ (document.getLineOfOffset(start) + 1));
						marker.setAttribute(IMarker.CHAR_START, start);
						marker.setAttribute(IMarker.CHAR_END, end);
					} catch (BadLocationException ble) {
						LOGGER.debug(
								"Updating marker failed for "
										+ marker.getType() + " due to "
										+ ble.getClass().getSimpleName(), ble);
					}
				}

				return true;
			}
		} catch (CoreException ce) {
			LOGGER.debug("Update failed for " + marker.getId() + " due to "
					+ ce.getClass().getSimpleName(), ce);
		}

		return false;
	}

}
