package bean;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Information extends UnicastRemoteObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String ip;
	public byte[] runConf;// the configuration of the sub-simulation.
	public boolean isLive;// the status of slave die=false, live=true
	public int isWorking;// -1: working   0:free    1:complete but not collect the result  2:ready and going to work 
	public double timeComsumed;
	public double capacity;
	public byte[] image; 
	public byte[] wsnReturn;
	public String result;
	public String nameAndFunction;
	public String taskName;
	public Information(String ip, boolean isLive, int isWorking) throws RemoteException {
		super();
		this.ip = ip;
		this.isLive = isLive;
		this.isWorking = isWorking;
	}
	@Override
	public String toString() {
		return "Information [ip=" + ip + ", runConf=" + runConf + ", isLive="
				+ isLive + ", isWorking=" + isWorking + ", startTime="
				+ "]";
	}
	
	
	

}
