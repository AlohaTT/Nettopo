package org.deri.nettopo.test;

public class TestSubString {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String filename="tasks.xml";
//		System.out.println(filename.substring(0,filename.indexOf(".")));//substring ÊÇ×ó±ÕºÏÓÒ±ß¿ª[)
//		String taskName="name:task";
//		taskName=taskName.substring(taskName.indexOf(":")+1,taskName.length());
////		System.out.println(taskName);
//		String line="[501, 437, 390, 66, 451, 190, 149, 502]";
//		System.out.println(line.substring(line.indexOf("[")+1,line.indexOf("]")));
		String line="name:./xml/task.xml";
		System.out.println(line.substring(line.indexOf(":")+7,line.length()-4));
	}
}
