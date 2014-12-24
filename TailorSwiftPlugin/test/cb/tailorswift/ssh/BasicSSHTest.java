package cb.tailorswift.ssh;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicSSHTest {

	
	
	protected void createFile(String content, String filePath) throws IOException{
		FileWriter writer = new FileWriter(filePath);
		writer.write(content);
		writer.close();
	}
	protected List<String> getConnectionInfo() throws IOException {


		String fileName = ".cbproperties";
		return getConnnectionInfo(fileName);


	}
	protected List<String> getLocalConnectionInfo() throws IOException {


		String fileName = ".localproperties";
		return getConnnectionInfo(fileName);


	}

	protected List<String> getConnnectionInfo(String fileName)
			throws IOException {
		Path file = Paths.get(fileName);
		BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset());
		java.util.List<String> elements = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			elements.add(line);
		}
		return   elements;
	}

}
