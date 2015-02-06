package com.recipegrace.tailorswift.commands;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import tailorswift.Activator;

import com.jcraft.jsch.JSchException;
import com.recipegrace.tailorswift.common.IOUtils;
import com.recipegrace.tailorswift.common.JobWithResult;
import com.recipegrace.tailorswift.ssh.FileTransfer;

public class SynchronizeCommand implements IHandler {

	IOUtils command= new IOUtils();
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get workbench window
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		// set selection service
		ISelectionService service = window.getSelectionService();
		// set structured selection
		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
	 
		//check if it is an IFile
		if (structured.getFirstElement() instanceof IFile) {
			// get the selected file
			final IFile file = (IFile) structured.getFirstElement();
			// get the path
			final IPath path = file.getLocation();

			Job job = new JobWithResult("Update the nohub.out file") {
				protected IStatus run(IProgressMonitor monitor) {
		
					try {
						
						monitor.beginTask("Connecting to server", 2);
						FileTransfer ft = new FileTransfer(Activator.getSSHUserName(),
								Activator.getSSHHostName(), Activator.getSSHPassword());
						ft.transferFromServer("nohup.out", path.toOSString());	
						monitor.worked(1);
						file
						.refreshLocal(IResource.DEPTH_ZERO, monitor);
						monitor.worked(1);
						monitor.done();
					} catch (JSchException | IOException | CoreException e) {
						// TODO Auto-generated catch block
						command.logError(e, "Update failed");
						
						return Status.CANCEL_STATUS;
					}
					
					return Status.OK_STATUS;
					
				
				}
			};
			job.schedule();

		}
		return "";
	 
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
