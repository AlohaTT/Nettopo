package org.deri.nettopo.app.wizard.page;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.deri.nettopo.visulizer.*;

public class Page_VisualizerType extends WizardPage {
	List vDescription;
	java.util.List<String> vNames = new ArrayList<String>();
	
	public Page_VisualizerType(){
		super("VisualizerType");
		setDescription("Please Choose a type of Visualizer you want to create.");
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		vDescription = new List(composite, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true);
		vDescription.setLayoutData(gridData);

		try{
			InputStream is = Visulizer.class.getResourceAsStream("visualizer.properties");
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		    String property;
		    
		    while((property = br.readLine())!=null){
		    	if(property.trim().startsWith("#")){
		    		continue;
		    	}
		    	
		    	String name, description;
		    	int index = property.indexOf("=");
				  if((index != -1)){
					  name = property.substring(0,index).trim();
					  description = property.substring(index+1,property.length()).trim();
				  }else{
					  name = property.trim();
					  try{
						  Class.forName(name);
					  }catch(ClassNotFoundException ex){
						  continue;
					  }
					  description = "";
				  }
		    	
				  vNames.add(name);
		    	vDescription.add(description);
		    }
		    is.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
	    vDescription.select(0);
		
		setControl(composite);
	}
	
	public IWizardPage getNextPage(){
		Visulizer visualizer = getVisualizer();
		String[] attrNames = visualizer.getAttrNames();
		if(attrNames.length > 0){
			return super.getNextPage();
		}else{
			WizardPage page = (WizardPage)getWizard().getPage("VisualizerAttributes");
			page.setPageComplete(true);
			return page.getNextPage();
		}
	}
	
	public Visulizer getVisualizer(){
		return VisualizerFactory.getInstance((String)vNames.get(vDescription.getSelectionIndex()));
	}
}