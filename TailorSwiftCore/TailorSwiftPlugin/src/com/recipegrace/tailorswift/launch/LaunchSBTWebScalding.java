package com.recipegrace.tailorswift.launch;

import static com.recipegrace.tailorswift.launch.ui.WebScaldingLaunchTab.WEBSCALDING_LAUNCH_SKIP_BUILD;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.recipegrace.tailorswift.common.IOUtils;
import com.recipegrace.tailorswift.launch.ui.WebScaldingLaunchTab;
import com.recipegrace.tailorswift.newproject.SBTProjectSupport;

public class LaunchSBTWebScalding extends JavaLaunchDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		boolean skipBuild = configuration.getAttribute(
				WEBSCALDING_LAUNCH_SKIP_BUILD, false);
		try {
			ILaunchConfiguration config = SBTProjectSupport
					.setSBTSpecificParms(configuration);

			if (!skipBuild){
				super.launch(config, mode, launch, monitor);
				ILaunchesListener2 launchListener = new WebScaldingBuildTerminateListener(
						configuration,false);
				DebugPlugin.getDefault().getLaunchManager()
				.addLaunches(new ILaunch[] { launch });
				DebugPlugin.getDefault().getLaunchManager()
				.addLaunchListener(launchListener);
			}else {
				Job job = new PostWebScaldingBuildTask(configuration,"Submitting Scalding Job", false);
				job.schedule();

			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			new IOUtils().logError(e, "launch failed");
		}


		if (skipBuild)
			launch.terminate();

	}

	@Override
	public String getMainTypeName(ILaunchConfiguration configuration)
			throws CoreException {
		return "xsbt.boot.Boot";
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration)
			throws CoreException {
		return "clean assembly";
	}

	@Override
	public String getVMArguments(ILaunchConfiguration configuration)
			throws CoreException {
		StringBuilder sb = new StringBuilder(
				super.getVMArguments(configuration));

		sb.append(" -Djline.WindowsTerminal.directConsole=false ");
		sb.append("-Dsbt.log.noformat=true ");
		return sb.toString();
	}

	@Override
	public IPath getWorkingDirectoryPath(ILaunchConfiguration configuration)
			throws CoreException {
		String projectName = configuration.getAttribute(
				WebScaldingLaunchTab.WEBSCALDING_LAUNCH_PROJECT_NAME, "");
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		return project.getLocation();
	}

}
