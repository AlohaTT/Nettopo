package org.deri.nettopo.algorithm.tpgf.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.edge.Edge;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.Boundary;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;

//当运行(1)某一平面化算法且概率化，计算DGas区域面积，停止算法(2)某一平面化算法且不概率化，计算DGas区域面积，停止算法；依次运行1、2
//之后直接运行DGas,会使得两次平面化算法形成的边叠加。----这是中间进行概率化操作之后，产生的问题
public class TPGF_Planarization_BoundaryArea implements AlgorFunc {

	private Painter paint;
	private Algorithm algorithm;
	private double result;
	private double oldarea;
	ArrayList<SensorNode> ibn;
	ArrayList<SensorNode> obn;
	List<Edge> edge;
	Boundary b;
	double outterArea=0;
	private int[] removedConnection;
	List<List<Integer>> allFace;
	List<Integer> allBoundaryNodeId;
	private WirelessSensorNetwork wsn;
	// source node extended sensor node,thus , source node is also got
	private Collection<VNode> sensorNodes = null;

	public TPGF_Planarization_BoundaryArea(Algorithm algorithm) {
		this.algorithm = algorithm;

	}
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public void run() {

		Planarization(true);
		Property.needpaint = true;

	}
	protected int middlePoint(int c1, int c2) {
		int midc;
		if (c1 > c2) {
			midc = (c1 - c2) / 2 + c2;
		} else {
			midc = (c2 - c1) / 2 + c1;
		}
		return midc;
	}

	public void Planarization(boolean needPaint) {
		Property.needpaint = false;
		wsn = NetTopoApp.getApp().getNetwork();
		deleteCopy();
		sensorNodes = wsn.getNodes(
				"org.deri.nettopo.node.tpgf.SensorNode_TPGF", true);
		
		System.out.println(sensorNodes.size());
		if (needPaint) {
			paint = NetTopoApp.getApp().getPainter();
		}
		paintArea(needPaint);
		ArrayList<SensorNode> temp = new ArrayList<SensorNode>();
		temp = wsn.getDeadList();
		if (temp != null) {
			if (needPaint) {
				paint = NetTopoApp.getApp().getPainter();
				wsn.getOriginalAllCoordinates();
				wsn.getOriginalAllNodes();
				for (SensorNode vNode : temp) {
						paint.paintNodeByCoors(vNode.getID(),
								NodeConfiguration.DeadNodeRGB);
					}
				}
			}

		}

	private void deleteCopy() {
		int[] iter = wsn.getSensorActiveNodes();
		ArrayList<Integer> realNeighbor;
		List<Integer> neighbor;
		for (int i : iter) {
			SensorNode_TPGF node = (SensorNode_TPGF) wsn.getNodeByID(i);
			neighbor = node.getNeighbors();
			realNeighbor = new ArrayList<Integer>();
			for (Integer id : neighbor) {
				if (!realNeighbor.contains(id))
					realNeighbor.add(new Integer(id));
			}
			node.setNeighbors(realNeighbor);
		}
	}

	public ArrayList<Edge> getEdge() {
		// this.Planarization(false);
		// InnerBoundaryNode inner = new InnerBoundaryNode();
		// OuterBoundaryNode outer = new OuterBoundaryNode();
		b = new Boundary(sensorNodes);
		ibn = b.getInnerBoundaryNode();// 获得内边界节点
		obn = b.getOuterBoundaryNode();// 获得外边界节点
		// obn.removeAll(alldeadNode);
		// System.out.println("ibn:"+ibn.size());
		// System.out.println("obn:"+obn.size());

		ArrayList<Edge> e = new ArrayList<Edge>();
		Iterator<SensorNode> iter = obn.iterator();
		List<Integer> neighbor;
		while (iter.hasNext()) {
			SensorNode_TPGF it = (SensorNode_TPGF) iter.next();
			neighbor = it.getNeighbors();
			for (int i = 0; i < neighbor.size(); i++) {
				int id_n = neighbor.get(i);
				if (wsn.getNodeByID(id_n) instanceof SinkNode)
					continue;
				SensorNode_TPGF n = (SensorNode_TPGF) wsn.getNodeByID(id_n);
				if (ibn.contains(n)) {// 如果它是内边界节点
					Edge eg = new Edge();
					eg.setId_s(it.getID());// 起点为外边界节点
					eg.setId_e(id_n); // 终点为内边界节点
					eg.setCs(wsn.getCoordianteByID(it.getID()));
					eg.setCe(wsn.getCoordianteByID(id_n));
					e.add(eg);
				}
			}
		}
		return e;
	}

