package org.deri.nettopo.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;


public class AttractorField  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	class Attractor {
		public final Coordinate pos;
		public final double level;
		public final double stdDev1;
		public final double stdDev2;

		public Attractor(Coordinate pos, double level, double stdDev) {
			this.pos = pos;
			this.level = level;
			this.stdDev1 = stdDev;
			this.stdDev2 = stdDev;
		}

		public Attractor(Coordinate pos, double level, double stdDev1, double stdDev2) {
			this.pos = pos;
			this.level = level;
			this.stdDev1 = stdDev1;
			this.stdDev2 = stdDev2;
		}

		public Coordinate getCoordinate() {
			return pos;
		}	        
	}

	protected Vector<Attractor> attractors = new Vector<Attractor>();
	/** Sum over all level-values. */
	protected double lTotal = 0.0;

	public final double x;
	public final double y;

	public AttractorField(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void add(Coordinate attractor, double level, double stdDev) {
		attractors.addElement(new Attractor(attractor, level, stdDev));
		lTotal += level;
	}

	public void add(Coordinate attractor, double level, double stdDev, double stdDev2) {
		attractors.addElement(new Attractor(attractor, level, stdDev, stdDev2));
		lTotal += level;
	}

	public void add(double[] param) {
		for (int p = 0; p < param.length; p += 5)
			if (param.length - p >= 5)
				add(new Coordinate(param[p], param[p+1]), param[p+2], param[p+3], param[p+4]);
			else
				System.out.println("warning: attraction field argument list has wrong number of elements!");
	}

	public Coordinate[] getCoordinates(){
		int size = attractors.size();
		Coordinate[] result = new Coordinate[size];
		int index = 0;
		Iterator<Attractor> iter = attractors.iterator();
		while(iter.hasNext()){
			Attractor attractor = iter.next();
			result[index++] = attractor.getCoordinate();
		}
		
		return result;
	}
	
	public double[] getFields(){
		int size = attractors.size();
		if(size == 0)
			return null;

		double[] attractorsFields = new double[size * 5];
		int index = 0;
		Iterator<Attractor> iter = attractors.iterator();
		while(iter.hasNext()){
			Attractor attractor = iter.next();
			attractorsFields[index++] = attractor.pos.x;
			attractorsFields[index++] = attractor.pos.y;
			attractorsFields[index++] = attractor.level;
			attractorsFields[index++] = attractor.stdDev1;
			attractorsFields[index++] = attractor.stdDev2;
		}
		
		return attractorsFields;
	}
	/**
	 * @param rndUniform  a double number between 0 and 1
	 * @param rndGaussian1 a double number gaussian
	 * @param rndGaussian2 a double number gaussian
	 * @return next Coordinate related to the last attractor of the attractorField
	 */
	public Coordinate getPos(double rndUniform, double rndGaussian1, double rndGaussian2) {
		double r = rndUniform * lTotal;
		double s = 0.0;
		Attractor a = null;
		for(int i=0;(i<attractors.size()) && (r >= s);i++){
			a = (Attractor)attractors.elementAt(i);
			s += a.level;
		}

		if ((r >= s) || (a == null)) {
			System.out.println("AttractorField.getPos: Somethings going wrong here");
			System.exit(0);
		}
		Coordinate rVal = new Coordinate(a.pos.x + rndGaussian1 * a.stdDev1, a.pos.y + rndGaussian2 * a.stdDev2);
		if ((rVal.x >= 0.0) && (rVal.y >= 0.0) && (rVal.x <= x) && (rVal.y <= y))
			return rVal;
		else
			return null;
	}
}
