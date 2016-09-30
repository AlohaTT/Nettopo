package org.deri.nettopo.app.table;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class VNodeLabelProvider implements ITableLabelProvider  {

	  /**
	   * Gets the image for the specified column
	   * 
	   * @param arg0 the player
	   * @param arg1 the column
	   * @return Image
	   */
	  public Image getColumnImage(Object arg0, int arg1) {
	    return null;
	  }
	  
	  /**
	   * @param obj
	   * @param index
	   */
	  public String getColumnText(Object obj, int index) {
	    Property prop = (Property)obj;
	    String text = "";
	    switch (index) {
	    case 0:
	    	text = prop.getKey();
	    	break;
	    case 1:
	    	text = prop.getValue();
	    	break;
	    }
	    return text;
	  }

	  /**
	   * Adds a listener
	   * 
	   * @param arg0 the listener
	   */
	  public void addListener(ILabelProviderListener arg0) {}

	  /**
	   * Dispose any created resources
	   */
	  public void dispose() {}

	  /**
	   * Returns whether the specified property,
	   * if changed, would affect the label
	   * 
	   * @param arg0 the player
	   * @param arg1 the property
	   * @return boolean
	   */
	  public boolean isLabelProperty(Object arg0, String arg1) {
	    return false;
	  }

	  /**
	   * Removes the specified listener
	   * 
	   * @param arg0 the listener
	   */
	  public void removeListener(ILabelProviderListener arg0) {}

}
