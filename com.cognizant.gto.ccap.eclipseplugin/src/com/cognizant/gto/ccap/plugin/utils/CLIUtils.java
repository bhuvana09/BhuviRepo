package com.cognizant.gto.ccap.plugin.utils;

import static com.ccap.deltaanalysis.internal.impl.constants.Constants.SONAR_HOST_URL_KEY;
import static com.ccap.deltaanalysis.internal.impl.constants.Constants.CCAP_URL_KEY;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.ccap.deltaanalysis.internal.impl.constants.Constants;
import com.cognizant.gto.ccap.eclipseplugin.Activator;
import org.sonar.runner.api.EmbeddedRunner;
//import com.cognizant.gto.ccap.plugin.utils.Constants;
public class CLIUtils {

	private static final CCAPLogger LOG = CCAPLogger.getLogger(CLIUtils.class);	
	private static File tempLocation = new File(
			System.getProperty("java.io.tmpdir"));
	private static File tempCcapLocation = Constants.ANALYSIS_TEMP_FOLDER;	
	//java_home,project_home,properties
	public static void main(String... args) throws IOException{
		File sonar_runner = null;
		//TODO copy the installation to temp
		File pluginLocation = null;
		try {
			pluginLocation = PluginUtils.getPluginLocation();
			final String ccap_runner = "CCAPRunner";
			File ccapRunner = new File(pluginLocation, ccap_runner);
			File tempFolder = Constants.ANALYSIS_TEMP_FOLDER;
			FileUtils.copyDirectory(ccapRunner, tempFolder);
			sonar_runner = new File(tempFolder, ccap_runner+File.separator+"bin"+File.separator+"ccap-runner.bat");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//END
		// String java_home="D:\\Users\\221013\\Software\\jdk\\jdk1.6.0_45";
		//TODO collect java_home & project_home
		String java_home="C:\\Program Files\\Java\\jdk1.8.0_11";
		String projectHome = "D:\\Users\\221013\\workspaces\\ccap\\RAD_ISSUE\\CCAPWeb";

		//TODO generate properties from the sonar-project.properties
		//TODO handle preference settings
		Properties prop = runnerProperties(projectHome+"\\sonar-project.properties");

		prop.setProperty("JAVA_HOME", java_home);

		executeProcess(projectHome, sonar_runner.getAbsolutePath(), prop);

	}

	/**
	 * CLI invocation for sonar runner
	 * @param projectHome
	 * @param prop
	 * @throws IOException
	 */
	public static void execute(String projectHome, Properties prop) throws IOException{
		File sonar_runner = null;
		//TODO copy the sonar-installation to temp
		File pluginLocation = null;
		try {
			pluginLocation = PluginUtils.getPluginLocation();
			final String ccap_runner = "CCAPRunner";
			File ccapRunner = new File(pluginLocation, ccap_runner);
			File tempFolder = Constants.ANALYSIS_TEMP_FOLDER;
			com.cognizant.gto.ccap.plugin.utils.Constants cont= new com.cognizant.gto.ccap.plugin.utils.Constants();
		   FileUtils.copyDirectory(ccapRunner, tempFolder);	   
		 File tempSonarRunnerPropFile = new File(tempFolder, "conf" + File.separator
	            + "sonar-runner.properties");
	   
	   FileInputStream inStream = null;
		 try {
	       inStream = new FileInputStream(tempSonarRunnerPropFile);
                 prop.load(inStream);
 } finally {
                 try {
                                 inStream.close();
                 } catch (Exception exp) {
                 }
 }
 OutputStream outPropFile = null;
 try {
                 outPropFile = new FileOutputStream(tempSonarRunnerPropFile);
                 prop.remove("sonar.host.url");
                 String sonarHostUrl = Activator.getDefault()
                                                 .getPreferenceStore().getString(cont.PROP_CCAP_URL);
                
                 prop.setProperty("sonar.host.url", sonarHostUrl);
                 String ccapUrl = Activator.getDefault()
                                                 .getPreferenceStore().getString(cont.PROP_CCAPWEB_URL);
                
                 prop.setProperty("ccap.url", ccapUrl);               
                 prop.store(outPropFile, "Sonar runner properties");
 } finally {
                 try {
                                 outPropFile.close();
                 } catch (Exception exp) {
                 }
 }	   
		sonar_runner = new File(tempFolder, "bin"+File.separator+"ccap-runner.bat");

		} catch (IOException e) {
			LOG.error(e);
			throw e;
		}
		//END
		executeProcess(projectHome, sonar_runner.getAbsolutePath(), prop);
	}


	private static void executeProcess(String launcherHome, String sonar_runner, Properties prop) throws IOException{
		BufferedReader reader = null;
		try {
			ProcessBuilder pb = new ProcessBuilder();

			pb.command("cmd", "/c", sonar_runner+" -X");

			pb.directory(new File(launcherHome));
			pb = pb.redirectErrorStream(true);
			Map<String, String> vari= pb.environment();
			vari.putAll(convert(prop));

			Process process = pb.start();
			reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line = reader.readLine();

			while (line != null) {
				line = reader.readLine();
				LOG.info(line);
			}
			if (process.exitValue() != 0) {
				LOG.error("Process terminated with an error.");
				throw new IllegalStateException("please check ccap.log file");
			}
		} catch (IOException e) {
			LOG.error(e);
			throw e;
			} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				reader = null;
			}
		}
	}

	private static Properties runnerProperties(String file){
//		String file = "sonar-runner.properties";
		Properties prop = new Properties();

		try {
			InputStream is = new FileInputStream(file);
			prop.load(is);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	//	prop.lo
		return prop;
	}

	private static Map<String,String> convert(Properties prop){
		Map<String,String> bucket = new HashMap<String, String>((Map) prop);
		return bucket;
	}

}
