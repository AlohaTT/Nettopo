package org.deri.nettopo.app.wizard;



import org.deri.nettopo.app.wizard.page.Page_CKNTimes;

import org.eclipse.jface.wizard.Wizard;


public class Wizard_CreateCKNTimes extends Wizard {

	private Page_CKNTimes page_CKNTimes;
	//private Page_CKNTimes page_CKNTimes;
	public Wizard_CreateCKNTimes() {
		setWindowTitle("Wizard Create TPGFTasks");
		page_CKNTimes = new Page_CKNTimes();
		// Add the pages
		addPage(page_CKNTimes);
		
	}

	@Override
	public boolean performFinish() {
		/*********************** ÐÞ¸Ä *****************************/
		return true;
	}

//	@Override
//	public void createPageControls(Composite pageContainer) {
//		//super.createPageControls(pageContainer);
//	}

	
	
	
	

/******************************end**************************************************************/

}
