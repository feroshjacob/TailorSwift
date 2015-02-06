package com.recipegrace.tailorswift.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;



public class StringTemplateWrapper {

	private char start;
	private char end;
	private  String templateInstance;
	private String templatePath;
	public StringTemplateWrapper(char start, char end, String templateInstance, String templatePath) {
		this.start = start;
		this.end = end;
		this.templateInstance = templateInstance;
		this.templatePath=templatePath;
	}
	public void overwriteFile(String newPath,
			Map<String,Object> properties) throws IOException {

		URL fileURL = new URL(templatePath);
		STGroup group = new STGroupFile(fileURL, "UTF-8", start, end);
		ST st = group.getInstanceOf(templateInstance);
		for(String key: properties.keySet()) {
			st.add(key, properties.get(key));	
		}
		
		st.write(new File(newPath), null);

	}
}
