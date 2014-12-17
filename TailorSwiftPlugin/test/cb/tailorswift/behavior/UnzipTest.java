package cb.tailorswift.behavior;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import cb.tailorswift.behviour.UnZip;
import cb.tailorswift.behviour.WebScaldingProjectSupport;

public class UnzipTest {

	@Test
	public void test() throws FileNotFoundException, IOException {
		InputStream is= WebScaldingProjectSupport.class.getClassLoader().getResourceAsStream("jobtemplate.zip");
		new UnZip().unZipIt(is, "/Users/fjacob/dump/test");

	}

} 
 