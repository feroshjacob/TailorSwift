package cb.tailorswift.template;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.recipegrace.tailorswift.launch.ui.KeyValuePair;

public class GenerateBuildFile {



	@Test
	public void testScriptPOM() throws IOException {
	//	URL fileURL =  new URL("platform:/plugin/"+ Activator.PLUGIN_ID+"/resources/jobtemplate.zip");
	//	STGroup group = new STGroupFile(fileURL,"UTF-8", '<', '>');
		STGroup group = new STGroupFile("resources/pom.stg", '$', '$');
	//	 System.out.println(group.encoding);
		ST st = group.getInstanceOf("template");
		//List<String> names =  new ArrayList<String>();
		st.add("version", "0.0.1");
		st.add("project", "someother");
		st.add("groupId", "com.recipegrace");
	
	
		
	// System.out.println(st.render());
	  
		
	}
	@Test
	public void testScriptFile() throws IOException {
	//	URL fileURL =  new URL("platform:/plugin/"+ Activator.PLUGIN_ID+"/resources/jobtemplate.zip");
	//	STGroup group = new STGroupFile(fileURL,"UTF-8", '<', '>');
		STGroup group = new STGroupFile("resources/script.stg", '<', '>');
	//	 System.out.println(group.encoding);
		ST st = group.getInstanceOf("template");
		//List<String> names =  new ArrayList<String>();
		st.add("executable", "someother-0.0.1-jar-with-dependencies.jar");
		st.add("main", "com.recipegrace.WordCountJob");
		List<KeyValuePair> pairs = new ArrayList<KeyValuePair>();
		pairs.add(new KeyValuePair("input", "hello.txt"));
		pairs.add(new KeyValuePair("output", "output.txt"));
		st.add("arguments", pairs);
		st.add("options", pairs);
		 System.out.println(st.render());
		Assert.assertEquals("#!/bin/bash"+"\n"+
"hadoop jar someother-0.0.1-jar-with-dependencies.jar -Dinput=hello.txt-Doutput=output.txt com.recipegrace.WordCountJob --hdfs --input hello.txt --output output.txt  ", st.render());
	
	  
		
	}
}
