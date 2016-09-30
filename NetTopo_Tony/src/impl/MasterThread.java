package impl;

import java.util.List;

import member.Master;

public class MasterThread implements Runnable {

	private Master master;
	
	
	
	public MasterThread(Master master, List<String> list) {
		super();
		this.master = master;
	}
	
	


	public MasterThread(Master master) {
		super();
		this.master = master;
	}




	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			master.runMaster();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public Master getMaster() {
		return master;
	}
	public void setMaster(Master master) {
		this.master = master;
	}
	

}
