package org.deri.nettopo.algorithm.tpgf.function;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.VNodeFactory;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;
import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;

public class StaticFunction {
	
	/*
	 * create wsn lists*********************************************************************************
	 */
	/**
	 * copy wsn
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static WirelessSensorNetwork CloneWSN(WirelessSensorNetwork wsnTemp) throws IOException, ClassNotFoundException {
		// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(wsnTemp);
		// 从流里读出来
		ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
		ObjectInputStream oi = new ObjectInputStream(bi);
		return ((WirelessSensorNetwork)oi.readObject());
	}
	
	
	
	
	public static java.util.List<WirelessSensorNetwork> createWSNLists(WirelessSensorNetwork wsnTemp) throws ClassNotFoundException, IOException, DuplicateCoordinateException
	{
		java.util.List<WirelessSensorNetwork> tasks = new java.util.ArrayList<WirelessSensorNetwork>();
		tasks.add(wsnTemp);
		for(int i=Property.randomAttr[2];i>Property.randomAttr[0];i-=Property.randomAttr[1])
		{
			wsnTemp=CloneWSN(wsnTemp);
			if(randomRemoveNode(wsnTemp,Property.TPGF_step))
			{
				tasks.add(wsnTemp);
			}
			else
				break;
			}
		return tasks;
	}
	
	
	private static boolean randomRemoveNode(WirelessSensorNetwork wsnTemp,int num) throws DuplicateCoordinateException
	{
		int[] nodesExist = wsnTemp.getAllNodesID();
		int size = nodesExist.length;
		if(num >= size){
			System.out.println("删除节点数量大于现有数量");
			return false;
		}
		int[] nodesID = new int[num];
		for(int i=0;i<num;){
			int nextIndex = 1 + new Random().nextInt(size-1);
			if(Util.isIntegerInIntegerArray(nodesExist[nextIndex],nodesID)){
				continue;
			}else if(!wsnTemp.getNodeByID(nodesExist[nextIndex]).getClass().getName().equals("org.deri.nettopo.node.tpgf.SensorNode_TPGF"))
			{
				continue;
			}
			else{
				
				nodesID[i] = nodesExist[nextIndex];
			}
			i++;
		}
		for(int i=0;i<nodesID.length;i++){
			Coordinate c = wsnTemp.deleteNodeByID(nodesID[i]);
			VNode node = VNodeFactory.getInstance("org.deri.nettopo.node.Hole");
			wsnTemp.addNode(node, c);
		}
		return true;
	}
	
	public static java.util.List<WirelessSensorNetwork> copyTasks(java.util.List<WirelessSensorNetwork> tasks) throws ClassNotFoundException, IOException
	{
		if(tasks==null||tasks.size()==0)
			return null;
		java.util.List<WirelessSensorNetwork> temp=new ArrayList<WirelessSensorNetwork>();
		for(int i=0,len=tasks.size();i<len;++i)
		{
			temp.add(CloneWSN(tasks.get(i)));
		}
		return temp;
	}
	
	public static ArrayList<WirelessSensorNetwork> copyAllTasks(ArrayList<WirelessSensorNetwork> tasks) throws ClassNotFoundException, IOException
	{
		if(tasks==null||tasks.size()==0)
			return null;
		ArrayList<WirelessSensorNetwork> temp=new ArrayList<WirelessSensorNetwork>();
		for(int i=0,len=tasks.size();i<len;++i)
		{
			temp.add(CloneWSN(tasks.get(i)));
		}
		return temp;
	}

}
