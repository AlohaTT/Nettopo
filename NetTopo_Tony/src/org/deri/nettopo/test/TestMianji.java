package org.deri.nettopo.test;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
public class TestMianji {

	/**
	 * @param args
	 */
	public static void main(String args[]){
	   Point p1 = new Point(10,0);
	   Point p2 = new Point(10,-10);
	   Point p3 = new Point(0,0);
	   Point p4 = new Point(-10,-10);
	   Point p5 = new Point(-10,0);
	   Point p6 = new Point(0,10);
	   List<Point> list = new ArrayList<Point>();
	   list.add(p1);
	   list.add(p2);
	   list.add(p3);
	   list.add(p4);
	   list.add(p5);
	   list.add(p6);
	   TestMianji t = new TestMianji();
	   double area = t.getArea(list);
	   System.out.println(area);
	   
	   System.out.println(String.format("%1$.2f", 12.12555));
	}
	
	public double getArea(List<Point> list)
	{
	   //S = 0.5 * ( (x0*y1-x1*y0) + (x1*y2-x2*y1) + ... + (xn*y0-x0*yn) )

	   double area = 0.00;
//	   for(int i = 0;i<list.size();i++){
//	    if(i<list.size()-1){
//	     Point p1 = list.get(i);
//	     Point p2 = list.get(i+1);
//	     area += p1.getX()*p2.getY() - p2.getX()*p1.getY();
//	    }else{
//	     Point pn = list.get(i);
//	     Point p0 = list.get(0);
//	     area += pn.getX()*p0.getY()- p0.getX()*pn.getY();
//	    }
//	   
//	   }
	   list.add(list.get(0));
	   for(int i=0;i<list.size()-1;++i)
	   {
		   area+=list.get(i).x*list.get(i+1).y-list.get(i).y*list.get(i+1).x;
	   }
	   area = area/2.00;
	  
	   return area>0?area:-area;
	}
}
