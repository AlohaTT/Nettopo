package org.deri.nettopo.util;

import java.util.HashMap;

import org.deri.nettopo.network.WirelessSensorNetwork;

public class Property {
	/**
	 * flag=false;//true 为递增，false为递减
	 * <p>
	 * 
	 */
	public static int TPGF_step = 100;
	public static int TPGF_min = 400; // 边界大小
	public static int TPGF_max = 600; // 当前传感器节点数量
	public static boolean flag = false;// true 为递增，false为递减
	public static boolean isSlave = true;
	public static boolean isTPGF = false; // 是否是并行
	public static HashMap<String, Integer> nodeAttr;
	public static int[] randomAttr;
	public static boolean isSensor = false;
	public static java.util.List<WirelessSensorNetwork> tasks;
	public static int times = 5;// ckn执行次数
	public static boolean needpaint = true;
	public static boolean isCreateTasks = false;
	public static boolean isCreateOneTask = false;
	public static final double R=0.7;	// delete neighbour rate
	
	public static double nodeRate=0.0;
	public static double neighborRate=0.0;
	
	/*denger level table*/
	public static int getDengerLevel(String gasName)
	{
		if(gasName.equals("CO"))		//一氧化碳
			return 1;
		else if(gasName.equals("CL2"))	//氯气
			return 14;//200
		else if(gasName.equals("C2H4O"))	//环氧乙烷
			return 6;//40
		else if(gasName.equals("HCHO"))//甲醛
			return 10;//100
		else if(gasName.equals("HCl"))//甲醛
			return 7;//52
		else if(gasName.equals("H2S"))//甲醛
			return 3;//10
		else if(gasName.equals("SO2"))//甲醛
			return 7;//50
		return -1;
	}
}
