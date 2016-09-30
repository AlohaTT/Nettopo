package org.deri.nettopo.app.table;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.deri.nettopo.node.VNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.app.NetTopoApp;

import java.util.List;
import java.util.ArrayList;


public class VNodeContentProvider implements IStructuredContentProvider  {
	 /**
	   * Gets the elements for the table
	   * 
	   * @param arg0 the model
	   * @return Object[]
	   */
	  public Object[] getElements(Object arg0) {
		  // Returns all the properties in VNode
		  VNode node = (VNode)arg0;
		  String[] attrNames = node.getAttrNames();
		  List<Property> properties = new ArrayList<Property>();
		  
		  /* add the id, node type, coordinate property */
		  Property prop_id = new Property("ID", String.valueOf(node.getID()));
		  String nodeTypeName = node.getClass().getName();
		  Property prop_type = new Property("Node Type", nodeTypeName);
		  Coordinate c = NetTopoApp.getApp().getNetwork().getCoordianteByID(node.getID());
		  Property prop_coordinate;
		  if(c==null)
		  {
			  System.out.println("C is null");
			  c = new Coordinate(600,600,0);
		  }
		  if(c.z==0){ // 2D
			  prop_coordinate = new Property("Coordinate", String.valueOf(c.x)+","+String.valueOf(c.y));
		  }else{ // 3D
			  prop_coordinate = new Property("Coordinate", String.valueOf(c.x)+","+String.valueOf(c.y)+","+String.valueOf(c.z));
		  }
		  properties.add(prop_id);
		  properties.add(prop_type);
		  properties.add(prop_coordinate);
		  
		  for(int i=0;i<attrNames.length;i++){
			  Property prop = new Property(attrNames[i],node.getAttrValue(attrNames[i]));
			  properties.add(prop);
		  }
		  return properties.toArray();
	  }

	  public void dispose() {} //We don't create any resources, so we don't dispose any

	  public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}// Nothing to do

}
