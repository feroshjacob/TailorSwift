package cb.tailorswift.behavior;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import tailorswift.Activator;

public class ExecuteCommand {
	


	public  void executeCommand(String[] command, String absolutePath) throws IOException, InterruptedException, PartInitException {


		Runtime r = Runtime.getRuntime();
		
		Process p = r.exec(command, null, new File(absolutePath));
				//p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader b1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line = "";
		MessageConsole myConsole = findConsole("WebScalding");
		showConsole(myConsole);
		MessageConsoleStream out = myConsole.newMessageStream();
		while ((line = b.readLine()) != null) {
			out.println(line);
		}
		while ((line = b1.readLine()) != null) {
			out.println(line);
		}

		b.close();
		b1.close();

	}
	private void showConsole(final MessageConsole myConsole) throws PartInitException {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();

				String id = IConsoleConstants.ID_CONSOLE_VIEW;
				IConsoleView view;
				try {
					view = (IConsoleView) page.showView(id);
					view.display(myConsole);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});


	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);

		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
	public   void logError(Exception ex, final String title) {
		final String message = ex.getMessage();
		final String formattedMessage = Activator.PLUGIN_ID + " : " + message; //$NON-NLS-1$
		final Status status = new Status(IStatus.ERROR,Activator. PLUGIN_ID, formattedMessage, ex);

		Activator.getDefault().getLog().log(status);
    }
	public   void openInfo(final String message, final String title, int statusCode) {
			
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(),
						title, message);
			}
		});
	}


}
