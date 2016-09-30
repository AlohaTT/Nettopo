package org.deri.nettopo.display;

import java.util.TimerTask;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.util.Coordinate;
import org.eclipse.swt.widgets.Display;

class PainterMobileNodeTask extends TimerTask{
	private int  index = 1; // 0 is the node id. 
	private double[] path;
	private NetTopoApp app = null;
	
	PainterMobileNodeTask(double[] path){
		if(path.length < 5){
			System.out.println("There isn't one more node position in the path");
			System.exit(0);
		}
		this.path = path;
		app = NetTopoApp.getApp();
	}
	
	public void run(){
		while(app.getIsPause());
		int x = (int)this.path[index++];
		int y = (int)this.path[index++];
		
		if(index >= path.length-1){
			if(app.getCurrentSelectedNode() != null){
				app.getPainter().removeNodeFocus(app.getCurrentSelectedNode().getID());
			}
			this.cancel();
		}
		if(x != 0 || y != 0){
			if(app.getNetwork().resetNodeCoordinateByID(
					(int)path[0], new Coordinate(x,y))
			&& app.getNetwork().resetNodeColorByID(
					(int)path[0], NodeConfiguration.MobilityNodeColor)){
				app.getPainter().rePaintAllNodes();
				if(app.getCurrentSelectedNode() != null){
					app.getPainter().paintNodeFocus(app.getCurrentSelectedNode().getID());
				}
			}
		}
		
		Display display = app.getDisplay();
		display.asyncExec(new Runnable(){
			public void run() {
				if(path[index-2] != 0 && path[index-1]!=0){
					app.addLog("nodeID: " + (int)path[0] + ". X=" + (int)path[index-2] + " Y=" + (int)path[index-1] + "\t");
				}
				app.refresh();
			}
		});
	}
}