package org.deri.nettopo.algorithm.astar.function;

//org.deri.nettopo.algorithm.astar.function.Connect_Graphic
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;

import bean.Edge;
import bean.Triangle;

public class Connect_LDelauany implements AlgorFunc {
	private WirelessSensorNetwork wsn;
	private Painter painter;

	private List<Triangle> tempTriangle;
	private List<Edge> edgeBuffer;
	private List<Triangle> triangles;
	private HashMap<Integer, Integer[]> neighbors;
	private HashMap<Integer, Integer[]> neighborsOf2Hops;
	private HashMap<Integer, List<Triangle>> delTriangle;
	private List<Triangle> lastTriangles;

	private Algorithm algorithm;

	public Connect_LDelauany(Algorithm algorithm) {
		this.algorithm = algorithm;
		neighbors = new HashMap<Integer, Integer[]>();
		neighborsOf2Hops = new HashMap<Integer, Integer[]>();
		delTriangle = new HashMap<Integer, List<Triangle>>();
		
		
		
	}

	public Connect_LDelauany() {
		this(null);
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public void run() {
//		connect(true);
		
		del(true);
	}
	
	public void del(boolean needPainting) {
		
		lastTriangles = new ArrayList<Triangle>();
		wsn = NetTopoApp.getApp().getNetwork();
		
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		
		func_connectNeighbors.connectNeighbors(false); // 计算所有一跳邻居节点

		initializeNeighbors();
		initializeNeighborsOf2Hops();
		computeAllDel();
		clearAllNeighbor();// 清除当前所有传感节点的邻居节点		
		TPGF_Planarization_GG gg = new TPGF_Planarization_GG(null);
		gg.Planarization("GG", false);
		
		int idA = 0, idB = 0, idC = 0;
		for (Triangle tr : lastTriangles) {
			idA = tr.getIDA();
			idB = tr.getIDB();
			idC = tr.getIDC();
			VNode nodeA = wsn.getNodeByID(idA);
			VNode nodeB = wsn.getNodeByID(idB);
			VNode nodeC = wsn.getNodeByID(idC);
			if (tr.FindPointName() == 1) {
				if (!((SensorNode_TPGF) (nodeA)).getNeighbors().contains(idB)) {
					((SensorNode_TPGF) (nodeA)).getNeighbors().add(idB);
					 ((SensorNode_TPGF)(nodeB)).getNeighbors().add(idA);
				}
				if (!((SensorNode_TPGF) (nodeA)).getNeighbors().contains(idC)) {
					((SensorNode_TPGF) (nodeA)).getNeighbors().add(idC);
					 ((SensorNode_TPGF)(nodeC)).getNeighbors().add(idA);
				}

			} else if (tr.FindPointName() == 2) {
				
				
				if (!((SensorNode_TPGF) (nodeB)).getNeighbors().contains(idA)) {
					((SensorNode_TPGF) (nodeB)).getNeighbors().add(idA);
					 ((SensorNode_TPGF)(nodeA)).getNeighbors().add(idB);
				}
				if (!((SensorNode_TPGF) (nodeB)).getNeighbors().contains(idC)) {
					((SensorNode_TPGF) (nodeB)).getNeighbors().add(idC);
					 ((SensorNode_TPGF)(nodeC)).getNeighbors().add(idB);
				}
			} else if (tr.FindPointName() == 3) {
				
				if (!((SensorNode_TPGF) (nodeC)).getNeighbors().contains(idB)) {
					((SensorNode_TPGF) (nodeC)).getNeighbors().add(idB);
					 ((SensorNode_TPGF)(nodeB)).getNeighbors().add(idC);
				}
				if (!((SensorNode_TPGF) (nodeC)).getNeighbors().contains(idA)) {
					((SensorNode_TPGF) (nodeA)).getNeighbors().add(idC);
					 ((SensorNode_TPGF)(nodeC)).getNeighbors().add(idA);
				}
			} else {
			}

		}

		if (needPainting) {
			painter = NetTopoApp.getApp().getPainter();
			paintAllConnection(painter);
		}

		if (needPainting) {
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}

	}

	public void computeAllDel() {

		
		int[] ids = wsn.getAllSensorNodesID();
		
		for (int index : ids) {

			computeOneDel(index);
		}
		for (int id : ids) {
			List<Triangle> trangles = delTriangle.get(id);
			
			for (Triangle tri : trangles) {
				if (tri.isSpecialTriangle()) {
					if (containTriangle(tri, id)) {
						tri.setCenterID(id);
						lastTriangles.add(tri);
					}
				} // special triangle
			}
		}
	}

	/**
	 * //判断三角形是否在别的del中出现
	 * 
	 * @param tr
	 *            待判断三角形
	 * @param currentId
	 *            当前三角形所属的核心点ID
	 * @return
	 */

	private boolean containTriangle(Triangle tr, int currentId) {
		Set<Integer> ids = delTriangle.keySet();
		for (Integer id : ids) {
			if (currentId != id) {
				List<Triangle> trangles = delTriangle.get(id);
				if (trangles.contains(tr))
					return true;
			}
		}
		return false;
	}

	public void computeOneDel(int nodeId) // del一个节点
	{
		tempTriangle = new ArrayList<Triangle>(); // step1 初始化临时三角形列表
		edgeBuffer = new ArrayList<Edge>(); // 初始化边表
		triangles = new ArrayList<Triangle>(); // 初始化del三角形列表
		createSuperTriangle(nodeId);// 创建超级三角形，并加入三角形列表
		List<Triangle> removeList = new ArrayList<Triangle>(); // 创建删除表
		Integer[] twoHopsIdSet = neighborsOf2Hops.get(nodeId);
		for (int id : twoHopsIdSet) {
			
			Coordinate coor = wsn.getCoordianteByID(id);
			for (Triangle tr : tempTriangle) {
				
				String tmpStr = tr.isInCircle(coor);
				if (tmpStr.equals("in")) {
					edgeBuffer.addAll(tr.getThreeEdges());// 把边加入 边表
					removeList.add(tr);
				}
			}
			for (Triangle removeTr : removeList) // 删除三角形
			{
				tempTriangle.remove(removeTr);
			}
			removeList.clear();
			clearCommonEdge();
			createTriangleByEdges(coor, id);// 根据边创建三角形
			edgeBuffer.clear();
		}
		triangles = tempTriangle;
		deleteTrianglesContainsSuperTriangle(); // 删除与超级三角形三个点有关的三角形
		deleteBigThanMaxTr();// 删除大于max的三角形;
		deleteNotContainAndDegree(nodeId); // 标记需要广播的三角形
		delTriangle.put(nodeId, triangles);
	}

	
	
	public void deleteNotContainAndDegree(int nodeId) {
		for (Triangle tr : triangles) {
			if (tr.containIdAndGreaterThanDegree(nodeId))
			tr.setSpecialTriangle(true);
		}
	}

	private void deleteBigThanMaxTr() {
		List<Triangle> tempTr = new ArrayList<Triangle>();
		int maxtr;
		for (Triangle tr : triangles) {
			maxtr = Integer.parseInt(wsn.getNodeByID(tr.getIDA()).getAttrValue("Max TR"));
			if (!tr.greaterThanMaxTr(maxtr))
				tempTr.add(tr);
		}
		triangles = tempTr;
	}

	private void createSuperTriangle(int nodeId) // 构建超级三角形
	{
		int xmin = Integer.MAX_VALUE, ymin = Integer.MAX_VALUE, xmax = Integer.MIN_VALUE, ymax = Integer.MIN_VALUE;
		Coordinate cor = null;
		Integer[] neighbors = neighborsOf2Hops.get(nodeId);
		for (int id : neighbors) {
			cor = wsn.getCoordianteByID(id);
			if (cor.x > xmax)
				xmax = cor.x;
			if (cor.x < xmin)
				xmin = cor.x;
			if (cor.y > ymax)
				ymax = cor.y;
			if (cor.y < ymin)
				ymin = cor.y;
		}

		Coordinate A = new Coordinate(xmin + (xmax - xmin) / 2, ymin
				- (ymax - ymin) - 3);// top
		Coordinate B = new Coordinate(xmin - (xmax - xmin) / 2 - 3, ymax + 3); // left
		Coordinate C = new Coordinate(xmax + (xmax - xmin) / 2 + 3, ymax + 3); // right
		Triangle superTriangle = new Triangle(A, B, C, -1, -2, -3);
		tempTriangle = new ArrayList<Triangle>();
		tempTriangle.add(superTriangle);

	}

	private void initializeNeighbors() {
		int[] ids = wsn.getAllSensorNodesID();
		for (int i = 0; i < ids.length; i++) {
			Integer ID = new Integer(ids[i]);
			Integer[] neighbor = getNeighbor(ids[i]);
			neighbors.put(ID, neighbor);
		}
	}

	private Integer[] getNeighbor(int id) {
		int[] ids = wsn.getAllSensorNodesID();
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int maxTR = Integer
				.parseInt(wsn.getNodeByID(id).getAttrValue("Max TR"));
		Coordinate coordinate = wsn.getCoordianteByID(id);
		for (int i = 0; i < ids.length; i++) {
			Coordinate tempCoordinate = wsn.getCoordianteByID(ids[i]);
			if (ids[i] != id
					&& Coordinate.isInCircle(tempCoordinate, coordinate, maxTR)) {
				neighbor.add(new Integer(ids[i]));
			}
		}
		return neighbor.toArray(new Integer[neighbor.size()]);
	}

	private void initializeNeighborsOf2Hops() {
		int[] ids = wsn.getAllSensorNodesID();
		for (int i = 0; i < ids.length; i++) {
			Integer[] neighbor1 = neighbors.get(new Integer(ids[i]));
			HashSet<Integer> neighborOf2Hops = new HashSet<Integer>(
					Arrays.asList(neighbor1));
			for (int j = 0; j < neighbor1.length; j++) {
				// neighborOf2Hops.add(new
				// Integer(neighbor1[j]));//两跳邻居节点添加一跳邻居邻居点
				Integer[] neighbor2 = neighbors.get(new Integer(neighbor1[j]));
				for (int k = 0; k < neighbor2.length; k++) {
					neighborOf2Hops.add(neighbor2[k]);
				}
			}
			if (neighborOf2Hops.contains(new Integer(ids[i])))
				neighborOf2Hops.remove(new Integer(ids[i]));

			neighborOf2Hops.add(new Integer(ids[i]));// 两条邻居节点添加自身
			neighborsOf2Hops.put(new Integer(ids[i]), neighborOf2Hops
					.toArray(new Integer[neighborOf2Hops.size()]));
		}
	}

	public void connect(boolean needPainting) {
		wsn = NetTopoApp.getApp().getNetwork();
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false); // 计算所有一跳邻居节点

		initializeNeighbors();
		initializeNeighborsOf2Hops();

		// Collection<VNode> allSensorsCol=wsn.getAllNodes();
		// Iterator<VNode> allSensors=allSensorsCol.iterator();
		// SensorNode_TPGF sng=null;

		// while(allSensors.hasNext())
		// {
		// VNode node=allSensors.next();
		// VNode node=wsn.getNodeByID(12);
		// if(node instanceof SensorNode)
		// {
		//

		// sng=(SensorNode_TPGF)node;
		// sng.calculateArc(k, true);
		delNode();

		clearAllNeighbor();// 清除当前所有传感节点的邻居节点
		TPGF_Planarization_GG gg = new TPGF_Planarization_GG(null);
		gg.Planarization("GG", true);
		int idA = 0, idB = 0, idC = 0;

		/***************** haha ***************************/
		
		/***************** haha ***************************/
		for (Triangle tr : triangles) {

			idA = tr.getIDA();
			idB = tr.getIDB();
			idC = tr.getIDC();
			if (!(idA == -1 || idA == -2 || idA == -3 || idB == -1 || idB == -2
					|| idB == -3 || idC == -1 || idC == -2 || idC == -3)) {

				double ab = tr.getA().distance(tr.getB());
				double bc = tr.getC().distance(tr.getB());
				double ac = tr.getA().distance(tr.getC());
				VNode nodeA = wsn.getNodeByID(idA);
				VNode nodeB = wsn.getNodeByID(idB);
				VNode nodeC = wsn.getNodeByID(idC);

				if (nodeA instanceof SensorNode_TPGF
						&& nodeB instanceof SensorNode_TPGF
						&& nodeC instanceof SensorNode_TPGF) // 假设所有点都是传感器节点
				{
					if (ab < Double.parseDouble(nodeA.getAttrValue("Max TR"))
							&& ab < Double.parseDouble(nodeB
									.getAttrValue("Max TR"))) {

						((SensorNode_TPGF) (nodeA)).getNeighbors().add(idB);
						((SensorNode_TPGF) (nodeB)).getNeighbors().add(idA);

					}
					if (bc < Double.parseDouble(nodeC.getAttrValue("Max TR"))
							&& bc < Double.parseDouble(nodeB
									.getAttrValue("Max TR"))) {
						((SensorNode_TPGF) (nodeC)).getNeighbors().add(idB);
						((SensorNode_TPGF) (nodeB)).getNeighbors().add(idC);
					}
					if (ac < Double.parseDouble(nodeC.getAttrValue("Max TR"))
							&& ac < Double.parseDouble(nodeA
									.getAttrValue("Max TR"))) {
						((SensorNode_TPGF) (nodeC)).getNeighbors().add(idA);
						((SensorNode_TPGF) (nodeA)).getNeighbors().add(idC);
					}
				}
				// painter.paintConnection(idA,idB);
				// painter.paintConnection(idA,idC);
				// painter.paintConnection(idC,idB);
			}
		}

		/***************** haha ***************************/

		if (needPainting) {
			painter = NetTopoApp.getApp().getPainter();
			paintAllConnection(painter);
		}

		if (needPainting) {
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}
	}

