package org.deri.nettopo.app.wizard;

import org.deri.nettopo.util.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.app.*;
import org.deri.nettopo.app.wizard.page.Page_KillOneNode_ID;

import org.eclipse.jface.wizard.Wizard;

public class Wizard_KillOneNode extends Wizard {
	private Page_KillOneNode_ID page_killOneNode;
	
	
	public Wizard_KillOneNode(){
		page_killOneNode = new Page_KillOneNode_ID();
		addPage(page_killOneNode);
	}
	
	/*
	 * Called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish(){
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Painter painter = NetTopoApp.getApp().getPainter();
		boolean visible = page_killOneNode.getIsvisible();
		int id = page_killOneNode.getID();
		
		try{
			if(visible == true){
				Coordinate c = wsn.deleteNodeByID(id);
				VNode node = VNodeFactory.getInstance("org.deri.nettopo.node.Hole");
				wsn.addNode(node, c);
				painter.paintNode(node.getID());
				NetTopoApp.getApp().refresh();
			}else{
				painter.removeNode(id);
				NetTopoApp.getApp().refresh();
			}
			
		} catch(DuplicateCoordinateException ex){
			ex.printStackTrace();
		}
		NetTopoApp.getApp().addLog("node:"+id+" was killed.");
		return true;
	}
}
