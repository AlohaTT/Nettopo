package org.deri.nettopo.algorithm.ckn.function;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.Property;

public class CKN_TPGF_ConnectNeighbors implements AlgorFunc {

	private Algorithm algorithm;
	private CKN_MAIN ckn;
	private TPGF_ConnectNeighbors connectNeighbors;
	
	
	/************add*****************/
	private StringBuffer result=new StringBuffer();
	
	public CKN_TPGF_ConnectNeighbors(Algorithm algorithm){
		this.algorithm = algorithm;
		ckn = new CKN_MAIN();
		connectNeighbors = new TPGF_ConnectNeighbors();
	}
	
	public CKN_TPGF_ConnectNeighbors(){
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
		}//清空
		int times=Property.times;//保存times
		System.out.println("   Property.times"+Property.times);
		app.setTime_func(new Timer());
		app.setTimertask_func(new TimerTask(){
			public void run() {
				//entry();
				/******************add**********************/
				//System.out.println("Property.isTPGF"+Property.isTPGF+"   Property.times"+Property.times);
				if(Property.isTPGF)
				{
					if (Property.times != 0) {
						entry();
						Property.times--;
					} else {	//清空
						NetTopoApp app = NetTopoApp.getApp();
						Timer timer = app.getTimer_func();
						TimerTask task = app.getTimertask_func();
						if(timer != null && task != null){
							task.cancel();
							timer.cancel();
							timer.purge();
							app.setTimertask_func(null);
							app.setTime_func(null);
						}//清空
					}
				}
				else
				{
					entry();
				}
			}
		});
		app.getTimer_func().schedule(app.getTimertask_func(), 0, app.getFunc_INTERVAL() * 1000);
		if(Property.isTPGF)
		{
			while(app.getTimer_func()!=null);
			System.out.println("pause");
			Property.times=times;//还原
		}
	}
	
	public void entry(){
		ckn.run();
		connectNeighbors.run();
		final StringBuffer message = new StringBuffer();
		int[] activeSensorNodes = NetTopoApp.getApp().getNetwork().getSensorActiveNodes();
		message.append("k=" +ckn.getK() +", Number of active nodes is:"+ activeSensorNodes.length +", they are: "+Arrays.toString(activeSensorNodes));
		
		
		/****************add****************************/
		result.append(message.toString());
		
		if(!Property.isTPGF)
		{
		
		NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
			public void run() {
				NetTopoApp.getApp().refresh();
				NetTopoApp.getApp().addLog(message.toString());
			}
		});
		}
	}
	
	public Algorithm getAlgorithm(){
		return algorithm;
	}

	@Override
	public String getResult() {
		return result.toString();
		// TODO Auto-generated method stub
		
	}

}
