package com.recipegrace.tailorswift.launchshortcut;

import static com.recipegrace.tailorswift.common.ScalaParsingHelper.getOneWebScaldingJobClass;
import static com.recipegrace.tailorswift.launch.ui.WebScaldingLaunchTab.*;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.scalaide.core.internal.jdt.model.ScalaClassElement;
import org.scalaide.core.internal.jdt.model.ScalaSourceFile;

import tailorswift.Activator;

import com.recipegrace.tailorswift.common.ExecuteCommand;
@SuppressWarnings("restriction")
public class WebScaldingShortCut implements ILaunchShortcut {

	
	private static final String WEBSCALDIN_LAUNCH_CONFIGURATION = "TailorSwift.launchWebScaldingConfiguration";
	ExecuteCommand command = new ExecuteCommand();
	
	   protected ILaunchConfigurationType getConfigurationType() {
	        return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(WEBSCALDIN_LAUNCH_CONFIGURATION);
	    }

	@Override
	public void launch(ISelection selection, String mode) {


			
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				Object first = structuredSelection.getFirstElement();
				if (first instanceof ScalaSourceFile) {
					ScalaSourceFile scalaSourceFile = (ScalaSourceFile) first;
				
					try {
						
						ScalaClassElement element=	getOneWebScaldingJobClass(scalaSourceFile);
						if(element==null) {
							command.openInfo("No websclading job", "Launch failed" ,IStatus.ERROR);
						}
						else {
							launch(element, mode);
						
						}
					} catch (CoreException e1) {
						 command.logError(e1, "WebScalding launch failed");
					}

				}
			}
		}

	

	   @Override
	    public void launch(IEditorPart editor, String mode) {
	        IEditorInput editorInput = editor.getEditorInput();
	        if (editorInput instanceof FileEditorInput) {
	            FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
	            ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(fileEditorInput.getFile());
	            if(compilationUnit instanceof ScalaSourceFile) {
	            	ScalaSourceFile scalaSourceFile = (ScalaSourceFile)compilationUnit;
	            	ScalaClassElement element;
					try {
						element = getOneWebScaldingJobClass(scalaSourceFile);
						 launch(element, mode);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						  command.logError(e, "WebScalding launch failed");
					}
	            	
	            }
	           
	        }

	    }

	//
		
	    protected void launch(ScalaClassElement element, String mode) {
	        ILaunchConfiguration config = findLaunchConfiguration(element, getConfigurationType());
	        launch(config, mode);
	    }

	    protected void launch(ILaunchConfiguration config, String mode) {
	        if (config != null) {
	            DebugUITools.launch(config, mode);
	        }
	    }

	
		protected ILaunchConfiguration findLaunchConfiguration(ScalaClassElement element, ILaunchConfigurationType configType) {
	        List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
	        try {
	            ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
	            for (int i = 0; i < configs.length; i++) {
	                ILaunchConfiguration config = configs[i];
	                String jobClassName = config.getAttribute(WEBSCALDING_LAUNCH_JOB_CLASS_NAME, "");
	                if (element.getElementName().equals(jobClassName)) {
	                    candidateConfigs.add(config);
	                }
	            }
	        } catch (CoreException e) {
	            command.logError(e, "WebScalding launch failed");
	            return null;
	        }

	        int candidateCount = candidateConfigs.size();

	        // If no matches, create a new configuration.
	        if (candidateCount < 1) {
	            return createConfiguration(element);
	        } else if (candidateCount == 1) {
	            return (ILaunchConfiguration) candidateConfigs.get(0);
	        } else {
	            // Prompt the user to choose a config. A null result means the user
	            // canceled the dialog, in which case this method returns null,
	            // since canceling the dialog should also cancel launching anything.
	            ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
	            if (config != null) {
	                return config;
	            }
	        }
	        return null;
	    }


	    /**
	     * Show a selection dialog that allows the user to choose one of the specified launch configurations. Return the chosen config, or <code>null</code> if the user canceled the
	     * dialog.
	     */
	    protected ILaunchConfiguration chooseConfiguration(List<?> configList) {
	        IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
	        ElementListSelectionDialog dialog = new ElementListSelectionDialog(Activator.getShell(), labelProvider);
	        dialog.setElements(configList.toArray());
	        dialog.setTitle("Select Launch Configuration");
	        dialog.setMessage("Selection the launch configuration you wish to use.");
	        dialog.setMultipleSelection(false);
	        int result = dialog.open();
	        labelProvider.dispose();
	        if (result == IStatus.OK) {
	            return (ILaunchConfiguration) dialog.getFirstResult();
	        }
	        return null;
	    }

	    /**
	     * Create a new configuration based on the Mule project.
	     * 
	     * @param project
	     * @return
	     */
	    protected ILaunchConfiguration createConfiguration(ScalaClassElement classElement) {
	        try {
	            ILaunchConfigurationType configType = getConfigurationType();
	            String jobClassName = classElement.getElementName();
	            String completeJobClassName = classElement.getFullyQualifiedName();
	            
	            ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(jobClassName));
	            wc.setAttribute(WEBSCALDING_LAUNCH_JOB_CLASS_NAME, jobClassName);

	            // needed for some examples to run
	            wc.setAttribute(WEBSCALDING_LAUNCH_PROJECT_NAME, classElement.getCompilationUnit().getResource().getProject().getName());
	            wc.setAttribute(WEBSCALDING_LAUNCH_JOB_QUALIFIED_CLASS_NAME, completeJobClassName);

	            return wc.doSave();

	        } catch (CoreException e) {
	        	  command.logError(e, "WebScalding launch failed");
	            return null;
	        }
	    }

	    /**
	     * Get the launch manager.
	     * 
	     * @return
	     */
	    protected ILaunchManager getLaunchManager() {
	        return DebugPlugin.getDefault().getLaunchManager();
	    }

}
