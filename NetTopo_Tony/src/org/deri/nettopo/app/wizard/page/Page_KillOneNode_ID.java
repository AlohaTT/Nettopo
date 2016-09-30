package org.deri.nettopo.app.wizard.page;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;

import org.deri.nettopo.util.*;
import org.deri.nettopo.app.NetTopoApp;


public class Page_KillOneNode_ID  extends WizardPage {
	private Label lbl_id;
	private Text txt_id;
	private int id;
	private boolean visible = true;
	
	public Page_KillOneNode_ID(){
		super("KillOneNode");
		setDescription("Please enter the id of node you want to kill");
		setPageComplete(false);
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout(3,false));
		
		lbl_id = new Label(composite, SWT.NONE);
		lbl_id.setText("id of the node:");
		txt_id = new Text(composite,SWT.BORDER);
		txt_id.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				String numStr = txt_id.getText().trim();
				if(FormatVerifier.isPositive(numStr)){
					id = Integer.parseInt(numStr);
					int[] nodeIDs = NetTopoApp.getApp().getNetwork().getAllNodesID();
					if(Util.isIntegerInIntegerArray(id, nodeIDs)){
						setPageComplete(true);
						setErrorMessage(null);
						
					}else{
						setErrorMessage("id of node should exist");
						setPageComplete(false);
					}
				}else{
					setErrorMessage("id of node should be a positive integer");
					setPageComplete(false);
				}
			}
		});
		final Button button = new Button(composite,SWT.CHECK);
		button.setText("invisible");
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(button.getSelection()){
					visible = false;
				}else{
					visible = true;
				}
			}
		});
		
		/* set the focus in the first text area when the page is first shown */
		composite.addPaintListener(new PaintListener(){
			boolean firstTime = true;
			public void paintControl(PaintEvent e){
				if(firstTime){
					txt_id.setFocus();
					firstTime = false;
				}
			}
		});
		
		setControl(composite);
	}

	public int getID(){
		return id;
	}
	
	public boolean getIsvisible(){
		return visible;
	}
}
