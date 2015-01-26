/*
 * 
 * This class is heavily copied from 
	/org.eclipse.m2e.launching/src/org/eclipse/m2e/internal/launch/MavenLaunchDelegate.java
 * Could have avoided the clone if extensionSupport and launchsupport was protected fields instead of private
 * 
 * Only change method is the launch, customized to assembly the jar
 * 
 */



package cb.tailorswift.launch;

import static org.eclipse.m2e.internal.launch.MavenLaunchUtils.quote;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.RefreshTab;

import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.internal.launch.LaunchingUtils;
import org.eclipse.m2e.internal.launch.MavenRuntimeLaunchSupport;
import org.eclipse.m2e.internal.launch.MavenRuntimeLaunchSupport.VMArguments;

import tailorswift.Activator;
import cb.tailorswift.behavior.ExecuteCommand;




@SuppressWarnings("restriction")
public class LaunchWebScalding extends JavaLaunchDelegate  implements MavenLaunchConstants{

	private static final String LAUNCHER_TYPE = "org.codehaus.classworlds.Launcher"; //$NON-NLS-1$

	//classworlds 2.0
	private static final String LAUNCHER_TYPE3 = "org.codehaus.plexus.classworlds.launcher.Launcher"; //$NON-NLS-1$

	private ILaunch launch;

	private IProgressMonitor monitor;

	private String programArguments;

	private MavenRuntimeLaunchSupport launchSupport;
	private final ExecuteCommand command = new ExecuteCommand();

	private MavenLaunchExtensionsSupport extensionsSupport;

	public static final String PROJECT_NAME = "PROJECT_NAME";


	private String getProjectPath(ILaunchConfiguration configuration) throws CoreException {
		String projectName = configuration.getAttribute(PROJECT_NAME,"");
		return Activator.getProjectAbsolutePath(projectName);

	}

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		this.launch = launch;
		this.monitor = monitor;
		this.programArguments = null;

