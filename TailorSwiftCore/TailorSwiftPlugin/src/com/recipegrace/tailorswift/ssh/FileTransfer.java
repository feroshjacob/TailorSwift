package com.recipegrace.tailorswift.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/*
 *  A lot of code taken  from  Jsch 
 * 
 */
public class FileTransfer {

	private Session session = null;

	public FileTransfer(String userName, String hostName, String passWord)
			throws JSchException {
		JSch jsch = new JSch();
		session = jsch.getSession(userName, hostName, 22);

		// username and password will be given via UserInfo interface.
		UserInfo ui = new NewUserInfo(passWord);
		session.setUserInfo(ui);
		session.connect();
	}

	public File transferFromServer(String serverPath, String localFilePath)
			throws IOException, JSchException {
		FileOutputStream fos = null;
		String rfile = serverPath;
		String lfile = localFilePath;
		String command = "scp -f " + rfile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();

		channel.connect();

		byte[] buf = new byte[1024];

		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();

		while (true) {
			int c = checkAck(in);
			if (c != 'C') {
				break;
			}

			// read '0644 '
			in.read(buf, 0, 5);

			long filesize = 0L;
			while (true) {
				if (in.read(buf, 0, 1) < 0) {
					// error
					break;
				}
				if (buf[0] == ' ')
					break;
				filesize = filesize * 10L + (long) (buf[0] - '0');
			}

			@SuppressWarnings("unused")
			String file = null;
			for (int i = 0;; i++) {
				in.read(buf, i, 1);
				if (buf[i] == (byte) 0x0a) {
					file = new String(buf, 0, i);
					break;
				}
			}

			// System.out.println("filesize="+filesize+", file="+file);

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			// read a content of lfile
			fos = new FileOutputStream(lfile);
			int foo;
			while (true) {
				if (buf.length < filesize)
					foo = buf.length;
				else
					foo = (int) filesize;
				foo = in.read(buf, 0, foo);
				if (foo < 0) {
					// error
					break;
				}
				fos.write(buf, 0, foo);
				filesize -= foo;
				if (filesize == 0L)
					break;
			}
			fos.close();
			fos = null;

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
		}
		// new File()
		channel.disconnect();
		session.disconnect();
		return new File(lfile);

	}

	public void transferToServer(File file, String outFile) throws IOException,
	JSchException {
		transferToServer(file, outFile, null);
	}
	public void transferToServer(File file, String outFile, IProgressMonitor monitor) throws IOException,
			JSchException {
		FileInputStream fis = null;

		boolean ptimestamp = true;
		long filesize = file.length();

		byte[] buf = new byte[1024];
		int blocks = (int)(filesize + buf.length-1)/ buf.length;
		
		if(monitor!=null)
		monitor.beginTask("Uploading file: "+ file.getName() ,blocks+2);
		
		// exec 'scp -t rfile' remotely
		String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + outFile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();

		channel.connect();

		if (checkAck(in) != 0) {
			return;
		}
		

		if (ptimestamp) {
			command = "T " + (file.lastModified() / 1000) + " 0";
			// The access time should be sent here,
			// but it is not accessible with JavaAPI ;-<
			command += (" " + (file.lastModified() / 1000) + " 0\n");
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				return;
			}
		}
		if(monitor!=null)
			monitor.worked(1);

		// send "C0644 filesize filename", where filename should not include '/'
		
		command = "C0644 " + filesize + " ";

		command += file.getName();
		command += "\n";
		out.write(command.getBytes());
		out.flush();
		if (checkAck(in) != 0) {
			return;
		}

		if(monitor!=null)
			monitor.worked(1);
		
		// send a content of lfile
		fis = new FileInputStream(file);

	  
		while (true) {
			
			int len = fis.read(buf, 0, buf.length);
			if (len <= 0)
				break;
			out.write(buf, 0, len); // out.flush();
			if(monitor!=null)
				monitor.worked(1);
		
		}
		fis.close();
		fis = null;
		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();
		if (checkAck(in) != 0) {
			return;
		}
		out.close();

		channel.disconnect();
		session.disconnect();
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

}
