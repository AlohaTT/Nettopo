package org.deri.nettopo.app.wizard;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.app.wizard.page.Page_RandomWaypointAttr;
import org.deri.nettopo.mobility.models.RandomWaypoint;
import org.deri.nettopo.util.AttractorField;
import org.deri.nettopo.util.DistrictedArea;
import org.eclipse.jface.wizard.Wizard;

public class Wizard_CreateRandomWaypointModel extends Wizard {
	private Page_RandomWaypointAttr randomWaypointAttr = null;
	private double start = 0.;
	private double interval = 1.;
	
	public Wizard_CreateRandomWaypointModel(){
		this.setWindowTitle("RandomWaypoint");
		randomWaypointAttr = new Page_RandomWaypointAttr();
		this.addPage(randomWaypointAttr);
	}
	
	public Wizard_CreateRandomWaypointModel(double interval){
		this.interval = interval;
		this.setWindowTitle("RandomWaypoint");
		randomWaypointAttr = new Page_RandomWaypointAttr();
		this.addPage(randomWaypointAttr);
	}
	
	public Wizard_CreateRandomWaypointModel(double start, double interval){
		this.start = start;
		this.interval = interval;
		this.setWindowTitle("RandomWaypoint");
		randomWaypointAttr = new Page_RandomWaypointAttr();
		this.addPage(randomWaypointAttr);
	}

	public boolean performFinish() {
		String log = "";
		String[] attrsNames = randomWaypointAttr.getAttrsNames();
		String[] attrs = randomWaypointAttr.getAttrs();
		if(attrsNames.length == attrs.length){
			for(int i=0;i<attrsNames.length;i++){
				log += attrsNames[i] + ":" + attrs[i] + ", ";
			}
		}
		log = log.substring(0, log.length() - 2) + ".";
		NetTopoApp.getApp().addLog("RandomWaypoint model has benn built.\n"+log);
		return true;
	}
	
	public RandomWaypoint getRandomWaypoint(){
		return randomWaypointAttr.getRandomWaypoint();
	}
	
	public double[][] getChangedPath(){
		RandomWaypoint randomWaypoint = this.getRandomWaypoint();
		randomWaypoint.generatePath();
		double duration = randomWaypoint.getDuration();
		return randomWaypoint.getChangedPath(start, duration, interval);
	}
	
	public AttractorField getAttractorField(){
		return randomWaypointAttr.getAttractorField();
	}
	
	public DistrictedArea getDistrictedArea(){
		return randomWaypointAttr.getDistrictedArea();
	}
}
