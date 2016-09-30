package org.deri.nettopo.algorithm.tpgf.function;

import java.io.*;
import java.util.Date;

public class TPGF_Statistics_MoreInfo {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		PrintWriter pw = new PrintWriter("C:/TPGF_Stat.log");
		pw.println("*******************************************************************");
		pw.println("* This file gives the statistical simulation results including ****");
		pw.println("* average number of searched paths, average number of original ****");
		pw.println("* hops and average number of optimized hops by using TPGF without**");
		pw.println("* any planarization ***********************************************");
		pw.println("*******************************************************************");
		pw.println();
		pw.println("Network Size: 600 * 600.");
		pw.println("Number of seed: 100.");
		pw.println("Start time: " + new Date());
		pw.flush();
		
		int tr_start = 60;
		int tr_step = 5;
		int tr_times = 10;
		int node_start = 100;
		int node_step = 100;
		int nodeTimes = 10;
		
		int netLength = 600;
		int netWidth = 600;
		int seedNum = 100;
		
		float[][] avgPathNums = new float[tr_times][nodeTimes];
		float[][] avgOrHopNums = new float[tr_times][nodeTimes];
		float[][] avgOpHopNums = new float[tr_times][nodeTimes];
		
		TPGF_Statistics statistics = new TPGF_Statistics();
		statistics.setSeedNum(seedNum);
		statistics.setSize(netLength, netWidth);
		for(int i=0;i<tr_times;i++){
			statistics.setTR(tr_start + i * tr_step);
			for(int j=0;j<nodeTimes;j++){
				statistics.setNodeNum(node_start + j * node_step);
				statistics.run();
				avgPathNums[i][j] = statistics.getAvgPathNum();
				avgOrHopNums[i][j] = statistics.getAveOrHopNum();
				avgOpHopNums[i][j] = statistics.getAvgOpHopNum();
			}
		}
		
		pw.println("End time: " + new Date());
		pw.println();
		pw.println("****** The followings are two versions of the result. ******");
		pw.println();
		pw.println("****** This is TR-fist version. ******");
		pw.println("TR\tNodeNum\tAvgPathNum\tAvgOrHopNum\tAvgOpHopNum");
		pw.flush();
		for(int i=0;i<tr_times;i++){
			for(int j=0;j<nodeTimes;j++){
				pw.print(tr_start + i * tr_step);
				pw.print("\t");
				pw.print(node_start + j * node_step);
				pw.print("\t\t");
				pw.print(TPGF_Statistics.reserve2(avgPathNums[i][j]));
				pw.print("\t\t");
				pw.print(TPGF_Statistics.reserve2(avgOrHopNums[i][j]));
				pw.print("\t\t");
				pw.print(TPGF_Statistics.reserve2(avgOpHopNums[i][j]));
				pw.println();
			}
			pw.flush();
		}
		pw.println();
		pw.println();
		pw.println("****** This is node-fist version. ******");
		pw.println("TR\tNodeNum\tAvgPathNum\tAvgOrHopNum\tAvgOpHopNum");
		pw.flush();
		/* print another kind of view of the result */
		for(int j=0;j<nodeTimes;j++){
			for(int i=0;i<tr_times;i++){
				pw.print(tr_start + i * tr_step);
				pw.print("\t");
				pw.print(node_start + j * node_step);
				pw.print("\t\t");
				pw.print(TPGF_Statistics.reserve2(avgPathNums[i][j]));
				pw.print("\t\t");
				pw.print(TPGF_Statistics.reserve2(avgOrHopNums[i][j]));
				pw.print("\t\t");
				pw.print(TPGF_Statistics.reserve2(avgOpHopNums[i][j]));
				pw.println();
			}
			pw.flush();
		}
		pw.println();
		pw.print("***** The End. *****");
		pw.close();
		
		FileOutputStream fos = new FileOutputStream("C:/TPGF_Stat.meta");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		StatisticsMeta stat = new StatisticsMeta();
		stat.tr_start = tr_start;
		stat.tr_step = tr_step;
		stat.tr_times = tr_times;
		stat.node_start = node_start;
		stat.node_step = node_step;
		stat.nodeTimes = nodeTimes;
		stat.netLength = netLength;
		stat.netWidth = netWidth;
		stat.seedNum = seedNum;
		stat.avgPathNums = avgPathNums;
		stat.avgOrHopNums = avgOrHopNums;
		stat.avgOpHopNums = avgOpHopNums;
		oos.writeObject(stat);
		
		
		FileInputStream fis = new FileInputStream("C:/TPGF_Stat.meta");
		ObjectInputStream ois = new ObjectInputStream(fis);
		StatisticsMeta stat_in = (StatisticsMeta)ois.readObject();
		System.out.println(stat_in.tr_start);
		System.out.println(stat_in.tr_step);
		System.out.println(stat_in.tr_times);
		System.out.println(stat_in.node_start);
		System.out.println(stat_in.node_step);
		System.out.println(stat_in.nodeTimes);
		System.out.println(stat_in.netLength);
		System.out.println(stat_in.netWidth);
		System.out.println(stat_in.seedNum);
		float avgPath[][] = stat_in.avgPathNums;
//		float avgOr[][] = stat_in.avgOrHopNums;
		float avgOp[][] = stat_in.avgOpHopNums;
		for(int i=0;i<stat_in.tr_times;i++){
			for(int j=0;j<stat_in.nodeTimes;j++){
				System.out.print(avgPath[i][j] + "\t");
				System.out.println(avgOp[i][j]);
			}
		}
	}
}
