package org.deri.nettopo.algorithm.astar.function;

//org.deri.nettopo.algorithm.astar.function.Connect_Graphic
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;
import bean.Edge;
import bean.Triangle;



public class Connect_Delauany implements AlgorFunc{
	private WirelessSensorNetwork wsn;
	private Painter painter;
	
	private HashMap<Integer,Boolean> searched;
	
	private List<Triangle> tempTriangle;
	private List<Edge> edgeBuffer;
	private List<Triangle> triangles;
	private Triangle superTriangle;
	private Algorithm algorithm;
	
	
	public Connect_Delauany(Algorithm algorithm){
		this.algorithm = algorithm;
	}

	public Connect_Delauany(){
		this(null);
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public void run(){
		connect(true);
//		connectNet(true);
//		connectDegree(true);
//		//system.out.println("OK");
	}
	
	public void connect(boolean needPainting)
	{
		wsn=NetTopoApp.getApp().getNetwork();
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);	//计算所有一跳邻居节点
//		Collection<VNode> allSensorsCol=wsn.getAllNodes();
//		Iterator<VNode> allSensors=allSensorsCol.iterator();
		SensorNode_TPGF sng=null;
		
		
//		while(allSensors.hasNext())
//		{
//			VNode node=allSensors.next();
//		VNode node=wsn.getNodeByID(12);
//			if(node instanceof SensorNode)
//			{
//				
				
//				sng=(SensorNode_TPGF)node;
//				sng.calculateArc(k, true);
				delNode(sng);
				
				clearAllNeighbor();//清除当前所有传感节点的邻居节点
				
				int idA=0,idB=0,idC=0;
				for(Triangle tr:triangles)
				{
					
					
					idA=tr.getIDA();
					idB=tr.getIDB();
					idC=tr.getIDC();
					if(!(idA==-1||idA==-2||idA==-3||idB==-1||idB==-2||idB==-3||idC==-1||idC==-2||idC==-3))
					{
						
						double ab=tr.getA().distance(tr.getB());
						double bc=tr.getC().distance(tr.getB());
						double ac=tr.getA().distance(tr.getC());
						VNode nodeA=wsn.getNodeByID(idA);
						VNode nodeB=wsn.getNodeByID(idB);
						VNode nodeC=wsn.getNodeByID(idC);
						
						if(nodeA instanceof SensorNode_TPGF &&nodeB instanceof SensorNode_TPGF &&nodeC instanceof SensorNode_TPGF)			//假设所有点都是传感器节点
						{
							if(ab<Double.parseDouble(nodeA.getAttrValue("Max TR"))&&ab<Double.parseDouble(nodeB.getAttrValue("Max TR")))
							{
								
								((SensorNode_TPGF)(nodeA)).getNeighbors().add(idB);
								((SensorNode_TPGF)(nodeB)).getNeighbors().add(idA);
								
							}
							if(bc<Double.parseDouble(nodeC.getAttrValue("Max TR"))&&bc<Double.parseDouble(nodeB.getAttrValue("Max TR")))
							{
								((SensorNode_TPGF)(nodeC)).getNeighbors().add(idB);
								((SensorNode_TPGF)(nodeB)).getNeighbors().add(idC);
							}
							if(ac<Double.parseDouble(nodeC.getAttrValue("Max TR"))&&ac<Double.parseDouble(nodeA.getAttrValue("Max TR")))
							{
								((SensorNode_TPGF)(nodeC)).getNeighbors().add(idA);
								((SensorNode_TPGF)(nodeA)).getNeighbors().add(idC);
							}
						}
//						painter.paintConnection(idA,idB);
//						painter.paintConnection(idA,idC);
//						painter.paintConnection(idC,idB);
					}
				}
				
				
				 deleteCopy();
				
				if(needPainting)
				{
//					int idA=0,idB=0,idC=0;
//						painter.paintConnection(sng.getID(), arc.getConnectionNodeId());
					painter=NetTopoApp.getApp().getPainter();
					paintAllConnection(painter);
//					for(Triangle tr:triangles)
//					{
//						
////						idA=wsn.findNodeIdByCoordinate(tr.getA());
////						idB=wsn.findNodeIdByCoordinate(tr.getB());
////						idC=wsn.findNodeIdByCoordinate(tr.getC());
//						idA=tr.getIDA();
//						idB=tr.getIDB();
//						idC=tr.getIDC();
////						if(idA!=-1&&idB!=-1&&idC!=-1)
//						if(!(idA==-1||idA==-2||idA==-3||idB==-1||idB==-2||idB==-3||idC==-1||idC==-2||idC==-3))
//						{
//							
//							double ab=tr.getA().distance(tr.getB());
//							double bc=tr.getC().distance(tr.getB());
//							double ac=tr.getA().distance(tr.getC());
//							VNode nodeA=wsn.getNodeByID(idA);
//							VNode nodeB=wsn.getNodeByID(idB);
//							VNode nodeC=wsn.getNodeByID(idC);
//							
//							if(nodeA instanceof SensorNode_TPGF &&nodeB instanceof SensorNode_TPGF &&nodeC instanceof SensorNode_TPGF)			//假设所有点都是传感器节点
//							{
//								if(ab<Double.parseDouble(nodeA.getAttrValue("Max TR"))&&ab<Double.parseDouble(nodeB.getAttrValue("Max TR")))
//								{
//									
//									((SensorNode_TPGF)(nodeA)).getNeighbors().add(idB);
//									
//								}
//							}
//							painter.paintConnection(idA,idB);
//							painter.paintConnection(idA,idC);
//							painter.paintConnection(idC,idB);
//						}
//						else
//						{
//							System.out.println("#500  fatel");
//						}
//					}
					
				}
//			}
//		}
		
		if(needPainting)
		{
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}
	}
	
