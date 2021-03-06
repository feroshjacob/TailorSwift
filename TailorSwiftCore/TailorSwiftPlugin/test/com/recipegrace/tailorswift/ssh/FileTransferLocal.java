package com.recipegrace.tailorswift.ssh;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.recipegrace.tailorswift.ssh.FileTransfer;

public class FileTransferLocal extends BasicSSHTest {

	@Test
	public void testServerToAndFrom()  {

		try {
			String localFilePath = ".tests/test";
			String testingContent = "testing";
			createFile(testingContent, localFilePath);
			List<String> userInfo= getLocalConnectionInfo();
			String userName =userInfo.get(0);
			String hostName =userInfo.get(1);
			String passWord =userInfo.get(2);
			FileTransfer ft1 = new FileTransfer(userName, hostName, passWord);
		//	String localFilePath = ".tests/test";
			String serverFilePath = "test";
			ft1.transferToServer(new File(localFilePath), serverFilePath);
			FileTransfer ft2 = new FileTransfer(userName, hostName, passWord);
			ft2.transferFromServer(serverFilePath,localFilePath);
			List<String> lines =getAllLines(localFilePath);
			assertEquals(lines.size(),1);
			assertEquals(lines.get(0),testingContent);
			//transferFile( new File(".project"), "project");
		} catch (IOException | JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
