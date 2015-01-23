package cb.tailorswift.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import tailorswift.Activator;
import cb.tailorswift.behavior.ExecuteCommand;
import cb.tailorswift.behavior.JobWithResult;
import cb.tailorswift.behavior.WebScaldingProjectSupport;
import cb.tailorswift.ssh.FileTransfer;
import cb.tailorswift.ssh.SSHCommand;

import com.jcraft.jsch.JSchException;

public class LaunchWebScaldingJob implements ILaunchConfigurationDelegate {

	public static final String SCRIPT_PATH = "SCRIPT_PATH";
	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String NEEDS_BUILD="NEEDS_BUILD";
	public static final String NEEDS_CLEAN="NEEDS_CLEAN";
	final ExecuteCommand command = new ExecuteCommand();
	WebScaldingProjectSupport support = new WebScaldingProjectSupport();

	public void runWithProgressMonitor(
			final ILaunchConfiguration configuration, IProgressMonitor monitor) {
		
	
		Job job = new JobWithResult("Submitting Scalding Job") {
			

			protected IStatus run(IProgressMonitor monitor) {

				IStatus status = null;
				try {
					monitor.beginTask("Submitting Scalding Job", 100);
					uploadScript(configuration, new SubProgressMonitor(monitor, 10));
					
					monitor.worked(10);
				   if( configuration.getAttribute(NEEDS_CLEAN, true))
						   sbtCleanProject(configuration);
						monitor.worked(10);
				   if( configuration.getAttribute(NEEDS_BUILD, true))
					   sbtAssemblyProject(configuration);
					monitor.worked(40);
				   if( configuration.getAttribute(NEEDS_BUILD, true))
					uploadAssembly(configuration,new SubProgressMonitor(monitor, 30));
					monitor.worked(30);
					submitJob(configuration);
					monitor.worked(10);
					status = Status.OK_STATUS;
					monitor.done();
		//			command.openInfo("Job submitted successfully", "WebScalding job submission");

				} catch (IOException | InterruptedException | CoreException
						| JSchException e) {
					// TODO Auto-generated catch block
					command.logError(e, "Job submission failed!");
					status = Status.CANCEL_STATUS;

				}

				return status;
			}

			private void sbtAssemblyProject(
					ILaunchConfiguration configuration) throws CoreException,
					IOException, InterruptedException {

				String projectName = configuration.getAttribute(PROJECT_NAME,
						"");

				command.executeCommand(new String[] { Activator.getSBTPath(),
						"-Dsbt.log.noformat=true",  "assembly" },
						Activator.getProjectAbsolutePath(projectName));

			}
			private void sbtCleanProject(
					ILaunchConfiguration configuration) throws CoreException,
					IOException, InterruptedException {

				String projectName = configuration.getAttribute(PROJECT_NAME,
						"");

				command.executeCommand(new String[] { Activator.getSBTPath(),
						"-Dsbt.log.noformat=true",  "clean" },
						Activator.getProjectAbsolutePath(projectName));

			}
			
			private void uploadResource(File file, IProgressMonitor monitor) throws CoreException,
					IOException, InterruptedException, JSchException {

		
				FileTransfer ft = new FileTransfer(Activator.getSSHUserName(),
						Activator.getSSHHostName(), Activator.getSSHPassword());
				ft.transferToServer(file, file.getName(), monitor);

			}

			private void uploadAssembly(ILaunchConfiguration configuration,  IProgressMonitor monitor)
					throws CoreException, IOException, InterruptedException,
					JSchException {
				String projectName = configuration.getAttribute(PROJECT_NAME,
						"");
				String path = Activator.getProjectAbsolutePath(projectName)
						+ File.separator + "target" + File.separator
						+ "scala-2.10" + File.separator + projectName
						+ "-assembly-0.0.1.jar";
				uploadResource(new File(path), monitor);

			}

			private void uploadScript(ILaunchConfiguration configuration, IProgressMonitor monitor)
					throws CoreException, IOException, InterruptedException,
					JSchException {
				String scriptFile = configuration.getAttribute(SCRIPT_PATH, "");
				String scriptPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(scriptFile))
						.getLocation().toFile().getAbsolutePath();
						
				uploadResource(new File(scriptPath),monitor);

			}

			private void submitJob(ILaunchConfiguration configuration)
					throws CoreException, IOException, InterruptedException,
					JSchException {
				String scriptPath = configuration.getAttribute(SCRIPT_PATH, "");
				String scriptFileName = scriptPath.substring(scriptPath
						.lastIndexOf('/') + 1);
		

				SSHCommand sshCommand = new SSHCommand(
						Activator.getSSHUserName(), Activator.getSSHHostName(),
						Activator.getSSHPassword());
				sshCommand.execute("nohup /bin/sh " + scriptFileName
						+ " `</dev/null` >nohup.out 2>&1 &");


			}

		};
		job.schedule();

	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		runWithProgressMonitor(configuration, monitor);

	}

}
