package cb.tailorswift.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jcraft.jsch.JSchException;

public class SSHCommandTest {

	private List<String> getConnectionInfo() throws IOException {
		
			  
		        Path file = Paths.get(".cbproperties");
		        BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset());
		        java.util.List<String> elements = new ArrayList<String>();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	elements.add(line);
		        }
		   return   elements;
			

	}
	
	@Test
	public void test() throws IOException, JSchException {
		List<String> userInfo= getConnectionInfo();
		String userName =userInfo.get(0);
		String hostName =userInfo.get(1);
		String passsWord =userInfo.get(2);
		

		SSHCommand sshCommand2 = new SSHCommand(userName,hostName, passsWord);
		   boolean ptimestamp = true;

		      // exec 'scp -t rfile' remotely
	    String command="scp " + (ptimestamp ? "-p" :"") +" -t "+".cbproperties";
	    sshCommand2.execute(command);
		SSHCommand sshCommand = new SSHCommand(userName,hostName, passsWord);
		sshCommand.execute("ls");
	}

}
