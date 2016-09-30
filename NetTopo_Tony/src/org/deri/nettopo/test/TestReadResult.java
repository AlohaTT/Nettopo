package org.deri.nettopo.test;

import java.io.IOException;
import java.util.List;

import org.deri.nettopo.util.ReadResult;

public class TestReadResult {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ReadResult rr=new ReadResult();
		List<Integer> path=rr.getOnePath("task1");
		System.out.println(path.toString());
	}
}
