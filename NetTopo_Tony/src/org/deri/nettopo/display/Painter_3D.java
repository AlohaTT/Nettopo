package org.deri.nettopo.display;

import java.util.List;
import java.util.Vector;

import org.deri.nettopo.util.AttractorField;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DistrictedArea;
import org.eclipse.swt.graphics.RGB;

public class Painter_3D implements Painter {

	public void paintAllNodes() {
		// TODO Auto-generated method stub
		
	}

	
	public void paintConnection(int id1, int id2, RGB rgb) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintConnection(int id1, int id2) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintMobileNodes(double[][] path) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintNode(int id, RGB rgb) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintNode(int id) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintNodeFocus(Coordinate c) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintNodeFocus(int id) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintNodes(int[] nodes) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeNode(Coordinate c) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeNode(int id) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeNodeFocus(Coordinate c) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeNodeFocus(int id) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeAllNodeFocus() {
		// TODO Auto-generated method stub
		
	}

	
	public void rePaintAllNodes() {
		// TODO Auto-generated method stub
		
	}

	
	public String toString() {
		String output = "3D has not been created";
		System.out.println(output);
		return output;
	}

	
	public void paintAttractors(AttractorField fields) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintDistrictedArea(DistrictedArea area) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeAttractors(AttractorField fields) {
		// TODO Auto-generated method stub
		
	}

	
	public void removeDistrictedArea(DistrictedArea area) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void removeDistrictedAreas(Vector<DistrictedArea> areas) {
		// TODO Auto-generated method stub
		
	}

	
	public void paintDistrictedAreas(Vector<DistrictedArea> areas) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintRec(Coordinate start, Coordinate end) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintCircle(Coordinate c1, String val) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void paintArea(List<int[]> p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintGas(int id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintGas(int id, RGB rgb) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintMobileGas(double[][] path) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintGasFocus(int id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintGasFocus(Coordinate c,int gasRadius) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeGas(int id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeGas(Coordinate c) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeGasFocus(int id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeGasFocus(Coordinate c,int gasRadius) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeAllGasFocus() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void rePaintAllGas() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void rePaint() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void rePaintAllNodesWithoutClear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintBoundary()
	{
		
	}

	@Override
	public void paintAllGas() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void paintNodeByCoors(int id,RGB COL) {
		// TODO Auto-generated method stub
		
	}
	
}
