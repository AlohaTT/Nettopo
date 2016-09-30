package org.deri.nettopo.app.wizard.page;

import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.util.*;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;


import java.util.*;

public class Page_NodeLocation extends WizardPage {
	Button btn_random;
	Button btn_assign;
	Text txt_x;
	Text txt_y;
	Label lbl_x;
	Label lbl_y;
	WirelessSensorNetwork wsn;
	boolean isRandom = true;
	
	public Page_NodeLocation(){
		super("LocationType");
		setDescription("Please set the location of the node.");
		wsn = NetTopoApp.getApp().getNetwork();
	}
	
	public void createControl(Composite parent){
		
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		
		btn_random = new Button(composite,SWT.RADIO);
		btn_random.setText("Random");
		btn_random.setSelection(true);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		btn_random.setLayoutData(gridData);
		
		btn_assign = new Button(composite,SWT.RADIO);
		btn_assign.setText("Use the following coordinate");
		btn_assign.setLayoutData(gridData);
		
		lbl_x = new Label(composite, SWT.NONE);
		lbl_x.setText("x:");
		txt_x = new Text(composite,SWT.BORDER);
		lbl_y = new Label(composite, SWT.NONE);
		lbl_y.setText("y:");
		txt_y = new Text(composite,SWT.BORDER);
		
		enabledAssign(false);
		
		btn_random.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				enabledAssign(false);
				setErrorMessage(null);
				setPageComplete(true);
				isRandom = true;
			}
		});
		btn_assign.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				enabledAssign(true);
				isRandom = false;
				txt_x.setFocus();
				setPageComplete(checkInput()); // page isn't complete until the input is checked valid
			}
		});
		txt_x.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				setPageComplete(checkInput()); // page isn't complete until the input is checked valid
			}
		});
		txt_y.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				setPageComplete(checkInput()); // page isn't complete until the input is checked valid
			}
		});
		
		setControl(composite);
	}
	
	public Coordinate getCoordinate(){
		Coordinate c;
		if(btn_random.getSelection()){ // Random button is selected
			/* get a random coordinate */
			Random random = new Random(System.currentTimeMillis());
			c = new Coordinate(random.nextInt(wsn.getSize().x),random.nextInt(wsn.getSize().y),0);
			
			/* get another random coordiante if there exists one */
			while(wsn.hasDuplicateCoordinate(c)){ //
				c.setX(random.nextInt(wsn.getSize().x));
				c.setY(random.nextInt(wsn.getSize().y));
			}
		}else{
			c = new Coordinate(Integer.parseInt(txt_x.getText().trim()),Integer.parseInt(txt_y.getText().trim()),0);
		}
		return c;
	}
	
	public Coordinate getOneCoordinate(){
		return new Coordinate(Integer.parseInt(txt_x.getText().trim()),Integer.parseInt(txt_y.getText().trim()),0);
	}
	
	public boolean getSelectItem()
	{
		return isRandom;
	}
	
	private void enabledAssign(boolean b){
		if(b){
			lbl_x.setEnabled(true);
			lbl_y.setEnabled(true);
			txt_x.setEnabled(true);
			txt_y.setEnabled(true);
		}else{
			lbl_x.setEnabled(false);
			lbl_y.setEnabled(false);
			txt_x.setEnabled(false);
			txt_y.setEnabled(false);
		}
	}
	
	private boolean checkInput(){
		String xString = txt_x.getText().trim();
		String yString = txt_y.getText().trim();
		
		if(Property.isCreateOneTask)
			return true;
		
		
		/* check the x input */
		if(xString.length()<=0)
			return false;
		if(!FormatVerifier.isInRange(xString, 0, wsn.getSize().x)){
			setErrorMessage("You must assign x between 0 (inclusive) and " + wsn.getSize().x +" (exclusive)");
			return false;
		}
		setErrorMessage(null);
		
		/* check the y input */
		if(yString.length()<=0)
			return false;
		if(!FormatVerifier.isInRange(yString, 0, wsn.getSize().y)){
			setErrorMessage("You must assign y between 0 (inclusive) and " + wsn.getSize().y +" (exclusive)");
			return false;
		}
		setErrorMessage(null);
		
		/*  check if the coordinate is duplicate  */
		Coordinate c = new Coordinate(Integer.parseInt(xString),Integer.parseInt(yString),0);
		if(NetTopoApp.getApp().getNetwork().hasDuplicateCoordinate(c)){ 
			setErrorMessage("Sorry, there is already one node at that coordinate");
			return false;
		}
		setErrorMessage(null);
		return true;
	}
	
}
