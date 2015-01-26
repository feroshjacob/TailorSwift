package cb.tailorswift.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class WebScaldingNewMavenProject extends Wizard implements INewWizard {

	private MavenWebscaldingPage _pageOne;
	
	private static String WIZARD_NAME ="New WebScalding Project (Maven)";



	public WebScaldingNewMavenProject() {
		 setWindowTitle(WIZARD_NAME);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addPages() {
	    super.addPages();
	 
	    _pageOne = new MavenWebscaldingPage("Tailor Swift: Create WebScalding Project (Maven)");
	    _pageOne.setTitle("WebScalding Project");
	    _pageOne.setDescription("Tailored for swift data access! ");
	 
	    addPage(_pageOne);
	}
}
