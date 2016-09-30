package org.deri.nettopo.xml;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.VNodeFactory;
import org.deri.nettopo.topology.Topology;
import org.deri.nettopo.topology.TopologyFactory;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * dom4j框架学习： 读取并解析xml
 * 
 * 
 */
public class ReadXML {
	
	private static long	changeTime=1L;
	public static  String topoTypeAndFunction;
	public static Map<String,String> topoTypesAndFunctions;
	public static String FILEPATH=null;
	private static ArrayList<String> tasksName;
	
	
	
	private static ArrayList<WirelessSensorNetwork> allTasksList=null;

	
	
	public static ArrayList<String> getTasksName() {
		if(tasksName==null||tasksName.size()==0)
		 tasksName=getAllTasksName();
		return tasksName;
	}


	public static ArrayList<WirelessSensorNetwork> getAllTasksList() throws Exception {
		if(allTasksList==null||allTasksList.size()==0)
			allTasksList=createAllTasks();
		return allTasksList;
	}


	public static void setAllTasksList(ArrayList<WirelessSensorNetwork> allTasksLista) {
		allTasksList = allTasksLista;
	}


	private static WirelessSensorNetwork  createWSN(String rootName) throws Exception {
		WirelessSensorNetwork wsn=new WirelessSensorNetwork();
		wsn.setName(rootName);
		File fileTemp=new File(rootName);
		String fileNameTemp = fileTemp.getName();
		wsn.setName(fileNameTemp.substring(0,fileNameTemp.indexOf(".")));
		WirelessSensorNetwork.setCurrentID(1);
		VNode node=null;
		Topology topo=null;
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File(rootName));

