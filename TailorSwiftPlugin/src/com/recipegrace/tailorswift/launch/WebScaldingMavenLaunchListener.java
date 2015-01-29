package com.recipegrace.tailorswift.launch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;

import tailorswift.Activator;

import com.jcraft.jsch.JSchException;
import com.recipegrace.tailorswift.common.ApplyTemplate;
import com.recipegrace.tailorswift.common.JobWithResult;
import com.recipegrace.tailorswift.launch.ui.WebScaldingLaunchTab;
import com.recipegrace.tailorswift.ssh.FileTransfer;
import com.recipegrace.tailorswift.ssh.SSHCommand;

import static com.recipegrace.tailorswift.common.ITemplateConstants.*;

public class WebScaldingMavenLaunchListener implements ILaunchesListener2 {
	
	
	public static String SCRIPT_PATH = "generated_script.sh";
	private ILaunchConfiguration configuration;

	public WebScaldingMavenLaunchListener(ILaunchConfiguration configuration) {
		this.configuration= configuration;
	}
	
	@Override
	public void launchesRemoved(ILaunch[] launches) {
		// TODO Auto-generated method stub

	}

	@Override
	public void launchesAdded(ILaunch[] launches) {
		// TODO Auto-generated method stub

	}

	@Override
	public void launchesChanged(ILaunch[] launches) {
		// TODO Auto-generated method stub

	}

	@Override
	public void launchesTerminated(ILaunch[] launches) {
		// TODO Auto-generated method stub
		try {
			String programOptions = configuration.getAttribute(WebScaldingLaunchTab.WEBSCALDING_LAUNCH_PROGRAM_OPTIONS,"");
			String programArguments = configuration.getAttribute(WebScaldingLaunchTab.WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS,"");
			IFile pomFile = 	ResourcesPlugin.getWorkspace().getRoot().getProject("HolaWonder").getFile(IMavenConstants.POM_FILE_NAME);
			String version = MavenPlugin.getMavenModelManager().readMavenModel(pomFile).getVersion();
			Job job = new JobWithResult("Submitting Scalding Job") {
				

				protected IStatus run(IProgressMonitor monitor) {

					IStatus status = null;
					try {
						monitor.beginTask("Submitting Scalding Job", 100);
						uploadScript(configuration, new SubProgressMonitor(monitor, 10));
						monitor.worked(10);
					    uploadAssembly(configuration,new SubProgressMonitor(monitor, 30));
						monitor.worked(80);
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


				private void uploadResource(File file, IProgressMonitor monitor) throws CoreException,
						IOException, InterruptedException, JSchException {

			
					FileTransfer ft = new FileTransfer(Activator.getSSHUserName(),
							Activator.getSSHHostName(), Activator.getSSHPassword());
					ft.transferToServer(file, file.getName(), monitor);

				}

				private void uploadAssembly(ILaunchConfiguration configuration,  IProgressMonitor monitor)
						throws CoreException, IOException, InterruptedException,
						JSchException {
					String projectName = configuration.getAttribute("",
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
				   ApplyTemplate template = new ApplyTemplate('<', '>', "template");
				   
				   template.overwriteFile("script.stg", "script.sh", getProperties(configuration), getProjectName(configuration) );
					String scriptFile = configuration.getAttribute(SCRIPT_PATH, "");
					String scriptPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(scriptFile))
							.getLocation().toFile().getAbsolutePath();
							
					uploadResource(new File(scriptPath),monitor);

				}

				private Map<String, String> getProperties(
						ILaunchConfiguration configuration) {
					Map<String, String> map = new HashMap<String, String>();
					map.put(TEMPALTE_VARIABLE_EXECUTABLE, getExecutable());
					return map;
				}


				private String getExecutable() {
					return "someother"+"-0.0.1"+"-jar-with-dependencies.jar";
				}


				private String getProjectName(ILaunchConfiguration configuration) {
					// TODO Auto-generated method stub
					return null;
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
		
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	}
