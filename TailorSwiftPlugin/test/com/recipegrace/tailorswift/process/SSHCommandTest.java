package com.recipegrace.tailorswift.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class SSHCommandTest extends BasicProcess {

	@Test
	public void test() throws IOException {
		
		String[] command =new String[]{"ssh", "fjacob.site@qtmhgate1.atl.careerbuilder.com","nohup /bin/sh /home/fjacob.site/runOnCB.sh`</dev/null` >nohup.out 2>&1 &"};
		executeCommand(command);
		
	}

}
