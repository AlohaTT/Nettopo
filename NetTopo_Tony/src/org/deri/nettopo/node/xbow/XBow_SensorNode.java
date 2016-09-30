package org.deri.nettopo.node.xbow;

import org.deri.nettopo.node.VNode;
import org.deri.nettopo.util.FormatVerifier;
import org.deri.nettopo.node.NodeConfiguration;
import org.eclipse.swt.graphics.RGB;

public class XBow_SensorNode implements VNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int vNodeID;
	private int amtype;
	private int nodeID;
	private int parentID;
	private int group;
	private int voltage;
	private int humid;
	private int temp;
	private float preTemp;
	private float press;
	private RGB color;
	private boolean available;
	private String[] attrNames = {
			"amType", 
			"xBow Node ID",
			"Parent ID",
			"Group",
			"Voltage",
			"Humid",
			"Temperature",
			"PreTemp",
			"Press"};
	private String errorMsg;
	
	public XBow_SensorNode(){
		setNodeID(-1);
		setParentID(-1);
		color = NodeConfiguration.XBowSensorNodeColorRGB;
	}
	
	public XBow_SensorNode(int NodeID, int ParentID){
		setNodeID(NodeID);
		setParentID(ParentID);
		color = NodeConfiguration.XBowSensorNodeColorRGB;
	}
	
	public void setID(int ID){
		this.vNodeID = ID;
	}
	
	public void setAmType(int type){
		this.amtype = type;
	}
	
	public void setNodeID(int value){
		this.nodeID = value;
	}
	
	public void setParentID(int value){
		this.parentID = value;
	}
	
	public void setGroup(int group){
		this.group = group;
	}
	
	public void setVoltage(int vol){
		this.voltage = vol;
	}
	
	public void setHumid(int humid){
		this.humid = humid;
	}
	
	public void setTemp(int temp){
		this.temp = temp;
	}
	
	public void setPreTemp(float preTemp){
		this.preTemp = preTemp;
	}
	
	public void setPress(float press){
		this.press = press;
	}
	
	public void setColor(RGB color){
		this.color = color;
	}
	
	public int getID(){
		return vNodeID;
	}
	
	public int getAmType(){
		return this.amtype;
	}
	
	public int getxBowNodeID(){
		return nodeID;
	}
	
	public int getParentID(){
		return parentID;
	}
	
	public int getGroup(){
		return this.group;
	}
	
	public int getVoltage(){
		return this.voltage;
	}
	
	public int getHumid(){
		return this.humid;
	}
	
	public int getTemp(){
		return this.temp;
	}
	
	public float getPreTemp(){
		return this.preTemp;
	}
	
	public float getPress(){
		return this.press;
	}
		
	public RGB getColor(){
		return color;
	}
	
	public String[] getAttrNames(){
		return attrNames;
	}
	
	public boolean setAttrValue(String attrName, String value){
		boolean isAttrValid = true;
		int index = indexOf(attrNames, attrName);
		switch(index){
		case 0:
			if(FormatVerifier.isNotNegative(value)){
				setAmType(Integer.parseInt(value));
			}else{
				errorMsg = "am type must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 1:
			if(FormatVerifier.isNotNegative(value)){
				setNodeID(Integer.parseInt(value));
			}else{
				errorMsg = "xBow Node ID must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 2:
			if(FormatVerifier.isNotNegative(value)){
				setParentID(Integer.parseInt(value));
			}else{
				errorMsg = "Parent ID must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 3: 
			if(FormatVerifier.isNotNegative(value)){
				setGroup(Integer.parseInt(value));
			}else{
				errorMsg = "Group must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 4: 
			if(FormatVerifier.isInteger(value)){
				setVoltage(Integer.parseInt(value));
			}else{
				errorMsg = "Voltage must be an integer";
				isAttrValid = false;
			}
			break;
		case 5: 
			if(FormatVerifier.isInteger(value)){
				setHumid(Integer.parseInt(value));
			}else{
				errorMsg = "Humid must be an integer";
				isAttrValid = false;
			}
			break;
		case 6: 
			if(FormatVerifier.isInteger(value)){
				setTemp(Integer.parseInt(value));
			}else{
				errorMsg = "Temperature must be an integer";
				isAttrValid = false;
			}
			break;
		case 7: 
			if(FormatVerifier.isFloat(value) || FormatVerifier.isInteger(value) ){
				setPreTemp(Float.parseFloat(value));
			}else{
				errorMsg = "PreTemp must be an integer or a float";
				isAttrValid = false;
			}
			break;
		case 8: 
			if(FormatVerifier.isFloat(value) || FormatVerifier.isInteger(value) ){
				setPress(Float.parseFloat(value));
			}else{
				errorMsg = "Press must be an integer or a float";
				isAttrValid = false;
			}
			break;
		}

		return isAttrValid;
	}
	
	public String getAttrValue(String attrName){
		int index = indexOf(attrNames, attrName);
		switch(index){
		case 0:
			return String.valueOf(getAmType());
		case 1:
			return String.valueOf(getxBowNodeID());
		case 2:
			return String.valueOf(getParentID());
		case 3:
			return String.valueOf(getGroup());
		case 4:
			return String.valueOf(getVoltage());
		case 5:
			return String.valueOf(getHumid());
		case 6:
			return String.valueOf(getTemp());
		case 7:
			return String.valueOf(getPreTemp());
		case 8:
			return String.valueOf(getPress());
		default:
			return null;
		}
	}
	
	public String getAttrErrorDesciption(){
		return errorMsg;
	}
	
	protected int indexOf(String[] attrNames, String attrName){
		for(int i=0;i<attrNames.length;i++){
			if(attrNames[i].equals(attrName))
				return i;
		}
		return -1;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSize(int size) {
		// TODO Auto-generated method stub
		
	}

}
