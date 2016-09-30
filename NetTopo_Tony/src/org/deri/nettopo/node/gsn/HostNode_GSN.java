package org.deri.nettopo.node.gsn;

import org.eclipse.swt.graphics.RGB;
import org.deri.nettopo.util.FormatVerifier;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.node.VNode;



public class HostNode_GSN implements VNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ID;
	private String IP_Address;
	private String Port;
	private String Discription; // transmission radius
	private boolean available;
	private RGB color;
	private String[] attrNames;
	private String errorMsg;
	
	public HostNode_GSN(){
		ID = 0;
		IP_Address = "";
		Port = "";
		Discription = "";
		available = true;
		color = NodeConfiguration.HostNodeColorRGB;
		attrNames = new String[]{"ID", "IP Address", "Port", "Discription"};
		errorMsg = null;
	}
	
	public void setID(int id){
		this.ID = id;
	}
	
	public void setIPAddress(String ipaddress){
		this.IP_Address = ipaddress;
	}
		
	public void setPort(String port){
		this.Port = port;
	}
	
	public void setDiscription(String dis){
		this.Discription = dis;
	}
	
	public void setAvailable(boolean available){
		this.available = available;
	}

	public void setColor(RGB color){
		this.color = color;
	}
	
	public int getID(){
		return ID;
	}
	
	public String getIPAddress(){
		return IP_Address;
	}
	
	public String getPort(){
		return Port;
	}
	
	public String getDiscription(){
		return Discription;
	}
	
	public boolean isAvailable(){
		return available;
	}
	
	public String[] getAttrNames(){
		return attrNames;
	}
	
	public String getAttrErrorDesciption(){
		return errorMsg;
	}
	
	public RGB getColor(){
		return color;
	}
	
	public boolean setAttrValue(String attrName, String value){
		boolean isAttrValid = true;
		int index = Util.indexOf(attrNames, attrName);
		switch(index){
		case 0:
			if(FormatVerifier.isNotNegative(value)){
				setID(Integer.parseInt(value));
			}else{
				errorMsg = "ID must be a non-negative integer";
				isAttrValid = false;
			}			
			break;
		case 1:
			setIPAddress(value);
			break;
		case 2: 
			setPort(value);
			break;
		case 3:
			setDiscription(value);
			break;
		}

		return isAttrValid;
	}
	
	public String getAttrValue(String attrName){
		int index = Util.indexOf(attrNames, attrName);
		switch(index){
		case 0:
			return String.valueOf(getID());
		case 1:
			return String.valueOf(getIPAddress());
		case 2:
			return String.valueOf(getPort());
		case 3:
			return String.valueOf(getDiscription());
		default:
			return null;
		}
	}
	
	public static void main(String[] args){
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
