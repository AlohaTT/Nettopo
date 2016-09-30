package org.deri.nettopo.edge;

import org.deri.nettopo.util.Coordinate;


public class Edge {
	
	private int id_s;//起点id
	private int id_e;//终点id
	private Coordinate cs;//起点坐标
	private Coordinate ce;//终点坐标
	
	public Edge(){
		
	}


	public Edge(int id_s, int id_e, Coordinate cs, Coordinate ce) {
		super();
		this.id_s = id_s;
		this.id_e = id_e;
		this.cs = cs;
		this.ce = ce;
	}

	public int getId_s() {
		return id_s;
	}


	public void setId_s(int id_s) {
		this.id_s = id_s;
	}


	public int getId_e() {
		return id_e;
	}


	public void setId_e(int id_e) {
		this.id_e = id_e;
	}


	public Coordinate getCs() {
		return cs;
	}


	public void setCs(Coordinate cs) {
		this.cs = cs;
	}


	public Coordinate getCe() {
		return ce;
	}


	public void setCe(Coordinate ce) {
		this.ce = ce;
	}
}
