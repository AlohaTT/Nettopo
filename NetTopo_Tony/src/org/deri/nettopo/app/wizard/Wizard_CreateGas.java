package org.deri.nettopo.app.wizard;

import org.deri.nettopo.util.*;
import org.deri.nettopo.gas.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.app.*;
import org.deri.nettopo.app.wizard.page.Page_GasAttributes;
import org.deri.nettopo.app.wizard.page.Page_GasType;
import org.deri.nettopo.app.wizard.page.Page_GasTopoAttributes;
import org.deri.nettopo.app.wizard.page.Page_GasTopoType;

import org.eclipse.jface.wizard.Wizard;


public class Wizard_CreateGas extends Wizard {
	private Page_GasType page_gasType;
	private Page_GasAttributes page_gasAttr;
	private Page_GasTopoType page_gastopoType;
	private Page_GasTopoAttributes page_gastopoAttr;
	
	public Wizard_CreateGas(){
		page_gasType = new Page_GasType();
		page_gasAttr = new Page_GasAttributes();
		page_gastopoType = new Page_GasTopoType();
		page_gastopoAttr = new Page_GasTopoAttributes();
		
		
		// Add the pages
		addPage(page_gasType);
		addPage(page_gasAttr);
		addPage(page_gastopoType);
		addPage(page_gastopoAttr);
		
	}
	
	/*
	 * Called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish(){
		
		/* get coordinates of all nodes to create */
		Coordinate[] coordinates = page_gastopoAttr.getCoordinates();
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		
		if(coordinates==null)
			return true;
		
		VGas gas = null;
		
		/* create nodes and set their attributes according to sample node */
		for(int i=0;i<coordinates.length;i++){
			/* create a wireless sensor node and set it's attributes */
			gas = page_gasType.getGas();
			String[] attrNames = gas.getAttrNames();
			for(int j=0;j<attrNames.length;j++){
				gas.setAttrValue(attrNames[j], page_gasAttr.getAttrValue(attrNames[j]));
			}
			try{
				wsn.addGas(gas, coordinates[i]);
				NetTopoApp.getApp().getPainter().paintGas(gas.getID());
			} catch(DuplicateCoordinateException ex){
				ex.printStackTrace();
				return false;
			}
		}
		NetTopoApp.getApp().getPainter().rePaint();
		NetTopoApp.getApp().addLog(coordinates.length + " gas with type "+ gas.getClass().getName() + " were created.");
		return true;
	}
}