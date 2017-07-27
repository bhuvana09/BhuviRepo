package com.cognizant.ccap.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;


public class SampleProject {

	private static final Logger LOGGER = Logger.getLogger("com.cognizant.ccap.testing.SampleProject");

	void bar(){
		System.out.println("new issue1");
		System.out.println("new changeset");
		System.out.println("new changeset1");
		String zzz1 = "asdfge";
		System.out.println("new changeset");
		System.out.println("new issue1");
		System.out.println("new changeset");
		System.out.println("new changeset");
		System.out.println("new issue1");
		System.out.println("new changeset");
		LOGGER.info("new changeset1");
		String zzz2 = "asdfge";
		LOGGER.info("new changeset");
		String fileName = null;
		if (zzz1 != null) {
			fileName = zzz1;
		}
	}

	void bazzz(String fileName) throws FileNotFoundException,IOException{
		// Read the file and write some text into the file.
					File file = new File(fileName);
					OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
					writer.write("Hello World");
					writer.close();
				//	secondMethod();
	}

	public static void main(String[] args) throws Throwable, Exception,IOException {
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
	
	void secondMethod() throws ArrayIndexOutOfBoundsException {
		throw new ArrayIndexOutOfBoundsException ("zdgff");
	}



}