	private void paintAllConnection(Painter painter) {
		int[] allNodesId = wsn.getAllNodesID();
		for (int id : allNodesId) {
			VNode vnode = wsn.getNodeByID(id);
			if (vnode instanceof SensorNode_TPGF) {
				for (int neiId : ((SensorNode_TPGF) (vnode)).getNeighbors()) {
					painter.paintConnection(id, neiId);
				}
			}
		}
	}

	private void clearAllNeighbor() {
		int[] allNodesId = wsn.getAllNodesID();
		for (int id : allNodesId) {
			VNode vnode = wsn.getNodeByID(id);
			if (vnode instanceof SensorNode_TPGF) {
				((SensorNode_TPGF) (vnode)).getNeighbors().clear();
			}
		}
	}

	public void delNode() // del一个节点
	{

		tempTriangle = new ArrayList<Triangle>(); // step1 初始化临时三角形列表
		edgeBuffer = new ArrayList<Edge>(); // 初始化边表
		triangles = new ArrayList<Triangle>(); // 初始化del三角形列表
		// List<Integer> nodeSet=new ArrayList<Integer>(node.getNeighbors());
		// //获得邻居节点集合
		// nodeSet.add(node.getID()); //把自己加入节点集合
		int[] nodeSet = wsn.getAllSensorNodesID(); // 所有点集合
		// Collections.sort(nodeSet,new ComparatorNeighbor());//根据x增排序 ok

		/**** ok ****/
		createSuperTriangle();// 创建超级三角形，并加入三角形列表

		// painter=NetTopoApp.getApp().getPainter();
		// // painter.paintCircle(superTriangle.getA(), "2");
		// painter.paintCircle(superTriangle.getB(), "2");
		// // painter.paintCircle(superTriangle.getC(), "2");

		List<Triangle> removeList = new ArrayList<Triangle>();
		for (int id : nodeSet) {
			// System.out.println(id);
			Coordinate coor = wsn.getCoordianteByID(id);
			for (Triangle tr : tempTriangle) {
				boolean hasKHops = tr.validateK2(neighborsOf2Hops);
				if (hasKHops) {
					// String tmpStr=tr.isInCircle(coor);
					// if(tmpStr.equals("in"))
					// 当在外接圆内时候，把改点与
					// System.out.println("in");
					edgeBuffer.addAll(tr.getThreeEdges());// 把边加入 边表
					removeList.add(tr);
					}
			}
			for (Triangle removeTr : removeList) // 删除三角形
			{
				tempTriangle.remove(removeTr);
			}
			removeList.clear();
			// System.out.println("Before:"+edgeBuffer.size());
			// clearRepeatEdges();
			clearCommonEdge();
			// printEdges();
			// System.out.println("edgeBuffer:"+edgeBuffer.size());
			createTriangleByEdges(coor, id);// 根据边创建三角形
			edgeBuffer.clear();

		}
		// triangles.addAll(tempTriangle);//合并
		triangles = tempTriangle;
		deleteTrianglesContainsSuperTriangle(); // 删除与超级三角形三个点有关的三角形
	}

