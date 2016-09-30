package org.deri.nettopo.visulizer.xBow;

public class xBowPacket {
	
	private int amtype;
	private int nodeID;
	private int parentID;
	private int group;
	private int voltage;
	private int humid;
	private int temp;
	private float preTemp;
	private float press;
	
	
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
	
}