	private void deleteCopy()
	{
		int[] iter = wsn.getSensorActiveNodes();
		ArrayList<Integer> realNeighbor;
		List<Integer> neighbor;
		for(int i:iter){
			SensorNode_TPGF node = (SensorNode_TPGF) wsn.getNodeByID(i);
			neighbor = node.getNeighbors();
			realNeighbor = new ArrayList<Integer>();
			for(Integer id:neighbor)
			{
				if(!realNeighbor.contains(id))
					realNeighbor.add(new Integer(id));
			}
			node.setNeighbors(realNeighbor);
		}
	}
	
	private void paintAllConnection(Painter painter)
	{
		int[] allNodesId=wsn.getAllNodesID();
		for(int id:allNodesId)
		{
			VNode vnode=wsn.getNodeByID(id);
			if(vnode instanceof SensorNode_TPGF)
			{
				for(int neiId:((SensorNode_TPGF)(vnode)).getNeighbors())
				{
					painter.paintConnection(id, neiId);
				}
			}
		}
	}
	
	
	private void clearAllNeighbor()
	{
		int[] allNodesId=wsn.getAllNodesID();
		for(int id:allNodesId)
		{
			VNode vnode=wsn.getNodeByID(id);
			if(vnode instanceof SensorNode_TPGF)
			{
				((SensorNode_TPGF)(vnode)).getNeighbors().clear();
			}
		}
	}
	
	
	public void delNode(SensorNode_TPGF node)		//del一个节点
	{
		
		tempTriangle=new ArrayList<Triangle>();	//step1 初始化临时三角形列表
		edgeBuffer= new  ArrayList<Edge>();		// 初始化边表
		triangles=new ArrayList<Triangle>();	//初始化del三角形列表
//		List<Integer> nodeSet=new ArrayList<Integer>(node.getNeighbors());	//获得邻居节点集合
//		nodeSet.add(node.getID());				//把自己加入节点集合
		int[] nodeSet=wsn.getAllNodesID();
//		Collections.sort(nodeSet,new ComparatorNeighbor());//根据x增排序 ok
		
		/****ok****/
		createSuperTriangle();//创建超级三角形，并加入三角形列表
		
//		painter=NetTopoApp.getApp().getPainter();
////		painter.paintCircle(superTriangle.getA(), "2");
//		painter.paintCircle(superTriangle.getB(), "2");
////		painter.paintCircle(superTriangle.getC(), "2");
		
		List<Triangle> removeList=new ArrayList<Triangle>();
		for(int id:nodeSet)
		{
//			System.out.println(id);
			Coordinate coor=wsn.getCoordianteByID(id);
			for(Triangle tr:tempTriangle)
			{
				String tmpStr=tr.isInCircle(coor);
				if(tmpStr.equals("in"))
					{
					//当在外接圆内时候，把改点与
//					System.out.println("in");
						edgeBuffer.addAll(tr.getThreeEdges());//把边加入  边表
						removeList.add(tr);
					}
//					else if(tmpStr.equals("out"))
//					{
//						System.out.println("out");
//						//边外的时候把三角形加入最终三角形，并删掉temp中三角形
//						triangles.add(tr);
//						removeList.add(tr);
//					}
//					else
//					{
//						System.out.println("dn");
//						//当无法确定时候跳过什么都不做
//					}
			}
			for(Triangle removeTr:removeList)		//删除三角形
			{
				tempTriangle.remove(removeTr);
			}
			removeList.clear();
//			System.out.println("Before:"+edgeBuffer.size());
//			clearRepeatEdges();
			clearCommonEdge();
//			printEdges();
//			System.out.println("edgeBuffer:"+edgeBuffer.size());
			createTriangleByEdges(coor,id);//根据边创建三角形
			edgeBuffer.clear();

		}
//			triangles.addAll(tempTriangle);//合并
		triangles=tempTriangle;
			deleteTrianglesContainsSuperTriangle();	//删除与超级三角形三个点有关的三角形
	}
	

