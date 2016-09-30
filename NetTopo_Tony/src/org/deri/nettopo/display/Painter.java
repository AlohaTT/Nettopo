package org.deri.nettopo.display;

import java.util.List;
import java.util.Vector;

import org.deri.nettopo.util.AttractorField;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DistrictedArea;
import org.eclipse.swt.graphics.RGB;

public interface Painter {
	public void paintNode(int id);
	public void paintNode(int id, RGB rgb);
	/**
	 * paint the nodes' path
	 * @param path the first element of every single array is the node id
	 * 		  	   Then the followings are the x and y of every position.
	 */
	public void paintMobileNodes(double[][] path);
	public void removeNode(int id);
	public void removeNode(Coordinate c);
	public void paintNodes(int[] nodes);
	public void paintAllNodes();
	public void rePaintAllNodes();
	public void paintNodeFocus(int id);
	public void paintNodeFocus(Coordinate c);
	public void removeNodeFocus(int id);
	public void removeNodeFocus(Coordinate c);
	public void removeAllNodeFocus();
	public void paintConnection(int id1, int id2);
	public void paintConnection(int id1, int id2, RGB rgb);
	public void paintAttractors(AttractorField fields);
	public void removeAttractors(AttractorField fields);
	public void paintDistrictedArea(DistrictedArea area);
	public void paintDistrictedAreas(Vector<DistrictedArea> areas);
	public void removeDistrictedArea(DistrictedArea area);
	public void removeDistrictedAreas(Vector<DistrictedArea> areas);
	
	public void paintRec(Coordinate start,Coordinate end);
	public void paintCircle(Coordinate c1,String val);
	
	
	public void paintArea(List<int[]> p);
	public void paintGas(int id);
	public void paintGas(int id, RGB rgb);
	public void paintMobileGas(double[][] path);
	public void paintGasFocus(int id);
	public void paintGasFocus(Coordinate c,int gasRadius);
	public void paintAllGas();
	public void removeGas(int id);
	public void removeGas(Coordinate c);
	public void removeGasFocus(int id);
	public void removeGasFocus(Coordinate c,int gasRadius);
	public void removeAllGasFocus();
	public void rePaintAllGas();
	public void rePaint();
	
	public void rePaintAllNodesWithoutClear();
	
	public void paintBoundary();
	
	public void paintNodeByCoors(int id,RGB COL);
	
}
