package org.deri.nettopo.app.wizard;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.app.wizard.page.Page_GasAttributes;
import org.deri.nettopo.app.wizard.page.Page_GasLocation;
import org.deri.nettopo.app.wizard.page.Page_GasType;

import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;

import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;
import org.eclipse.jface.wizard.Wizard;

public class Wizard_CreateOneGas extends Wizard {
	private Page_GasType page_gasType;
	private Page_GasAttributes page_gasAttr;
	private Page_GasLocation page_gasLoc;
	
	private VGas gas;
	
	public Wizard_CreateOneGas(){
		page_gasType = new Page_GasType();
		page_gasAttr = new Page_GasAttributes();
		page_gasLoc = new Page_GasLocation();
		
		//add the pages
		addPage(page_gasType);
		addPage(page_gasAttr);
		addPage(page_gasLoc);
	}

	/**
	 * called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard@performFinish()
	 */
	public boolean performFinish(){
		
		gas = page_gasType.getGas();
		String[] attrNames = gas.getAttrNames();
		for(int i=0;i<attrNames.length;i++){
			gas.setAttrValue(attrNames[i], page_gasAttr.getAttrValue(attrNames[i]));
			
		}
		/*get the display location of the gas*/
		Coordinate c = page_gasLoc.getCoordinate();
		
		if(c==null){
			NetTopoApp.getApp().addLog("No gas are created");
			return true;
		}
		
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		try{
			wsn.addGas(gas, c);
			NetTopoApp.getApp().getPainter().paintGas(gas.getID());
			NetTopoApp.getApp().getPainter().rePaint();
		}catch(DuplicateCoordinateException ex){
			ex.printStackTrace();
		}
		NetTopoApp.getApp().addLog("One Gas with type" + gas.getClass().getName()+ "was created");
		return true;
		
	}
}
