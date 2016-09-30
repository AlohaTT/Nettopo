package impl;

import java.util.ArrayList;
import main.MainApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.xml.ReadXML;


public class RunTasksThread implements Runnable {

	private boolean isFinish;
	private ArrayList<WirelessSensorNetwork> allTasks;

	
	 public boolean isFinish() {
		return isFinish;
	}


	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}


	
	
	public RunTasksThread(ArrayList<WirelessSensorNetwork> allTasks) {
		super();
		this.allTasks = allTasks;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		MainApp.master.setTopoTypeAndFunction(ReadXML.topoTypeAndFunction);
		MainApp.master.setTasksList(allTasks);
//		try {
//			allTasks=StaticFunction.copyAllTasks(allTasks);
//		} catch (ClassNotFoundException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
				isFinish = MainApp.master.compute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

}