	private void clearCommonEdge()
	{
		List<Edge> edgeTemp= new  ArrayList<Edge>();
		boolean hasSame=false;
		for(Edge edg:edgeBuffer)
		{
			hasSame=false;
			for(int i=0;i<edgeTemp.size();++i)
			{
				Edge e=edgeTemp.get(i);
				if(e.compare(edg))
				{
					hasSame=true;
					break;
				}
					
			}
			if(!hasSame)
				edgeTemp.add(edg);
		}
		int[] flag=new int[edgeTemp.size()];
		//system.out.println("edgeSize:"+edgeTemp.size());
		for(int i=0;i<edgeBuffer.size();++i)
		{
			Edge resEdg=edgeBuffer.get(i);
			for(int j=0;j<edgeTemp.size();++j)
			{
				Edge tarEdg=edgeTemp.get(j);
				if(resEdg.compare(tarEdg))
				{
					flag[j]++;
				}
			}
		}
		//system.out.println("flag.length:"+flag.length);
		edgeBuffer.clear();
		for(int i=0;i<flag.length;++i)
		{
			if(flag[i]<2)
			{
				edgeBuffer.add(edgeTemp.get(i));
			}
		}
//		edgeBuffer=edgeTemp;
		
	}
	
	private void createTriangleByEdges(Coordinate coordinate,int ID)			//通过边创建三角形
	{
		for(Edge edge:edgeBuffer)
		{
			if(Triangle.createTriangle(coordinate, edge, ID))
			tempTriangle.add(new Triangle(coordinate,edge,ID));
		}
	}
	
