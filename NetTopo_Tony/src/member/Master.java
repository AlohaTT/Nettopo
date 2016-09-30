package member;

import impl.ConvertImpl;
import impl.SlavesComparator;
import impl.WSNComparator;
import intf.ProxyInterface;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.Property;
import bean.Information;

public class Master {
	/**
	 * @param args
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */

	/**
	 * Master只有一个实例
	 */
	private String topoTypeAndFunction;
	private boolean allTasksDistributed = false;
	private List<WirelessSensorNetwork> tasks = null;
	private List<Information> list = null; // slave 信息列表
	private List<String> mayDeadSlavesIp = null;// 死亡的slaves ip 列表
	private ProxyInterface pi = null;

	// private ProxyInterface px =null;

	public Master(List<WirelessSensorNetwork> tasks) {
		super();
		this.tasks = tasks;
		System.out.println("tasks:" + tasks.size());
	}

	public String getTopoTypeAndFunction() {
		return topoTypeAndFunction;
	}

	public void setTopoTypeAndFunction(String topoTypeAndFunction) {
		this.topoTypeAndFunction = topoTypeAndFunction;
	}

	public Master() {
		super();
	}

	public void setTasksList(List<WirelessSensorNetwork> tasks) {
		this.tasks = tasks;
	}

	public void initialize() throws RemoteException, MalformedURLException,
			AlreadyBoundException, InterruptedException {
		// System.setSecurityManager(new RMISecurityManager());

		// tasks=inputTasks;
		if (pi == null) {
			pi = new ConvertImpl(); // 远程调用类

			// 绑定到服务器上，其实是放到服务器列表里
			LocateRegistry.createRegistry(1099);
			Naming.bind("hello", pi);

		}

		// c=new ConvertImpl(1);
		// Naming.rebind("hello", c);

	}



	public void runMaster() throws Exception {// 运行方法

		initialize(); // 绑定pi类
		// startBeat(); // 开始心跳监测,开启timer线程
		heartBeatverity(); // 2s监测一次
	}

	public boolean compute() throws Exception // 计算包括分配任务，收集结果等过程，这个过程应该是在点击
											// 仿真按钮之后要做的。
	{	
		boolean isComplete = false;
		if (!isPrepared()) {
			return false;
		}
		
		
		File file=new File("./result.txt"); 	//如果结果文件存在，先删除
		if(file.exists())
			file.delete();
		
		while (!isComplete) {
			System.out
					.println("-------------------------------------start--------------");
			distributeTasks();
			isComplete = collectAllResultAndResetStatus();
			System.out.println("is Complete?  " + isComplete);
			System.out
					.println("-------------------------------------end--------------");
			Thread.sleep(1000); // 等1s再询问
		}

		System.out.println("end in master");
		isComplete = false;
		return true;
	}

	public boolean isPrepared() // 判断是否准备工作完成
	{
		if (list == null || list.size() == 0) {
			System.out.println("没有 slave");
			return false;
		} else if (tasks == null || tasks.size() == 0) {
			System.out.println("没有任务");
			return false;
		}
		return true;
	}

	public boolean collectAllResultAndResetStatus() throws IOException {
		boolean finish = false;
		Information inf = null;
		getResultAndResetWorking();// 收集已经计算完的Task
		if (!allTasksDistributed) {// 如果所有任务没有被分配完
			// distributeTasks(list);
			finish = false;
		} else {
			int i = 0;
			if (list == null || list.size() == 0) {
				System.out
						.println("collectAllResultAndResetStatus list is null ");
				return false;
			}
			for (i = 0; i < list.size(); ++i) {
				inf = list.get(i);
				if (inf.isWorking == -1) {
					finish = false;
					break;
				}
			}
			if (i == list.size()) {
				System.out.println("over la");
				finish = true;
			}
		}
		return finish;
	}

	public void heartBeatverity() throws Exception {
		while (true) {
			list = pi.getSlavesList();// 获取slaves列表

			printSlavesIP();// 打印slave列表

			mayDeadSlavesIp = testfyLivingStatus();// 获取死亡slaves
			printDeadSlavesIP();// 打印死亡slaves
			deleteDeadSlaves();// 删除死亡slaves
			resetLivingStatus();// 复位存活标志
			Thread.sleep(2000);
		}
	}

