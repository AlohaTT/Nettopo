package org.deri.nettopo.app.wizard;

import org.deri.nettopo.app.wizard.page.Page_VisualizerAttributes;
import org.deri.nettopo.app.wizard.page.Page_VisualizerType;
import org.eclipse.jface.wizard.Wizard;

import org.deri.nettopo.visulizer.Visulizer;

public class Wizard_VisualizeNetwork extends Wizard {
	private Page_VisualizerType page_type;
	private Page_VisualizerAttributes page_attr;
	private Visulizer visualizer;
	
	public Wizard_VisualizeNetwork(){
		page_type = new Page_VisualizerType();
		page_attr = new Page_VisualizerAttributes();
		
		// Add the pages
		addPage(page_type);
		addPage(page_attr);
	}
	
	/*
	 * Called when user clicks Finish button
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish(){
		visualizer = page_type.getVisualizer();
		String[] attrNames = visualizer.getAttrNames();
		for(int j=0;j<attrNames.length;j++){
			visualizer.setAttrValue(attrNames[j], page_attr.getAttrValue(attrNames[j]));
		}
		return true;
	}
	
	public Runnable getRunnable(){
		return visualizer;
	}
}