	private void deleteTrianglesContainsSuperTriangle()		//删除包含超级三角形的三角形
	{
		List<Triangle> tempTr=new ArrayList<Triangle>();
		for(Triangle tr:triangles)
		{
			if(!(tr.containPoint(superTriangle.getA())||tr.containPoint(superTriangle.getB())||tr.containPoint(superTriangle.getC())))
				tempTr.add(tr);
		}
		triangles=tempTr;
	}
	
	
//	private void createSuperTriangle(SensorNode_TPGF node)			//构建超级三角形
//	{
//		Coordinate cor=wsn.getCoordianteByID(node.getID());
//		int r=node.getMaxTR();
//		Coordinate A=new Coordinate(cor.x,cor.y-2*r-1);
//		Coordinate B=new Coordinate(cor.x+r*Math.sqrt(3)+1,cor.y+r+1);
//		Coordinate C=new Coordinate(cor.x-r*Math.sqrt(3)-1,cor.y+r+1);
//		Triangle superTriangle=new Triangle(A,B,C,-1,-2,-3);
//		if(tempTriangle==null)
//			tempTriangle=new ArrayList<Triangle>();
//		tempTriangle.add(superTriangle);
//		this.superTriangle=superTriangle;
//		
//	}
//	
//	private void createSuperTriangle(SensorNode_TPGF node)			//构建超级三角形
//	{
//		List<Integer> neighbors=new ArrayList<Integer>(node.getNeighbors());
//		int xmin=Integer.MAX_VALUE,ymin=Integer.MAX_VALUE,xmax=Integer.MIN_VALUE,ymax=Integer.MIN_VALUE;
//		Coordinate cor=null;
//		for(int id:neighbors)
//		{
//			cor=wsn.getCoordianteByID(id);
//			if(cor.x>xmax)
//				xmax=cor.x;
//			if(cor.x<xmin);
//			xmin=cor.x;
//			if(cor.y>ymax)
//				ymax=cor.y;
//			if(cor.y<ymin);
//			ymin=cor.y;
//		}
//		
//		Coordinate A=new Coordinate(xmin+(xmax-xmin)/2,ymin-(ymax-ymin)-3);
//		Coordinate B=new Coordinate(xmin-(xmax-xmin)/2-3,ymax+3);
//		Coordinate C=new Coordinate(xmax+(xmax-xmin)/2+3,ymax+3);
//		Triangle superTriangle=new Triangle(A,B,C,-1,-2,-3);
//				if(tempTriangle==null)
//			tempTriangle=new ArrayList<Triangle>();
//		tempTriangle.add(superTriangle);
//		this.superTriangle=superTriangle;
//		
//	}
//	private void createSuperTriangle()			//构建超级三角形
//	{
////		Coordinate size=wsn.getSize();
//		int[] allNodeId=wsn.getAllNodesID();
//		int xmin=Integer.MAX_VALUE,ymin=Integer.MAX_VALUE,xmax=Integer.MIN_VALUE,ymax=Integer.MIN_VALUE;
//		Coordinate cor=null;
//		for(int id:allNodeId)
//		{
//			cor=wsn.getCoordianteByID(id);
//			if(cor.x>xmax)
//				xmax=cor.x;
//			if(cor.x<xmin);
//			xmin=cor.x;
//			if(cor.y>ymax)
//				ymax=cor.y;
//			if(cor.y<ymin);
//			ymin=cor.y;
//		}
//		int dmax;
//		if((xmax-xmin)>(ymax-ymin))
//			dmax=ymax-ymin;
//		else
//			dmax=xmax-xmin;
//		int xmid=(int) Math.ceil((xmax+xmin)/2);
//		int ymid=(int) Math.ceil((ymax+ymin)/2);
//		Coordinate A=new Coordinate(xmid-2*dmax,ymid-dmax);
//		Coordinate B=new Coordinate(xmid,ymid+2*dmax);
//		Coordinate C=new Coordinate(xmid-2*dmax,ymid-dmax);
//		if(Triangle.createTriangle(A,B,C,-1,-2,-3))
//		{
//			Triangle superTriangle=new Triangle(A,B,C,-1,-2,-3);
//			if(tempTriangle==null)
//				tempTriangle=new ArrayList<Triangle>();
//			tempTriangle.add(superTriangle);
//			this.superTriangle=superTriangle;
//		}
//		
//	}

	
	private void createSuperTriangle()
	{
		Coordinate size=wsn.getSize();
		Coordinate A=new Coordinate(Math.ceil(size.x/2),-size.y);
		Coordinate B=new Coordinate(-Math.floor(size.x/2),size.y);
		Coordinate C=new Coordinate(size.x+Math.floor(size.x/2),size.y);
		if(Triangle.createTriangle(A,B,C,-1,-2,-3))
		{
			Triangle superTriangle=new Triangle(A,B,C,-1,-2,-3);
			if(tempTriangle==null)
				tempTriangle=new ArrayList<Triangle>();
			tempTriangle.add(superTriangle);
			this.superTriangle=superTriangle;
		}
		
		
	}
	
	
	class ComparatorNeighbor implements Comparator<Integer>
	{

