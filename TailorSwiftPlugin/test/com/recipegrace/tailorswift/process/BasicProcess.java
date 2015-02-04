package com.recipegrace.tailorswift.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class BasicProcess {

	protected void executeCommand(String[] command) throws IOException {
		
	Runtime r = Runtime.getRuntime();
		
		Process p = r.exec(command, new String[]{});
				//p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader b1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line = "";
		while ((line = b.readLine()) != null) {
			System.out.println(line);
		}
		while ((line = b1.readLine()) != null) {
			System.err.println(line);
		}

		b.close();
		b1.close();
		
	}
}
