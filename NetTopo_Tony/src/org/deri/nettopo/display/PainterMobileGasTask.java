package org.deri.nettopo.display;

import java.util.TimerTask;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.GasConfiguration;
import org.deri.nettopo.util.Coordinate;
import org.eclipse.swt.widgets.Display;

class PainterMobileGasTask extends TimerTask{
	private int  index = 1; // 0 is the gas id. 
	private double[] path;
	private NetTopoApp app = null;
	
	PainterMobileGasTask(double[] path){
		if(path.length < 5){
			System.out.println("There isn't one more gas position in the path");
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
			if(app.getCurrentSelectedGas() != null){
				app.getPainter().removeGasFocus(app.getCurrentSelectedGas().getID());
			}
			this.cancel();
		}
		if(x != 0 || y != 0){
			if(app.getNetwork().resetGasCoordinateByID(
					(int)path[0], new Coordinate(x,y))
			&& app.getNetwork().resetGasColorByID(
					(int)path[0], GasConfiguration.MobilityGasColor)){
				app.getPainter().rePaintAllGas();
				if(app.getCurrentSelectedGas() != null){
					app.getPainter().paintGasFocus(app.getCurrentSelectedGas().getID());
				}
			}
		}
		
		Display display = app.getDisplay();
		display.asyncExec(new Runnable(){
			public void run() {
				if(path[index-2] != 0 && path[index-1]!=0){
					app.addLog("gasID: " + (int)path[0] + ". X=" + (int)path[index-2] + " Y=" + (int)path[index-1] + "\t");
				}
				app.refresh();
			}
		});
	}
}