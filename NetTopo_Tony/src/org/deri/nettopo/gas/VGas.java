package org.deri.nettopo.gas;

import java.io.Serializable;
import org.eclipse.swt.graphics.RGB;

//
/**VGas½Ó¿Ú
 * @author root
 *
 */
public interface VGas extends Serializable {
	public String[] getAttrNames();
	public boolean setAttrValue(String attrName, String value);
	public String getAttrValue(String attrName);
	public String getAttrErrorDesciption();
	public int getID();
	public void setAvailable(boolean available);
	public void setActive(boolean active);
	public void setID(int id);
	public RGB getColor();
	public void setColor(RGB rgb);
	
}

