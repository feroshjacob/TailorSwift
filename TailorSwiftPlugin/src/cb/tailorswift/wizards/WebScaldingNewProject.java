package cb.tailorswift.wizards;

import java.net.URI;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import cb.tailorswift.behavior.WebScaldingProjectSupport;

public class WebScaldingNewProject extends Wizard implements INewWizard {
	
	private WizardNewProjectCreationPage _pageOne;
	
	private static String WIZARD_NAME ="New WebScalding Project";

	public WebScaldingNewProject() {
		  setWindowTitle(WIZARD_NAME);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performFinish() {
		  String name = _pageOne.getProjectName();
		    URI location = null;
		    if (!_pageOne.useDefaults()) {
		        location = _pageOne.getLocationURI();
		    } // else location == null
		 
		    WebScaldingProjectSupport project= new WebScaldingProjectSupport();
		    project.createProject(name, location);
		    return true;
	}
	@Override
	public void addPages() {
	    super.addPages();
	 
	    _pageOne = new WizardNewProjectCreationPage("Tailor Swift: Create WebScalding Project");
	    _pageOne.setTitle("WebScalding Project");
	    _pageOne.setDescription("Tailored for swift data access! ");
	 
	    addPage(_pageOne);
	}

}
