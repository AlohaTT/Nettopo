package org.deri.nettopo.app.wizard.page;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.*;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.node.*;

public class Page_KillNodes_Circle extends WizardPage {

	private Label lbl_circle, lbl_x, lbl_y, lbl_radius;
	private Text txt_x, txt_y, txt_radius;
	private boolean visible = true;
	private Coordinate centre;
	private int radius;
	
	private WirelessSensorNetwork wsn;
	private boolean[] argValid;
	
	public Page_KillNodes_Circle(){
		super("KillNode_Circle");
		setDescription("Please enter the arguments of the circle");
		setPageComplete(false);
		centre = new Coordinate();
		radius = 0;
		wsn = NetTopoApp.getApp().getNetwork();
		argValid = new boolean[3];
		for(int i=0;i<argValid.length;i++){
			argValid[i] = false;
		}
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		lbl_circle = new Label(composite, SWT.NONE);
		lbl_circle.setText("Coordiante of Circle Centre");
		lbl_circle.setLayoutData(gridData);
		
		lbl_x = new Label(composite, SWT.NONE);
		lbl_x.setText("x: ");
		txt_x = new Text(composite, SWT.BORDER);
		txt_x.addModifyListener(new ModifyListener(){		
			public void modifyText(ModifyEvent e){
				String xString = txt_x.getText().trim();
				if(FormatVerifier.isInRange(xString, 0, wsn.getSize().x)){
					setErrorMessage(null);
					centre.x = Integer.parseInt(xString);
					argValid[0]=true;
					if(checkAllArgValid()){
						setAllPagesComplete(true);
						setPageComplete(true);
					}
				}else{
					argValid[0]=false;
					setPageComplete(false);
					setErrorMessage("You must assign x between 0 (inclusive) and " + wsn.getSize().x +" (exclusive)");
				}
			}
		});
		
		lbl_y = new Label(composite, SWT.NONE);
		lbl_y.setText("y: ");
		txt_y = new Text(composite, SWT.BORDER);
		txt_y.addModifyListener(new ModifyListener(){		
			public void modifyText(ModifyEvent e){
				String yString = txt_y.getText().trim();
				if(FormatVerifier.isInRange(yString, 0, wsn.getSize().y)){
					setErrorMessage(null);
					centre.y = Integer.parseInt(yString);
					argValid[1]=true;
					if(checkAllArgValid()){
						setAllPagesComplete(true);
						setPageComplete(true);
					}
				}else{
					argValid[1]=false;
					setPageComplete(false);
					setErrorMessage("You must assign y between 0 (inclusive) and " + wsn.getSize().y +" (exclusive)");
				}
			}
		});
		
		lbl_radius = new Label(composite, SWT.NONE);
		lbl_radius.setText("Circle Radius");
		txt_radius = new Text(composite, SWT.BORDER);
		txt_radius.addModifyListener(new ModifyListener(){		
			public void modifyText(ModifyEvent e){
				String radiusStr = txt_radius.getText().trim();
				if(FormatVerifier.isNotNegative(radiusStr)){
					setErrorMessage(null);
					radius = Integer.parseInt(radiusStr);
					argValid[2]=true;
					if(checkAllArgValid()){
						setAllPagesComplete(true);
						setPageComplete(true);
					}
				}else{
					argValid[2]=false;
					setPageComplete(false);
					setErrorMessage("Raidus must be a non-negative integer");
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
					txt_x.setFocus();
					firstTime = false;
				}
			}
		});
		
		setControl(composite);
	}
	
	public int[] getNodesID(){
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Iterator<VNode> iter = wsn.getAllNodes().iterator();
		List<Integer> idList = new ArrayList<Integer>();
		while(iter.hasNext()){
			VNode node = (VNode)iter.next();
			Coordinate c = wsn.getCoordianteByID(node.getID());
			double dis = c.distance(centre);
			if(dis<=radius){
				idList.add(Integer.valueOf(node.getID()));
			}
		}
		int[] nodesID = new int[idList.size()];
		for(int i=0;i<nodesID.length;i++){
			nodesID[i] = ((Integer)idList.get(i)).intValue();
		}
		return nodesID;
	}
	
	private void setAllPagesComplete(boolean b){
		IWizardPage[] pages = getWizard().getPages();
		for(int i=0;i<pages.length;i++){
			((WizardPage)pages[i]).setPageComplete(b);
		}
	}
	
	private boolean checkAllArgValid(){
		for(int i=0;i<argValid.length;i++){
			if(!argValid[i])
				return false;
		}
		return true;
	}
	
	public boolean getIsvisible(){
		return visible;
	}
}
