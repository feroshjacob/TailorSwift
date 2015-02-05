package com.recipegrace.tailorswift.newproject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import tailorswift.Activator;

import com.recipegrace.tailorswift.common.IOUtils;

/**
*   Thanks
* 	Naoki Takezoe
*/

public class SBTExecutor {

	private String projectName;
	

	public SBTExecutor(String projectName){
		this.projectName = projectName;
		
	}

	public ILaunch execute(String command, IProgressMonitor monitor){
	
		try {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

			ILaunchConfigurationType type
			   = manager.getLaunchConfigurationType(
			     IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

			// Creates new configuration
			ILaunchConfigurationWorkingCopy wc = null;
			if(command == null || command.length() == 0){
				wc = type.newInstance(null, String.format("sbt - %s", projectName));
			} else {
				wc = type.newInstance(null, String.format("sbt %s - %s", command, projectName));
			}

		//	wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,projectName);

			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "xsbt.boot.Boot");

			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, createVMarguments());

		    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, Activator.getProjectAbsolutePath(projectName));
		    
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, command);
			
	        ILaunchConfiguration launchConfig = SBTProjectSupport.setSBTSpecificParms( wc.doSave());
			

			ILaunch launch = launchConfig.launch ( ILaunchManager.RUN_MODE, monitor);
			ILaunchesListener2 launchListener = new WebScaldingSBTLaunchListener(projectName); 
            DebugPlugin.getDefault().getLaunchManager().addLaunches(new ILaunch[] {launch});
            DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
			
			final IProcess process = launch.getProcesses()[0];
	
				IStreamListener StreamListener = new IStreamListener() {
				@Override
				public void streamAppended(String message, IStreamMonitor monr) {
					if(message.trim().endsWith("(r)etry, (q)uit, (l)ast, or (i)gnore?")){
						try {
							process.terminate();
						} catch(Exception ex){
						}
					}
				}
			};
				process.getStreamsProxy().getOutputStreamMonitor().addListener(StreamListener);
			    
	
			
			return launch;

		} catch(Exception ex){
			
			new IOUtils().logError(ex, "launch failed");
		}

		return null;
	}

	private  String createVMarguments(){
		try {
			
			StringBuilder sb = new StringBuilder();
	
			sb.append("-Djline.WindowsTerminal.directConsole=false ");
			sb.append("-Dsbt.log.noformat=true ");
			return sb.toString();
		
		} catch(Exception ex) {
			new IOUtils().logError(ex, "launch failed");
			return "";
		}
	}
}