package bean;

import org.deri.nettopo.util.Coordinate;

public class Edge {
	private Coordinate head;	//外边界节点
	private int IDHead;
	private Coordinate end;	//内边界节点
	private int IDEnd;
	
	
	public Edge(Coordinate head, Coordinate end,int idHead,int idEnd) {
		super();
		this.head = head;
		this.end = end;
		this.IDHead=idHead;
		this.IDEnd=idEnd;
	}
	public int getIDHead() {
		return IDHead;
	}
	public void setIDHead(int iDHead) {
		IDHead = iDHead;
	}

	public int getIDEnd() {
		return IDEnd;
	}



	public void setIDEnd(int iDEnd) {
		IDEnd = iDEnd;
	}



	public Coordinate getHead() {
		return head;
	}
	public void setHead(Coordinate head) {
		this.head = head;
	}
	public Coordinate getEnd() {
		return end;
	}
	public void setEnd(Coordinate end) {
		this.end = end;
	}
	
	
	public boolean compare(Edge e2)
	{
//		if((e2.head.x ==this.head.x&&e2.head.y==this.head.y)&&(e2.end.x ==this.end.x&&e2.end.y==this.end.y))
//			return true;
//		if((e2.head.x ==this.end.x&&e2.head.y==this.end.y)&&(e2.end.x ==this.head.x&&e2.end.y==this.head.y))
//			 return true;
//		 else return false;
		if(this.IDEnd==e2.getIDEnd()&&this.IDHead==e2.getIDHead())
		return true;
	if(this.IDEnd==e2.IDHead&&this.IDHead==e2.getIDEnd())
		return true;
	return false;
	}
	
	public static void main(String[] args) {
		Edge e2=new Edge(new Coordinate(1,1),new Coordinate(3,3),1,2);
		Edge e3=new Edge(new Coordinate(1,1),new Coordinate(3,3),2,1);
		System.out.println(e2.compare(e3));
	}

}
