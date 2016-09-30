package org.deri.nettopo.topology.simpletopo;

import java.util.Random;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.*;
import org.deri.nettopo.topology.Topology;

/**
 * @author wupure & Lei
 * 
 */

public class Topo_TPGFRange implements Topology {

	String[] names = { "Min node numbers", "Max node numbers", "Step" };

	private int maxNodeNumbers;
	private String errorMsg;

	public Topo_TPGFRange() {
	}

	public String[] getArgNames() {
		return names;
	}

	public boolean setArgValue(String name, String value) {
		boolean isArgValid = true;
		if (name.equals(names[0])) {
			if (FormatVerifier.isPositive(value)) {
				Integer.parseInt(value);
			} else {
				errorMsg = "Node number must be a positive integer";
				isArgValid = false;
			}
		} else if (name.equals(names[1])) {
			if (FormatVerifier.isPositive(value)) {
				maxNodeNumbers = Integer.parseInt(value);
			} else {
				errorMsg = "Node number must be a positive integer";
				isArgValid = false;
			}
		} else if (name.equals(names[2])) {
			if (FormatVerifier.isPositive(value)) {
				Integer.parseInt(value);
			} else {
				errorMsg = "Step must be a positive integer";
				isArgValid = false;
			}
		} else {
			errorMsg = "No such argument";
			isArgValid = false;
		}
		return isArgValid;
	}

	public String getArgErrorDescription() {
		return errorMsg;
	}

	public Coordinate[] getCoordinates() {
		Coordinate[] coordinates = new Coordinate[maxNodeNumbers];
		Coordinate displaySize = NetTopoApp.getApp().getNetwork().getSize();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = new Coordinate(random.nextInt(displaySize.x),
					random.nextInt(displaySize.y), 0);

			/*
			 * check if it is duplicate with the previouse generated in the
			 * array
			 */
			for (int j = 0; j < i; j++) {
				if (coordinates[j].equals(coordinates[i])) {
					i--;
					break;
				}
			}

			/*
			 * check if any coordiante is duplicate with already exist ones in
			 * the network
			 */
			if (NetTopoApp.getApp().getNetwork()
					.hasDuplicateCoordinate(coordinates[i])) {
				i--;
			}

		}
		return coordinates;
	}

	@Override
	public String getArgValue(String argName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	/*
	 * public static void main(String[] args) { Topology topo = new Topo_Line();
	 * String[] names = topo.getArgNames(); topo.setArgValue(names[0], "4");
	 * String startC = "80 , 80"; String endC = "0 ,0";
	 * topo.setArgValue(names[1], startC); topo.setArgValue(names[2], endC);
	 * Coordinate[] c = topo.getCoordinates(); if(c!=null){ for(int
	 * i=0;i<c.length;i++){ System.out.println(c[i]); } } }
	 */
}
