package cb.tailorswift.behavior;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.junit.Test;

public class SSHCommandTest {

	@Test
	public void test() throws IOException {
		
		String[] command =new String[]{"ssh", "fjacob.site@qtmhgate1.atl.careerbuilder.com","nohup /bin/sh /home/fjacob.site/runOnCB.sh`</dev/null` >nohup.out 2>&1 &"};
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
