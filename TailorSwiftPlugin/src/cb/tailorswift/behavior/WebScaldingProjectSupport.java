package cb.tailorswift.behavior;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.osgi.framework.Bundle;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import tailorswift.Activator;

public class WebScaldingProjectSupport {

	private ExecuteCommand command = new ExecuteCommand();

	/**
	 * For this marvelous project we need to: - create the default Eclipse
	 * project - add the custom project nature - create the folder structure
	 * 
	 * @param projectName
	 * @param location
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public void createProject(String projectName, URI location)
			throws CoreException {
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		createBaseProject(projectName, location);
		String absolutePath = getProjectAbsolutePath(projectName);
		runWithProgressMonitor(absolutePath, projectName, null);

	}

	public String getProjectAbsolutePath(String projectFolder) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// get location of workspace (java.io.File)
		IPath workspaceDirectory = workspace.getRoot().getLocation();
		IPath path = workspaceDirectory.append(projectFolder);
		String absolutePath = path.toFile().getAbsolutePath();
		return absolutePath;
	}

	public void runWithProgressMonitor(final String absolutePath,
			final String projectName, IProgressMonitor monitor) {
		Job job = new Job("Generate a template Webscalding  project") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Creating a webscalding project..", 100);
				try {
					unzipProject(absolutePath);
					monitor.worked(20);
					applyTemplates(projectName);
					monitor.worked(20);
					command.executeCommand(
							new String[] { Activator.getSBTPath(),
									"-Dsbt.log.noformat=true", "clean",
									"eclipse" }, absolutePath);
					monitor.worked(40);
					refreshProject(projectName, monitor);
					monitor.worked(20);

				} catch (IOException | InterruptedException | CoreException e) {
					// TODO Auto-generated catch block
					command.logError(e, "Project creation failed");
					return Status.CANCEL_STATUS;

				}
				monitor.done();
				return Status.OK_STATUS;
			}

			private void unzipProject(String absolutePath)
					throws FileNotFoundException, IOException {

				// Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
				URL fileURL = new URL("platform:/plugin/" + Activator.PLUGIN_ID
						+ "/resources/jobtemplate.zip");
				// bundle.getResource("jobtemplate.zip");
				InputStream is = fileURL.openConnection().getInputStream();
				new UnZip().unZipIt(is, absolutePath);

			}
		};
		  job.addJobChangeListener(new JobChangeAdapter() {
		        public void done(IJobChangeEvent event) {
		        if (event.getResult().isOK())
		          command.openInfo("Job completed successfully", "WebScalding Project Creation", IStatus.INFO);
		           else
		        	   command.openInfo("Job failed, check error log for details", "WebScalding Project Creation", IStatus.ERROR);
		        }
		     });
		  job.setUser(true);
		job.schedule();


	}

	private void refreshProject(final String projectName,
			IProgressMonitor monitor) throws CoreException {

		ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
				.refreshLocal(IResource.DEPTH_INFINITE, monitor);

	}

	private void applyTemplates(String projectName) throws IOException {
		overwriteFile("build.stg", "build.sbt", projectName);
		overwriteFile("runOnHadoop.stg", "scripts" + File.separator
				+ "runOnHadoop.sh", projectName);
	}

	private void overwriteFile(String templateName, String newFile,
			String projectName) throws IOException {

		URL fileURL = new URL("platform:/plugin/" + Activator.PLUGIN_ID
				+ "/resources/" + templateName);
		STGroup group = new STGroupFile(fileURL, "UTF-8", '<', '>');
		ST st = group.getInstanceOf("template");
		// List<String> names = new ArrayList<String>();
		st.add("project", projectName);
		String fullPath = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName).getLocation().toFile()
				.getAbsolutePath()
				+ File.separator + newFile;
		st.write(new File(fullPath), null);

	}

	/**
	 * Just do the basics: create a basic project.
	 * 
	 * @param location
	 * @param projectName
	 * @throws CoreException
	 */
	private void createBaseProject(String projectName, URI location)
			throws CoreException {
		// it is acceptable to use the ResourcesPlugin class

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject newProject = workspace.getRoot().getProject(projectName);

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

}