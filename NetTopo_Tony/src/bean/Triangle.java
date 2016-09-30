package bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.Coordinate;

public class Triangle {
	private Coordinate A;
	private int IDA;
	private Coordinate B;
	private int IDB;
	private Coordinate C;
	private int IDC;
	
	private List<Edge> threeEdges;
	private boolean specialTriangle = false;
	private Coordinate center;
	private double radius;
	
	private int centerID;
	private final static double eps=0.0001;
	
	private List<Integer> inCircleNodeId;
	
	
	
	public int getCenterID() {
		return centerID;
	}


	public void setCenterID(int centerID) {
		this.centerID = centerID;
	}


	public boolean isSpecialTriangle() {
		return specialTriangle;
	}


	public void setSpecialTriangle(boolean specialTriangle) {
		this.specialTriangle = specialTriangle;
	}


	public boolean containIdAndGreaterThanDegree(int nodeId)
	{
		int x1,x2,y1,y2;
		if(IDA==nodeId)
		{
			x1=B.x-A.x;
			x2=C.x-A.x;
			y1=B.y-A.y;
			y2=C.y-A.y;
		}
		else if(IDB==nodeId)
		{
			x1=A.x-B.x;
			x2=C.x-B.x;
			y1=A.y-B.y;
			y2=C.y-B.y;
		}
		else if(IDC==nodeId)
		{
			x1=A.x-C.x;
			x2=B.x-C.x;
			y1=A.y-C.y;
			y2=B.y-C.y;
		}
		else
			return false;
		if(((x1*x2+y1*y2)/Math.sqrt((x1*x1+y1*y1)*(x2*x2+y2*y2)))<=Math.cos(Math.toRadians(60)))
			return true;
		return false;
	}
	
	
	public Triangle(Coordinate a, Coordinate b, Coordinate c,int ida,int idb,int idc) {	//三点创建方式
		super();
		A = a;
		B = b;
		C = c;
		IDA=ida;
		IDB=idb;
		IDC=idc;
		calculateCircle();
		inCircleNodeId = new ArrayList<Integer>();
		threeEdges=new ArrayList<Edge>();
		threeEdges.add(new Edge(A,B,IDA,IDB));
		threeEdges.add(new Edge(A,C,IDA,IDC));
		threeEdges.add(new Edge(B,C,IDB,IDC));
		calculateAllInCircleNodeId();
	}
	
	public static boolean createTriangle(Coordinate a, Coordinate b, Coordinate c,int ida,int idb,int idc) {	//三点创建方式
		
		double ab=a.distance(b);
		double bc=b.distance(c);
		double ac=a.distance(c);
		
		double max=ab,partsum=bc+ac;
		if(max<bc)
		{
			max=bc;
			partsum=ab+ac;
		}
		if(max<ac)
		{
			max=ac;
			partsum=ab+bc;
		}
		if(Math.abs(max-partsum)<eps)
			return false;
		
		return true;
	}
	
	
	
	public Triangle(Coordinate a,Edge edge,int ida) {		//一边一点创建方式
		super();
		A=a;
		IDA=ida;
		B=edge.getHead();
		IDB=edge.getIDHead();
		C=edge.getEnd();
		IDC=edge.getIDEnd();
		calculateCircle();
		inCircleNodeId = new ArrayList<Integer>();
		threeEdges=new ArrayList<Edge>();
		threeEdges.add(new Edge(A,B,IDA,IDB));
		threeEdges.add(new Edge(A,C,IDA,IDC));
		threeEdges.add(new Edge(B,C,IDB,IDC));
		calculateAllInCircleNodeId();
	}
	
	public static boolean createTriangle(Coordinate a,Edge edge,int ida)
	{
		Coordinate b=edge.getHead(),c=edge.getEnd();
		
		double ab=a.distance(b);
		double bc=b.distance(c);
		double ac=a.distance(c);
		
		double max=ab,partsum=bc+ac;
		if(max<bc)
		{
			max=bc;
			partsum=ab+ac;
		}
		if(max<ac)
		{
			max=ac;
			partsum=ab+bc;
		}
		if(Math.abs(max-partsum)<eps)
			return false;
		return true;
	}



	public List<Edge> getThreeEdges() {
		return threeEdges;
	}



	public int getIDA() {
		return IDA;
	}



	public void setIDA(int iDA) {
		IDA = iDA;
	}



	public int getIDB() {
		return IDB;
	}



	public void setIDB(int iDB) {
		IDB = iDB;
	}



	public int getIDC() {
		return IDC;
	}



	public void setIDC(int iDC) {
		IDC = iDC;
	}



	public void setThreeEdges(List<Edge> threeEdges) {
		this.threeEdges = threeEdges;
	}



