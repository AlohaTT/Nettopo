package org.deri.nettopo.app.wizard;

import org.deri.nettopo.util.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.app.*;
import org.deri.nettopo.app.wizard.page.Page_NodeAttributes;
import org.deri.nettopo.app.wizard.page.Page_NodeLocation;
import org.deri.nettopo.app.wizard.page.Page_NodeType;

import org.eclipse.jface.wizard.Wizard;

public class Wizard_CreateOneNode extends Wizard {
	private Page_NodeType page_nodeType;
	private Page_NodeAttributes page_nodeAttr;
	private Page_NodeLocation page_nodeLoc;
	private Coordinate cor;
	
	private VNode node;
	
	public Wizard_CreateOneNode(){
		page_nodeType = new Page_NodeType();
		page_nodeAttr = new Page_NodeAttributes();
		page_nodeLoc = new Page_NodeLocation();
		
		// Add the pages
		addPage(page_nodeType);
		addPage(page_nodeAttr);
		addPage(page_nodeLoc);
	}
	
	public boolean getCreateType()
	{
		return page_nodeLoc.getSelectItem();
	}
	
	public Coordinate getOneCoordinate(){
		return cor;
	}
	
	public VNode getDataVnode()
	{
		return page_nodeAttr.getNode();
	}
	
	/*
	 * Called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish(){
		/* create a wireless sensor node and set it's attributes */
		
		if(Property.isCreateOneTask)
		{
			cor = page_nodeLoc.getOneCoordinate();
			return true;
		}
		
		node = page_nodeType.getNode();
		String[] attrNames = node.getAttrNames();
		for(int i=0;i<attrNames.length;i++){
			node.setAttrValue(attrNames[i], page_nodeAttr.getAttrValue(attrNames[i]));
		}
		
		/* get the display location of the node */
		Coordinate c = page_nodeLoc.getCoordinate();
		
		if(c==null){
			NetTopoApp.getApp().addLog("No nodes are created.");
			return true;
		}
		
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		try{
			wsn.addNode(node, c);
			NetTopoApp.getApp().getPainter().paintNode(node.getID());
		} catch(DuplicateCoordinateException ex){
			ex.printStackTrace();
		}
		NetTopoApp.getApp().addLog("A node with type "+ node.getClass().getName() + " was created.");
		return true;
	}
}
