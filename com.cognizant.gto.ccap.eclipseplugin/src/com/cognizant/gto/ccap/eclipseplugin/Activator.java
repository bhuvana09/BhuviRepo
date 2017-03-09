package com.cognizant.gto.ccap.eclipseplugin;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cognizant.gto.ccap.plugin.utils.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.cognizant.gto.ccap.eclipseplugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Initialize images to the registry.
	 *
	 * @param registry
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		addToImageRegistry(Constants.MARKER_ICON, "icons/cap-marker.gif",
				registry);
		addToImageRegistry(Constants.FOLDER_ICON, "icons/folder_large.gif",
				registry);
		addToImageRegistry(Constants.JAVA_FILE_ICON, "icons/java_file.gif",
				registry);
		addToImageRegistry(Constants.INFO_ICON, "icons/info.gif", registry);
		addToImageRegistry(Constants.WARN_ICON, "icons/warn.gif", registry);
		addToImageRegistry(Constants.CAP_LOGO, "icons/CAP_logo.jpg", registry);
		addToImageRegistry(Constants.PACKAGE_ICON, "icons/package_obj.gif",
				registry);
		addToImageRegistry(Constants.SRC_FOLDER_ICON,
				"icons/showpackageroots.gif", registry);
		addToImageRegistry(Constants.CLASS_ICON, "icons/class_obj.gif",
				registry);
		addToImageRegistry(Constants.METHOD_ICON, "icons/method_obj.gif",
				registry);
		addToImageRegistry(Constants.LINE_ICON, "icons/line_obj.gif", registry);
		addToImageRegistry(Constants.CAP_VIEW_ICON, "icons/ccap.jpg", registry);
		addToImageRegistry(Constants.IMG_BLOCKER, "icons/ccapblocker.gif",
				registry);
		addToImageRegistry(Constants.IMG_CRITICAL, "icons/ccapcritical.gif",
				registry);
		addToImageRegistry(Constants.IMG_MAJOR, "icons/ccapmajor.gif", registry);
		addToImageRegistry(Constants.IMG_MINOR, "icons/ccapminor.gif", registry);
		addToImageRegistry(Constants.IMG_INFO, "icons/ccapinfo.gif", registry);
		addToImageRegistry(Constants.IMG_FIXIT, "icons/yes.png", registry);
		addToImageRegistry(Constants.IMG_NOFIX, "icons/cross.png", registry);

		/**
		 * new violation icon
		 */
		addToImageRegistry(Constants.NEW_VIOLATION_ICON, "icons/new-bug-icon.png", registry);
	}

	/**
	 * Method to get the image
	 *
	 * @param key
	 */
	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}

	/**
	 * Add images to the registry
	 *
	 * @param key
	 * @param imagePath
	 * @param registry
	 */
	private void addToImageRegistry(String key, String imagePath,
			ImageRegistry registry) {
		IPath ipath = new Path(imagePath);
		Bundle capBundle = Platform.getBundle(PLUGIN_ID);
		URL url = Platform.find(capBundle, ipath);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		registry.put(key, desc);
	}

}
