package org.deri.nettopo.app.wizard.page;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;

public class Page_KillNodesRange extends WizardPage {
	List range;
	
	public Page_KillNodesRange(){
		super("KillRange");
		setDescription("Please select nodes in one of following way");
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		range = new List(composite, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		range.setLayoutData(gridData);
		range.add("Random");
		range.add("In a circle");
		
		range.select(0);
		
		setControl(composite);
	}
	
	public IWizardPage getNextPage(){
		int index = getRangeIndex();
		switch (index){
		case 0: // random
			return (WizardPage)getWizard().getPage("KillNode_Random");
		case 1: // in a circle
			return (WizardPage)getWizard().getPage("KillNode_Circle");
		}
		return null; // cannot happen
	}
	
	public int getRangeIndex(){
		return range.getSelectionIndex();
	}
}