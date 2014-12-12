package cb.tailorswift.behvior;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;

import tailorswift.Activator;

public class WebScaldingProjectSupport {
	
	private ExcecuteCommand command = new ExcecuteCommand();

	/**
	 * For this marvelous project we need to:
	 * - create the default Eclipse project
	 * - add the custom project nature
	 * - create the folder structure
	 *
	 * @param projectName
	 * @param location
	 * @param natureId
	 * @return
	 */
	public  IProject createProject(String projectName, URI location) {
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		IProject project = createBaseProject(projectName, location);
		try {
			//   addNature(project);

			addToProjectStructure(projectName);
		} catch (CoreException e) {

			e.printStackTrace();
			command.openError(e, "End of the world is near");
			project = null;
		}

		return project;
	}

	private  void addToProjectStructure(String projectFolder) throws CoreException {
		String absolutePath = getProjectAbsolutePath(projectFolder);
		try {
			runWithProgressMonitor(absolutePath, projectFolder,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			command.openError(e, "Unzip error");
			e.printStackTrace();
		}



	}

	public String getProjectAbsolutePath(String projectFolder) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  

		//get location of workspace (java.io.File)  
		IPath workspaceDirectory = workspace.getRoot().getLocation();
		IPath path = workspaceDirectory.append(projectFolder);
		String absolutePath =path.toFile().getAbsolutePath();
		return absolutePath;
	}

	public void  runWithProgressMonitor( final String absolutePath,final String projectName, IProgressMonitor monitor) {
		Job job = new Job("Generate a template Webscalding  project") {
			protected IStatus run(IProgressMonitor monitor) { 
				monitor.beginTask("Creating a webscalding project..", 3); 
				try {
					unzipProject(absolutePath);
					monitor.worked(1);
					command.executeCommand(new String[]{"/Users/fjacob/sbt/bin/sbt",  "-Dsbt.log.noformat=true", "clean", "eclipse"},absolutePath);
					monitor.worked(1);
					refreshProject(projectName, monitor);
					monitor.worked(1);

				} catch ( IOException | InterruptedException | CoreException e) {
					// TODO Auto-generated catch block
					command.openError(e, "Error thrown");
					e.printStackTrace();
				}
				monitor.done(); 
				return Status.OK_STATUS; 
			}

			private void unzipProject(String absolutePath) throws FileNotFoundException, IOException {
				
			//	Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
				URL fileURL =  new URL("platform:/plugin/"+ Activator.PLUGIN_ID+"/resources/jobtemplate.zip");
			//	bundle.getResource("jobtemplate.zip");
				InputStream is=fileURL.openConnection().getInputStream();
				new UnZip().unZipIt(is,absolutePath);
				
			} 
		}; 
		job.setUser(true);
		job.schedule();

	}

	
	private void refreshProject(final String projectName, IProgressMonitor monitor) throws CoreException {

					ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).refreshLocal(IResource.DEPTH_INFINITE, monitor);
	
	}


	/**
	 * Just do the basics: create a basic project.
	 *
	 * @param location
	 * @param projectName
	 */
	private  IProject createBaseProject(String projectName, URI location) {
		// it is acceptable to use the ResourcesPlugin class

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject newProject =workspace.getRoot().getProject(projectName);

		if (!newProject.exists()) {
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);
			try {
				newProject.create(desc, null);

				if (!newProject.isOpen()) {
					newProject.open(null);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return newProject;
	}





	
}