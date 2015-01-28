package cb.tailorswift.template;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class GenerateBuildFile {

	@Test
	public void testBuild() throws IOException {
	//	URL fileURL =  new URL("platform:/plugin/"+ Activator.PLUGIN_ID+"/resources/jobtemplate.zip");
	//	STGroup group = new STGroupFile(fileURL,"UTF-8", '<', '>');
		STGroup group = new STGroupFile("resources/build.stg", "UTF-8", '<', '>');
	//	 System.out.println(group.encoding);
		ST st = group.getInstanceOf("template");
		//List<String> names =  new ArrayList<String>();
		st.add("project", "HSSS");
	
	
		
	  st.write(new File(".tests/hello.txt"), null);
	  
		
	}
	@Test
	public void testScript() throws IOException {
	//	URL fileURL =  new URL("platform:/plugin/"+ Activator.PLUGIN_ID+"/resources/jobtemplate.zip");
	//	STGroup group = new STGroupFile(fileURL,"UTF-8", '<', '>');
		STGroup group = new STGroupFile("resources/runOnHadoop.stg", "UTF-8", '<', '>');
	//	 System.out.println(group.encoding);
		ST st = group.getInstanceOf("template");
		//List<String> names =  new ArrayList<String>();
		st.add("project", "HSSS");
	
	
		
	 System.out.println(st.render());
	  
		
	}
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
	
	
		
	 System.out.println(st.render());
	  
		
	}
}
