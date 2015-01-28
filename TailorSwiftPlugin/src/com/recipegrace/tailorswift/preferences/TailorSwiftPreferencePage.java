package com.recipegrace.tailorswift.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import tailorswift.Activator;

public class TailorSwiftPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Text sbtHome;

	private Text sshConnectionString;
	private Text sshPassword;

	

	@Override
	public void init(IWorkbench arg0) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		
	}


	private void initalizeValues() {
		IPreferenceStore store = getPreferenceStore();
		sbtHome.setText(store.getString(Activator.SBT_HOME));
		sshConnectionString.setText(store.getString(Activator.SSH_CONNECTION_STRING));
		sshPassword.setText(store.getString(Activator.SSH_PASSWORD));
	}

	
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&SBT executable:");

		sbtHome = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		sbtHome.setLayoutData(gd);
		sbtHome.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				   dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().getAbsolutePath());
						sbtHome.setText(dialog.open());
			}

		
		});
		label = new Label(container, SWT.NULL);
		label.setText("&SSH connection string:");

		sshConnectionString = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		sshConnectionString.setLayoutData(gd);
		sshConnectionString.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
			}
		});
	
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		label = new Label(container, SWT.NULL);
		label.setText("&SSH password");

		sshPassword = new Text(container, SWT.BORDER | SWT.SINGLE| SWT.PASSWORD);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		sshPassword.setLayoutData(gd);
		sshPassword.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
			}
		});
		initalizeValues();
		return container;
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(Activator.SBT_HOME,sbtHome.getText());
		store.setValue(Activator.SSH_CONNECTION_STRING,sshConnectionString.getText());
		store.setValue(Activator.SSH_PASSWORD,sshPassword.getText());
		
		return super.performOk();
	}
}