	public List<List<Integer>> getFace() {
		edge = this.getEdge();
		allFace = new ArrayList<List<Integer>>();
		List<Integer> neighbor = null;
		SensorNode_TPGF centerNode = null;
		Coordinate coordinateCenter, coordinateEnd, coordinateNext, temp;
		for (Edge e : edge) {
			List<Integer> pathNode = new ArrayList<Integer>();
			int endId = e.getId_e();
			pathNode.add(endId);
			pathNode.add(e.getId_s());
			coordinateEnd = wsn.getCoordianteByID(e.getId_e());
			boolean isNotFind = true;
			int canFinish = 1;
			centerNode = (SensorNode_TPGF) wsn.getNodeByID(e.getId_s());// 第一次中心节点为边外界点
			do {
				neighbor = centerNode.getNeighbors();// 获得中心的邻居节点
				int tempId = -1;
				boolean needConsider = true;
				double cos = -1000;
				double cosBig = 1000;
				coordinateCenter = wsn.getCoordianteByID(centerNode.getID());// //
																				// 获得中心的坐标
				double tempCos = 0;
				List<Integer> list = new ArrayList<Integer>();
				for (int id : neighbor) {
					if (wsn.getNodeByID(id) instanceof SinkNode)
						continue;
					list = ((SensorNode_TPGF) wsn.getNodeByID(id))
							.getNeighbors();
					coordinateNext = wsn.getCoordianteByID(id);// 获得邻居节点的一个的坐标
					// if (wsn.getNodeByID(id) instanceof SinkNode)
					// continue;
					if (list.size() == 1)
						continue;
					if (Util.getCrossMultiple(coordinateCenter, coordinateEnd,
							coordinateNext) > 0)// 排除自己,顺时针第一个，左手法则
					{
						needConsider = false;
						tempCos = Util.getDotMultiple(coordinateCenter,
								coordinateEnd, coordinateNext);
						if (tempCos > cos) {
							cos = tempCos;
							tempId = id;
						}
					} else if (needConsider) {
						tempCos = Util.getDotMultiple(coordinateCenter,
								coordinateEnd, coordinateNext);
						if (tempCos != 1) {
							if (tempCos < cosBig) {
								cosBig = tempCos;
								tempId = id;
							}
						}
					}
				}
				if (tempId != -1) {
					if (tempId == endId) {// 找到了。
						isNotFind = false;
					}else {
						pathNode.add(tempId);
						temp = coordinateCenter;
						coordinateCenter = wsn.getCoordianteByID(tempId);
						coordinateEnd = temp;
						centerNode = (SensorNode_TPGF) wsn.getNodeByID(tempId);
					}

				}
				++canFinish;// 防止陷入死循环
			} while (isNotFind && canFinish <= 2000);
/*			 if (canFinish >= 10000) // 可以了
			 {
			 pathNode.clear();
			 allFace.clear();
			 return allFace; //只要有一个没有face，那就认为没有构成边界
			 } else {*/
			allFace.add(pathNode);// pathNode可能为零
		//	 }

		}
		return allFace;
	}

