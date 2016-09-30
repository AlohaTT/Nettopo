package org.deri.nettopo.app.wizard.page;

import org.deri.nettopo.util.Util;
import org.deri.nettopo.util.*;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class Page_NetworkSize extends WizardPage {
	Text[] txt_attrs;
	Label[] lbl_attrs;
	String[] attrNames;
	boolean[] attrValid;
	String dimension;
	Composite composite;
	Coordinate netSize;
	
	public Page_NetworkSize(){
		super("NetworkSize");
		setDescription("Please enter the information of network size");
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
			
			this.createDimension(((Page_NetworkDimension)getWizard().getPage("DimensionType")).getDimension());
			
			attrValid = new boolean[attrNames.length];
			lbl_attrs = new Label[attrNames.length];
			txt_attrs = new Text[attrNames.length];
			netSize = new Coordinate(0,0,0);
			
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
						/* check if the current argument is valid and set successfully */
						if(FormatVerifier.isPositive(txt_arg.getText())){
							setSize(attrNames[index],Integer.parseInt(txt_arg.getText()));
							setErrorMessage(null);
							attrValid[index] = true;
						}else{
							setErrorMessage("It should be a positive integer");
							attrValid[index] = false;
						}
						/* check if all arguments are valid*/
						if(Util.checkAllArgValid(attrValid)){
							setPageComplete(true);
						}else{
							setPageComplete(false);
						}
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
	
	public void setSize(String name, int value){
		if(name.equals("Length")){
			netSize.x = value;
		}else if(name.equals("Width")){
			netSize.y = value;
		}else if(name.equals("Height")){
			netSize.z = value;
		}
	}
	
	public Coordinate getNetworkSize(){
		return netSize;
	}
	
	private void createDimension(String dimension){
		if(dimension.equals("2D")){
			attrNames = new String[2];
			attrNames[0] = "Length";
			attrNames[1] = "Width";
		}else if(dimension.equals("3D")){
			attrNames = new String[3];
			attrNames[0] = "Length";
			attrNames[1] = "Width";
			attrNames[2] = "Height";
		}else{
			return;
		}
	}
}