		@Override
		public int compare(Integer o1, Integer o2) {
			Coordinate co1=wsn.getCoordianteByID(o1);
			Coordinate co2=wsn.getCoordianteByID(o2);
			return co1.x-co2.x;
		}
		
	}
	
	
	/********************************************Net************************************************************************************************/
	
	public void connectNet(boolean needPainting)		//del一个节点
	{
		wsn=NetTopoApp.getApp().getNetwork();
		searched=new HashMap<Integer,Boolean>();
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);	//计算所有一跳邻居节点
		triangles=new ArrayList<Triangle>();
		edgeBuffer=new ArrayList<Edge>();
		initialize();
		Coordinate c1,c2;
		int ids[] = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;++i)			//对每一个没有查询过的点进行扩展
		{	
			if(!searched.get(ids[i]))
			{
				//system.out.println("#100");
				int id2=nearestPoint(ids[i]);
				if(id2==-1)	//没有点
				{
					
					//system.out.println("#101");
					searched.put(ids[i], true);
					continue;
				}
					
				c1=wsn.getCoordianteByID(ids[i]);
				c2=wsn.getCoordianteByID(id2);
				edgeBuffer.add(new Edge(c1,c2,ids[i],id2));
				Coordinate coorHead=null,coorEnd=null,cor3=null;
				while(edgeBuffer!=null&&edgeBuffer.size()!=0)
				{
					Edge edge=edgeBuffer.remove(0);
					int id3=nearestPoint(edge.getIDHead(),edge.getIDEnd());
					
					if(id3!=-1)
					{
						searched.put(id3, true);
						coorHead = wsn.getCoordianteByID(edge.getIDHead());
						coorEnd = wsn.getCoordianteByID(edge.getIDEnd());
						cor3=wsn.getCoordianteByID(id3);
						triangles.add(new Triangle(coorHead,coorEnd,cor3,edge.getIDHead(),edge.getIDEnd(),id3));
						edgeBuffer.add(new Edge(cor3,wsn.getCoordianteByID(edge.getIDHead()),id3,edge.getIDHead()));
						edgeBuffer.add(new Edge(cor3,wsn.getCoordianteByID(edge.getIDEnd()),id3,edge.getIDEnd()));
					}
				}
			}
		}
		
		if(needPainting)
		{
			int idA=0,idB=0,idC=0;
			painter=NetTopoApp.getApp().getPainter();
			for(Triangle tr:triangles)
			{
				
				idA=tr.getIDA();
				idB=tr.getIDB();
				idC=tr.getIDC();
					painter.paintConnection(idA,idB);
					painter.paintConnection(idA,idC);
					painter.paintConnection(idC,idB);
			}
			
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
					public void run() {
						NetTopoApp.getApp().refresh();
					}
				});
		}
		
		
	}
	
	public void connectDegree(boolean needPainting)		//del一个节点
	{
		wsn=NetTopoApp.getApp().getNetwork();
		searched=new HashMap<Integer,Boolean>();
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);	//计算所有一跳邻居节点
		triangles=new ArrayList<Triangle>();
		edgeBuffer=new ArrayList<Edge>();
		initialize();
		Coordinate c1,c2;
		int ids[] = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;++i)			//对每一个没有查询过的点进行扩展
		{	
			if(!searched.get(ids[i]))
			{
				//system.out.println("#100");
				int id2=nearestPoint(ids[i]);
				if(id2==-1)	//没有点
				{
					
					//system.out.println("#101");
					searched.put(ids[i], true);
					continue;
				}
					
				c1=wsn.getCoordianteByID(ids[i]);
				c2=wsn.getCoordianteByID(id2);
				edgeBuffer.add(new Edge(c1,c2,ids[i],id2));//head:center   end:node2
				Coordinate coorHead=null,coorEnd=null,cor3=null;
				while(edgeBuffer!=null&&edgeBuffer.size()!=0)
				{
					Edge edge=edgeBuffer.remove(0);
					int id3=nearestPoint(edge.getIDHead(),edge.getIDEnd());
					
					if(id3!=-1)
					{
						searched.put(id3, true);
						coorHead = wsn.getCoordianteByID(edge.getIDHead());
						coorEnd = wsn.getCoordianteByID(edge.getIDEnd());
						cor3=wsn.getCoordianteByID(id3);
						triangles.add(new Triangle(coorHead,coorEnd,cor3,edge.getIDHead(),edge.getIDEnd(),id3));
						edgeBuffer.add(new Edge(cor3,wsn.getCoordianteByID(edge.getIDHead()),id3,edge.getIDHead()));
						edgeBuffer.add(new Edge(cor3,wsn.getCoordianteByID(edge.getIDEnd()),id3,edge.getIDEnd()));
					}
				}
			}
		}
		
		if(needPainting)
		{
			int idA=0,idB=0,idC=0;
			painter=NetTopoApp.getApp().getPainter();
			for(Triangle tr:triangles)
			{
				
				idA=tr.getIDA();
				idB=tr.getIDB();
				idC=tr.getIDC();
					painter.paintConnection(idA,idB);
					painter.paintConnection(idA,idC);
					painter.paintConnection(idC,idB);
			}
			
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
					public void run() {
						NetTopoApp.getApp().refresh();
					}
				});
		}
		
		
	}
	
	
	private int nearestPoint(int id)				//计算距离节点最近的邻居节点   (一个节点)
	{
		Coordinate coor=wsn.getCoordianteByID(id);
		double minDistance=10000;
		int nearstPointId=-1;
		SensorNode_TPGF sng=(SensorNode_TPGF) wsn.getNodeByID(id);
		List<Integer>  nei=sng.getNeighbors();
		Coordinate coorTemp=null;
		double distemp=0;
		for(Integer neiId: nei)
		{
			coorTemp=wsn.getCoordianteByID(neiId);
			distemp=coor.distance(coorTemp);
			if(distemp<minDistance&&!searched.get(neiId))
			{
				minDistance=distemp;
				nearstPointId=neiId;
			}
		}
		
		return nearstPointId;
	}
	
	
	private boolean inSameLine(Coordinate c1,Coordinate c2,Coordinate c3)	//是否共线
	{
		double k1=((double)(c1.y-c3.y))/((double)(c1.x-c3.y));
		double k2=((double)(c2.y-c3.y))/((double)(c2.x-c3.y));
		if(Math.abs(k1-k2)<1e-6)
			return true;
		return false;
	}
	
	private int nearestPoint(int id1,int id2)				//计算距离节点最近的邻居节点   (2个节点)
	{
		Coordinate coor=wsn.getCoordianteByID(id1);
		Coordinate coor2=wsn.getCoordianteByID(id2);
		double minDistance=10000;
		int nearstPointId=-1;
		SensorNode_TPGF sng=(SensorNode_TPGF) wsn.getNodeByID(id1);
		List<Integer>  nei=sng.getNeighbors();
		Coordinate coorTemp=null;
		double distemp=0;
		for(Integer neiId: nei)
		{
			coorTemp=wsn.getCoordianteByID(neiId);
			distemp=coor.distance(coorTemp);
			if(distemp<minDistance&&!inSameLine(coor,coor2,coorTemp)&&!searched.get(neiId))
			{
				minDistance=distemp;
				nearstPointId=neiId;
			}
		}
		
		
		double minDistance2=10000;
		int nearstPointId2=-1;
		SensorNode_TPGF sng2=(SensorNode_TPGF) wsn.getNodeByID(id2);
		List<Integer>  nei2=sng2.getNeighbors();
		Coordinate coorTemp2=null;
		double distemp2=0;
		for(Integer neiId2: nei2)
		{
			coorTemp2=wsn.getCoordianteByID(neiId2);
			distemp2=coor2.distance(coorTemp2);
			if(distemp2<minDistance2&&!inSameLine(coor,coor2,coorTemp)&&!searched.get(neiId2))
			{
				minDistance2=distemp2;
				nearstPointId2=neiId2;
			}
		}
		
		
		if(minDistance2<minDistance)
			return nearstPointId2;

			return nearstPointId;
	}
	
	
	private void initialize(){		//初始化查询列表
		int ids[] = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			searched.put(new Integer(ids[i]), false);
		}
	}
	
	/********************************************Net end************************************************************************************************/	
	
	@Override
	public String getResult() {
		return null;
	}

}
