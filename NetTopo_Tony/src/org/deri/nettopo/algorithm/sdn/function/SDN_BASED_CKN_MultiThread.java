package org.deri.nettopo.algorithm.sdn.function;

import java.util.Timer;
import java.util.TimerTask;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;

public class SDN_BASED_CKN_MultiThread implements AlgorFunc {

	private Algorithm algorithm;
	private SDN_CKN_MAIN2_MutilThread sdn_Ckn;

	public SDN_BASED_CKN_MultiThread(Algorithm algorithm) {
		this.algorithm = algorithm;
		sdn_Ckn = new SDN_CKN_MAIN2_MutilThread();
	}

	public SDN_BASED_CKN_MultiThread() {
		this(null);
	}

	public void run() {
		NetTopoApp app = NetTopoApp.getApp();
		Timer timer = app.getTimer_func();
		TimerTask task = app.getTimertask_func();
		if (timer != null && task != null) {
			task.cancel();
			timer.cancel();
			timer.purge();
			app.setTimertask_func(null);
			app.setTime_func(null);
		}

		app.setTime_func(new Timer());
		app.setTimertask_func(new TimerTask() {
			public void run() {
				entry();
			}
		});
		app.getTimer_func().schedule(app.getTimertask_func(), 0,app.getFunc_INTERVAL() * 100);
	}

	public void entry() {
		sdn_Ckn.run();
//		final StringBuffer message = new StringBuffer();
//		int[] activeSensorNodes = NetTopoApp.getApp().getNetwork().getSensorActiveNodes();
//		message.append("k=" + sdn_Ckn.getK() + ", Number of active nodes is:" + activeSensorNodes.length + ", they are: "
//				+ Arrays.toString(activeSensorNodes));
//
//		NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				NetTopoApp.getApp().refresh();
//				NetTopoApp.getApp().addLog(message.toString());
//			}
//		});
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
