package com.recipegrace.tailorswift.newproject;

import static com.recipegrace.tailorswift.common.ITemplateConstants.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.ui.PartInitException;

import com.recipegrace.tailorswift.common.ApplyTemplate;

@SuppressWarnings("restriction")
public class MavenProjectSupport extends WebScaldingProjectSupport {


	private String version,groupId;
	public MavenProjectSupport(String projectName, String version, String groupId) {
		super(projectName);
		this.version=version;
		this.groupId=groupId;
	}

	@Override
	protected void createProject(String absolutePath, IProgressMonitor monitor)
			throws FileNotFoundException, IOException, InterruptedException,
			PartInitException, CoreException {
		unzipProject(absolutePath);
		monitor.worked(20);
		applyTemplates();
		monitor.worked(20);
		refreshProject( monitor);
		monitor.worked(20);
		enableMavenNature(monitor);
		monitor.worked(40);
	}


	private void enableMavenNature( IProgressMonitor monitor) throws CoreException {
	
	          ResolverConfiguration configuration = new ResolverConfiguration();
	          configuration.setResolveWorkspaceProjects(true);
	          configuration.setSelectedProfiles(""); //$NON-NLS-1$
	          
	          IProject project = getProject();

	         
			boolean hasMavenNature = project.hasNature(IMavenConstants.NATURE_ID);

	          IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();

	          configurationManager.enableMavenNature(project, configuration, monitor);

	          if(!hasMavenNature) {
	            configurationManager.updateProjectConfiguration(project, monitor);
	          }
	        
		
	}

	@Override
	protected String getZipFile() {
		return "/resources/maventemplate.zip";
	} 
	private void applyTemplates() throws IOException {
		ApplyTemplate templates = new ApplyTemplate('$','$', "template");
		templates.overwriteFile("pom.stg", "pom.xml",getProperties(),getProjectName());
		
	}

	private Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(TEMPLATE_VARIABLE_PROJECT, getProjectName().toLowerCase());
		properties.put(TEMPLATE_VARIABLE_GROUP_ID, groupId.toLowerCase());
		properties.put(TEMPLATE_VARIABLE_VERSION, version);
		return properties;

	}

}
