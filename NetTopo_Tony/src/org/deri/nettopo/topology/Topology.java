package org.deri.nettopo.topology;

import org.deri.nettopo.util.*;


public interface Topology {
	public String[] getArgNames();
	public boolean setArgValue(String argName, String argValue);
	public String getArgErrorDescription();
	public Coordinate[] getCoordinates();
	public String getArgValue(String argName);
}
