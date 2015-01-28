package com.recipegrace.tailorswift.newproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PartInitException;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.recipegrace.tailorswift.common.JobWithResult;
import com.recipegrace.tailorswift.common.UnZip;

import tailorswift.Activator;

public abstract class WebScaldingProjectSupport {

	private String projectName;
	protected String getProjectName() {
		return projectName;
	}


	protected void setProjectName(String projectName) {
		this.projectName = projectName;
	}



	public WebScaldingProjectSupport(String projectName) {
		this.projectName=projectName;
	
	}
	
	
	public void createProject(URI location)
			throws CoreException {
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		createBaseProject( location);
		String absolutePath = Activator.getProjectAbsolutePath(projectName);
		runWithProgressMonitor(absolutePath, null);

	}

	protected abstract void createProject(final String absolutePath,
			 IProgressMonitor monitor)
			throws FileNotFoundException, IOException,
			InterruptedException, PartInitException, CoreException ;

	public void runWithProgressMonitor(final String absolutePath,
			 IProgressMonitor monitor) {
		Job job = new JobWithResult("Create Webscalding  project") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Creating a webscalding project..", 100);
				try {
					createProject(absolutePath,  monitor);

				} catch (IOException | InterruptedException | CoreException e) {
					// TODO Auto-generated catch block
					command.logError(e, "Project creation failed");
					return Status.CANCEL_STATUS;

				}
				monitor.done();
				return Status.OK_STATUS;
			}


		};
		job.schedule();


	}


	protected void unzipProject(String absolutePath)
			throws FileNotFoundException, IOException {

		// Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL fileURL = new URL("platform:/plugin/" + Activator.PLUGIN_ID
				+ getZipFile());
		// bundle.getResource("jobtemplate.zip");
		InputStream is = fileURL.openConnection().getInputStream();
		new UnZip().unZipIt(is, absolutePath);

	}


	protected abstract String getZipFile();
	
	
	protected void refreshProject(
			IProgressMonitor monitor) throws CoreException {

		ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
				.refreshLocal(IResource.DEPTH_INFINITE, monitor);

	}


	protected void overwriteFile(String templateName, String newFile,
			Map<String,String> properties) throws IOException {

		URL fileURL = new URL("platform:/plugin/" + Activator.PLUGIN_ID
				+ "/resources/" + templateName);
		STGroup group = new STGroupFile(fileURL, "UTF-8", getTemplateStartChar(), getTemplateEndChar());
		ST st = group.getInstanceOf("template");
		for(String key: properties.keySet()) {
			st.add(key, properties.get(key));	
		}
		
		String fullPath = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName).getLocation().toFile()
				.getAbsolutePath()
				+ File.separator + newFile;
		st.write(new File(fullPath), null);

	}


	protected char getTemplateEndChar() {
		return '>';
	}


	protected char getTemplateStartChar() {
		return '<';
	}

	/**
	 * Just do the basics: create a basic project.
	 * 
	 * @param location
	 * @param projectName
	 * @throws CoreException
	 */
	
	

	protected void createBaseProject(URI location)
			throws CoreException {
		// it is acceptable to use the ResourcesPlugin class

		IProject newProject = getProject();

		if (!newProject.exists()) {
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace()
					.newProjectDescription(newProject.getName());
			if (location != null
					&& ResourcesPlugin.getWorkspace().getRoot()
							.getLocationURI().equals(location)) {
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);

			newProject.create(desc, null);

			if (!newProject.isOpen()) {
				newProject.open(null);
			}

		}

		
	}


	protected IProject getProject() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject newProject = workspace.getRoot().getProject(projectName);
		return newProject;
	}

}