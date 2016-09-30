package intf;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


import bean.Information;

public interface ProxyInterface extends Remote {
	public int containsSlave(String ip) throws RemoteException;

	public boolean setAlive(String ip) throws RemoteException;

	public boolean addSlave(String ip) throws RemoteException;

	public List<Information> getSlavesList() throws RemoteException;

	public String obtainTask(String ip) throws RemoteException;

	public Information getInformation(String ip) throws RemoteException;

	public boolean setInformation(Information inf) throws RemoteException;
	
	public boolean setWorking(String ip, int flag) throws RemoteException;
	
	public int getWorking(String ip) throws RemoteException;
	
	public boolean setResult(String ip,String result) throws RemoteException;
	
	public boolean setStartingTime(String ip,String startingTime) throws RemoteException;

	public boolean setEndingTime(String ip,String EndingTime) throws RemoteException;

	public byte[] getRunConfiguration(String ip) throws RemoteException;
	
	public String getNameAndFunction(String ip) throws RemoteException;
	
	public boolean setWSNReturn(String ip,byte[] wsnReturn) throws RemoteException;
	
	public boolean setImage(String ip,byte[] imageByte) throws IOException;
	
}
