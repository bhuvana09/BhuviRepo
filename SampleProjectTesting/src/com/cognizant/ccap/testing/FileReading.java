package com.cognizant.ccap.testing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class FileReading {

	private static final Logger LOGGER = Logger.getLogger("com.cognizant.ccap.testing.FileReading");

	void bar() throws FileNotFoundException, IOException{
		try{
		SampleProject prj=new SampleProject();
		String zzz1 = "testdatumuungfhfg";
		String fileName = null;
		prj.bazzz(fileName);
		secondMethod();
		if (zzz1 != null) {
			fileName = zzz1;
		}
		}
		catch(IOException x){
			LOGGER.info(x.getMessage());
			x.printStackTrace();
		}
	}

	void baz(String fileName) throws IOException, FileNotFoundException{
		try{
		// Read the file and write some text into the file.
					File file = new File(fileName);
					OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
					writer.write("Hello World");
					writer.close();
					secondMethod();
		}
		catch(IOException x){
		System.out.println(x.getMessage());
			x.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, FileNotFoundException, IOException {
		String fileName = null;

		if (args != null && args.length > 0) {
			fileName = args[0];
		}
			// Read the file and write some text into the file.
			File file = new File(fileName);
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
			writer.write("Hello World");
			writer.close();
	}
	
	void secondMethod() throws ArrayIndexOutOfBoundsException, FileNotFoundException, IOException {
		throw new ArrayIndexOutOfBoundsException ("zdgff");
	}
}