	private void clearCommonEdge() {
		List<Edge> edgeTemp = new ArrayList<Edge>();
		boolean hasSame = false;
		for (Edge edg : edgeBuffer) {
			hasSame = false;
			for (int i = 0; i < edgeTemp.size(); ++i) {
				Edge e = edgeTemp.get(i);
				if (e.compare(edg)) {
					hasSame = true;
					break;
				}

			}
			if (!hasSame)
				edgeTemp.add(edg);
		}
		int[] flag = new int[edgeTemp.size()];
		// system.out.println("edgeSize:"+edgeTemp.size());
		for (int i = 0; i < edgeBuffer.size(); ++i) {
			Edge resEdg = edgeBuffer.get(i);
			for (int j = 0; j < edgeTemp.size(); ++j) {
				Edge tarEdg = edgeTemp.get(j);
				if (resEdg.compare(tarEdg)) {
					flag[j]++;
				}
			}
		}
		// system.out.println("flag.length:"+flag.length);
		edgeBuffer.clear();
		for (int i = 0; i < flag.length; ++i) {
			if (flag[i] < 2) {
				edgeBuffer.add(edgeTemp.get(i));
			}
		}
		// edgeBuffer=edgeTemp;

	}

	private void createTriangleByEdges(Coordinate coordinate, int ID) // 通过边创建三角形
	{
		for (Edge edge : edgeBuffer) {
			if (Triangle.createTriangle(coordinate, edge, ID))
				tempTriangle.add(new Triangle(coordinate, edge, ID));
		}
	}

