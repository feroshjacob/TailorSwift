package com.recipegrace.tailorswift.ssh;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.recipegrace.tailorswift.ssh.SSHCommand;

public class SSHCommandTest extends BasicSSHTest {

	
	@Test
	public void test() throws IOException, JSchException {
		List<String> userInfo= getConnectionInfo();
		String userName =userInfo.get(0);
		String hostName =userInfo.get(1);
		String passsWord =userInfo.get(2);
		

	//	SSHCommand sshCommand2 = new SSHCommand(userName,hostName, passsWord);
	//	   boolean ptimestamp = true;

		      // exec 'scp -t rfile' remotely
	//    String command="scp " + (ptimestamp ? "-p" :"") +" -t "+".cbproperties";
	 //   sshCommand2.execute(command);
		SSHCommand sshCommand = new SSHCommand(userName,hostName, passsWord);
		sshCommand.execute("/bin/sh runOnCB.sh");
		sshCommand.execute("ls");
	}

}
