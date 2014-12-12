package cb.tailorswift.behvior;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import javax.swing.ProgressMonitor;

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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import tailorswift.Activator;

public class WebScaldingProjectSupport {
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
			openError(e, "End of the world is near");
			project = null;
		}

		return project;
	}

	private  void addToProjectStructure(String projectFolder) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  

		//get location of workspace (java.io.File)  
		IPath workspaceDirectory = workspace.getRoot().getLocation();
		IPath path = workspaceDirectory.append(projectFolder);
		try {
			InputStream is= WebScaldingProjectSupport.class.getClassLoader().getResourceAsStream("jobtemplate.zip");
			new UnZip().unZipIt(is, path.toFile().getAbsolutePath());
			runWithProgressMonitor(path.toFile().getAbsolutePath(), projectFolder,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			openError(e, "Unzip error");
			e.printStackTrace();
		}



	}

	public void  runWithProgressMonitor( final String absolutePath,final String projectName, IProgressMonitor monitor) {
		Job job = new Job("Build Scala project") {
			protected IStatus run(IProgressMonitor monitor) { 
				monitor.beginTask("Job started...", 2); 
				try {
					buildProject(absolutePath);
					monitor.worked(1);
					refreshProject(projectName, monitor);
					monitor.worked(1);

				} catch ( IOException | InterruptedException | CoreException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}
				monitor.done(); 
				return Status.OK_STATUS; 
			} 
		}; 
		job.setUser(true);
		job.schedule();

	}

	private  void buildProject(String absolutePath) throws IOException, InterruptedException, PartInitException {


		Runtime r = Runtime.getRuntime();
		Process p = r.exec(new String[]{"sbt", "clean", "eclipse"}, new String[]{}, new File(absolutePath));
		//	p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader b1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line = "";
		MessageConsole myConsole = findConsole("WebScalding");
		showConsole(myConsole);
		MessageConsoleStream out = myConsole.newMessageStream();
		while ((line = b.readLine()) != null) {
			out.println(line);
		}
		while ((line = b1.readLine()) != null) {
			out.println(line);
		}

		b.close();
		b1.close();

	}
	private void refreshProject(final String projectName, IProgressMonitor monitor) throws CoreException {

					ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).refreshLocal(IResource.DEPTH_INFINITE, monitor);
	
	}

	private void showConsole(final MessageConsole myConsole) throws PartInitException {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();

				String id = IConsoleConstants.ID_CONSOLE_VIEW;
				IConsoleView view;
				try {
					view = (IConsoleView) page.showView(id);
					view.display(myConsole);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});


	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);

		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
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





	public  void openError(Exception ex, final String title) {
		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));

		final String message = ex.getMessage();
		final String formattedMessage = Activator.PLUGIN_ID + " : " + message; //$NON-NLS-1$
		final Status status = new Status(IStatus.ERROR,Activator. PLUGIN_ID, formattedMessage, new Throwable(writer.toString()));

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(Display.getDefault().getActiveShell(),
						title, message, status);
			}
		});
	}

}