package cb.tailorswift.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;


public class LaunchConfigurationMainTab extends AbstractLaunchConfigurationTab {

    public static final String MAIN_TAB_NAME = "WebscaldingMain";
    private Text fileText;
    private Button fileButton;
    private Button projectButton;
    private Text projectText;
    private Button needsBuild;
    private Button needsClean;
    

    /**
     * @wbp.parser.entryPoint (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse
     *      .swt.widgets.Composite)
     **/
    public void createControl(Composite parent) {
        Font font = parent.getFont();
        Composite comp = createComposite(parent, font, 1, 1, GridData.FILL_BOTH);
        createProjectGroup(comp);
        createFileGroup(comp);
        createOtherOpts(comp);
        setControl(comp);

    }

	protected void createOtherOpts(Composite parent) {
		
        Group projectGroup = new Group(parent, SWT.NONE);
        projectGroup.setText("Other options");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        projectGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setLayout(layout);
        projectGroup.setFont(parent.getFont());
        
		needsBuild = createCheckButton(projectGroup, "Needs rebuild"); //$NON-NLS-1$
        needsBuild.setSelection(true);
         gd = new GridData(GridData.FILL_HORIZONTAL);
         needsBuild.setLayoutData(gd);
         needsBuild.addSelectionListener(new SelectionAdapter() {
        	 public void widgetSelected(SelectionEvent e) {
             	if(needsBuild.getSelection()== false && needsClean.getSelection()==true )
             		needsClean.setSelection(false);
             }
        	 
		});
         
         needsClean = createCheckButton(projectGroup, "Needs clean"); //$NON-NLS-1$
         needsClean.setSelection(true);
         needsClean.setLayoutData(gd);
         needsClean.addSelectionListener(new SelectionAdapter() {
        	 public void widgetSelected(SelectionEvent e) {
             	if(needsBuild.getSelection()== false && needsClean.getSelection()==true )
             		needsBuild.setSelection(true);
             }
        	 
		});

	}

    private void createProjectGroup(Composite parent) {
        Group projectGroup = new Group(parent, SWT.NONE);
        projectGroup.setText("Select project");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        projectGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setLayout(layout);
        projectGroup.setFont(parent.getFont());

        projectText = new Text(projectGroup, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        projectText.setLayoutData(gd);
        projectText.setFont(parent.getFont());
        projectText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
        });

        projectButton = createPushButton(projectGroup, "Search", null); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        projectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	browseContents(IResource.PROJECT);
            }
        });
    }

    private void createFileGroup(Composite parent) {
        Group fileGroup = new Group(parent, SWT.NONE);
        fileGroup.setText("Select script file");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        fileGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        fileGroup.setLayout(layout);
        fileGroup.setFont(parent.getFont());

        fileText = new Text(fileGroup, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.setFont(parent.getFont());
        fileText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
        });

        fileButton = createPushButton(fileGroup, "Search", null); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	browseContents(IResource.FILE);
            }
        });
    }

    /***
     * Open a resource chooser to select a file
     **/
    
    protected void browseContents(int type) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), root,type);
        dialog.setTitle("Search");
        if (dialog.open() == Window.OK) {
            Object[] files = dialog.getResult();
           
            if(type== IResource.FILE){
            	 IFile file = (IFile) files[0];
            fileText.setText(file.getFullPath().toString());
            }
            else {
            	IProject project = (IProject) files[0];
            projectText.setText(project.getName());
            }
        }

    }
 

    /**
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
     *      debug.core.ILaunchConfigurationWorkingCopy)
     **/
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse
     *      .debug.core.ILaunchConfiguration)
     **/
    public void initializeFrom(ILaunchConfiguration configuration) {

        try {
            String project = null;
            project = configuration.getAttribute(LaunchWebScaldingJob.PROJECT_NAME, "");

            String file = null;
            file = configuration.getAttribute(LaunchWebScaldingJob.SCRIPT_PATH, "");
            if (project != null) {
                projectText.setText(project);
            }
            if (file != null) {
                fileText.setText(file);
            }
        } catch (CoreException e) {
            setErrorMessage(e.getMessage());
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse
     *      .debug.core.ILaunchConfigurationWorkingCopy)
     **/
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        String file = fileText.getText().trim();
        if (file.length() == 0) {
            file = null;
        }
        String project = projectText.getText().trim();
        if (project.length() == 0) {
        	project = null;
        }
        configuration.setAttribute(LaunchWebScaldingJob.SCRIPT_PATH, file);
        configuration.setAttribute(LaunchWebScaldingJob.PROJECT_NAME, project);
        configuration.setAttribute(LaunchWebScaldingJob.NEEDS_BUILD, needsBuild.getSelection());
        configuration.setAttribute(LaunchWebScaldingJob.NEEDS_CLEAN, needsClean.getSelection());
        
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug
     *      .core.ILaunchConfiguration)
     **/
    public boolean isValid(ILaunchConfiguration launchConfig) {
        setErrorMessage(null);
        setMessage(null);
        

      return  resourceExists(fileText.getText()) && resourceExists(projectText.getText()); 
         

       
     
    }

	protected boolean resourceExists(String text) {
		if (text.length() > 0) {
            IPath path = new Path(text);
            if (ResourcesPlugin.getWorkspace().getRoot().findMember(path) == null) {
                setErrorMessage("Specified file does not exist");
                return false;
            }
            return true;
        } else {
            setMessage("Specify an file");
            return false;
        }
	}

    public Composite createComposite(Composite parent, Font font, int columns, int hspan, int fill) {
        Composite g = new Composite(parent, SWT.NONE);
        g.setLayout(new GridLayout(columns, false));
        g.setFont(font);
        GridData gd = new GridData(fill);
        gd.horizontalSpan = hspan;
        g.setLayoutData(gd);
        return g;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
     **/
    public Image getImage() {
    	
    	
        return null;
        		//Activator.getImageDescriptor("icons/sample.gif").createImage();
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     **/
    public String getName() {
        return MAIN_TAB_NAME;
    }

}