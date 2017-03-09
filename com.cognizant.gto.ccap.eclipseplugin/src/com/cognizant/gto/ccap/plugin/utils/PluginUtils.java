package com.cognizant.gto.ccap.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.cognizant.gto.ccap.eclipseplugin.Activator;

public class PluginUtils {

	public static File getPluginLocation() throws IOException {
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			URL fileURL = FileLocator.find(bundle, new Path("/"), null);
			URL url = FileLocator.toFileURL(fileURL);
			String resLocation = url.getPath();
			resLocation = StringUtils.removeStart(resLocation, "/");
			File pluginLocation = new File(resLocation);
		return pluginLocation;
	}
}
