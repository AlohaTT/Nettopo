package org.deri.nettopo.app.wizard.page;

import java.util.Random;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import org.deri.nettopo.util.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.app.NetTopoApp;


public class Page_KillNodes_Random  extends WizardPage {
	private int num;
	private boolean visible;
	
	public Page_KillNodes_Random(){
		super("KillNode_Random");
		setDescription("Please enter the number of nodes you want to kill");
		setPageComplete(false);
		num = 0;
		visible = true;
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout(3,false));
		
		Label lbl_num = new Label(composite, SWT.NONE);
		lbl_num.setText("number of nodes:");
		final Text txt_num = new Text(composite,SWT.BORDER);
		txt_num.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				String numStr = txt_num.getText().trim();
				if(FormatVerifier.isPositive(numStr)){
					setErrorMessage(null);
					num = Integer.parseInt(numStr);
					setAllPagesComplete(true);
					setPageComplete(true);
				}else{
					setErrorMessage("Number of nodes should be a positive integer");
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
					txt_num.setFocus();
					firstTime = false;
				}
			}
		});
		
		setControl(composite);
	}
	
	public int[] getNodesID(){
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		int[] nodesExist = wsn.getAllNodesID();
		int size = nodesExist.length;
		if(num >= size){
			return nodesExist;
		}
		int[] nodesID = new int[num];
		for(int i=0;i<num;){
			int nextIndex = 1 + new Random().nextInt(size-1);
			if(Util.isIntegerInIntegerArray(nodesExist[nextIndex],nodesID)){
				continue;
			}else{
				nodesID[i] = nodesExist[nextIndex];
			}
			i++;
		}
		return nodesID;
	}
	
	public IWizardPage getNextPage(){
		return null; // no next page
	}
	
	private void setAllPagesComplete(boolean b){
		IWizardPage[] pages = getWizard().getPages();
		for(int i=0;i<pages.length;i++){
			((WizardPage)pages[i]).setPageComplete(b);
		}
	}
	
	public boolean getIsvisible(){
		return visible;
	}
	
}
