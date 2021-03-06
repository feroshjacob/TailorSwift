package tailorswift;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "TailorSwiftPlugin"; //$NON-NLS-1$
	

	//The identifiers for the preferences	
	public static final String SBT_LAUNCH_JAR = "sbt_launch_jar";
	public static final String SSH_CONNECTION_STRING = "ssh_connection_string";
	public static final String SSH_PASSWORD = "ssh_password";

	//The default values for the preferences
	public static final String DEFAULT_SBT_HOME= "bug;bogus;hack;";
	public static final String DEFAULT_SSH_CONNECTION_STRING = "username@hostname.com";
	public static final String DEFAULT_SSH_PASSWORD = "password";


	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(SBT_LAUNCH_JAR, DEFAULT_SBT_HOME);
		store.setDefault(SSH_CONNECTION_STRING, DEFAULT_SSH_CONNECTION_STRING);
		store.setDefault(SSH_PASSWORD, DEFAULT_SSH_PASSWORD);

	}

	public static String getSBTLaunchJar() {
		return getDefault().getPreferenceStore().getString(SBT_LAUNCH_JAR) ;
	}
	public static String getSSHUserName() {
		String connectionString= getDefault().getPreferenceStore().getString(SSH_CONNECTION_STRING) ;
	     String user=connectionString.substring(0, connectionString.indexOf('@'));
	     return user;
	  
	}
	public static String getSSHHostName() {
		String connectionString= getDefault().getPreferenceStore().getString(SSH_CONNECTION_STRING) ;
		return connectionString.substring(connectionString.indexOf('@')+1);
	    
	}
	public static String getSSHPassword() {
		return getDefault().getPreferenceStore().getString(SSH_PASSWORD) ;
		
	}
	public static String getProjectAbsolutePath(String projectFolder) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IProject newProject = workspace.getRoot().getProject(projectFolder);

		String absolutePath =newProject.getLocation().toFile().getAbsolutePath();
		return absolutePath;
	}

	public static Shell getShell() {
		return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}