		// 获取根元素
		Element root = document.getRootElement();
		// 获取所有子元素
		@SuppressWarnings("unchecked")
		List<Element> childList = root.elements();
		@SuppressWarnings("unchecked")
		List<Element> elementOfWSNs=root.elements("WirelessSensorNetwork");//get all the elements call WirelessSensorNetwork and put into the list
		if(elementOfWSNs==null||elementOfWSNs.size()==0)
			{
			System.out.println("wsn worng");
				return null;
			}
		Coordinate c=new Coordinate();
		@SuppressWarnings("unchecked")
		List<Element> elementOfWSN=elementOfWSNs.get(0).elements();
		for (Element ec : elementOfWSN) {
			String sun=ec.getName();
			if(sun.equals("Length"))
			{
				c.x=Integer.parseInt(ec.getText());
			}
			else if(sun.equals("Width"))
			{
				c.y=Integer.parseInt(ec.getText());
			}
			else if(sun.equals("Hight"))
			{
				c.z=Integer.parseInt(ec.getText());
			}
			else
				NetTopoApp.getApp().addLog("There are some problems in xml at tag of WilessSensorNetwork");
		}
		wsn.setSize(c);
		NetTopoApp.getApp().setNetwork(wsn);
		for (Element e : childList) {
			String child=e.getName();
			if (child.equals("WirelessSensorNetwork")) 
				{
				//do nothing
				}
			else if(child.equals("Node"))//
			{	boolean propertyReady=false;
				boolean topoReady=false;
				node=VNodeFactory.getInstance(e.attributeValue("class").trim());
				
				if("CreateOneNode".equals(e.attributeValue("type").trim()))
				{
					@SuppressWarnings("unchecked")
					List<Element> singleNode = e.elements();
					for (Element sn : singleNode) {
						if(sn.getName().equals("Method"))
							{
								if(sn.attributeValue("name").equals("random"))
								{
									Coordinate co = new Coordinate((int)(Math.random()*c.x),(int)(Math.random()*c.y),(int)(Math.random()*c.z));
									createOneNode(node,wsn,co);
									
								}
								else if(sn.getText().equals("location"))
								{
									Coordinate coLocation = new Coordinate();
									@SuppressWarnings("unchecked")
									List<Element> xyz = sn.elements();
									for (Element subXYZ : xyz) {
										if(subXYZ.getName().equals("x"))
											coLocation.x = Integer.parseInt(subXYZ.getText());
										if(subXYZ.getName().equals("y"))
											coLocation.y = Integer.parseInt(subXYZ.getText());
										if(subXYZ.getName().equals("z"))
											coLocation.z = Integer.parseInt(subXYZ.getText());
										else
											NetTopoApp.getApp().addLog("There are some problems in xml at tag of Method");
									}
									createOneNode(node,wsn,coLocation);
								}
							}
					}
				}

				else if("CreateNodes".equals(e.attributeValue("type").trim()));
				{
				@SuppressWarnings("unchecked")
				List<Element> childchildList = e.elements();
				for (Element ec : childchildList) {
					if(ec.getName().equals("Property"))
					{
						@SuppressWarnings("unchecked")
						List<Element> cccl = ec.elements();
						for(Element ecc : cccl)
						{
							node.setAttrValue(ecc.getName().replaceAll("_"," "), ecc.getText());
						}
						propertyReady=true;
					}
					else if(ec.getName().equals("Topology"))
					{
						topo=TopologyFactory.getInstance(ec.attributeValue("class").trim());
						@SuppressWarnings("unchecked")
						List<Element> cccl = ec.elements();
						for(Element ecc : cccl)
						{
							topo.setArgValue(ecc.getName().replaceAll("_"," "), ecc.getText());
						}
						topoReady=true;
					}
					else
						NetTopoApp.getApp().addLog("There are some problems in xml at tag of Node");
				}
				if(propertyReady&&topoReady)
				{
					createNodes(node,wsn,topo);
				}
			}
			}
			else if(child.equals("Algorithm"))
			{
				String className=e.attributeValue("class").trim();
				String[] alg=className.split("\\.");
				topoTypeAndFunction=className.substring(0,className.lastIndexOf("."));
				topoTypeAndFunction+="."+"Algor_"+alg[alg.length-3].toUpperCase();
				topoTypeAndFunction=topoTypeAndFunction.replace("function.", "");
				topoTypeAndFunction +=":"+ e.attributeValue("class").trim();
			}
			else
				{
					NetTopoApp.getApp().addLog("There are some problems in xml at tag of Node");
					return null;
				}
		}
			return wsn;
		
	}
	
	
	private static void createNodes(VNode node,WirelessSensorNetwork wsn,Topology topo)
	{
		Coordinate[] coordinates=topo.getCoordinates();
		/* create a wireless sensor node and set it's attributes */
		for(int i=0;i<coordinates.length;i++){
			VNode newNode=VNodeFactory.getInstance(node.getClass().getName());
			String[] attrNames = node.getAttrNames();
			for(int j=0;j<attrNames.length;j++){
				//System.out.println(page_nodeAttr.getAttrValue(attrNames[j])+":"+attrNames[j]);
				newNode.setAttrValue(attrNames[j], node.getAttrValue(attrNames[j]));
			}
			try{
				//System.out.println("NodeNewMaxTR:"+newNode.getAttrValue("Max TR"));
				wsn.addNode(newNode, coordinates[i]);
				//NetTopoApp.getApp().getPainter().paintNode(node.getID());
			} catch(DuplicateCoordinateException ex){
				ex.printStackTrace();
			}	
		}
	}
	
	
	private static void createOneNode(VNode node,WirelessSensorNetwork wsn,Coordinate coor)
	{
		VNode newNode=VNodeFactory.getInstance(node.getClass().getName());
		String[] attrNames = node.getAttrNames();
		for(int j=0;j<attrNames.length;j++){
			//System.out.println(page_nodeAttr.getAttrValue(attrNames[j])+":"+attrNames[j]);
			newNode.setAttrValue(attrNames[j], node.getAttrValue(attrNames[j]));
		}
		try{
			//System.out.println("NodeNewMaxTR:"+newNode.getAttrValue("Max TR"));
			wsn.addNode(newNode, coor);
			//NetTopoApp.getApp().getPainter().paintNode(node.getID());
		} catch(DuplicateCoordinateException ex){
			ex.printStackTrace();
		}	
	}
	
	public static ArrayList<String> getAllTasksName()
	{
		ArrayList<String> tasksName=new ArrayList<String>();
		File file=new File(FILEPATH);
		File[] tasksFile=file.listFiles();
		for(File f:tasksFile)
		{
			String filename=f.getName();
			if(filename.endsWith(".xml"))
			{
				filename=filename.substring(0,filename.indexOf("."));
				tasksName.add(filename);
			}
		}
		return tasksName;
	}
	
	public static ArrayList<WirelessSensorNetwork> createAllTasks() throws Exception
	{
		File file=new File(FILEPATH);
		
		File[] tasksFile=file.listFiles();
		 tasksName=getAllTasksName();//计算任务要保存当时的task列表
		changeTime=file.lastModified();
		WirelessSensorNetwork tempWSN=null;
		ArrayList<WirelessSensorNetwork> allTasks=new ArrayList<WirelessSensorNetwork>();
		//if(topoTypesAndFunctions==null||topoTypesAndFunctions.size()==0)
			topoTypesAndFunctions=new HashMap<String, String>();
		
		for(File f:tasksFile)
		{	
			tempWSN=createWSN(f.getPath());
			if(tempWSN!=null)
			{
				tempWSN.setAlgorithmName(topoTypeAndFunction);
				allTasks.add(tempWSN);
				System.out.println("topoTypeAndFunction:"+topoTypeAndFunction);
				topoTypesAndFunctions.put(f.getName(), new String(topoTypeAndFunction));
			}
			
		}
		return allTasks;
	}
	
	
	public static boolean isModified()
	{
		if(changeTime==(new File(FILEPATH).lastModified()))
			return false;
		return true;
	}
	
}