	public void deleteDeadSlaves() throws Exception {// 删除已死slaves
		synchronized (this.list) {
			Information inf = null;
			boolean isAdd = false;
			if (list == null || list.size() == 0 || mayDeadSlavesIp == null
					|| mayDeadSlavesIp.size() == 0)
				return;
			for (String deadIP : mayDeadSlavesIp) {
				for (int i = 0; i < list.size(); ++i) {
					inf = list.get(i);
					if (deadIP.equals(inf.ip)) {
						if (inf.isWorking == 2)// 如果死亡节点中有已经计算完结果的
						{
							writeResult(inf.result);
							//writeImage(inf.taskName,Util.getImageFromByte(inf.image));//-------------------------------------------------------------------------------------
						
							writeImage(inf.taskName,inf.image);
							list.remove(i);
						} else if (inf.isWorking == 0)
							list.remove(i);
						else {
							tasks.add(byteArrayToObject(inf.runConf)); // 把没有做完的任务继续加入到任务列表里
							isAdd = true;
							list.remove(i);

						}
						break;
					}
				}
			}
			if (isAdd) {
				Collections.sort(tasks, new WSNComparator());// 降序排列task
				isAdd = false;
			}

		}
	}

	public void resetLivingStatus()// 重设存活状态
	{
		Information ls = null;
		if (list == null || list.size() == 0) {
			System.out.println("no slave to reset!");
			return;
		}
		for (int i = 0; i < list.size(); ++i) {
			ls = list.get(i);
			ls.isLive = false;
		}
	}

	public List<String> testfyLivingStatus()// 测试死亡slaves,并返回IP列表
	{
		List<String> ls = new ArrayList<String>();
		if (list == null || list.size() == 0) {
			System.out.println("testfyLivingStatus list is null ");
			return null;
		}
		for (Information inf : list) {
			if (!inf.isLive)
				ls.add(inf.ip);
		}
		return ls;
	}

	public void printDeadSlavesIP()// 打印已死slaves ip
	{
		if (mayDeadSlavesIp == null || mayDeadSlavesIp.size() == 0) {
			// System.out.println("They are all alive");
			return;
		}

		else {
			for (String str : mayDeadSlavesIp) {
				System.out.println(str + " is dead.");
			}
		}
	}

	public void printSlavesIP()// 打印slaves ip
	{
		if (list == null || list.size() == 0)
			System.out.println("No Slaves");
		else {
			for (Information inf : list) {
				System.out.println(inf.toString() + " is in list");

			}
		}
	}

	public void removeSlavesByIP(List<String> IPs, ProxyInterface pi) // 删除死亡节点
			throws RemoteException// 删除 slaves ip,
	{
		if (IPs == null || IPs.size() == 0)
			System.out.println("No Slaves going to remove");
		else {
			int index = -1;
			for (String ip : IPs) {
				index = pi.containsSlave(ip);
				if (index > -1)
					pi.getSlavesList().remove(index);

			}
		}
	}

	synchronized public void getResultAndResetWorking() throws IOException // 获取
																			// 所有
																			// 完成的task结果
																			// 写入文件
																			// 并重设
																			// 标记
	{
		Information inf = null;
		if (list == null || list.size() == 0) {
			System.out.println("getResultAndResetWorking list is null ");
			return;
		}

		for (int i = 0; i < list.size(); ++i) {
			inf = list.get(i);
			System.out.println("isWorking:" + inf.isWorking);
			System.out.println("runConf:" + inf.runConf);
			System.out.println("result:" + inf.result);
			
			if (inf.isWorking == 1) {
				if (inf.result != null) {
					System.out.println("Image:"+ inf.image.length);
					writeResult(inf.result);
					//writeImage(inf.taskName,Util.getImageFromByte(inf.image));//--------------------------------------------------------------------------------------
					writeImage(inf.taskName,inf.image);
					inf.result = null;
					
					inf.image=null;
					inf.taskName=null;
					
					inf.isWorking = 0;
					// inf.result="EOF";
					list.set(i, inf);
				}
			}
		}
	}

