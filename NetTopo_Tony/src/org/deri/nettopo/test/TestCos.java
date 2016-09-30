package org.deri.nettopo.test;

import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Util;

public class TestCos {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(Util.getDotMultiple(new Coordinate(338,175,0),new Coordinate(313,182,0), new Coordinate(332,150,0)));//正确

		System.out.println(Util.getDotMultiple(new Coordinate(338,175,0),new Coordinate(313,182,0), new Coordinate(352,159,0)));//错误
		

		System.out.println(Util.angel(new Coordinate(338,175,0),new Coordinate(313,182,0), new Coordinate(332,150,0)));//正确

		System.out.println(Util.angel(new Coordinate(338,175,0),new Coordinate(313,182,0), new Coordinate(352,159,0)));//错误
		
	}

}
