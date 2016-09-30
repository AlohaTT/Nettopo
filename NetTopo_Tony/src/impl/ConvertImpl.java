package impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


import bean.Information;

import intf.ProxyInterface;

public class ConvertImpl extends UnicastRemoteObject implements ProxyInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Information> list=new ArrayList<Information>();
	
	public ConvertImpl() throws RemoteException{
		super();
	}

	@Override
	synchronized public boolean setAlive(String ip) throws RemoteException {//设置某个slave状态为 “活”
		// TODO Auto-generated method stub
		Information inf=null;
		for(int i=0;i<list.size();++i)
		{
			inf=list.get(i);
			if(ip.equals(inf.ip))
			{
				inf.isLive=true;
				list.set(i, inf);
				return true;
			}
		}
		return false;
	}
	

	@Override
	synchronized public boolean addSlave(String ip) throws RemoteException {//添加slave自己到list
		// TODO Auto-generated method stub
		if(containsSlave(ip)>-1)
		{
			System.out.println("slave "+ip+" exists");
			return false;
		}
		Information temp=new Information(ip,true,0);//ip ;is alive; not working
		list.add(temp);
		System.out.println("init isworking:"+list.get(0).isWorking);
		System.out.println("Add slave "+ip+" successful");
		return true;
	}

	@Override
	synchronized public List<Information> getSlavesList() throws RemoteException {
		// TODO Auto-generated method stub
		return list;
	}

	@Override
	synchronized public int containsSlave(String ip) throws RemoteException {
		// TODO Auto-generated method stub
		Information inf=null;
		for(int i=0;i<list.size();++i)
		{
			inf=list.get(i);
			if(ip.equals(inf.ip))
				return i;
		}
		return -1;
	}

	@Override
	synchronized public String obtainTask(String ip) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	synchronized public Information getInformation(String ip) throws RemoteException {
		// TODO Auto-generated method stub
		Information infTask=null;
		for(Information inf: list)
		{
			if(ip.equals(inf.ip))
			{
				infTask=inf;
			}
		}
		return infTask;
	}

	@Override
	synchronized public boolean setInformation(Information inf) throws RemoteException {
		// TODO Auto-generated method stub
		Information infTemp=null;
		for(int i=0;i<list.size();++i)
		{
			infTemp=list.get(i);
			if(infTemp.ip.equals(inf.ip))
				list.set(i, inf);
		}
		return false;
	}

	@Override
	synchronized public boolean setWorking(String ip, int flag) throws RemoteException {
		// TODO Auto-generated method stub
		Information infTemp=null;
		for(int i=0;i<list.size();++i)
		{
			infTemp=list.get(i);
			if(infTemp.ip.equals(ip))
				{
				infTemp.isWorking=flag;
					list.set(i, infTemp);
					return true;
				}
		}
		return false;
	}

	@Override
	synchronized public int getWorking(String ip) throws RemoteException {
		// TODO Auto-generated method stub
		for(Information inf:list)
		{
			if(ip.equals(inf.ip))
				return inf.isWorking;
		}
		return -10000;
	}


	@Override
	synchronized public boolean setResult(String ip, String result) throws RemoteException {
		// TODO Auto-generated method stub
		Information infTemp=null;
		for(int i=0;i<list.size();++i)
		{
			infTemp=list.get(i);
			if(infTemp.ip.equals(ip))
				{
				infTemp.result=result;
					list.set(i, infTemp);
					return true;
				}
		}
		return false;
	}

	@Override
	synchronized public boolean setStartingTime(String ip, String startingTime)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	synchronized public boolean setEndingTime(String ip, String EndingTime)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	synchronized public byte[] getRunConfiguration(String ip)
			throws RemoteException {
		// TODO Auto-generated method stub
		for(Information inf:list)
		{
			if(ip.equals(inf.ip))
				return inf.runConf;
		}
		return null;
	}

	@Override
	public String getNameAndFunction(String ip) throws RemoteException {
		// TODO Auto-generated method stub
		for(Information inf:list)
		{
			if(ip.equals(inf.ip))
				return inf.nameAndFunction;
		}
		return null;
	}

	@Override
	synchronized public boolean setWSNReturn(String ip,byte[] wsnReturn) throws RemoteException {
		// TODO Auto-generated method stub
		Information infTemp=null;
		for(int i=0;i<list.size();++i)
		{
			infTemp=list.get(i);
			if(infTemp.ip.equals(ip))
				{
				infTemp.wsnReturn=wsnReturn;
					list.set(i, infTemp);
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean setImage(String ip, byte[] imageByte) throws IOException {
		// TODO Auto-generated method stub
		Information infTemp=null;
		for(int i=0;i<list.size();++i)
		{
			infTemp=list.get(i);
			if(infTemp.ip.equals(ip))
				{
				infTemp.image=imageByte;
					list.set(i, infTemp);
					return true;
				}
		}
		return false;
	}
		
	
}
