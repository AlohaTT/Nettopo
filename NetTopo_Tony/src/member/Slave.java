package member;

import impl.SlaveHeartBeatThread;
import intf.ProxyInterface;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.Util;


public class Slave {
	private ProxyInterface pi;
	private String ip;
	private String masterip;

	public void heartBeat() throws Exception {
		// TODO Auto-generated method stub
		// System.setSecurityManager(new RMISecurityManager());

		// while (true) {// 这里因为是在本地所以省略了地址跟协议，若在网络中的远程方法调用，需要这样写
		pi = getProxy();
		if (pi == null)
			return;
		getIP();
		if (pi.containsSlave(ip) == -1) {
			pi.addSlave(ip);
		} else {
			if (pi.setAlive(ip)) ;// 成功返回
//				System.out.println("set slave " + ip + " alive true");
//			else
//				System.out.println("die");
		}
	}

	public ProxyInterface getProxy() {
		ProxyInterface pi0=null;
		try {
			pi0 = (ProxyInterface) Naming
					.lookup("rmi://"+masterip+":1099/hello");
			return pi0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		
	}

	public void getIP() throws UnknownHostException {
		ip = InetAddress.getLocalHost().getHostAddress();
	}

	public void runTask() throws Exception {
		boolean isDone = false;
		int isworkingOrNot = -10000;
		Thread th=null;
		SlaveHeartBeatThread hbt=null;
		
		System.out.println("slave-----------------------------------");
		while (!isDone) {
			heartBeat();
			isworkingOrNot = pi.getWorking(ip);
			//System.out.println("isworkingOrNot:" + isworkingOrNot);
			if (isworkingOrNot == 2) {// 任务已经准备好
				pi.setWorking(ip, -1); // 设置正在工作
			//System.out.println("Cnf:" + pi.getRunConfiguration(ip));
				hbt = new SlaveHeartBeatThread(
						Util.byteArrayToObject(pi.getRunConfiguration(ip)),pi.getNameAndFunction(ip));
				 th = new Thread(hbt);
				th.start();
				//pi.setWorking(ip, -1);
				if (!th.isAlive()) {
					pi.setResult(ip, hbt.getResult() + "");
					pi.setWorking(ip, 1); // 工作结束
					System.out.println("isworking:" + pi.getWorking(ip));
				}
			}
			else if(isworkingOrNot == -1)	//等于-1时，要判断是不是工作已经结束，还没有改变标志位
			{
				if (!th.isAlive()) {
					pi.setResult(ip, hbt.getResult() + "");
					System.out.println("result:"+hbt.getResult());
					pi.setWorking(ip, 1); // 工作结束
					pi.setWSNReturn(ip, Util.objectToByteArray(NetTopoApp.getApp().getNetwork()));
					System.out.println("NetTopoApp.getApp().getBufferImage(false).getImageData().data.length:"+NetTopoApp.getApp().getBufferImage(false).getImageData().data.length);
					byte[] image=Util.imageToArray(NetTopoApp.getApp().getBufferImage(false));
					System.out.println("hehehhaahdiaidsa:"+image.length);
					pi.setImage(ip,image);
				}
			}
			Thread.sleep(1000);
		}
	}
	
	public int testMaster(String masterip){
		this.masterip=masterip;
		@SuppressWarnings("unused")
		ProxyInterface piTest=null;
		try {
			piTest = (ProxyInterface) Naming
					.lookup("rmi://"+masterip+":1099/hello");
			return 1;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -1;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -2;
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -3;
		}
	}
	
	
}
