package com.cognizant.gto.sea.ccap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class User {

	public static void main(String[] args) {
	}

	public void getTypes() throws InterruptedException, IOException {

		Car car = new Car();
		String text = "N/A";
		int intVal = 0;
		String ptr = null;
		FileInputStream fis = null;

		try {

			intVal = Integer.parseInt(text);
			car.openFile();
			car.openFile1();
			FileWriter fw = new FileWriter("D:\\testout.txt");
			fw.write("Welcome to javaTpoint.");
			fw.close();
			fis = new FileInputStream("abc.txt");

			if (ptr.equals("gfg"))
				System.out.print("Same");
			else
				System.out.print("Not Same");

		} catch (NumberFormatException nfe) {
			System.out.print("NumberFormatException Caught");

		}
		catch (Error e) {
			// TODO Auto-generated catch block
			System.out.print("IOException Caught");
		}
		
	
	try {
		
        Thread.sleep(100);
        
			intVal = Integer.parseInt(text);
			FileWriter fw = new FileWriter("D:\\testout.txt");
			fw.write("Welcome to javaTpoint.");
			fw.close();
			if (ptr.equals("gfg"))
				System.out.print("Same");
			else
				System.out.print("Not Same");

		} catch (Error nfe) {
			System.out.print("NumberFormatException Caught");
		} 

	}

	public void getPrice() {

		Car car = new Car();
		String text = "N/A";
		int intVal = 0;
		String ptr = null;
		FileInputStream fis = null;
		try {
			intVal = Integer.parseInt(text);
			car.openFile1();
			if (ptr.equals("gfg"))
				System.out.print("Same");
			else
				System.out.print("Not Same");
			fis = new FileInputStream("abc.txt");
		} catch (NumberFormatException nfe) {
			System.out.print("FileNotFoundException Caught");

		} catch (FileNotFoundException e) {
			System.out.print("FileNotFoundException Caught");
		}

	}

}
