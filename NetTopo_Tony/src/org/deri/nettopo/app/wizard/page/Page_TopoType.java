package org.deri.nettopo.app.wizard.page;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;

import org.deri.nettopo.topology.*;
import org.deri.nettopo.util.Property;

public class Page_TopoType extends WizardPage {
	List topoDescription;
	ArrayList<String> topoNames = new ArrayList<String>();
	
	public Page_TopoType(){
		super("TopoType");
		setDescription("Please Choose a topology type of multiple nodes.");

	}
	
	
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		topoDescription = new List(composite, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
		topoDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		try{
			InputStream is = Topology.class.getResourceAsStream("topology.properties");
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
					  description = "";
				  }
				  
				  /* if the class with that name does not exist, just neglect this line */
				  try{
					  Class.forName(name);
				  }catch(ClassNotFoundException ex){
					  continue;
				  }
		    	
		    	topoNames.add(name);
		    	topoDescription.add(description);
		    }
		    is.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
	    topoDescription.select(0);
		
		setControl(composite);
	}
	
	public Topology getTopology(){
		if(Property.isTPGF&&Property.isSensor)
		return TopologyFactory.getInstance((String)topoNames.get(topoDescription.getSelectionIndex())+"_Parallel");
		return TopologyFactory.getInstance((String)topoNames.get(topoDescription.getSelectionIndex()));
	}
}