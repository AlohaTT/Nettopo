package org.deri.nettopo.node;

import org.eclipse.swt.graphics.RGB;

public class NodeConfiguration {
	public static final int paintRadius = 6;
	public static final int AttractorRadius = 7;
	public static final RGB SensorNodeColorRGB = new RGB(140,106,230);
	public static final RGB XBowSensorNodeColorRGB = new RGB(248,114,207);
	public static final RGB SinkNodeColorRGB = new RGB(52,137,61);
	public static final RGB XBowSinkNodeColorRGB = new RGB(70,137,78);
	public static final RGB BlackHoleNodeColorRGB = new RGB(0,0,0);
	public static final RGB WhiteColorRGB = new RGB(255,255,255);
	public static final RGB DistrictedAreaColor = new RGB(95,43,135);
	public static final RGB AttractorFieldsColor = new RGB(255,0,0);
	public static final RGB MobilityNodeColor = new RGB(247,50,245);
//	public static final RGB ConnectionColorRGB = new RGB(119, 211, 217);
	public static final RGB ConnectionColorRGB = new RGB(0, 0, 0);
	public static final RGB SourceNodeColorRGB = new RGB(237,5,64);
	public static final RGB HostNodeColorRGB = new RGB(250,14,215);
	public static final RGB AwakeNodeColorRGB = new RGB(36,94,220);
	public static final RGB SleepNodeColorRGB = new RGB(0,0,0);
	
	/*for dv-hop*/
	public static final RGB AnchorNodeColorRGB = new RGB(65,105,225);
	public static final RGB UnknownNodeColorRGB = new RGB(237,145,33);
	public static final RGB UndefinedNodeColorRGB = new RGB(0,0,0);
	
	
	public static final RGB AStarLineColorRGB = new RGB(194,52,176);
	
	
	/* for boundaryCalculate */
	public static final RGB InnerBoundaryNodeRGB = new RGB(171, 105, 197);
	public static final RGB OuterBoundaryNodeRGB = new RGB(240, 128, 128);
	
	//public static final RGB NodeInGasRGB = new RGB(180, 20, 1);
//	public static final RGB NodeInGasRGB = new RGB(36,94,220);
	public static final RGB NodeInGasRGB = new RGB(245, 241, 56);
	
	
	/* connection line color*/
	public static final RGB NodeInPathColor = new RGB(180, 20, 1);
	public static final RGB lineConnectPathColor = new RGB(185,149,86);
	
	/*InterferonNodeColor*/
	public static final RGB InterferonNodeColor = new RGB(128,128,128);
	
	public static final RGB ColorOfBoundaryLine = new RGB(255,0,0);
	// node failure
	public static final RGB DeadNodeRGB = new RGB(0,0,0);
	public static final RGB UnConnectNodeRGB = new RGB(2,12,193);
	
}