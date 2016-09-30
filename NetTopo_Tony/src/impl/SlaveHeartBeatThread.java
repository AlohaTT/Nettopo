package impl;

import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.eclipse.swt.widgets.Display;

public class SlaveHeartBeatThread implements Runnable {

	private WirelessSensorNetwork wsn;
	private String result="";
	private Algorithm algorithm;
	private String nameAndFunction;
	
	public SlaveHeartBeatThread(WirelessSensorNetwork wsn,String nameAndFunction) {
		super();
		this.wsn = wsn;
		this.nameAndFunction=nameAndFunction;
	}
	
	
	@Override
	public void run() {
		 new Thread(new Runnable(){
			 @Override
			 public void run() {
				 Display.getDefault().syncExec(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(!NetTopoApp.getApp().isOpen())
						NetTopoApp.getApp().run();
					}
				 });
				}
			 }).start();	

			 
//			 while(NetTopoApp.getApp().getCmp_graph()==null);
		class TempRunnable implements Runnable{
			 private boolean isFinished=false;
			 
			 
			 
			 @Override
			 public void run() {
				 Display.getDefault().syncExec(new Runnable(){

				@Override
				public void run() {
					NetTopoApp.getApp().setNetwork(wsn);
					isFinished = NetTopoApp.getApp().cmd_repaintNetwork();
				}
			 });
	}
			
		}		 
		TempRunnable tr= new TempRunnable();
	Thread tempThread =	 new Thread(tr);
	tempThread.start();
	while(!tr.isFinished);
	nameAndFunction = wsn.getAlgorithmName();
		String[] str=nameAndFunction.trim().split(":");
		try {
			algorithm = (Algorithm) Class.forName(str[0]).newInstance();
			AlgorFunc[] algorFunc=algorithm.getFunctions();
			AlgorFunc function=null;
			for(AlgorFunc af:algorFunc)
			{
				if(af.getClass().getName().equals(str[1]))
				{
					function=af;
				}
			}
			function.run();
			result="name:"+wsn.getName()+"\n"+"functionName:"+str[1]+"\n"+function.getResult()+"\n"+"sensorNumber:"+wsn.getAllSensorNodesID().length+"\n";
			//Property.isTPGF=false;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}



	public String getResult() {
		return result;
	}

	
	public void intgerListToString(List<Integer> path)
	{
		for(Integer i:path)
		{
			result+=" "+i;
		}
	}
}
