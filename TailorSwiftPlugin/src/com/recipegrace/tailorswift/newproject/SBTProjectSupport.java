package com.recipegrace.tailorswift.newproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.PartInitException;

import tailorswift.Activator;

import com.recipegrace.tailorswift.common.ApplyTemplate;
import com.recipegrace.tailorswift.common.IOUtils;

import static com.recipegrace.tailorswift.common.ITemplateConstants.*;

public class SBTProjectSupport extends WebScaldingProjectSupport {

	public SBTProjectSupport(String projectName) {
		super(projectName);
	}
	protected void createProject(final String absolutePath,
			 IProgressMonitor monitor)
			throws FileNotFoundException, IOException,
			InterruptedException, PartInitException, CoreException {
	
		unzipProject(absolutePath);
		monitor.worked(30);
		applyTemplates();
		monitor.worked(30);
		new SBTExecutor(getProjectName()).execute("clean eclipse", monitor);
		monitor.worked(40);

	}
	protected String getZipFile() {
		return "/resources/jobtemplate.zip";
	}
	private void applyTemplates() throws IOException {
		
		ApplyTemplate templates = new ApplyTemplate('<','>', "template");
		templates.overwriteFile("build.stg", "build.sbt",getProperties(), getProjectName());

	}
	private Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(TEMPLATE_VARIABLE_PROJECT, getProjectName());
		return properties;
	}
	
	public static ILaunchConfiguration setSBTSpecificParms(
			ILaunchConfiguration configuration) throws CoreException, MalformedURLException {
		
		ILaunchConfigurationWorkingCopy wc= configuration.getWorkingCopy();

		// Sets the classpath
		File jarFile = new File(Activator.getSBTLaunchJar());
		URL sbtRuntimeUrl = null;
		if(jarFile != null){
			sbtRuntimeUrl = jarFile.toURI().toURL();
		}
		if(sbtRuntimeUrl == null){
			new IOUtils().openInfo( "Make sure sbt-launch.jar is set in preferences","Launch", IStatus.ERROR);
			return null;
		}

		IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(sbtRuntimeUrl.getPath()));

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, Arrays.asList(entry.getMemento()));
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);

	
		// Saves the created configuration
		ILaunchConfiguration launchConfig = wc.doSave();

		return launchConfig;
		
	}


}
