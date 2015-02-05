package com.recipegrace.tailorswift.launch.ui;

import static com.recipegrace.tailorswift.common.ScalaParsingHelper.findWebScaldingJobClasses;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.launcher.DebugTypeSelectionDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

@SuppressWarnings("restriction")
public abstract class WebScaldingLaunchTab extends AbstractLaunchConfigurationTab {

	
	public static final String WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS="WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS";
	public static final String WEBSCALDING_LAUNCH_PROGRAM_OPTIONS="WEBSCALDING_LAUNCH_PROGRAM_OPTIONS";
	public static final String WEBSCALDING_LAUNCH_JOB_CLASS_NAME="WEBSCALDING_LAUNCH_JOB_CLASS_NAME";
	public static final String WEBSCALDING_LAUNCH_PROJECT_NAME="WEBSCALDING_LAUNCH_PROJECT_NAME";
	public static final String WEBSCALDING_LAUNCH_JOB_QUALIFIED_CLASS_NAME= "WEBSCALDING_LAUNCH_JOB_QUALIFIED_CLASS_NAME";
	public static final String WEBSCALDING_LAUNCH_SKIP_BUILD= "WEBSCALDING_LAUNCH_SKIP_BUILD";

	
	private Text txtMainClass;
	private Text txtProject;
	private Button btnSkipBuild;
	

	private ListViewer optionsLV;
	private ListViewer argumentsLV;

	private List<KeyValuePair> programOptions = new ArrayList<KeyValuePair>();
	private List<KeyValuePair> programArguments = new ArrayList<KeyValuePair>();

