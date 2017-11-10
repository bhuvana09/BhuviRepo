package com.cognizant.gto.sea.ccap;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Car {

	private String tyreType;

	private String wheelType;

	private String oilType;

	private String mirrorType;

	private String doorType;

	public String getTyreType() {
		return tyreType;
	}

	public void setTyreType(String tyreType) {
		this.tyreType = tyreType;
	}

	public String getWheelType() {
		return wheelType;
	}

	public void setWheelType(String wheelType) {
		this.wheelType = wheelType;
	}

	public String getOilType() {
		return oilType;
	}

	public void setOilType(String oilType) {
		this.oilType = oilType;
	}

	public String getMirrorType() {
		return mirrorType;
	}

	public void setMirrorType(String mirrorType) {
		this.mirrorType = mirrorType;
	}

	public String getDoorType() {
		return doorType;
	}

	public void setDoorType(String doorType) {
		this.doorType = doorType;
	}

	public void openFile() throws IOException {
		FileReader reader = new FileReader("someFile");
		int i = 0;
		while (i != -1) {
			i = reader.read();
			System.out.println((char) i);
		}
		reader.close();
		System.out.println("--- File End ---");
	}

	
	   public void openFile1() {
	        try {
	            // constructor may throw FileNotFoundException
	            FileReader reader = new FileReader("someFile");
	            int i=0;
	            while(i != -1){
	                //reader.read() may throw IOException
	                i = reader.read();
	                System.out.println((char) i );
	            }
	            reader.close();
	            System.out.println("--- File End ---");
	        } catch (FileNotFoundException e) {
	            //do something clever with the exception
	        } catch (IOException e) {
	            //do something clever with the exception
	        } catch (Exception e) {
	            //do something clever with the exception
	        }
	    }
	   


}
