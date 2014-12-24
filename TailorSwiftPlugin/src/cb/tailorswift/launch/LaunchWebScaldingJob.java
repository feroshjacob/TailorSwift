package cb.tailorswift.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
	final ExecuteCommand command = new ExecuteCommand();
	WebScaldingProjectSupport support = new WebScaldingProjectSupport();

	public void runWithProgressMonitor(
			final ILaunchConfiguration configuration, IProgressMonitor monitor) {
		Job job = new JobWithResult("Running Webscalding  project") {
			

			protected IStatus run(IProgressMonitor monitor) {

				IStatus status = null;
				try {
					monitor.beginTask("Submitting Scalding Job", 100);
					createAssemblyProject(configuration);
					monitor.worked(50);
					uploadScript(configuration);
					monitor.worked(10);
					uploadAssembly(configuration);
					monitor.worked(30);
					submitJob(configuration);
					monitor.worked(10);
					status = Status.OK_STATUS;
					monitor.done();
		//			command.openInfo("Job submitted successfully", "WebScalding job submission");

				} catch (IOException | InterruptedException | CoreException
						| JSchException e) {
					// TODO Auto-generated catch block
		//			command.openError(e, "Job submission failed!");
					status = Status.CANCEL_STATUS;

				}

				return status;
			}

			private void createAssemblyProject(
					ILaunchConfiguration configuration) throws CoreException,
					IOException, InterruptedException {

				String projectName = configuration.getAttribute(PROJECT_NAME,
						"");

				command.executeCommand(new String[] { Activator.getSBTPath(),
						"-Dsbt.log.noformat=true", "clean", "assembly" },
						support.getProjectAbsolutePath(projectName));

			}

			private void uploadResource(File file) throws CoreException,
					IOException, InterruptedException, JSchException {

				// scp scripts/runOnCB.sh
				// fjacob.site@qtmhgate1.atl.careerbuilder.com:
				FileTransfer ft = new FileTransfer(Activator.getSSHUserName(),
						Activator.getSSHHostName(), Activator.getSSHPassword());
				ft.transferToServer(file, file.getName());

			}

			private void uploadAssembly(ILaunchConfiguration configuration)
					throws CoreException, IOException, InterruptedException,
					JSchException {
				String projectName = configuration.getAttribute(PROJECT_NAME,
						"");
				String path = support.getProjectAbsolutePath(projectName)
						+ File.separator + "target" + File.separator
						+ "scala-2.10" + File.separator + projectName
						+ "-assembly-0.0.1.jar";
				uploadResource(new File(path));

			}

			private void uploadScript(ILaunchConfiguration configuration)
					throws CoreException, IOException, InterruptedException,
					JSchException {
				String scriptFile = configuration.getAttribute(SCRIPT_PATH, "");
				String scriptPath = ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toFile().getAbsolutePath()
						+ scriptFile;
				uploadResource(new File(scriptPath));

			}

			private void submitJob(ILaunchConfiguration configuration)
					throws CoreException, IOException, InterruptedException,
					JSchException {
				String scriptPath = configuration.getAttribute(SCRIPT_PATH, "");
				String scriptFileName = scriptPath.substring(scriptPath
						.lastIndexOf('/') + 1);
				// ssh fjacob.site@qtmhgate1.atl.careerbuilder.com 'nohup
				// /bin/sh /home/fjacob.site/runOnCB.sh`</dev/null` >nohup.out
				// 2>&1 &'

				SSHCommand sshCommand = new SSHCommand(
						Activator.getSSHUserName(), Activator.getSSHHostName(),
						Activator.getSSHPassword());
				sshCommand.execute("nohup /bin/sh " + scriptFileName
						+ " `</dev/null` >nohup.out 2>&1 &");
				// command.executeCommand(new String[]{"/usr/bin/ssh",
				// "fjacob.site@qtmhgate1.atl.careerbuilder.com","nohup /bin/sh /home/fjacob.site/runOnCB.sh`</dev/null` >nohup.out 2>&1 &"},
				// support.getProjectAbsolutePath(projectName));

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
