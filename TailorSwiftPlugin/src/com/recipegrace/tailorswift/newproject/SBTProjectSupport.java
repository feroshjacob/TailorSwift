package com.recipegrace.tailorswift.newproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;

import com.recipegrace.tailorswift.common.ExecuteCommand;

import tailorswift.Activator;

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
		overwriteFile("build.stg", "build.sbt",getProperties());
		overwriteFile("runOnHadoop.stg", "scripts" + File.separator
				+ "runOnHadoop.sh", getProperties());
	}
	private Map<String, String> getProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("project", getProjectName());
		return properties;
	}


}
