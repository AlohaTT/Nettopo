package org.deri.nettopo.mobility.models;

import java.io.Serializable;

import org.deri.nettopo.util.FormatVerifier;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Waypoint;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.mobility.scenario.Scenario;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.node.MobileNode;

/**
 * RandomWaypoint model.
 * To generate the mobility of node in 3 kinds of dimension
 * @author Lei Shu, Yuanbo Ha
 */

public class RandomWaypoint extends Scenario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MODEL_NAME = "RandomWaypoint";
	private static final String[] AttrNames = {"minSpeed","maxSpeed","maxPause","dimension"};
	private static final String[] HelpInfo = {
		"positive float, less than " + AttrNames[1],
		"positive float, more than " + AttrNames[0],
		"positive float",
		"positive integer between 1 and 3"
	};
	private static final String[] ErrorInfo = {
		" should be less than ",
		" should be more than ",
		" should be positive float ",
		" should be integer between "
	};
	private double minspeed = 0.5;
	private double maxspeed = 1.5;
	private double maxpause = 60.0;
	private int dim = 3;
	private String[] attrs = null;
	private String errorMessage = null;

	public RandomWaypoint(){
		super();
		this.dim = 3;
		this.minspeed = 0.5;
		this.maxspeed = 1.5;
		this.maxpause = 60.0;
		
		this.attrs = new String[AttrNames.length];
		this.errorMessage = "";
	}
	
	public RandomWaypoint(double minspeed, double maxspeed, double maxpause, int dim) {
		super();
		this.dim = dim;
		this.minspeed = minspeed;
		this.maxspeed = maxspeed;
		this.maxpause = maxpause;
		
		this.attrs = new String[AttrNames.length];
		this.errorMessage = "";
	}
	
	public RandomWaypoint(double x, double y, int nodes, double duration, double ignore, long randomSeed, 
			double minspeed, double maxspeed, double maxpause, int dim) {
		super(x, y, nodes, duration, ignore, randomSeed);
		this.dim = dim;
		this.minspeed = minspeed;
		this.maxspeed = maxspeed;
		this.maxpause = maxpause;
		
		this.attrs = new String[AttrNames.length];
		this.errorMessage = "";
	}
	
	public void generatePath(){
		preGeneration();
		generate();
		postGeneration();
	}
	
	public void generate() {
		double simulationDuration = this.getDuration();
		for (int i = 0; i < this.getNodes().length; i++) {
			MobileNode currentNode = this.getNode(i);
			double t = 0.0;
			Coordinate src = null;
			Coordinate dst = null;
			NetTopoApp app = this.getApp();
			synchronized (app){
				int id = currentNode.getID();
				int x = app.getNetwork().getCoordianteByID(id).x;
				int y = app.getNetwork().getCoordianteByID(id).y;
				src = new Coordinate(x,y);
				currentNode.add(t, src);
			}

			while (t < simulationDuration) {
				switch (dim) {
					case 1 :
						dst = randomNextCoordinate(-1., src.y);
						break;
					case 2 :
						switch ((int) (randomNextDouble() * 2.0)) {
							case 0 :
								dst = randomNextCoordinate(-1., src.y);
								break;
							case 1 :
								dst = randomNextCoordinate(src.x, -1.);
								break;
							default :
								throw new RuntimeException(
									MODEL_NAME + ".generate(): This is impossible - how can (int)(randomNextDouble() * 2.0) be something other than 0 or 1?!");
						}
						break;
					case 3 :
						dst = randomNextCoordinate();
						break;
					default :
						throw new RuntimeException(MODEL_NAME + ".generate(): dimension may only be of value 1, 2 or 3.");
				}
				double speed = (maxspeed - minspeed) * randomNextDouble() + minspeed;
				t += src.distance(dst) / speed;
				if (!currentNode.add(t, dst))
					throw new RuntimeException(MODEL_NAME + ".generate(): error while adding waypoint");
				if ((t < simulationDuration) && (maxpause > 0.0)) {
					double pause = maxpause * randomNextDouble();
					t += pause;
				}
				src = dst;
			}
		}
	}

	/**
	 * remove the last time position if its time exceed the duration;
	 * and node[i].(duration, p);
	 * and super.postGeneration();
	 */
	protected void postGeneration() {
		for ( int i = 0; i < this.getNodes().length; i++ ) {
			MobileNode currentNode = this.getNode(i);
			Waypoint last = currentNode.lastElement();
			if (last.time > this.getDuration()) {
				Coordinate p = last.pos;
				currentNode.removeLastElement();
				currentNode.add(this.getDuration(), p);
			}
		}
		super.postGeneration();
	}
	
	public static String[] getAttrsNames(){
		return Util.stringArrayConcat(Scenario.getAttrsNames(),AttrNames);
	}

	public String[] getAttrs(){
		return Util.stringArrayConcat(super.getAttrs(), this.attrs);
	} 
	
	public boolean setAttrs(String key, String value){
		boolean isValid = false;
		errorMessage = null;
		if(key.equals(AttrNames[0])){
			if(FormatVerifier.isPositiveFloat(value)){
				double tempSpeed = Double.parseDouble(value); 
				if(attrs[1] != null){
					if(tempSpeed <= Double.parseDouble(attrs[1])){
						this.attrs[0] = value;
						minspeed =  tempSpeed;
						isValid = true;
					}else{
						isValid = false;
						errorMessage = AttrNames[0] + ErrorInfo[0] + AttrNames[1];
					}
				}else{
					this.attrs[0] = value;
					minspeed =  tempSpeed;
					isValid = true;
				}
			}else{
				errorMessage = ErrorInfo[2];
				isValid = false;
			}
		}else if(key.equals(AttrNames[1])){
			if(FormatVerifier.isPositiveFloat(value)){
				double tempSpeed = Double.parseDouble(value); 
				if(attrs[0] != null){
					if(tempSpeed > Double.parseDouble(attrs[0])){
						this.attrs[1] = value;
						maxspeed =  tempSpeed;
						isValid = true;
					}else{
						isValid = false;
						errorMessage =  AttrNames[1] + ErrorInfo[1] + AttrNames[0];
					}
				}else{
					this.attrs[1] = value;
					maxspeed =  tempSpeed;
					isValid = true;
				}
			}else{
				errorMessage = ErrorInfo[2];
				isValid = false;
			}
		}else if(key.equals(AttrNames[2])){
			if (FormatVerifier.isPositiveFloat(value)) {
				this.attrs[2] = value;
				maxpause = Double.parseDouble(value);
				isValid = true;
			}else{
				errorMessage = ErrorInfo[2];
				isValid = false;
			}
		}else if(key.equals(AttrNames[3])){
			if (FormatVerifier.isPositiveInteger(value) && FormatVerifier.isInRange(value, 1, 4)) {
				this.attrs[3] = value;
				dim = Integer.parseInt(value);
				isValid = true;
			}else{
				errorMessage = ErrorInfo[3] + "1 and 3";
				isValid = false;
			}
		}else{
			return super.setAttrs(key, value);
		}
		return isValid;
	}
	
	public String getErrorMessage() {
		if(errorMessage != null){
			return errorMessage;
		}else{
			return super.getErrorMessage();
		}
	}

	public void setModelName(String name) {
		super.setModelName(name);
	}

	/**
	 * The original time and path of the nodes
	 * @return
	 */
	public double[][] getChangedTimeAndPath(){
		double[][] timeAndPositionOfMobileNodes = new double[this.getNodes().length][];
		for(int i=0;i<this.getNodes().length;i++){
			timeAndPositionOfMobileNodes[i] = this.getNode(i).toOriginalDoubleArray_T_P();
		}
		return timeAndPositionOfMobileNodes;
	}
	
	/**
	 * The first element of every line of the return array is the nodeID 
	 * @param start 
	 * 		  the start time of the path you want to get.
	 * 	      if you just want to see the whole path, set start=0
	 * @param duration the duration of the path you want to get
	 * 		  if you just want to see the whole path, set the duration = this.getDuration(); 
	 * @param interval 
	 * 		  this is to get the path every interval time.
	 * 		  that is to say to process the path and divide the path in every interval time. 
	 * @return
	 */
	public double[][] getChangedPath(double start, double duration, double interval){
		if(start < 0){
			System.out.println("Warning: The start time you choose is invalid.");
			System.exit(0);
		}else if(start + duration > this.getDuration()){
			System.out.println("Warning: The start time add the duration is longer than the real duration time.");
			System.exit(1);
		}else if(interval <= 0){
			System.out.println("Warning: The interval should be longer than 0.");
			System.exit(2);
		}
		double[][] positionOfMobileNodes = new double[this.getNodes().length][];
		for(int i=0;i<this.getNodes().length;i++){
			double[] temp = this.getNode(i).toProcessedDoubleArray_P(start, duration, interval);
			positionOfMobileNodes[i] = new double[temp.length + 1];
			positionOfMobileNodes[i][0] = this.getNode(i).getID();
			System.arraycopy(temp, 0, positionOfMobileNodes[i], 1, temp.length);
		}
		return positionOfMobileNodes;
	}

	public static String[] getHelpInfo() {
		return Util.stringArrayConcat(Scenario.getHelpInfo(), HelpInfo);
	}

	
	public static String[] getErrorInfo() {
		return Util.stringArrayConcat(Scenario.getErrorInfo(), ErrorInfo);
	}
}