	synchronized public void distributeTasks() throws Exception // 分配任务,是不是没有必要使用synchronized？
	{

		if (tasks == null) {
			System.out.println("There is no tasks");
			return;
		}
		if (tasks.size() == 0) {
			System.out.println("All tasks has been distribute");
			allTasksDistributed = true;
			return;
		}

		if (list == null || list.size() == 0) {
			System.out.println("distributeTasks no slaves");// 没有slave就要等待
			allTasksDistributed = false;
			return;
		}
		distributMethodDec();

	}

	/********************************************** 任务分配策略 *********************************/

	public void distributMethodDec() throws Exception // FIFS 当任务难度递减
	{
		Information inf = null;

		for (int i = 0; i < list.size(); ++i) {
			inf = list.get(i);
			System.out.println("distribute:==========" + inf.isWorking);
			if (inf.isWorking == 0) {// ------------------
				inf.runConf = objectToByteArray(tasks.get(0));
				inf.taskName=tasks.get(0).getName();
				inf.nameAndFunction = topoTypeAndFunction;
				inf.isWorking = 2; // 任务已经分配好了
				if (tasks.size() != 0) {
					tasks.remove(0);
				}
				list.set(i, inf);
			}
		}
		System.out.println("runconf:" + list.get(0).runConf);
	}

	public void distributMethodInc() throws Exception // FIFS 当任务难度递增
	{
		Information inf = null;

		for (int i = 0; i < list.size(); ++i) {
			inf = list.get(i);
			System.out.println("distribute:==========" + inf.isWorking);
			if (inf.isWorking == 0) {// ------------------
				inf.runConf = objectToByteArray(tasks.get(tasks.size() - 1));
				inf.nameAndFunction = topoTypeAndFunction;
				inf.isWorking = 2; // 任务已经分配好了
				if (tasks.size() != 0) {
					tasks.remove(0);
				}
				list.set(i, inf);
			}
		}
		System.out.println("runconf:" + list.get(0).runConf);
	}

	public void distributMethodByCapacity() throws Exception {	//根据计算能力分配任务
		Collections.sort(list, new SlavesComparator());// 性能降序排序
		if (Property.flag)// 任务升序排列
		{
			distributMethodInc();
		} else // //任务降序排列
		{
			distributMethodDec();
		}
	}

	/********************************************** 任务分配策略end *********************************/
	public void writeResult(String result) throws IOException // 将结果写入文件
	{
		File file = new File("./result.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.append(result);
		bw.flush();
		bw.close();
	}
	
	
	private void writeImage(String wsnName,byte[] data)	//get the image from slaves
	{
		System.out.println("data.length:"+data.length);
		 if(data.length<3) return;//判断输入的byte是否为空
		    try{
		    	String imagePath="./image/";
		    FileImageOutputStream imageOutput = new FileImageOutputStream(new File(imagePath+"/"+wsnName + ".bmp"));//打开输入流
		    System.out.println("imagePath:"+imagePath);
		    imageOutput.write(data, 0, data.length);//将byte写入硬盘
		    imageOutput.close();
		    } catch(Exception ex) {
		      System.out.println("Exception: " + ex);
		      ex.printStackTrace();
		    }
		
		
	}

	static public  byte[] objectToByteArray(WirelessSensorNetwork wsn0)
			throws Exception // 序列化
	{
		byte[] bytes = null;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		// ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(
		// new File("E:/Person.txt")));
		oo.writeObject(wsn0);
		bytes = bo.toByteArray();
		System.out.println("wsn length:" + bytes.length);
		oo.close();
		bo.close();
		return bytes;
	}

	static public WirelessSensorNetwork byteArrayToObject(byte[] bytesBackup)
			throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bytesBackup));
		WirelessSensorNetwork wsn0 = (WirelessSensorNetwork) ois.readObject();
		ois.close();
		return wsn0;
	}

}
