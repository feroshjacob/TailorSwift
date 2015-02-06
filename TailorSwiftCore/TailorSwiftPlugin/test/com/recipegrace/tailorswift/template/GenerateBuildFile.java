package com.recipegrace.tailorswift.template;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Test;

import com.recipegrace.tailorswift.common.StringTemplateWrapper;

public class GenerateBuildFile {



	@Test
	public void testScriptPOM() throws IOException {
	/*//	URL fileURL =  new URL("platform:/plugin/"+ Activator.PLUGIN_ID+"/resources/jobtemplate.zip");
	//	STGroup group = new STGroupFile(fileURL,"UTF-8", '<', '>');
		STGroup group = new STGroupFile("resources/pom.stg", '$', '$');
	//	 System.out.println(group.encoding);
		ST st = group.getInstanceOf("template");
		//List<String> names =  new ArrayList<String>();
		st.add("version", "0.0.1");
		st.add("project", "someother");
		st.add("groupId", "com.recipegrace");
	
	
		
	 System.out.println(st.render());
	  
	*/	
	}
	
	@Test
	public void testScriptFile() throws IOException {
		
		URL url = new File("resources/script.stg").toURL();
		StringTemplateWrapper wrapper = new StringTemplateWrapper('<', '>', "template", url.toString());
		wrapper.overwriteFile(".tests/out", new HashMap<String, Object>());
	
	  
		
	}
}
