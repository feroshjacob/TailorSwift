package com.recipegrace.tailorswift.common;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;

import tailorswift.Activator; 
 
public class ApplyTemplate {

    private StringTemplateWrapper wrapper;
	public ApplyTemplate(char start, char end, String templateInstance, String templateName) {

		String templatePath ="platform:/plugin/" + Activator.PLUGIN_ID
				+ "/resources/" + templateName;
		wrapper = new StringTemplateWrapper(start, end, templateInstance, templatePath);
	}
	
	public void overwriteFile( String newFile,
			Map<String,Object> properties,String projectName) throws IOException {


		String newPath = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName).getLocation().toFile()
				.getAbsolutePath()
				+ File.separator + newFile;
		wrapper.overwriteFile(newPath, properties);
	}
	
}