		try {

			ILaunchConfigurationWorkingCopy workingCopy = launchMavenJob(
					configuration, launch, monitor);

			super.launch(workingCopy, mode, launch, monitor);
		} finally {
			this.launch = null;
			this.monitor = null;
			this.launchSupport = null;
			this.extensionsSupport = null;
		}
	}

	protected ILaunchConfigurationWorkingCopy launchMavenJob(
			ILaunchConfiguration configuration, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		this.launchSupport = MavenRuntimeLaunchSupport.create(configuration, launch, monitor);
		this.extensionsSupport = MavenLaunchExtensionsSupport.create(configuration, launch);

		ILaunchConfigurationWorkingCopy workingCopy= configuration.getWorkingCopy();

		workingCopy.setAttribute(ATTR_POM_DIR, getProjectPath(configuration));
		workingCopy.setAttribute(ATTR_GOALS, "clean package");
		workingCopy.setAttribute(ATTR_SKIP_TESTS, true);

		workingCopy.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
		workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE, "${project}"); //$NON-NLS-1$
		workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);


		extensionsSupport.configureSourceLookup(workingCopy, launch, monitor);
		return workingCopy;
	}

	public IVMRunner getVMRunner(final ILaunchConfiguration configuration, String mode) throws CoreException {
		return launchSupport.decorateVMRunner(super.getVMRunner(configuration, mode));
	}

	public String getMainTypeName(ILaunchConfiguration configuration) {
		return launchSupport.getVersion().startsWith("3.") ? LAUNCHER_TYPE3 : LAUNCHER_TYPE; //$NON-NLS-1$
	}

	public String[] getClasspath(ILaunchConfiguration configuration) {
		List<String> cp = launchSupport.getBootClasspath();
		return cp.toArray(new String[cp.size()]);
	}

	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		if(programArguments == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getProperties(configuration));
			sb.append(" ").append(getPreferences(configuration));
			sb.append(" ").append(getGoals(configuration));

			extensionsSupport.appendProgramArguments(sb, configuration, launch, monitor);

			programArguments = sb.toString();
		}
		return programArguments;
	}
	protected String getGoals(ILaunchConfiguration configuration) throws CoreException {
	    return configuration.getAttribute(MavenLaunchConstants.ATTR_GOALS, ""); //$NON-NLS-1$
	  }
	
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException {
		VMArguments arguments = launchSupport.getVMArguments();

		extensionsSupport.appendVMArguments(arguments, configuration, launch, monitor);

		// user configured entries
		arguments.append(super.getVMArguments(configuration));

		return arguments.toString();
	}



	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) {
		return false;
	}


	/**
	 * Construct string with properties to pass to JVM as system properties
	 */
	private String getProperties(ILaunchConfiguration configuration) {
		StringBuffer sb = new StringBuffer();

		try {
			@SuppressWarnings("unchecked")
			List<String> properties = configuration.getAttribute(ATTR_PROPERTIES, Collections.EMPTY_LIST);
			for(String property : properties) {
				int n = property.indexOf('=');
				String name = property;
				String value = null;

				if(n > -1) {
					name = property.substring(0, n);
					if(n > 1) {
						value = LaunchingUtils.substituteVar(property.substring(n + 1));
					}
				}

				sb.append(" -D").append(name); //$NON-NLS-1$
				if(value != null) {
					sb.append('=').append(quote(value));
				}
			}
		} catch(CoreException e) {
			String msg = "Exception while getting configuration attribute " + ATTR_PROPERTIES;
			command.logError(e,msg);
		}

		try {
			String profiles = configuration.getAttribute(ATTR_PROFILES, (String) null);
			if(profiles != null && profiles.trim().length() > 0) {
				sb.append(" -P").append(profiles.replaceAll("\\s+", ",")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		} catch(CoreException ex) {
			String msg = "Exception while getting configuration attribute " + ATTR_PROFILES;
			command.logError(ex,msg);
		}

		return sb.toString();
	}

	/**
	 * Construct string with preferences to pass to JVM as system properties
	 */
	private String getPreferences(ILaunchConfiguration configuration) throws CoreException {
		IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();

		StringBuffer sb = new StringBuffer();

		sb.append(" -B"); //$NON-NLS-1$

		if(configuration.getAttribute(MavenLaunchConstants.ATTR_DEBUG_OUTPUT, mavenConfiguration.isDebugOutput())) {
			sb.append(" -X").append(" -e"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// sb.append(" -D").append(MavenPreferenceConstants.P_DEBUG_OUTPUT).append("=").append(debugOutput);

		if(configuration.getAttribute(MavenLaunchConstants.ATTR_OFFLINE, mavenConfiguration.isOffline())) {
			sb.append(" -o"); //$NON-NLS-1$
		}
		// sb.append(" -D").append(MavenPreferenceConstants.P_OFFLINE).append("=").append(offline);

		if(configuration.getAttribute(MavenLaunchConstants.ATTR_UPDATE_SNAPSHOTS, false)) {
			sb.append(" -U"); //$NON-NLS-1$
		}

		if(configuration.getAttribute(MavenLaunchConstants.ATTR_NON_RECURSIVE, false)) {
			sb.append(" -N"); //$NON-NLS-1$
		}

		if(configuration.getAttribute(MavenLaunchConstants.ATTR_SKIP_TESTS, false)) {
			sb.append(" -Dmaven.test.skip=true -DskipTests"); //$NON-NLS-1$
		}

		int threads = configuration.getAttribute(MavenLaunchConstants.ATTR_THREADS, 1);
		if(threads > 1) {
			sb.append(" --threads ").append(threads);
		}

		String settings = configuration.getAttribute(MavenLaunchConstants.ATTR_USER_SETTINGS, (String) null);
		if(settings == null || settings.trim().length() <= 0) {
			settings = mavenConfiguration.getUserSettingsFile();
			if(settings != null && settings.trim().length() > 0 && !new File(settings.trim()).exists()) {
				settings = null;
			}
		}
		if(settings != null && settings.trim().length() > 0) {
			sb.append(" -s ").append(quote(settings)); //$NON-NLS-1$
		}

		// boolean b = preferenceStore.getBoolean(MavenPreferenceConstants.P_CHECK_LATEST_PLUGIN_VERSION);
		// sb.append(" -D").append(MavenPreferenceConstants.P_CHECK_LATEST_PLUGIN_VERSION).append("=").append(b);

		// b = preferenceStore.getBoolean(MavenPreferenceConstants.P_UPDATE_SNAPSHOTS);
		// sb.append(" -D").append(MavenPreferenceConstants.P_UPDATE_SNAPSHOTS).append("=").append(b);

		// String s = preferenceStore.getString(MavenPreferenceConstants.P_GLOBAL_CHECKSUM_POLICY);
		// if(s != null && s.trim().length() > 0) {
		//   sb.append(" -D").append(MavenPreferenceConstants.P_GLOBAL_CHECKSUM_POLICY).append("=").append(s);
		// }

		return sb.toString();
	}

	static void removeTempFiles(ILaunch launch) {
		MavenRuntimeLaunchSupport.removeTempFiles(launch);
	}

}
