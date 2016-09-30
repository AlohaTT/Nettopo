package org.deri.nettopo.algorithm.sdn.function;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.Property;

public class SDN_BASED_CKN implements AlgorFunc {

	private Algorithm algorithm;
	private SDN_CKN_MAIN sdn_Ckn;

	public SDN_BASED_CKN(Algorithm algorithm) {
		this.algorithm = algorithm;
		sdn_Ckn = new SDN_CKN_MAIN();
	}

	public SDN_BASED_CKN() {
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
		int times=Property.times;//����times
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
					} else {	//���
						NetTopoApp app = NetTopoApp.getApp();
						Timer timer = app.getTimer_func();
						TimerTask task = app.getTimertask_func();
						if(timer != null && task != null){
							task.cancel();
							timer.cancel();
							timer.purge();
							app.setTimertask_func(null);
							app.setTime_func(null);
						}//���
					}
				}
				else
				{
					entry();
				}
			}
		});
		app.getTimer_func().schedule(app.getTimertask_func(), 0, app.getFunc_INTERVAL() * 10000);
		if(Property.isTPGF)
		{
			while(app.getTimer_func()!=null);
			System.out.println("pause");
			Property.times=times;//��ԭ
		}
			sdn_Ckn.run();
	}

	public void entry() {
		sdn_Ckn.run();
		final StringBuffer message = new StringBuffer();
		int[] activeSensorNodes = NetTopoApp.getApp().getNetwork().getSensorActiveNodes();
		message.append("k=" + sdn_Ckn.getK() + ", Number of active nodes is:" + activeSensorNodes.length
				+ ", they are: " + Arrays.toString(activeSensorNodes));

		NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
			public void run() {
				NetTopoApp.getApp().refresh();
				NetTopoApp.getApp().addLog(message.toString());
			}
		});
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
