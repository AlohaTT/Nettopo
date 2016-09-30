package org.deri.nettopo.test;

import java.util.ArrayList;

import org.deri.nettopo.xml.ReadXML;

public class TestReadXML {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//ReadXML.createAllTasks();
		ArrayList<String> filename=ReadXML.getAllTasksName();
		System.out.println(filename.toString());
	}

}
