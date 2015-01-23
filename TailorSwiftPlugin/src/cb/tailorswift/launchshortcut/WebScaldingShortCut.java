package cb.tailorswift.launchshortcut;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.launcher.AppletLaunchConfigurationUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.ui.launcher.AbstractLaunchShortcut;
import org.eclipse.ui.IEditorPart;

import cb.tailorswift.behavior.ExecuteCommand;
import tailorswift.Activator;
public class WebScaldingShortCut extends AbstractLaunchShortcut {

	ExecuteCommand command = new ExecuteCommand();
	@Override
	public void launch(ISelection selection, String mode) {
	    if(selection instanceof IStructuredSelection) {
	        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
	        Object first = structuredSelection.getFirstElement();
	        if (first instanceof IType){
	        		IType type = (IType)first;
	        try {
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(
				        new NullProgressMonitor());
			      IWorkspaceRoot workspaceRoot=ResourcesPlugin.getWorkspace().getRoot();
			        IJavaModel javaModel=JavaCore.create(workspaceRoot);
			        IJavaProject javaProject=    javaModel.getJavaProject("Dsss");
				        IType javaLangApplet = JavaLaunchConfigurationUtils.getMainType(
				            "com.hello.HelloWorld", javaProject);
				        if (!hierarchy.contains(javaLangApplet)) {
				        	command.openInfo("All good", "OK", IStatus.INFO);
				        }else 
				        	command.openInfo("All bad", "OK", IStatus.INFO);
			        }
					
			catch ( CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  
	        }
	    }
		
	  
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getLaunchConfigurationTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeConfiguration(ILaunchConfigurationWorkingCopy wc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isGoodMatch(ILaunchConfiguration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
