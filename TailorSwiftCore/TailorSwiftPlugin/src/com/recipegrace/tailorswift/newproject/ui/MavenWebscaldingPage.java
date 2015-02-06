package com.recipegrace.tailorswift.newproject.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class MavenWebscaldingPage extends WizardNewProjectCreationPage {

	protected MavenWebscaldingPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	private Text txtGroupId;
	private Text txtVersion;

	private String groupId;
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private String version;

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite control = (Composite) getControl();
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 15;
		control.setLayout(layout);

		Group buttonGroup = new Group(control, SWT.NULL);
		buttonGroup.setText("Maven project details"); //$NON-NLS-1$

		initializeDialogUnits(parent);
		layout = new GridLayout();
		layout.numColumns = 2;
		buttonGroup.setLayout(layout);
		buttonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblGroupId = new Label(buttonGroup, SWT.NONE);
		lblGroupId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblGroupId.setText("groupId");

		txtGroupId = new Text(buttonGroup, SWT.BORDER);
		txtGroupId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		ModifyListener textModifylistener = new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		};
		txtGroupId.setText("com.recipegrace");
		txtGroupId.addModifyListener(textModifylistener);

		Label lblVersion = new Label(buttonGroup, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblVersion.setText("version");

		txtVersion = new Text(buttonGroup, SWT.BORDER);
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		txtVersion.setText("0.0.1");
		txtVersion.addModifyListener(textModifylistener);
		setPageComplete(validatePage());
		setControl(buttonGroup);
		Dialog.applyDialogFont(buttonGroup);

	}

	protected boolean validatePage() {
		if (!super.validatePage())
			return false;

		version = txtVersion.getText();
		groupId = txtGroupId.getText();



		if (groupId.length() < 1) {
			setErrorMessage("GroupId cannot be empty");
			return false;
		}
		if (version.length() < 1) {
			setErrorMessage("Version cannot be empty");
			return false;

		}
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

}
