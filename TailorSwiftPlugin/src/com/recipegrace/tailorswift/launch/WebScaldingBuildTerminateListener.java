package com.recipegrace.tailorswift.launch;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchesListener2;

public class WebScaldingBuildTerminateListener implements ILaunchesListener2 {

		private ILaunchConfiguration configuration;

	public WebScaldingBuildTerminateListener(ILaunchConfiguration configuration) {
		this.configuration = configuration;
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

		Job job = new PostWebScaldingBuildTask(configuration,"Submitting Scalding Job");
		job.schedule();

	}

}