	@Override
	public void createControl(Composite parent) {

		Font font = parent.getFont();
		Composite comp = createComposite(parent, font, 1, 1, GridData.FILL_BOTH);
		comp.setLayout(new GridLayout(3, false));


		
		Label lblProject = new Label(comp, SWT.NONE);
		lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblProject.setText("Project");
		
		txtProject = new Text(comp, SWT.BORDER);
		txtProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
		Button btnProjectSearch = new Button(comp, SWT.NONE);
		btnProjectSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnProjectSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		        ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), root,IResource.PROJECT);
		        dialog.setTitle("Search");
		        if (dialog.open() == Window.OK) {
		            Object[] files = dialog.getResult();
		           
		            	IProject project = (IProject) files[0];
		            txtProject.setText(project.getName());
		            
		        }
			}
		});
		btnProjectSearch.setText("Search");
		
		Label lblMainClass = new Label(comp, SWT.NONE);
		lblMainClass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMainClass.setText("Main class");

	
		txtMainClass = new Text(comp, SWT.BORDER);
		txtMainClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));


		Button btnMainClassSearch = new Button(comp, SWT.NONE);
		btnMainClassSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnMainClassSearch.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				

				String projectName = txtProject.getText();
				IType[] types = null;
				try {
					types = findWebScaldingJobClasses(getLaunchConfigurationDialog(), projectName);
				} 
				catch (InterruptedException ex) {return;} 
				catch (InvocationTargetException ex) {
					setErrorMessage(ex.getTargetException().getMessage());
					return;
				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DebugTypeSelectionDialog dialog = new DebugTypeSelectionDialog(getShell(), types, "Select one Job"); 
				if (dialog.open() == Window.CANCEL) {
					return;
				}
				Object[] results = dialog.getResult();	
				IType type = (IType)results[0];
				if (type != null) {
					txtMainClass.setText(type.getFullyQualifiedName());
					txtProject.setText(type.getJavaProject().getElementName());
				}
			}
		});
		btnMainClassSearch.setText("Search");

		Label lblOptions = new Label(comp, SWT.NONE);
		lblOptions.setText("Options");
		optionsLV = intitializeListViewer(comp);

		addList(optionsLV, programOptions);
		createOptionBtns(comp);


		Label lblArguments = new Label(comp, SWT.NONE);
		lblArguments.setText("Arguments");

		argumentsLV = intitializeListViewer(comp);
		addList(argumentsLV, programArguments);

		createArgumentBtns(comp);

		setControl(comp);
		
		Label lblSkipBuild = new Label(comp, SWT.NONE);
		lblSkipBuild.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSkipBuild.setText("Skip build");
		
		btnSkipBuild = new Button(comp, SWT.CHECK);
		
		btnSkipBuild.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
		new Label(comp, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
	}

	protected void createArgumentBtns(Composite comp) {
		Composite arugmentsButtonHolder = new Composite(comp, SWT.NONE);
		arugmentsButtonHolder.setLayout(new GridLayout(1, false));
		Button btnAddArgument = new Button(arugmentsButtonHolder, SWT.NONE);
		btnAddArgument.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnAddArgument.setText("Add");
		btnAddArgument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KeyValueDialog pairDialog = new KeyValueDialog(getShell());

				int returnCode = pairDialog.open();
				if (returnCode == KeyValueDialog.OK) {
					programArguments.add(pairDialog.getKeyValuePair());
					argumentsLV.setInput(programArguments);
					argumentsLV.refresh();

				}

			}
		});
		Button btnEditArgument = new Button(arugmentsButtonHolder, SWT.NONE);
		btnEditArgument.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnEditArgument.setText("Edit");
		btnEditArgument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!argumentsLV.getSelection().isEmpty()) {

					IStructuredSelection selection = (IStructuredSelection) argumentsLV
							.getSelection();

					KeyValuePair pair = (KeyValuePair) selection
							.getFirstElement();
					KeyValueDialog pairDialog = new KeyValueDialog(getShell(),
							pair);

					int returnCode = pairDialog.open();
					if (returnCode == KeyValueDialog.OK) {
						remove(programArguments, pair);
						programArguments.add(pairDialog.getKeyValuePair());
						argumentsLV.setInput(programArguments);
						argumentsLV.refresh();

					}

				}

			}

		});

		Button btnDeleteArgument = new Button(arugmentsButtonHolder, SWT.NONE);
		btnDeleteArgument.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnDeleteArgument.setText("Delete");
		btnDeleteArgument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!argumentsLV.getSelection().isEmpty()) {

					IStructuredSelection selection = (IStructuredSelection) argumentsLV
							.getSelection();

					KeyValuePair pair = (KeyValuePair) selection
							.getFirstElement();
					 remove(programArguments, pair);
					argumentsLV.setInput(programArguments);
					argumentsLV.refresh();

				}

			}

		});

	}

	protected void createOptionBtns(Composite comp) {
		Composite arugmentsButtonHolder = new Composite(comp, SWT.NONE);
		arugmentsButtonHolder.setLayout(new GridLayout(1, false));
		Button btnAddArgument = new Button(arugmentsButtonHolder, SWT.NONE);
		btnAddArgument.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnAddArgument.setText("Add");
		btnAddArgument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KeyValueDialog pairDialog = new KeyValueDialog(getShell());

				int returnCode = pairDialog.open();
				if (returnCode == KeyValueDialog.OK) {
					programOptions.add(pairDialog.getKeyValuePair());
					optionsLV.setInput(programOptions);
					optionsLV.refresh();

				}

			}
		});
		Button btnEditArgument = new Button(arugmentsButtonHolder, SWT.NONE);
		btnEditArgument.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnEditArgument.setText("Edit");
		btnEditArgument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!optionsLV.getSelection().isEmpty()) {

					IStructuredSelection selection = (IStructuredSelection) optionsLV
							.getSelection();

					KeyValuePair pair = (KeyValuePair) selection
							.getFirstElement();
					KeyValueDialog pairDialog = new KeyValueDialog(getShell(),
							pair);

					int returnCode = pairDialog.open();
					if (returnCode == KeyValueDialog.OK) {
						remove(programOptions, pair);
						programOptions.add(pairDialog.getKeyValuePair());
						optionsLV.setInput(programOptions);
						optionsLV.refresh();

					}

				}

			}

		});

		Button btnDeleteArgument = new Button(arugmentsButtonHolder, SWT.NONE);
		btnDeleteArgument.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnDeleteArgument.setText("Delete");
		btnDeleteArgument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!optionsLV.getSelection().isEmpty()) {

					IStructuredSelection selection = (IStructuredSelection) optionsLV
							.getSelection();

					KeyValuePair pair = (KeyValuePair) selection
							.getFirstElement();
					 remove(programOptions, pair);
					optionsLV.setInput(programOptions);
					optionsLV.refresh();

				}

			}

		});

	}
	private List<KeyValuePair> remove(List<KeyValuePair> list, KeyValuePair pair) {
		Iterator<KeyValuePair> iterator = list.iterator();
		while (iterator.hasNext()) {
			KeyValuePair current = iterator.next();
			if (current.equals(pair))
				iterator.remove();
		}
		return list;
	}



	private ListViewer intitializeListViewer(Composite comp) {

		ListViewer viewer = new ListViewer(comp, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.SINGLE);
		viewer.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return viewer;
	}

	protected void addList(final ListViewer listViewer, List<KeyValuePair> input) {
		// listViewer.getList().set
		listViewer.setContentProvider(new IStructuredContentProvider() {
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<KeyValuePair> v = (List<KeyValuePair>) inputElement;
				return v.toArray();
			}

			public void dispose() {
				return;
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				return;
			}
		});

		listViewer.setInput(input);

		listViewer.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				KeyValuePair pair = (KeyValuePair) element;

				return pair.getName() + "=" + pair.getValue();
			}
		});

	}


	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}


	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			
			
			    txtMainClass.setText( configuration.getAttribute(WEBSCALDING_LAUNCH_JOB_QUALIFIED_CLASS_NAME, ""));
			    txtProject.setText( configuration.getAttribute(WEBSCALDING_LAUNCH_PROJECT_NAME, ""));
			    btnSkipBuild.setSelection(configuration.getAttribute(WEBSCALDING_LAUNCH_SKIP_BUILD, false));
			    
				String  programArgumentsString = configuration.getAttribute(WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS, "");
				programArguments = KeyValuePair.parseString(programArgumentsString);
				argumentsLV.setInput(programArguments);
				argumentsLV.refresh();
				String  programOptionsString = configuration.getAttribute(WEBSCALDING_LAUNCH_PROGRAM_OPTIONS, "");
				programOptions = KeyValuePair.parseString(programOptionsString);
				optionsLV.setInput(programOptions);
				optionsLV.refresh();
				
	
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		   String argsContent = KeyValuePair.mkString(programArguments);
		   configuration.setAttribute(WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS, argsContent);
		   String optsContent = KeyValuePair.mkString(programOptions);
		   configuration.setAttribute(WEBSCALDING_LAUNCH_PROGRAM_OPTIONS, optsContent);
		   configuration.setAttribute(WEBSCALDING_LAUNCH_JOB_QUALIFIED_CLASS_NAME, txtMainClass.getText());
		   
		   String[] jobClassNameArray =  txtMainClass.getText().split("\\.");
		   String jobClassName = jobClassNameArray[jobClassNameArray.length-1];
		   configuration.setAttribute(WEBSCALDING_LAUNCH_JOB_CLASS_NAME, jobClassName);
		   configuration.setAttribute(WEBSCALDING_LAUNCH_PROJECT_NAME, txtProject.getText());
		   configuration.setAttribute(WEBSCALDING_LAUNCH_SKIP_BUILD, btnSkipBuild.getSelection());
	}

	

	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);

		return resourceExists(txtProject.getText());

	}

	public Composite createComposite(Composite parent, Font font, int columns,
			int hspan, int fill) {
		Composite g = new Composite(parent, SWT.NONE);
		g.setLayout(new GridLayout(columns, false));
		g.setFont(font);
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return g;
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
}

