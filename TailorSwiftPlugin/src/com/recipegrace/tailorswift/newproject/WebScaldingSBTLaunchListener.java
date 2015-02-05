package com.recipegrace.tailorswift.newproject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;

import com.recipegrace.tailorswift.common.IOUtils;

public class WebScaldingSBTLaunchListener implements ILaunchesListener2 {

	private String projectName = null;
	public WebScaldingSBTLaunchListener(String projectName) {
		this.projectName=projectName;
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
		try {
			new SBTProjectSupport (projectName).refreshProject(new NullProgressMonitor());
		} catch (CoreException e) {
			new IOUtils().logError(e, "refresh project:" + projectName + "  failed");
		}

	}

}
