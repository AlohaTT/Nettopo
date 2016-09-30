package org.deri.nettopo.algorithm.cknplus.function;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;

public class CKNplus_TPGF_ConnectNeighbors implements AlgorFunc {

	private Algorithm algorithm;
	private CKNplus_MAIN ckn_plus;
	private TPGF_ConnectNeighbors connectNeighbors;
	
	
	public CKNplus_TPGF_ConnectNeighbors(Algorithm algorithm){
		this.algorithm = algorithm;
		ckn_plus = new CKNplus_MAIN();
		connectNeighbors = new TPGF_ConnectNeighbors();
	}
	
	public CKNplus_TPGF_ConnectNeighbors(){
		this(null);
	}
	
	public void run() {
		NetTopoApp app = NetTopoApp.getApp();
		Timer timer = app.getTimer_func();
		TimerTask task = app.getTimertask_func();
		if(timer != null && task != null){
			task.cancel();
			timer.cancel();
			timer.purge();
			app.setTimertask_func(null);
			app.setTime_func(null);
		}
	
		app.setTime_func(new Timer());
		app.setTimertask_func(new TimerTask(){
			public void run() {
				entry();
			}
		});
		app.getTimer_func().schedule(app.getTimertask_func(), 0, app.getFunc_INTERVAL() * 1000);
	}
	
	public void entry(){
		ckn_plus.run();
		connectNeighbors.run();
		final StringBuffer message = new StringBuffer();
		int[] activeSensorNodes = NetTopoApp.getApp().getNetwork().getSensorActiveNodes();
		message.append("k=" +ckn_plus.getK() +", Number of active nodes is:"+ activeSensorNodes.length +", they are: "+Arrays.toString(activeSensorNodes));
		
		
		NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
			public void run() {
				NetTopoApp.getApp().refresh();
				NetTopoApp.getApp().addLog(message.toString());
			}
		});
	}
	
	public Algorithm getAlgorithm(){
		return algorithm;
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}

}
