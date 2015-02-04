package com.recipegrace.tailorswift.process;

import java.io.IOException;

import org.junit.Test;

public class SBTLaunchTest extends  BasicProcess {

	@Test
	public void test() throws IOException {
	
		
		String[] command =new String[]{"java", "-jar","/Users/fjacob/sbt/bin/sbt-launch.jar", "-Dsbt.log.noformat=true","test"};
		executeCommand(command);

	}

}