	private void deleteTrianglesContainsSuperTriangle() // 删除包含超级三角形的三角形
	{
//		List<Triangle> tempTr = new ArrayList<Triangle>();
//		for (Triangle tr : triangles) {
//			if (!(tr.containPoint(superTriangle.getA())
//					|| tr.containPoint(superTriangle.getB()) || tr
//						.containPoint(superTriangle.getC())))
//				tempTr.add(tr);
//		}
//		triangles = tempTr;
		
		List<Triangle> tempTr = new ArrayList<Triangle>();
		int ida,idb,idc;
		for (Triangle tr : triangles) {
			ida = tr.getIDA();
			idb = tr.getIDB();
			idc = tr.getIDC();

			if(!(ida==-1||ida==-2||ida==-3||idb==-1||idb==-2||idb==-3||idc==-1||idc==-2||idc==-3))
				tempTr.add(tr);
		}
		triangles = tempTr;
	}

	private void createSuperTriangle() {
		Coordinate size = wsn.getSize();
		Coordinate A = new Coordinate(Math.ceil(size.x / 2), -size.y);
		Coordinate B = new Coordinate(-Math.floor(size.x / 2), size.y);
		Coordinate C = new Coordinate(size.x + Math.floor(size.x / 2), size.y);
		if (Triangle.createTriangle(A, B, C, -1, -2, -3)) {
			Triangle superTriangle = new Triangle(A, B, C, -1, -2, -3);
			if (tempTriangle == null)
				tempTriangle = new ArrayList<Triangle>();
			tempTriangle.add(superTriangle);
		}

	}

	/******************************************** Net end ************************************************************************************************/

	@Override
	public String getResult() {
		return null;
	}

}
