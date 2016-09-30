package org.deri.nettopo.node.localization.dvhop;

import org.deri.nettopo.util.Coordinate;

/**************************************************
 * 跳数表的一条表项
 * ************************************************/
public class HopItem {
	
	private int aID;    //anchor节点id
	private Coordinate coor;
	private int hops;   //跳数
	//private int from;   //上一跳id
	private boolean isCasted;  //是否已经广播过
	
	public HopItem(){
		aID=0;
		hops=0;
		//from=0;
		coor=new Coordinate(0,0);
		isCasted=false;
	}
	
	public HopItem(int id,int hops,Coordinate c){
		aID=id;
		//this.from=from;
		this.hops=hops;
		this.isCasted=false;
		coor=new Coordinate(c);
	}
	
	public HopItem Copy(){
		HopItem item = new HopItem();
		item.setaID(this.aID);
		item.setCasted(this.isCasted);
		item.setCoor(this.getCoor());
		item.setHops(this.hops);
		return item;
	}
	
	public void CopyFrom(HopItem e){
		this.aID=e.getaID();
		this.coor.x=e.getCoor().x;
		this.coor.y=e.getCoor().y;
		this.coor.z=e.getCoor().z;
		this.hops=e.getHops();
		this.isCasted=e.isCasted();
	}
	
	public String toString(){
		String des="HopItem:( anchor id:"+this.aID+","+"hops:"+this.hops
		+",isBroadcast:"+this.isCasted+" )";
		return des;
	}
	
	public void HopsIncrease(){
		this.hops++;
	}
	
	public int getaID() {
		return aID;
	}
	public void setaID(int aID) {
		this.aID = aID;
	}
	public int getHops() {
		return hops;
	}
	public void setHops(int hops) {
		this.hops = hops;
	}
	
	public boolean isCasted() {
		return isCasted;
	}
	public void setCasted(boolean isCasted) {
		this.isCasted = isCasted;
	}

	public Coordinate getCoor() {
		return coor;
	}

	public void setCoor(Coordinate coor) {
		this.coor = coor;
	}
	
	

}
