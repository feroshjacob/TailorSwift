package cb.tailorswift.ssh;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class FileTransfer {
	
	private Session session=null;
	public FileTransfer(String userName, String hostName, String passWord) throws JSchException {
		JSch jsch=new JSch();
		 session=jsch.getSession(userName, hostName, 22);

		// username and password will be given via UserInfo interface.
		UserInfo ui=new NewUserInfo(passWord);
		session.setUserInfo(ui);
		session.connect();
	}
	public void transferFile(File file, String outFile) throws IOException, JSchException {
		FileInputStream fis=null;

		boolean ptimestamp = true;

		// exec 'scp -t rfile' remotely
		String command="scp " + (ptimestamp ? "-p" :"") +" -t "+outFile;
		Channel channel=session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();

		channel.connect();

		if(checkAck(in)!=0){
			return;
		}


		if(ptimestamp){
			command="T "+(file.lastModified()/1000)+" 0";
			// The access time should be sent here,
			// but it is not accessible with JavaAPI ;-<
			command+=(" "+(file.lastModified()/1000)+" 0\n"); 
			out.write(command.getBytes()); out.flush();
			if(checkAck(in)!=0){
				return;
			}
		}

		// send "C0644 filesize filename", where filename should not include '/'
		long filesize=file.length();
		command="C0644 "+filesize+" ";

		command+=file.getName();
		command+="\n";
		out.write(command.getBytes()); out.flush();
		if(checkAck(in)!=0){
			return;
		}

		// send a content of lfile
		fis=new FileInputStream(file);
		byte[] buf=new byte[1024];
		while(true){
			int len=fis.read(buf, 0, buf.length);
			if(len<=0) break;
			out.write(buf, 0, len); //out.flush();
		}
		fis.close();
		fis=null;
		// send '\0'
		buf[0]=0; out.write(buf, 0, 1); out.flush();
		if(checkAck(in)!=0){
			return;
		}
		out.close();

		channel.disconnect();
		session.disconnect();
	}
private	int checkAck(InputStream in) throws IOException{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0) return b;
		if(b==-1) return b;

		if(b==1 || b==2){
			StringBuffer sb=new StringBuffer();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			}
			while(c!='\n');
			if(b==1){ // error
				System.out.print(sb.toString());
			}
			if(b==2){ // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}


}
