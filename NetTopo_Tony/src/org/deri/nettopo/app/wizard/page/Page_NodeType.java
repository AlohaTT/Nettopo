package org.deri.nettopo.app.wizard.page;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;

import org.deri.nettopo.node.*;

public class Page_NodeType extends WizardPage {
	List nodeDescription;
	ArrayList<String> nodeNames = new ArrayList<String>();
	Composite composite;
	
	public Page_NodeType(){
		super("NodeType");
		setDescription("Please Choose a type of node you want to create.");
	}
	
	public void createControl(Composite parent){
		 composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		nodeDescription = new List(composite, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
		nodeDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
		try{
			InputStream is = VNode.class.getResourceAsStream("node.properties");
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		    String property;
		    
		    while ((property = br.readLine()) != null) {
				if (property.trim().startsWith("#")) {
					continue;
				}

				String name, description;
				int index = property.indexOf("=");
				if ((index != -1)) {
					name = property.substring(0, index).trim();//节点名字
					description = property.substring(index + 1,
							property.length()).trim();
				} else {
					name = property.trim();
					try {
						Class.forName(name);
					} catch (ClassNotFoundException ex) {
						continue;
					}
					description = "";
				}

				nodeNames.add(name);
				nodeDescription.add(description);
			}
		    is.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
	    nodeDescription.select(0);//默认选中第一个
		
		setControl(composite);
	}
	
	public IWizardPage getNextPage(){
		VNode node = getNode();
		String[] attrNames = node.getAttrNames();
		 
		if(attrNames.length > 0){
			{
				WizardPage page = (WizardPage)getWizard().getPage("NodeAttributes");
				if(page.getControl()!=null)
					page.dispose();
				return super.getNextPage();
			}
		}else{
			WizardPage page = (WizardPage)getWizard().getPage("NodeAttributes");
			page.setPageComplete(true);

			return page.getNextPage();
		}
	}
	
	
	
	public VNode getNode(){
			return VNodeFactory.getInstance((String)nodeNames.get(nodeDescription.getSelectionIndex()));
	}
}