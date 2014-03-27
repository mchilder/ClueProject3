// Chris Butler
// Ben Sattelberg
package clueGame;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class BadConfigFormatException extends RuntimeException {
	// We're not sure what this is, but Eclipse really, really wants us to have it.
	// I trust Eclipse... on this.
	private static final long serialVersionUID = 6823240926991539462L;
	private final String LOG_FILENAME = "log.txt";
	
	public BadConfigFormatException(String msg) {
		super(msg);
		log(msg);
	}
	
	private void log(String msg) {
		FileWriter reader = null;
		try {
			// Every one of those lines can throw exceptions.
			reader = new FileWriter(LOG_FILENAME);
			reader.write(msg);
			reader.close();
		} catch (FileNotFoundException e) {
			// Yo dawg...
		} catch (IOException e) {
			
		}
	}
}
