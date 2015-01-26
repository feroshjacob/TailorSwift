package cb.tailorswift.launch.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class KeyValueDialog extends Dialog {
	private Text txtName;
	private Text txtValue;
	private KeyValuePair pair;


	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public KeyValueDialog(Shell parentShell) {
		super(parentShell);
	}
	public KeyValueDialog(Shell parentShell, KeyValuePair pair) {
		super(parentShell);
		this.pair = pair;
	}

	@Override
	protected void okPressed() {
		pair= new KeyValuePair(txtName.getText(), txtValue.getText());
		super.okPressed();
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Name");
		new Label(container, SWT.NONE);
		
		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if(pair!=null) txtName.setText( pair.getName());
		
		Label lblValue = new Label(container, SWT.NONE);
		lblValue.setText("Value");
		new Label(container, SWT.NONE);
		
		txtValue = new Text(container, SWT.BORDER);
		txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if(pair!=null) txtValue.setText( pair.getValue());
		
        container.pack();
        ModifyListener listener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(txtName.getText().trim().length() >0 && txtValue.getText().trim().length()>0 )
					
					getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		};
		txtName.addModifyListener(listener);
		txtValue.addModifyListener(listener);
		return container;
	}

	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(250, 150);
	}
	public KeyValuePair getKeyValuePair() {
		return pair;
	}
	
	

}
