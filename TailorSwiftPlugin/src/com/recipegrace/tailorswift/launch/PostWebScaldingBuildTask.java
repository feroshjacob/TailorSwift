package com.recipegrace.tailorswift.launch;

import static com.recipegrace.tailorswift.common.ITemplateConstants.TEMPLATE_VARIABLE_ARGUMENTS;
import static com.recipegrace.tailorswift.common.ITemplateConstants.TEMPLATE_VARIABLE_EXECUTABLE;
import static com.recipegrace.tailorswift.common.ITemplateConstants.TEMPLATE_VARIABLE_MAIN;
import static com.recipegrace.tailorswift.common.ITemplateConstants.TEMPLATE_VARIABLE_OPTIONS;
import static com.recipegrace.tailorswift.common.ITemplateConstants.TEMPLATE_VARIABLE_SCALDING_TOOL;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

import tailorswift.Activator;

import com.jcraft.jsch.JSchException;
import com.recipegrace.tailorswift.common.ApplyTemplate;
import com.recipegrace.tailorswift.common.JobWithResult;
import com.recipegrace.tailorswift.launch.ui.KeyValuePair;
import com.recipegrace.tailorswift.launch.ui.WebScaldingLaunchTab;
import com.recipegrace.tailorswift.ssh.FileTransfer;
import com.recipegrace.tailorswift.ssh.SSHCommand;


public class PostWebScaldingBuildTask extends JobWithResult {
	
	
	private static final String SCRIPT_FILE = "script.sh";
	private ILaunchConfiguration configuration =null;
	private boolean isMaven =false;
	public PostWebScaldingBuildTask(ILaunchConfiguration configuration, String jobName, boolean isMaven) {
		super(jobName);
		 this.configuration=configuration;
		 this.isMaven=isMaven;
		 
	}

	protected IStatus run(IProgressMonitor monitor) {

		IStatus status = null;
		try {
			monitor.beginTask("Submitting Scalding Job", 100);
			uploadScript(configuration, new SubProgressMonitor(monitor,
					10));
			monitor.worked(10);
			uploadAssembly(configuration, new SubProgressMonitor(
					monitor, 30));
			monitor.worked(70);
			submitJob(configuration);
			monitor.worked(10);
			refreshProject(configuration,new SubProgressMonitor(
					monitor, 30));
			monitor.worked(10);
			status = Status.OK_STATUS;
			monitor.done();
			// command.openInfo("Job submitted successfully",
			// "WebScalding job submission");

		} catch (IOException | InterruptedException | CoreException
				| JSchException e) {
			// TODO Auto-generated catch block
			command.logError(e, "Job submission failed!");
			status = Status.CANCEL_STATUS;

		}

		return status;
	}

	private void refreshProject(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName())
		.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		
	}

	private void uploadResource(File file, IProgressMonitor monitor)
			throws CoreException, IOException, InterruptedException,
			JSchException {

		FileTransfer ft = new FileTransfer(Activator.getSSHUserName(),
				Activator.getSSHHostName(), Activator.getSSHPassword());
		ft.transferToServer(file, file.getName(), monitor);

	}

	private void uploadAssembly(ILaunchConfiguration configuration,
			IProgressMonitor monitor) throws CoreException,
			IOException, InterruptedException, JSchException {
		String projectName = getProjectName();
		String scalaTargetFolder = isMaven?"": ("scala-2.10"+ File.separator);
		String path = Activator.getProjectAbsolutePath(projectName)
				+ File.separator + "target" + File.separator + scalaTargetFolder+ getExecutable();
		uploadResource(new File(path), monitor);

	}

	private void uploadScript(ILaunchConfiguration configuration,
			IProgressMonitor monitor) throws CoreException,
			IOException, InterruptedException, JSchException {
		ApplyTemplate template = new ApplyTemplate('<', '>', "template");

		String scriptName = SCRIPT_FILE;
		template.overwriteFile("script.stg", scriptName,
				getProperties(), getProjectName());
		String scriptPath = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(getProjectName()).getFile(scriptName)
				.getLocation().toFile().getAbsolutePath();
		uploadResource(new File(scriptPath), monitor);

	}

	private Map<String, Object> getProperties() throws CoreException {
		Map<String, Object> map = new HashMap<String, Object>();

		String programOptions = configuration
				.getAttribute(
						WebScaldingLaunchTab.WEBSCALDING_LAUNCH_PROGRAM_OPTIONS,
						"");
		String programArguments = configuration
				.getAttribute(
						WebScaldingLaunchTab.WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS,
						"");
		
		List<KeyValuePair> options = KeyValuePair
				.parseString(programOptions);
		List<KeyValuePair> arguments = KeyValuePair
				.parseString(programArguments);

		map.put(TEMPLATE_VARIABLE_OPTIONS, options);
		map.put(TEMPLATE_VARIABLE_ARGUMENTS, arguments);
		map.put(TEMPLATE_VARIABLE_MAIN, getQualifiedJobClass());
		map.put(TEMPLATE_VARIABLE_EXECUTABLE, getExecutable());
		map.put(TEMPLATE_VARIABLE_SCALDING_TOOL, getScaldingTool());
			
//
		return map;
	}

	protected String getScaldingTool() {
		
		if(isMaven)
		return "com.twitter.scalding.Tool ";
		else return "";
	}

	private String getQualifiedJobClass() throws CoreException {
		return configuration
				.getAttribute(
						WebScaldingLaunchTab.WEBSCALDING_LAUNCH_JOB_QUALIFIED_CLASS_NAME,
						"");
	}

	private String getExecutable() throws CoreException {
		return getProjectName() 
				+ "-job.jar";
	}





	private String getProjectName() throws CoreException {

		return configuration.getAttribute(
				WebScaldingLaunchTab.WEBSCALDING_LAUNCH_PROJECT_NAME,
				"");
	}

	private void submitJob(ILaunchConfiguration configuration)
			throws CoreException, IOException, InterruptedException,
			JSchException {
	

		SSHCommand sshCommand = new SSHCommand(
				Activator.getSSHUserName(), Activator.getSSHHostName(),
				Activator.getSSHPassword());
		sshCommand.execute("nohup /bin/sh " + SCRIPT_FILE
				+ " `</dev/null` >nohup.out 2>&1 &");

	}


}
