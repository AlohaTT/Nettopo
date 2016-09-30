package org.deri.nettopo.gas;

import org.eclipse.swt.graphics.RGB;

public class Gas implements VGas {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		

		private int id;
		private RGB color;
		private int radius;
		private boolean available;
		private String[] attrNames;
		private String errorMsg;
		private boolean active;
		
		public Gas(){
			id=0;
			color = GasConfiguration.GasColorRGB;
			available = true;
			active = true;
			attrNames = new String[]{"Radius"};
			errorMsg = null;
			
		}
		
		
		public int getRadius() {
			return radius;
		}


		public void setRadius(int radius) {
			this.radius = radius;
		}


		public void setID(int id){
			this.id=id;
		}
		
		public int getID(){
			return id;
		}
		
		public void setErrorMsg(String msg){
			this.errorMsg=msg;
		}
		
		public String getAttrErrorDesciption(){
			return errorMsg;
		}
		
		public void setColor(RGB color){
			this.color= color;
		}
		
		public RGB getColor(){
			return color;
		}
		
	/*	public void setAttrNames(String[] attrNames){
			this.attrNames= attrNames; 
		}
	*/	
		public String[] getAttrNames(){
			return attrNames;
		}
		

		public boolean setAttrValue(String attrName, String value){
			if("Radius".equals(attrName))
			{	radius=Integer.parseInt(value);
			return true;
			}
			return false;
		}
		
		public String getAttrValue(String attrName){
			if("Radius".equals(attrName))
			return radius+"";
			else
				return null;
		}
		
		public void setAvailable(boolean available){
			this.available= available;
		}
		
		public boolean getAvailable(){
			return this.available;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}


	}


