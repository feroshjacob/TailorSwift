package cb.tailorswift.launch.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
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

public class WebScaldingLaunchTab extends AbstractLaunchConfigurationTab {

	public static final String TAB_NAME = "WebscaldingLaunch_tab";
	public static final String WEBSCALDING_LAUNCH_PROGRAM_ARGUMENTS="WEBSCALDING_PROGRAM_ARGUMENTS";
	public static final String WEBSCALDING_LAUNCH_PROGRAM_OPTIONS="WEBSCALDING_PROGRAM_OPTIONS";
	private Text text;

	private ListViewer optionsLV;
	private ListViewer argumentsLV;

	private List<KeyValuePair> programOptions = new ArrayList<KeyValuePair>();
	private List<KeyValuePair> programArguments = new ArrayList<KeyValuePair>();

	@Override
	public void createControl(Composite parent) {

		Font font = parent.getFont();
		Composite comp = createComposite(parent, font, 1, 1, GridData.FILL_BOTH);
		comp.setLayout(new GridLayout(3, false));

		Label lblMainClass = new Label(comp, SWT.NONE);
		lblMainClass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMainClass.setText("Main class");

		text = new Text(comp, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSearch = new Button(comp, SWT.NONE);
		btnSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSearch.setText("Search");

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
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		// TODO Auto-generated method stub
		super.deactivated(workingCopy);
		performApply(workingCopy);
	}
	
	@Override
	public void activated(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		initializeFrom(configuration);
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}


	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			
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
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return TAB_NAME;
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);

		return true;

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

}

/**
 * This class represents a player
 */

class KeyValuePair {

	private static final String NAME_VALUE_SEPARATOR = "=";

	private static final String PAIR_SEPARATOR = "\n";

	// Column constants
	public static final int NAME = 0;

	public static final int VALUE = 1;

	private String name;

	private String value;

	public String getName() {
		return name;
	}

	public KeyValuePair(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KeyValuePair))
			return false;
		KeyValuePair pair = (KeyValuePair) obj;
		return (pair.name + pair.value).equals(this.name + this.value);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (this.name + this.value).hashCode();
	}

	public static String mkString(Iterable<KeyValuePair> values){
		// if the array is null or empty return an empty string
		if(values == null || !values.iterator().hasNext())
			return "";
		
		// move all non-empty values from the original array to a new list (empty is a null, empty or all-whitespace string)
		List<String> nonEmptyVals = new LinkedList<String>();
		for (KeyValuePair val : values) {
			if(val != null && val.toString().trim().length() > 0){
				nonEmptyVals.add(val.getName()+NAME_VALUE_SEPARATOR+ val.getValue());
			}
		}
		
		// if there are no "non-empty" values return an empty string
		if(nonEmptyVals.size() == 0)
			return "";
		
		// iterate the non-empty values and concatenate them with the separator, the entire string is surrounded with "start" and "end" parameters
		StringBuilder result = new StringBuilder();
	
		int i = 0; 
		for (String val : nonEmptyVals) {
			if(i > 0)
				result.append(PAIR_SEPARATOR);
			result.append(val);
			i++;
		}
		
		return result.toString();
	}
	public static List<KeyValuePair> parseString(String content){
		// if the array is null or empty return an empty string
		if(content == null ||  content.toString().trim().length() <1)
			return new ArrayList<KeyValuePair>();
		
	     List<KeyValuePair> list = new ArrayList<KeyValuePair>();
		for(String pair : content.split(PAIR_SEPARATOR)) {
			list.add(new KeyValuePair(pair.split(NAME_VALUE_SEPARATOR)[0], pair.split(NAME_VALUE_SEPARATOR)[1]));
		}
		
		return list;
	}

}
