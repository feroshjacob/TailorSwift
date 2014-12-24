package cb.tailorswift.ssh;
/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the file transfer from local to remote.
 *   $ CLASSPATH=.:../build javac ScpTo.java
 *   $ CLASSPATH=.:../build java ScpTo file1 user@remotehost:file2
 * You will be asked passwd. 
 * If everything works fine, a local file 'file1' will copied to
 * 'file2' on 'remotehost'.
 *
 */
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.jcraft.jsch.JSchException;

public class FileTransferTest extends BasicSSHTest{



	@Test
	public void test()  {

		try {
			List<String> userInfo= getConnectionInfo();
			String userName =userInfo.get(0);
			String hostName =userInfo.get(1);
			String passWord =userInfo.get(2);
			FileTransfer ft = new FileTransfer(userName, hostName, passWord);
			ft.transferToServer(new File("/Users/fjacob/dump/runtime-EclipseApplication/WordCount/target/scala-2.10/WordCount-assembly-0.0.1.jar"), "WordCount-assembly-0.0.1.jar");
			//transferFile( new File(".project"), "project");
		} catch (IOException | JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	


}