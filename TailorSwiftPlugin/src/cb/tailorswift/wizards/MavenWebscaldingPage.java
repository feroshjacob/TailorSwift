package cb.tailorswift.wizards;
import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.pde.internal.ui.IHelpContextIds;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
public class MavenWebscaldingPage extends WizardNewProjectCreationPage{

	protected MavenWebscaldingPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	private Text txtGroupId;
	private Text txtVersion;
		
	@Override
	public void createControl(Composite parent) {
	       super.createControl(parent);
	       Composite control = (Composite)getControl();
			GridLayout layout = new GridLayout();
			layout.verticalSpacing = 15;
			control.setLayout(layout);
			
			Group buttonGroup = new Group(control, SWT.NULL);
			buttonGroup.setText(PDEUIMessages.NewSiteProjectCreationPage_webTitle); //$NON-NLS-1$
			
			initializeDialogUnits(parent);
			layout = new GridLayout();
			layout.numColumns = 2;
			buttonGroup.setLayout(layout);
			buttonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label lblGroupId = new Label(buttonGroup, SWT.NONE);
			lblGroupId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblGroupId.setText("groupId");
			
			txtGroupId = new Text(buttonGroup, SWT.BORDER);
			txtGroupId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			

			
			Label lblVersion = new Label(buttonGroup, SWT.NONE);
			lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblVersion.setText("version");
			
			txtVersion = new Text(buttonGroup, SWT.BORDER);
			txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			setPageComplete(validatePage());
			setControl(buttonGroup);
			Dialog.applyDialogFont(buttonGroup);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(control, IHelpContextIds.NEW_SITE_MAIN);
		}



		protected boolean validatePage() {
			if (!super.validatePage())
				return false;
	
			return true;
		}
}