	public double computeAreaOfFace(List<Integer> path) {
		double area = 0.0;
		if (path == null || path.size() == 0)
			return 0;

		int[] points = new int[path.size() * 2];
		Coordinate ctemp = null, ctempNext = null;
		for (int i = 0, len = path.size(); i < len; ++i) {
			ctemp = wsn.getCoordianteByID(path.get(i));
			points[i * 2] = ctemp.x;
			points[i * 2 + 1] = ctemp.y;
			if (i < len - 1) {
				ctempNext = wsn.getCoordianteByID(path.get(i + 1));
				area += ctemp.x * ctempNext.y - ctemp.y * ctempNext.x;
			}
		}
		ctemp = wsn.getCoordianteByID(path.get(path.size() - 1));
		ctempNext = wsn.getCoordianteByID(path.get(0));
		area += ctemp.x * ctempNext.y - ctemp.y * ctempNext.x;
		area /= 2;
		area = area > 0 ? area : -area;
		return area;

	}
//计算全部面积时，没有用到此方法
	public List<Integer> getInnerFaceArea() {
		System.out.println("0000000000000000000000");
		Edge e = null;
		if (edge != null && edge.size() != 0)
			e = edge.get(0);
		List<Integer> neighbor = null;
		List<Integer> innerFace = new ArrayList<Integer>();
		Coordinate zeroCoor = wsn.getCoordianteByID(e.getId_s());
		Coordinate centerCoor = wsn.getCoordianteByID(e.getId_e());
		innerFace.add(e.getId_e()); // 向face中添加第一个节点
		SensorNode_TPGF firstNode = (SensorNode_TPGF) (wsn.getNodeByID(e
				.getId_e()));
		neighbor = firstNode.getNeighbors();
		Coordinate coor = null;
		double cos = -1000;
		double cosBig = 1000;
		boolean needConsider = true;
		double tempCos = 0;
		int tempId = -1;
		int endId = e.getId_e();
		int canFinish = 1;
		do {
			cos = -1000;
			needConsider = true;
			cosBig = 1000;
			tempId = -1;
			tempCos = 0;

			for (Integer id : neighbor) {
				if (obn.contains(wsn.getNodeByID(id))) // 过滤外边界节点
					continue;
				if (wsn.getNodeByID(id) instanceof SinkNode)
					continue;
				coor = wsn.getCoordianteByID(id);

				if (Util.getCrossMultiple(centerCoor, zeroCoor, coor) < 0) {
					needConsider = false;
					tempCos = Util.getDotMultiple(centerCoor, zeroCoor, coor);
					if (tempCos > cos) {
						cos = tempCos;
						tempId = id;
					}
				} else if (needConsider) {
					tempCos = Util.getDotMultiple(centerCoor, zeroCoor, coor);
					if (tempCos != 1) {
						if (tempCos < cosBig) {
							cosBig = tempCos;
							tempId = id;
						}
					}
				}
			}

			if (tempId == -1)
				return null;
			else {
				if (tempId == endId) {
					return innerFace;
				}

				innerFace.add(tempId);
				zeroCoor = centerCoor;
				centerCoor = wsn.getCoordianteByID(tempId);
				neighbor = ((SensorNode_TPGF) (wsn.getNodeByID(tempId)))
						.getNeighbors();
			}
			canFinish++;
		} while (canFinish <= 200);
		return null;
	}

	public List<Integer> getOuterFaceArea() {
		Edge e = null;
		int index = 0;
		if (edge == null || edge.size() == 0)
			return null;
		filterNode();
		if (allBoundaryNodeId.size() == 0)
			return null;
		for (index = 0; index < edge.size(); ++index) {
			// System.out.println("____________________________________________________");
			e = edge.get(index);
			List<Integer> neighbor = null;
			List<Integer> innerFace = new ArrayList<Integer>();
			Coordinate zeroCoor = wsn.getCoordianteByID(e.getId_e());
			Coordinate centerCoor = wsn.getCoordianteByID(e.getId_s());//获得中心的坐标
			innerFace.add(e.getId_e()); // 添加内边界节点，一个
			innerFace.add(e.getId_s()); // 向face中添加第一个节点

			SensorNode_TPGF firstNode = (SensorNode_TPGF) (wsn.getNodeByID(e
					.getId_s()));
			neighbor = firstNode.getNeighbors();
			Coordinate coor = null;
			double cos = -1000;
			double cosBig = 1000;
			boolean needConsider = true;
			double tempCos = 0;
			int tempId = -1;
			int endId = e.getId_s();
			int canFinish = 1;
			do {
				cos = -1000;
				needConsider = true;
				cosBig = 1000;
				tempId = -1;
				tempCos = 0;
				if (innerFace.size() > 5) {
					if (neighbor.contains(endId)) {
						innerFace.remove(0);
						return innerFace;
					}
				}
				for (Integer id : neighbor) {
					if (wsn.getNodeByID(id) instanceof SinkNode)
						continue;
					if (innerFace.contains(id))
						continue;
					if (!allBoundaryNodeId.contains(id))
						continue;
					coor = wsn.getCoordianteByID(id);//获得一个邻居节点的坐标

					if (Util.getCrossMultiple(centerCoor, zeroCoor, coor) > 0) {//排除自己，顺时针第一个，左手法则
						needConsider = false;
						tempCos = Util.getDotMultiple(centerCoor, zeroCoor,
								coor);
						if (tempCos > cos) {
							cos = tempCos;
							tempId = id;
						}
					} else if (needConsider) {
						tempCos = Util.getDotMultiple(centerCoor, zeroCoor,
								coor);
						if (tempCos != 1) {
							if (tempCos < cosBig) {
								cosBig = tempCos;
								tempId = id;
							}
						}
					}
				}
				if (tempId == -1) {
					break;
				
				} else { // tempId != -1
					if (tempId == endId) {

						if (innerFace.size() == 3) {
						
							break;
							
						} else {
							innerFace.remove(0);
							return innerFace;
						}
					} else // tempId = someId
					{
						innerFace.add(tempId);
						zeroCoor = centerCoor;
						centerCoor = wsn.getCoordianteByID(tempId);
						neighbor = ((SensorNode_TPGF) (wsn.getNodeByID(tempId)))
								.getNeighbors();
					}
				}
				canFinish++;
			} while (canFinish <= 4000);
			recoveryConnection();
		} 
		return null;
	}

