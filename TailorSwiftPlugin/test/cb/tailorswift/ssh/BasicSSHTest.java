package cb.tailorswift.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicSSHTest {

	protected List<String> getConnectionInfo() throws IOException {


		Path file = Paths.get(".cbproperties");
		BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset());
		java.util.List<String> elements = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			elements.add(line);
		}
		return   elements;


	}

}
