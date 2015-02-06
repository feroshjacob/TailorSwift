package com.recipegrace.tailorswift.newproject.ui;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.recipegrace.tailorswift.common.IOUtils;
import com.recipegrace.tailorswift.newproject.MavenProjectSupport;
import com.recipegrace.tailorswift.newproject.WebScaldingProjectSupport;

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
		  String name = _pageOne.getProjectName();
		    URI location = null;
		    if (!_pageOne.useDefaults()) {
		        location = _pageOne.getLocationURI();
		    } // else location == null
		 
		    WebScaldingProjectSupport project= new MavenProjectSupport(name, _pageOne.getVersion(), _pageOne.getGroupId());
		    try {
				project.createProject( location);
			} catch (CoreException e) {
			 new IOUtils().logError(e, "Project creation");	
			}
		    return true;

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