	private void recoveryConnection() {
		List<Integer> neighbor;
		for (int i = 0; i < removedConnection.length; ++i) {
			if (removedConnection[i] != 0) {
				neighbor = ((SensorNode_TPGF) (wsn.getNodeByID(i)))
						.getNeighbors();
				neighbor.add(new Integer(removedConnection[i]));
			}
		}
		removedConnection = new int[20000];
	}

	private void filterNode() {
		int i = 0;
		SensorNode_TPGF sn = null;
		List<Integer> tmpList = null;
		do {
			boolean delete[] = new boolean[allBoundaryNodeId.size()];
			i = 0;
			for (int k = 0; k < allBoundaryNodeId.size(); ++k) {
				sn = (SensorNode_TPGF) wsn
						.getNodeByID(allBoundaryNodeId.get(k));
				int count = 0;

				List<Integer> sn_neighbor = sn.getNeighbors();
				for (Integer index : sn_neighbor) {
					if (allBoundaryNodeId.contains(index))
						count++;
				}
				if (count < 2) {
					delete[k] = true;
					i++;
				}
			}
			tmpList = new LinkedList<Integer>();
			for (int m = 0; m < delete.length; ++m) {
				if (!delete[m]) {
					tmpList.add(allBoundaryNodeId.get(m));
				}
			}
			allBoundaryNodeId = tmpList;
		} while (i != 0 && allBoundaryNodeId.size() != 0);

	}
	
	private void paintAllConnection() {
		int[] allNodesId = wsn.getAllNodesID();
		for (int id : allNodesId) {
			VNode vnode = wsn.getNodeByID(id);
			if (vnode instanceof SensorNode_TPGF) {
				for (int neiId : ((SensorNode_TPGF) (vnode)).getNeighbors()) {

					paint.paintConnection(id, neiId);
				}
			}
		}
	}
	public void paintArea(boolean needPaint) {
		removedConnection = new int[20000];
		allBoundaryNodeId = new ArrayList<Integer>();
		List<List<Integer>> allFaces = getFace();
		List<int[]> faces = new ArrayList<int[]>();
		double area = 0.0, allArea = 0.0;
		for (List<Integer> path : allFaces) {
			if (path.size() != 0) {
				area = 0.0;
				int[] points = new int[path.size() * 2];
				Coordinate ctemp = null, ctempNext = null;
				for (int i = 0, len = path.size(); i < len; ++i) {
					if (((!b.isContainedInnodeInGas(path.get(i))) && !allBoundaryNodeId
							.contains(new Integer(path.get(i)))))// add all boundary node not the node in gas but in the path
					{
						allBoundaryNodeId.add(new Integer(path.get(i)));
					}
					ctemp = wsn.getCoordianteByID(path.get(i));
					points[i * 2] = ctemp.x;
					points[i * 2 + 1] = ctemp.y;
					if (i < len - 1) {
						ctempNext = wsn.getCoordianteByID(path.get(i + 1));
						area += ctemp.x * ctempNext.y - ctemp.y * ctempNext.x;
					}
				}
				ctemp = wsn.getCoordianteByID(path.get(path.size() - 1));
				ctempNext = wsn.getCoordianteByID(path.get(0));
				area += ctemp.x * ctempNext.y - ctemp.y * ctempNext.x;
				area /= 2;
				faces.add(points);
			}
			allArea += (area > 0 ? area : -area);
		}
		result = allArea;
		oldarea=allArea;
		List<Integer> outFace = getOuterFaceArea();
		outterArea = computeAreaOfFace(outFace);
		result = outterArea;
		if (needPaint) {
			// NetTopoApp.getApp().addLog("The area of the boundary is "+String.format("%1$.2f",
			// allArea));
			NetTopoApp.getApp().addLog(
					"The area of the boundary is "
							+ String.format("%1$.2f", outterArea));
			paint.paintArea(faces);
			paint.paintAllNodes();
			paint.paintBoundary();
			paintAllConnection();
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}
	}

	public double getArea() {
		return result;
	}

	public void setArea(double area) {
		this.result = area;
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
