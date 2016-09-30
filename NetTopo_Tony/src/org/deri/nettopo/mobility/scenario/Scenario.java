package org.deri.nettopo.mobility.scenario;

import java.io.Serializable;
import java.util.Random;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.MobileNode;
import org.deri.nettopo.util.*;

/**
 * Base class for creating new scenarios.
 * core methods for generate mobile path are:
 * Pre&&PostGeneration(), nextPosition(), setAttr(), cut().
 * @author Lei Shu, Yuanbo Han
 *
 */
public class Scenario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String[] AttrNames = {
		"nodesID","duration","randomSeed",
		"ignorance","attractorFields","districtedAreas"
		};
	private static final String[] HelpInfo = {
		"different positive existed nodeID",
		"positive float",
		"positive integer",
		"positive float",
		"total numbers should be divided by 5",
		"2:nodeID,width; 3:nodeID,width,height; 4:x,y,width,height"
	};
	private static final String[] ErrorInfo={
		" should be positive integer ",
		" should be positive float ",
		" should be different ",
		" should exist ",
		" should be non-negative ",
		" should be divided by ",
		" range should be valid ",
		" should be in the canvas "
	};
	private MobileNode[] node;
	private double x = 600,y=400;
	private double duration = 600.0;
	private double ignore = 3600.0;
	private long randomSeed = System.currentTimeMillis();
	private Random rand;
	private DistrictedArea districtedArea= null;
	private AttractorField aField = null;
	
	private NetTopoApp app = null;
	private String[] attrs = null;
	private String errorMessage = null;
	private String modelName = "";

	/**
	 * default is:
	 * x=600
	 * y=400
	 * n=10
	 * duration = 100
	 * ignore = 3600
	 * randomSeed = System.currentTimeMillis()
	 */
	public Scenario() {
		this(600,400,10,100,3600,System.currentTimeMillis());
	}

	public Scenario(double x, double y, int nodes,double duration, double ignore, long randomSeed) {
		this.x = x;
		this.y = y;
		node = new MobileNode[nodes];
		this.duration = duration;
		this.ignore = ignore;
		rand = new Random(this.randomSeed = randomSeed);
		
		modelName = "";
		app = NetTopoApp.getApp();
		attrs = new String[AttrNames.length]; // node, duration, ignore, randomSeed, aField, districtedArea
		errorMessage = "";
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getIgnore() {
		return ignore;
	}

	public void setIgnore(double ignore) {
		this.ignore = ignore;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public AttractorField getAField() {
		return aField;
	}

	public void setAField(AttractorField field) {
		aField = field;
	}
	
	public NetTopoApp getApp() {
		return app;
	}

	public DistrictedArea getDistrictedArea() {
		return districtedArea;
	}

	public void setDistrictedArea(DistrictedArea districtedArea) {
		this.districtedArea = districtedArea;
	}
	
	public String[] getAttrs() {
		return attrs;
	}

	public void setAttrs(String[] attrs) {
		this.attrs = attrs;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public MobileNode[] getNodes() {
		MobileNode[] r = new MobileNode[node.length];
		System.arraycopy(node, 0, r, 0, node.length);
		return r;
	}
	
	public MobileNode getNode(int n) {
		if (node[n] == null)
			node[n] = new MobileNode();
		return node[n];
	}

	public static String[] getAttrsNames(){
		return AttrNames;
	}
	
	/** Extract a certain time span from the scenario. */
	public void cut(double begin, double end) {
		if ((begin >= 0.0) && (end <= duration) && (begin <= end)) {
			for (int i = 0; i < node.length; i++)
				node[i].cut(begin, end);
			
			duration = end - begin;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Coordinate randomNextCoordinate() {
		return randomNextCoordinate(-1., -1.);
	}

	public Coordinate randomNextCoordinate(double fx, double fy) {
		int centerX = 0, centerY = 0, height = 0, width = 0;
		double rx = 0., ry = 0.;
		int[] area = null;
		int type = 2;
		if(districtedArea != null){
			area = districtedArea.getDistrictedArea();
			type = districtedArea.getType();
		}
		if(area != null){
			centerX = area[0];
			centerY = area[1];
			width = area[2];
			height = area[3];
		}

		Coordinate pos = null;
		if(type == 0){
			do {
				if (aField == null) {
					rx = (fx < 0) ? x * randomNextDouble() : fx;
					ry = (fy < 0) ? y * randomNextDouble() : fy;
				} else {
					pos = aField.getPos(randomNextDouble(), randomNextGaussian(), randomNextGaussian());
					if (pos != null) {
						rx = pos.x;
						ry = pos.y;
					}
				}
			} while (
					( (aField != null) && (pos == null) ) || 
					(districtedArea.getType() == 0 && !((rx - centerX) * (rx - centerX) * height * height + (ry - centerY) * (ry - centerY) * width * width < width * width * height * height) )
					);
		}else if(type == 1){
			do {
				if (aField == null) {
					rx = (fx < 0) ? x * randomNextDouble() : fx;
					ry = (fy < 0) ? y * randomNextDouble() : fy;
				} else {
					pos = aField.getPos(randomNextDouble(), randomNextGaussian(), randomNextGaussian());
					if (pos != null) {
						rx = pos.x;
						ry = pos.y;
					}
				}
			} while (
					((aField != null) && (pos == null)) || 
					(districtedArea.getType() == 1 && !(rx >= (centerX-width) && rx <= (centerX+width) && ry >= (centerY-height) && ry <= (centerY+height)) )
					);
		}else{
			do {
				if (aField == null) {
					rx = (fx < 0) ? x * randomNextDouble() : fx;
					ry = (fy < 0) ? y * randomNextDouble() : fy;
				} else {
					pos = aField.getPos(randomNextDouble(), randomNextGaussian(), randomNextGaussian());
					if (pos != null) {
						rx = pos.x;
						ry = pos.y;
					}
				}
			} while ((aField != null) && (pos == null));
			
		}
		if (pos == null)
			pos =new Coordinate(rx, ry);
		
		return pos;
	}
	
	/** Called by subclasses after they generate node movements. */
	protected void postGeneration() {
		if (ignore < 600.0)
			System.out.println("warning: setting the initial phase to be cut off to be too short may result in very weird scenarios");
		if (ignore > 0.0)
			cut(ignore, duration);
	}

	/** Called by subclasses before they generate node movements. */
	protected void preGeneration() {
		this.setDuration(duration + ignore);
		this.setRand(new Random(randomSeed));
	}

	/**
	 * Returns random double form the RandomSeed.
	 * @return double
	 */
	protected double randomNextDouble() {
		return rand.nextDouble(); 
	}

	/**
	 * Returns random Gaussian form the RandomSeed
	 * @return double
	 */
	protected double randomNextGaussian() {
		return rand.nextGaussian();
	}

	/**
	 * validate if the input parameters are correct
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setAttrs(String key, String value){
		boolean isValid = false;
		errorMessage = "";
		if(key.equals(AttrNames[0])){
			if("ALL".equals(value.trim())){
				int[] nodeIDs = this.getCurrentIDs();
				node = new MobileNode[nodeIDs.length];
				setAppMobileNodeNum(nodeIDs.length);
				for(int i=0;i<node.length;i++){
					node[i] = new MobileNode(this.getApp().getNetwork().getNodeByID(nodeIDs[i]));
				}
				this.attrs[0]= value;
				isValid = true;
				errorMessage = "";
			}else{
				if(!Util.string2PositiveIntArrayBoolean(value)){
					if(Util.isStringRange(value.trim())){
						int[] array = Util.stringRange2IntArray(value);
						int[] IDs = getCurrentIDs();
						if(Util.isIntegerArrayInIntegerArray(array, IDs)){
							node = new MobileNode[array.length];
							for(int i=0;i<node.length;i++){
								node[i] = new MobileNode(this.getApp().getNetwork().getNodeByID(array[i]));
							}
							setAppMobileNodeNum(array.length);
							this.attrs[0]= value;
							isValid = true;
							errorMessage = "";
						}else{
							isValid = false;
							errorMessage = ErrorInfo[6] + "or" + ErrorInfo[3];
						}
					}else{
						isValid = false;
						errorMessage = ErrorInfo[6] + "or" + ErrorInfo[0];
					}
				}else{
					int[] nodeIDs1 = Util.string2IntArray(value);
					int[] nodeIDs2 = this.getCurrentIDs();
					if(Util.isIntegerArrayInIntegerArray(nodeIDs1, nodeIDs2)){
						if(!Util.isDuplicatedIntegerArray(nodeIDs1)){
							node = new MobileNode[nodeIDs1.length];
							for(int i=0;i<node.length;i++){
								node[i] = new MobileNode(this.getApp().getNetwork().getNodeByID(nodeIDs1[i]));
							}
							this.setAppMobileNodeNum(nodeIDs1.length);
							this.attrs[0]= value;
							isValid = true;
							errorMessage = "";
						}else{
							isValid = false;
							errorMessage = ErrorInfo[2];
						}
					}else{
						isValid = false;
						errorMessage = ErrorInfo[3];
					}
				}
			}
		}else if(key.equals(AttrNames[1])){
			if (FormatVerifier.isPositiveFloat(value)) {
				this.duration = Double.parseDouble(value);
				this.attrs[1] = value;
				isValid = true;
				errorMessage = "";
			}else{
				errorMessage = ErrorInfo[1];
			}
		}else if(key.equals(AttrNames[2])){
			if (FormatVerifier.isNotNegative(value) && FormatVerifier.isLong(value)) {
				this.randomSeed = Long.parseLong(value);
				rand = new Random(this.randomSeed);
				this.attrs[2] = value;
				isValid = true;
				errorMessage = "";
			}else{
				errorMessage =  ErrorInfo[4] + " integer";
				isValid = false;
			}
		}else if(key.equals(AttrNames[3])){
			if (FormatVerifier.isPositiveFloat(value)) {
				this.attrs[3] = value;
				this.ignore = Double.parseDouble(value);
				isValid = true;
				errorMessage = "";
			}else{
				errorMessage = ErrorInfo[1];
			}
		}else if(key.equals(AttrNames[4])){
			if(value.equals("false")){
				this.attrs[4] = "false";
				this.aField = null;
				isValid = true;
				errorMessage = "";
			}else{
				if(!Util.string2PositiveDoubleArrayBoolean(value)){
					errorMessage = ErrorInfo[1];
					isValid = false;
				}else{
					double[] aFieldParams = Util.string2DoubleArray(value);
					if(aFieldParams.length % 5 == 0){
						this.attrs[4] = value;
						this.aField = new AttractorField(x,y);
						aField.add(aFieldParams);
						isValid = true;
						errorMessage = "";
					}else{
						errorMessage = ErrorInfo[5] + " 5";
						isValid = false;
					}
				}
			}
		}else if(key.startsWith(AttrNames[5])){
			if(value.equals("false")){
				attrs[5] = "false";
				isValid = true;
				errorMessage = "";
			}
			else if(!Util.string2PositiveDoubleArrayBoolean(value)){
				errorMessage = ErrorInfo[1];
				isValid = false;
			}else{
				double[] dAreaFields = Util.string2DoubleArray(value);
				if(dAreaFields.length == 4 || dAreaFields.length == 3 || dAreaFields.length == 2){
					if(key.endsWith("CIRCLE") || key.endsWith("RECTANGLE")){
						if(dAreaFields.length == 2 || dAreaFields.length == 3){
							int[] nodesExist = getCurrentIDs();
							if(Util.isIntegerInIntegerArray((int)dAreaFields[0], nodesExist)){
								attrs[5] = value;
								int x = app.getNetwork().getCoordianteByID((int)dAreaFields[0]).x;
								int y = app.getNetwork().getCoordianteByID((int)dAreaFields[0]).y;
								int width = 0;
								int height = 0;
								
								if(dAreaFields.length == 2){
									width =  (int)dAreaFields[1];
									height = (int)dAreaFields[1];
								}else if(dAreaFields.length == 3){
									width =  (int)dAreaFields[1];
									height = (int)dAreaFields[2];
								}
								int[] tempDistrictArea = new int[]{x,y,width,height};
								if(key.endsWith("CIRCLE")){
									this.setDistrictedArea(new DistrictedArea(0,tempDistrictArea));
								}else if(key.endsWith("RECTANGLE")){
									this.setDistrictedArea(new DistrictedArea(1,tempDistrictArea));
								}
								
								isValid = true;
								errorMessage = "";
							}else{
								errorMessage = "The node "+ErrorInfo[3];
								isValid = false;
							}
						}else if(dAreaFields.length == 4){
							int x = (int)dAreaFields[0];
							int y = (int)dAreaFields[1];
							if(x > this.x || y > this.y){
								isValid = false;
								errorMessage = "("+x+","+y+") "+ ErrorInfo[7];
							}else{
								attrs[5] = value;
								int width = (int)dAreaFields[2];
								int height = (int)dAreaFields[3];
								int[] tempDistrictArea = new int[]{x,y,width,height};
								if(key.endsWith("CIRCLE")){
									this.setDistrictedArea(new DistrictedArea(0,tempDistrictArea));
								}else if(key.endsWith("RECTANGLE")){
									this.setDistrictedArea(new DistrictedArea(1,tempDistrictArea));
								}
								
								isValid = true;
								errorMessage = "";
							}
						}

					}
				}else{
					errorMessage = "there should be 2 ,3 or 4 numbers.";
					isValid = false;
				}
			}
		}else{
			errorMessage = "No such Arguments";
			isValid = false;
		}
		return isValid;
	}

	/**
	 * This is not a very good one.
	 * This method has to rely on the NetTopoApp
	 * @return current Node ID generated by NetTopoApp
	 */
	public int[] getCurrentIDs(){
		synchronized (app){
			WirelessSensorNetwork wsn = app.getNetwork();
			int[] IDs = null;
			synchronized (wsn){
				IDs = wsn.getAllNodesID();
			}
			return IDs;
		}
	}

	/**
	 * this relys on app
	 * @param num
	 */
	public void setAppMobileNodeNum(int num){
		app.addMobileNodeNum(num);
	}
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public static String[] getHelpInfo() {
		return HelpInfo;
	}

	public static String[] getErrorInfo() {
		return ErrorInfo;
	}


}
