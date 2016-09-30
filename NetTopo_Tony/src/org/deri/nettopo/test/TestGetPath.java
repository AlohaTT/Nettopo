package org.deri.nettopo.test;

import java.io.File;

public class TestGetPath {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		File file=new File("./xml/");
		File[] tasksFile=file.listFiles();
		for(File f:tasksFile)
		{	
			System.out.println(f.getPath());
		}
	}

}
