package org.deri.nettopo.app.wizard.page;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;

public class Page_NetworkDimension extends WizardPage {
	Button btn_2D;
	Button btn_3D;
	
	public Page_NetworkDimension(){
		super("DimensionType");
		setDescription("Please Choose a dimension.");
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		Group types = new Group(composite, SWT.SHADOW_IN);
		types.setText("Dimension");
		types.setLayout(new RowLayout(SWT.VERTICAL));
		GridData gridData = new GridData(SWT.FILL,SWT.NONE,true,false);
		types.setLayoutData(gridData);
		btn_2D = new Button(types,SWT.RADIO);
		btn_2D.setText("2D");
		btn_2D.setSelection(true);
		btn_3D = new Button(types,SWT.RADIO);
		btn_3D.setText("3D");
		setControl(composite);
	}
	
	public String getDimension(){
		if(btn_3D.getSelection()){
			return "3D";
		}else 
			return "2D";
	}
}
