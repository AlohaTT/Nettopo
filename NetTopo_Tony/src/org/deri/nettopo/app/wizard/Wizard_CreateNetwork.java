package org.deri.nettopo.app.wizard;

import org.deri.nettopo.app.*;
import org.deri.nettopo.app.wizard.page.Page_NetworkDimension;
import org.deri.nettopo.app.wizard.page.Page_NetworkSize;
import org.deri.nettopo.util.Coordinate;

import org.eclipse.jface.wizard.Wizard;

public class Wizard_CreateNetwork extends Wizard {
	private Page_NetworkDimension page_dimType;
	private Page_NetworkSize page_netSize;

	public Wizard_CreateNetwork() {
		page_dimType = new Page_NetworkDimension();
		page_netSize = new Page_NetworkSize();

		// Add the pages
		addPage(page_dimType);
		addPage(page_netSize);
	}

	/*
	 * Called when user clicks Finish button
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		Coordinate size = getNetSize();
		String dimenstion = getNetDimension();
		if (dimenstion.equals("2D")) {
			NetTopoApp.getApp().addLog("A new network (" + size.x + ", " + size.y + ") was created.");
		} else if (dimenstion.equals("3D")) {
			NetTopoApp.getApp().addLog("A new network (" + size.x + ", " + size.y + ", " + size.z + ") was created.");
		}
		return true;
	}

	public String getNetDimension() {
		return page_dimType.getDimension();
	}

	public Coordinate getNetSize() {
		return page_netSize.getNetworkSize();
	}
}
