package org.deri.nettopo.app.wizard.page;

import java.util.HashMap;

import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.node.*;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class Page_NodeAttributes extends WizardPage {
	private Text[] txt_attrs;
	private Label[] lbl_attrs;
	private VNode node;
	private String[] attrNames;
	private boolean[] attrValid;
	/**********add**************/
	private Text[] txt_step;
	private Text[] txt_max;
	
	private HashMap<String,Integer> nodeAttr=null;
	/**********add**************/
	Composite composite;
	
	
	
	
	public VNode getNode() {
		return node;
	}

	public void setNode(VNode node) {
		this.node = node;
	}

	public Page_NodeAttributes(){
		super("NodeAttributes");
		setDescription("Please enter the information of node attributes.");
		setPageComplete(false);
	}
	
	public void createControl(Composite parent){
		
		Page_NodeType page_nodeType = (Page_NodeType)getWizard().getPage("NodeType");
		node = page_nodeType.getNode();
		composite = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout(); 
		if(Property.isTPGF&&node.getClass().getName().contains("Sensor"))
			{
			gridLayout.numColumns = 4;
			nodeAttr=new HashMap<String,Integer>();
			}
		else
			gridLayout.numColumns = 2;
		
		/***************************************修改******************************************/
		composite.setLayout(gridLayout);
		setControl(composite);
	}
	
	public void setVisible(boolean visible){
		if(visible){
			Page_NodeType page_nodeType = (Page_NodeType)getWizard().getPage("NodeType");
			node = page_nodeType.getNode();
			if(node.getClass().getName().contains("Sensor"))
			Property.isSensor=true;
			else
				Property.isSensor=false;
			/* if the page is shown before, dispose all the controls on it first */
			if(lbl_attrs!=null){
				for(int i=0;i<attrNames.length;i++){
					lbl_attrs[i].dispose();
					txt_attrs[i].dispose();
					
					/**********add***************/
					if(txt_step!=null&&Property.isTPGF&&Property.isSensor)
						{
							txt_step[i].dispose();
							txt_max[i].dispose();
						}
					/**********add***************/
				}
			}
			/* dynamically build the page controls based on user's choose in the NodeType page */
			//Page_NodeType page_nodeType = (Page_NodeType)getWizard().getPage("NodeType");
			//可以通过Wizard来获取前面页面的对象
			//node = page_nodeType.getNode();
			attrNames = node.getAttrNames();
			attrValid = new boolean[attrNames.length];
			lbl_attrs = new Label[attrNames.length];
			txt_attrs = new Text[attrNames.length];
		/**********add***************/
			if(Property.isTPGF&&Property.isSensor)
			{
				txt_step=new Text[attrNames.length];
				txt_max=new Text[attrNames.length];
			}
		/**********add***************/
			
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
						boolean setSuccess = node.setAttrValue(attrNames[index], txt_arg.getText().trim());
						ifPageIsComplete(setSuccess,index);
						
						
						/*************add*****************************/
						if(Property.isTPGF&&Property.isSensor)
						{
							
							if(!Util.isNumeric(txt_arg.getText().trim()))
							setDescription("Please enter a number.");
							else
								nodeAttr.put(lbl_attrs[index].getText()+":min", Integer.parseInt(txt_arg.getText().trim()));
						}
						

						/*************add*****************************/
						
					}
				});
				
				/**********add*******************************************/
				
				if(Property.isTPGF&&Property.isSensor)
				{
				/**********add step*******************************************/
				txt_step[index] = new Text(composite,SWT.BORDER);
				txt_step[index].setData(new Integer(index)); // store the current index for listener to use later
				txt_step[index].addModifyListener(new ModifyListener(){
					public void modifyText(ModifyEvent e){
						Text txt_arg = (Text)e.widget;
						int index = ((Integer)txt_arg.getData()).intValue(); // get the current index now
						if(!Util.isNumeric( txt_arg.getText().trim()))
							setDescription("Please enter a number.");
						else
							nodeAttr.put(lbl_attrs[index].getText()+":step", Integer.parseInt(txt_arg.getText().trim()));
					}
				});
				
				/**********add step*******************************************/
				
				/**********add max*******************************************/
				txt_max[index] = new Text(composite,SWT.BORDER);
				txt_max[index].setData(new Integer(index)); // store the current index for listener to use later
				txt_max[index].addModifyListener(new ModifyListener(){
					
					public void modifyText(ModifyEvent e){
						Text txt_arg = (Text)e.widget;
						int index = ((Integer)txt_arg.getData()).intValue(); // get the current index now
						if(!Util.isNumeric( txt_arg.getText().trim()))
							setDescription("Please enter a number.");
						else
						{
							nodeAttr.put(lbl_attrs[index].getText()+":max", Integer.parseInt(txt_arg.getText().trim()));
						}
					}
				});
				
				/**********add max*******************************************/
				
				/**********add end*******************************************/
				}
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
		return node.getAttrValue(attrName);
	}
	
	private void ifPageIsComplete(boolean setSuccess, int index){
		if(setSuccess){
			setErrorMessage(null);
		}else{
			setErrorMessage(node.getAttrErrorDesciption());
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

	public HashMap<String, Integer> gethMap() {
		return nodeAttr;
	}

	public void sethMap(HashMap<String, Integer> hMap) {
		this.nodeAttr = hMap;
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
			 setControl(null);   
	}
	
	

}
