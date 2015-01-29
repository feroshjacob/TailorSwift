package com.recipegrace.tailorswift.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import tailorswift.Activator;

public class ApplyTemplate {

	private char start;
	private char end;
	private  String templateInstance;
	public ApplyTemplate(char start, char end, String templateInstance) {
		this.start = start;
		this.end = end;
		this.templateInstance = templateInstance;
	}
	
	public void overwriteFile(String templateName, String newFile,
			Map<String,String> properties,String projectName) throws IOException {

		URL fileURL = new URL("platform:/plugin/" + Activator.PLUGIN_ID
				+ "/resources/" + templateName);
		STGroup group = new STGroupFile(fileURL, "UTF-8", start, end);
		ST st = group.getInstanceOf(templateInstance);
		for(String key: properties.keySet()) {
			st.add(key, properties.get(key));	
		}
		
		String fullPath = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName).getLocation().toFile()
				.getAbsolutePath()
				+ File.separator + newFile;
		st.write(new File(fullPath), null);

	}
	
}
