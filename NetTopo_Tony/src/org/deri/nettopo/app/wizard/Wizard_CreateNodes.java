package org.deri.nettopo.app.wizard;

import org.deri.nettopo.topology.Topology;
import org.deri.nettopo.util.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.app.*;
import org.deri.nettopo.app.wizard.page.Page_NodeAttributes;
import org.deri.nettopo.app.wizard.page.Page_NodeType;
import org.deri.nettopo.app.wizard.page.Page_TopoAttributes;
import org.deri.nettopo.app.wizard.page.Page_TopoType;

import org.eclipse.jface.wizard.Wizard;


public class Wizard_CreateNodes extends Wizard {
	private Page_NodeType page_nodeType;
	private Page_NodeAttributes page_nodeAttr;
	private Page_TopoType page_topoType;
	private Page_TopoAttributes page_topoAttr;
	
	
	public VNode getDataVnode()
	{
		return page_nodeAttr.getNode();
	}
	
	public Topology getTopology()
	{
		return page_topoAttr.getTopology();
	}

	public Wizard_CreateNodes(){
		page_nodeType = new Page_NodeType();
		page_nodeAttr = new Page_NodeAttributes();
		page_topoType = new Page_TopoType();
		page_topoAttr = new Page_TopoAttributes();
		
		
		// Add the pages
		addPage(page_nodeType);
		addPage(page_nodeAttr);
		addPage(page_topoType);
		addPage(page_topoAttr);
		
	}
	
	/*
	 * Called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish(){
		
		
//		if(Property.isCreateTasks)
//		{
//			
//			return true;
//		}
			
		/* get coordinates of all nodes to create */
		Coordinate[] coordinates = page_topoAttr.getCoordinates();
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		
		if(coordinates==null)
			return true;
		
		VNode node = null;
		
		/* create nodes and set their attributes according to sample node    在此出画点*/
//		int k=0;
		for(int i=0;i<coordinates.length;i++){
			/* create a wireless sensor node and set it's attributes */
			node = page_nodeType.getNode();
			String[] attrNames = node.getAttrNames();
			for(int j=0;j<attrNames.length;j++){
				//System.out.println(page_nodeAttr.getAttrValue(attrNames[j])+":"+attrNames[j]);
				node.setAttrValue(attrNames[j], page_nodeAttr.getAttrValue(attrNames[j]));
			}
			try{
//					if((node instanceof SensorNode) && Util.roulette(30))
//						{
//							node.setAttrValue("Max TR", 30+"");
//							++k;
//						}
				wsn.addNode(node, coordinates[i]);
				NetTopoApp.getApp().getPainter().paintNode(node.getID());
			} catch(DuplicateCoordinateException ex){
				ex.printStackTrace();
				return false;
			}
		}
//		System.out.println("k:"+k);
		NetTopoApp.getApp().addLog(coordinates.length + " nodes with type "+ node.getClass().getName() + " were created.");
		return true;
	}
}