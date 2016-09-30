package org.deri.nettopo.algorithm.ckn.function;

import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.Property;

public class CKN_TPGF_FindOnePath implements AlgorFunc {

	private Algorithm algorithm;
	private CKN_MAIN ckn;
	private TPGF_FindOnePath findOnePath;
	
	public CKN_TPGF_FindOnePath(Algorithm algorithm){
		this.algorithm = algorithm;
		ckn = new CKN_MAIN();
		findOnePath = new TPGF_FindOnePath(new Algor_TPGF());
	}

	public CKN_TPGF_FindOnePath(){
		this(null);
	}
	
	
	public void run() {
		NetTopoApp app = NetTopoApp.getApp();
		Timer timer = app.getTimer_func();
		if(timer != null)
			timer.cancel();
	
		int times=Property.times;
		app.setTime_func(new Timer());
		app.setTimertask_func(new TimerTask(){
			public void run() {
				//entry();
				/******************add**********************/
				if(Property.isTPGF)
				{
					if (Property.times != 0) {
						entry();
						Property.times--;
					} else {
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
			Property.times=times;//»¹Ô­
		}
		
	}
	
	public void entry() {
		ckn.run();
		findOnePath.run();
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
