package com.recipegrace.tailorswift.newproject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;

import tailorswift.Activator;

import com.recipegrace.tailorswift.common.ApplyTemplate;
import com.recipegrace.tailorswift.common.ExecuteCommand;
import static com.recipegrace.tailorswift.common.ITemplateConstants.*;

public class SBTProjectSupport extends WebScaldingProjectSupport {

	public SBTProjectSupport(String projectName) {
		super(projectName);
	}
	protected void createProject(final String absolutePath,
			 IProgressMonitor monitor)
			throws FileNotFoundException, IOException,
			InterruptedException, PartInitException, CoreException {
		ExecuteCommand command = new ExecuteCommand();
		unzipProject(absolutePath);
		monitor.worked(20);
		applyTemplates();
		monitor.worked(20);
		command.executeCommand(
				new String[] { Activator.getSBTPath(),
						"-Dsbt.log.noformat=true", "clean",
						"eclipse" }, absolutePath);
		monitor.worked(40);
		refreshProject( monitor);
		monitor.worked(20);
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


}
