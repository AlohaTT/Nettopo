package org.deri.nettopo.topology.simpletopo;

import org.deri.nettopo.topology.Topology;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.FormatVerifier;

public class Topo_Arangement implements Topology{

	String[] names={"x","y","z"};
	Coordinate coor = null;
	String errorMsg = null;
	@Override
	public String[] getArgNames() {
		// TODO Auto-generated method stub
		return names;
	}

	@Override
	public boolean setArgValue(String name, String value) {
		// TODO Auto-generated method stub
		if(coor==null)
			coor = new Coordinate();
		boolean isArgValid = true;
		if(name.equals(names[0])){
			if(FormatVerifier.isPositive(value)){
				coor.x = Integer.parseInt(value);
			}else{
				errorMsg = "x must be a non-negative integer";
				isArgValid = false;
			}
		}else if (name.equals(names[1])){
			if(FormatVerifier.isNotNegative(value)){
				coor.y = Integer.parseInt(value);
			}else{
				errorMsg = "y must be a non-negative integer";
				isArgValid = false;
			}
		}else if (name.equals(names[2])){
			if(FormatVerifier.isNotNegative(value)){
				coor.z = Integer.parseInt(value);
			}else{
				errorMsg = "z must be a non-negative integer";
				isArgValid = false;
			}
		}
		else{
			errorMsg = "No such argument";
			isArgValid = false;
		}
		return isArgValid;
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
		coordinates[0] = coor;
		return coordinates;
	}
	@Override
	public String getArgValue(String name) {
		if(coor==null)
			return null;
		if(name.equals(names[0]))
			return coor.x+"";
		else if(name.equals(names[1]))
			return coor.y+"";
		else if(name.equals(names[2]))
			return coor.z+"";
		else return null;
	}

}
