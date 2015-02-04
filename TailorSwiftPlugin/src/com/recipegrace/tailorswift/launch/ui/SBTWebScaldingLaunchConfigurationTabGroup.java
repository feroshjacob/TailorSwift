package com.recipegrace.tailorswift.launch.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class SBTWebScaldingLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {


	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				//	new LaunchConfigurationMainTab(),
					new WebScaldingLaunchTab(),
					new CommonTab()
			};
			setTabs(tabs);

	}

}
