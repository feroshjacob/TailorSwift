package cb.tailorswift.ssh;

import java.io.File;

import tailorswift.Activator;

public class PathTest {

	public static void main(String[] args) {
		String connectionString = "ferosh@careerbuilder.com";
				connectionString=connectionString.substring(connectionString.indexOf('@')+1);
	     
	    
	 System.out.println(connectionString.substring(connectionString.indexOf('@')+1));
	 
	 System.out.println(File.separatorChar);

	}

}
