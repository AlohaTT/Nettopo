package org.deri.nettopo.visulizer;

public interface Visulizer extends Runnable {
	
	public String[] getAttrNames();
	public boolean setAttrValue(String attrName, String value);
	public String getAttrValue(String attrName);
	public String getAttrErrorDesciption();
	
}
