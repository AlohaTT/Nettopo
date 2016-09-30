package org.deri.nettopo.app.wizard.page;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.gas.VGasFactory;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Page_GasType extends WizardPage {
	List gasDescription;
	ArrayList<String> gasNames = new ArrayList<String>();
	
	public Page_GasType(){
		super("GasType");
		setDescription("Please Choose a type of gas you want to create.");
	}
	
	public void createControl(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		gasDescription =  new List(composite, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
		gasDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		try{
			InputStream is = VGas.class.getResourceAsStream("gas.properties");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String property;
			
			while((property = br.readLine())!= null){
				if(property.trim().startsWith("#")){
					continue;
				}
				String name,description;
				int index = property.indexOf("=");
				if((index != -1)){
					name = property.substring(0,index).trim();
					description = property.substring(index+1, property.length()).trim();
				}else{
					name =  property.trim();
					try{
						Class.forName(name);
					}catch (ClassNotFoundException ex){
						continue;
					}
					description= "";
				}
				gasNames.add(name);
				gasDescription.add(description);
				
			}
			is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		gasDescription.select(0);
		
		setControl(composite);
		
	}
	
	public IWizardPage getNextPage(){
//		VGas gas = getGas();
//		String[] attrNames = gas.getAttrNames();
//		if(attrNames.length>0){
//			return super.getNextPage();
//		}else{
//			WizardPage page = (WizardPage)getWizard().getPage("GasAttributes");
//			page.setPageComplete(true);
//			return page.getNextPage();
//		}
		
		
		VGas gas = getGas();
		String[] attrNames = gas.getAttrNames();
		 
		if(attrNames.length > 0){
			{
				//System.out.println("-----------------");
				WizardPage page = (WizardPage)getWizard().getPage("GasAttributes");
				if(page.getControl()!=null)
					page.dispose();
				return super.getNextPage();
			}
		}else{
			
			System.out.println("++++++++++++++++++++++++++");
			WizardPage page = (WizardPage)getWizard().getPage("GasAttributes");
			page.setPageComplete(true);
			return page.getNextPage();
		}
		
	}
	
	public VGas getGas(){
		return VGasFactory.getInstance((String)gasNames.get(gasDescription.getSelectionIndex()));
	}
	
	
}
