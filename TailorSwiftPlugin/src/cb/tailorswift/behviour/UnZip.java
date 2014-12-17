package cb.tailorswift.behviour;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
	List<String> fileList;

	/**
	 * Unzip it
	 * 
	 * @param zipFile
	 *            input zip file
	 * @param output
	 *            zip file output folder
	 *            http://www.mkyong.com/java/how-to-decompress
	 *            -files-from-a-zip-file/
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void unZipIt(InputStream theFile, String dest) throws FileNotFoundException, IOException {

		// buffer for read and write data to file
		byte[] buffer = new byte[2048];
        
		try {
			ZipInputStream zipInput = new ZipInputStream(theFile);
            
			ZipEntry entry = zipInput.getNextEntry();
            
			while(entry != null){
				String entryName = entry.getName();
				File file = new File(dest + File.separator + entryName);
                
				System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());
                
				// create the directories of the zip directory
				if(entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());
					if(!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if(success == false) {
							System.out.println("Problem creating Folder");
						}
					}
                }
				else {
					FileOutputStream fOutput = new FileOutputStream(file);
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						// write 'count' bytes to the file output stream
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				// close ZipEntry and take the next one
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}
            
			// close the last ZipEntry
			zipInput.closeEntry();
            
			zipInput.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}