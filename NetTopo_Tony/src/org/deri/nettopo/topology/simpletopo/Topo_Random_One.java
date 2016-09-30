package org.deri.nettopo.topology.simpletopo;

import java.util.Random;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.topology.Topology;
import org.deri.nettopo.util.Coordinate;

public class Topo_Random_One implements Topology{

	
	
	@Override
	public String[] getArgNames() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	@Override
	public boolean setArgValue(String argName, String argValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getArgErrorDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinate[] getCoordinates() {
		// TODO Auto-generated method stub
		Coordinate[] coordinates = new Coordinate[1];
		Coordinate displaySize = NetTopoApp.getApp().getNetwork().getSize();
		Random random = new Random(System.currentTimeMillis());
		coordinates[0] = new Coordinate(random.nextInt(displaySize.x),random.nextInt(displaySize.y),0);
		return coordinates;
	}

	@Override
	public String getArgValue(String argName) {
		// TODO Auto-generated method stub
		return null;
	}

}
