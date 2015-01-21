package org.eclipse.jdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

public class CustomJavaAppletLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		IVMInstall vm = verifyVMInstall(configuration);
		 IVMRunner runner = vm.getVMRunner(mode);
		 VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
			        "xsbt.boot.Boot", getClasspath(configuration));
			    runConfig.setProgramArguments(new String[] {});

			 runConfig.setVMArguments(new String[]{});
			   
			    runConfig.setWorkingDirectory("/Users/fjacob/dump/SBTtest");
			    // Bootpath
			    String[] bootpath = getBootpath(configuration);
			 runConfig.setBootClassPath(bootpath);
			   
			    // Launch the configuration
			 runner.run(runConfig, launch, monitor); 

	}

}
