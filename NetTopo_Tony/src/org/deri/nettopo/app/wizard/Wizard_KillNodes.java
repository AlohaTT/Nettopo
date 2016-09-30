package org.deri.nettopo.app.wizard;

import org.deri.nettopo.util.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.app.*;
import org.deri.nettopo.app.wizard.page.Page_KillNodesRange;
import org.deri.nettopo.app.wizard.page.Page_KillNodes_Circle;
import org.deri.nettopo.app.wizard.page.Page_KillNodes_Random;

import org.eclipse.jface.wizard.Wizard;

public class Wizard_KillNodes extends Wizard {
	private Page_KillNodesRange page_range;
	private Page_KillNodes_Random page_random;
	private Page_KillNodes_Circle page_circle;
	
	public Wizard_KillNodes(){
		page_range = new Page_KillNodesRange();
		page_random = new Page_KillNodes_Random();
		page_circle = new Page_KillNodes_Circle();
		// Add the pages
		addPage(page_range);
		addPage(page_random);
		addPage(page_circle);
	}
	
	/*
	 * Called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish(){
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Painter painter = NetTopoApp.getApp().getPainter();
		int index = page_range.getRangeIndex();
		int[] nodesID=null;
		boolean visible = true;
		switch(index){
		case 0: // random
			nodesID = page_random.getNodesID();
			visible = page_random.getIsvisible();
			break;
		case 1: // in a circle
			nodesID = page_circle.getNodesID();
			visible = page_circle.getIsvisible();
			break;
		}
		if(nodesID!=null){
			try{
				if(visible == true){
					for(int i=0;i<nodesID.length;i++){
						Coordinate c = wsn.deleteNodeByID(nodesID[i]);
						VNode node = VNodeFactory.getInstance("org.deri.nettopo.node.Hole");
						wsn.addNode(node, c);
						painter.paintNode(node.getID());
					}
					NetTopoApp.getApp().refresh();
				}else{
					for(int i=0;i<nodesID.length;i++){
						painter.removeNode(nodesID[i]);
					}
					NetTopoApp.getApp().refresh();
				}
				
			} catch(DuplicateCoordinateException ex){
				ex.printStackTrace();
			}
		}
		NetTopoApp.getApp().addLog(nodesID.length + " nodes were killed.");
		return true;
	}
}