	public Coordinate getA() {
		return A;
	}
	public void setA(Coordinate a) {
		A = a;
	}
	public Coordinate getB() {
		return B;
	}
	public void setB(Coordinate b) {
		B = b;
	}
	public Coordinate getC() {
		return C;
	}
	public void setC(Coordinate c) {
		C = c;
	}
	public Coordinate getCenter() {
		return center;
	}
	public void setCenter(Coordinate center) {
		this.center = center;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public void calculateCircle()
	{
		int x_c=(int)((A.x*A.x*(B.y-C.y)+B.x*B.x*(C.y-A.y)+C.x*C.x*(A.y-B.y)-(A.y-B.y)*(B.y-C.y)*(C.y-A.y))/(2*(A.x*(B.y-C.y)+B.x*(C.y-A.y)+C.x*(A.y-B.y))));
		int y_c=-(int)((A.y*A.y*(B.x-C.x)+B.y*B.y*(C.x-A.x)+C.y*C.y*(A.x-B.x)-(A.x-B.x)*(B.x-C.x)*(C.x-A.x))/(2*(A.x*(B.y-C.y)+B.x*(C.y-A.y)+C.x*(A.y-B.y))));
		center=new Coordinate( x_c ,y_c) ;
		double a=Math.sqrt((A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y));
		double b=Math.sqrt((C.x-B.x)*(C.x-B.x)+(C.y-B.y)*(C.y-B.y));
		double c=Math.sqrt((A.x-C.x)*(A.x-C.x)+(A.y-C.y)*(A.y-C.y));
		radius=	a*b*c/(Math.sqrt((a+b-c)*(a+c-b)*(b+c-a)*(a+b+c)));
	}

	
	public String isInCircle(Coordinate cor)
	{
		if(cor.distance(center)<radius)
//		if(cor.isInCircle(cor, center, radius))
			return "in";//在园内
		else if(cor.x>center.x+radius)
			return "out";//院外
		else return "dn";//未知跳过
	}
	
	public boolean containPoint(Coordinate coordinate)
	{
		if(A.x==coordinate.x&&A.y==coordinate.y)
			return true;
		if(B.x==coordinate.x&&B.y==coordinate.y)
			return true;
		if(C.x==coordinate.x&&C.y==coordinate.y)
			return true;
		return false;
	}
	
	
	
	public void calculateAllInCircleNodeId()
	{
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		HashMap<Integer, Coordinate>  coordinates =	wsn.getOriginalAllCoordinates();
		Set<Integer> ids = coordinates.keySet();
		for(int id: ids)
		{
			if(coordinates.get(id).distance(center)<radius)
				inCircleNodeId.add(id);
		}
		
		
	}
	
	public boolean greaterThanMaxTr(int maxTR)
	{
		if(A.distance(B)>maxTR||A.distance(C)>maxTR||B.distance(C)>maxTR)
			return true;
		return false;
	}
	
	public boolean validateK2( HashMap<Integer,Integer[]> neighborsOf2Hops )
	{
		
		if(inCircleNodeId.size()-3>2)
			return true;
		Integer[] aTwoHops = neighborsOf2Hops.get(IDA);
		Integer[] bTwoHops = neighborsOf2Hops.get(IDB);
		Integer[] cTwoHops = neighborsOf2Hops.get(IDC);
		if(aTwoHops==null)
			return false;
		for(int indexA:aTwoHops)
		{
			if(inCircleNodeId.contains(indexA))
				return true;
		}
		if(bTwoHops==null)
			return false;
		for(int indexB:bTwoHops)
		{
			if(inCircleNodeId.contains(indexB))
				return true;
		}
		if(cTwoHops==null)
			return false;
		for(int indexC:cTwoHops)
		{
			if(inCircleNodeId.contains(indexC))
				return true;
		}
		return false;
	}
	
	public int FindPointName()
	{
		if(centerID==IDA)
		return 1;
		else if(centerID==IDB)
			return 2;
		else if(centerID==IDC)
			return 3;
		else return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Triangle tr = (Triangle)obj;
		int c_A=tr.getIDA();
		int c_B=tr.getIDB();
		int c_C=tr.getIDC();
		if(c_A==IDA||c_B==IDA||c_C==IDA);
		else
			return false;
		if(c_A==IDB||c_B==IDB||c_C==IDB);
		else
			return false;
		if(c_A==IDC||c_B==IDC||c_C==IDC);
		else
			return false;
		return true;
	}


	public static void main(String[] args) {
		Triangle tr=new Triangle(new Coordinate(20,0),new Coordinate(-20,0),new Coordinate(0,-20),-1,-2,-3);
		System.out.println(tr.isInCircle(new Coordinate(10,-20) ));
		System.out.println(tr.center.x+"    "+tr.center.y);
	}

}
