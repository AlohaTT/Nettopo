package org.deri.nettopo.app.wizard;



import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.app.wizard.page.Page_NodeAttributes;
import org.deri.nettopo.app.wizard.page.Page_NodeType;

import org.deri.nettopo.app.wizard.page.Page_TopoAttributes;
import org.deri.nettopo.app.wizard.page.Page_TopoType;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.VNode;

import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;


public class Wizard_CreateCKNTasks extends Wizard {

	private Page_NodeType page_nodeType;
	private Page_NodeAttributes page_nodeAttr;
	private Page_TopoType page_topoType;
	private Page_TopoAttributes page_topoAttr;
	//private Page_CKNTimes page_CKNTimes;
	public Wizard_CreateCKNTasks() {
		setWindowTitle("Wizard Create TPGFTasks");
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

	@Override
	public boolean performFinish() {
		/*********************** 修改 *****************************/
		Coordinate[] coordinates = page_topoAttr.getCoordinates();
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		
		if(coordinates==null)
			return true;
		
		VNode node = null;
		
		/* create nodes and set their attributes according to sample node    在此出画点*/
		for(int i=0;i<coordinates.length;i++){
			/* create a wireless sensor node and set it's attributes */
			node = page_nodeType.getNode();
			String[] attrNames = node.getAttrNames();
			
			for(int j=0;j<attrNames.length;j++){
				//System.out.println(page_nodeAttr.getAttrValue(attrNames[j])+":"+attrNames[j]);
				node.setAttrValue(attrNames[j], page_nodeAttr.getAttrValue(attrNames[j]));
			}
			try{
				wsn.addNode(node, coordinates[i]);
				NetTopoApp.getApp().getPainter().paintNode(node.getID());
			} catch(DuplicateCoordinateException ex){
				ex.printStackTrace();
				return false;
			}
		}
		NetTopoApp.getApp().addLog(coordinates.length + " nodes with type "+ node.getClass().getName() + " were created.");
		return true;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		//super.createPageControls(pageContainer);
	}

	
	
	
	

/******************************end**************************************************************/

}
