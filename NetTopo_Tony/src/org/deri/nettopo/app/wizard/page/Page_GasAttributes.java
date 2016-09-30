package org.deri.nettopo.app.wizard.page;

import org.deri.nettopo.util.Util;
import org.deri.nettopo.gas.*;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class Page_GasAttributes extends WizardPage {
	private Text[] txt_attrs;
	private Label[] lbl_attrs;
	private VGas gas;
	private String[] attrNames;
	private boolean[] attrValid;
	
	Composite composite;
	
	public Page_GasAttributes(){
		super("GasAttributes");
		setDescription("Please enter the information of gas attributes.");
		setPageComplete(false);
	}
	
	public void createControl(Composite parent){
		composite = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		
		setControl(composite);
	}
	
	public void setVisible(boolean visible){
		if(visible){
			/* if the page is shown before, dispose all the controls on it first */
			if(lbl_attrs!=null){
				for(int i=0;i<attrNames.length;i++){
					lbl_attrs[i].dispose();
					txt_attrs[i].dispose();
				}
			}
			
			/* dynamically build the page controls based on user's choose in the GasType page */
			Page_GasType page_gasType = (Page_GasType)getWizard().getPage("GasType");
			gas = page_gasType.getGas();
			attrNames = gas.getAttrNames();
			
			attrValid = new boolean[attrNames.length];
			lbl_attrs = new Label[attrNames.length];
			txt_attrs = new Text[attrNames.length];
						
			for(int i=0;i<attrNames.length;i++){
				attrValid[i]=false;
			}
			
			for(int index=0;index<attrNames.length;index++){
				lbl_attrs[index] = new Label(composite,SWT.NONE);
				lbl_attrs[index].setText(attrNames[index]);
				txt_attrs[index] = new Text(composite,SWT.BORDER);
				txt_attrs[index].setData(new Integer(index)); // store the current index for listener to use later
				txt_attrs[index].addModifyListener(new ModifyListener(){
					
					public void modifyText(ModifyEvent e){
						Text txt_arg = (Text)e.widget;
						int index = ((Integer)txt_arg.getData()).intValue(); // get the current index now
						boolean setSuccess = gas.setAttrValue(attrNames[index], txt_arg.getText().trim());
						ifPageIsComplete(setSuccess,index);
					}
				});
			}
			composite.layout();
			
			/* set the focus in the first text area when the page is first shown */
			composite.addPaintListener(new PaintListener(){
				boolean firstTime = true;
				public void paintControl(PaintEvent e){
					if(firstTime){
						if(txt_attrs[0]!=null){
							txt_attrs[0].setFocus();
							firstTime = false;
						}
					}
				}
			});
			
			
		}
		super.setVisible(visible);
	}

	public String getAttrValue(String attrName){
		return gas.getAttrValue(attrName);
	}
	
	private void ifPageIsComplete(boolean setSuccess, int index){
		if(setSuccess){
			setErrorMessage(null);
		}else{
			setErrorMessage(gas.getAttrErrorDesciption());
			setPageComplete(false);
		}
		attrValid[index] = setSuccess;
		/* check if all arguments are valid*/
		if(Util.checkAllArgValid(attrValid)){
			setPageComplete(true);
		}else{
			setPageComplete(false);
		}
	}
}
