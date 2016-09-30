package org.deri.nettopo.algorithm.dvhop.function;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.dvhop.Algor_DVHOP;
import org.deri.nettopo.app.NetTopoApp;

import org.deri.nettopo.util.FormatVerifier;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.Window;

/*设置不对称链路百分比*/

public class DVHOP_SetAsymmetryPercentage implements AlgorFunc{

	private Algorithm algorithm;
	
	public DVHOP_SetAsymmetryPercentage(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public DVHOP_SetAsymmetryPercentage(){
		this.algorithm = null;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	//@Override
	public void run() {
		set_Percentage();
	}
	private void set_Percentage() {
		InputDialog dlg = new InputDialog(NetTopoApp.getApp().getSh_main(), 
				"Asymmetry Percentage",
				"Please enter", 
				String.valueOf(((Algor_DVHOP)this.algorithm).getPercentage()),
				new IInputValidator() {
					public String isValid(String input) {
						if (!FormatVerifier.isPositive(input)) {
							return "percentange must be a positive integer.";
						} else {
							return null;
						}
					}
				});
		if (dlg.open() == Window.OK) {
			int nodeID = Integer.parseInt(dlg.getValue());
			if(this.algorithm instanceof Algor_DVHOP){
				((Algor_DVHOP) this.algorithm).setPercentage(nodeID);
				/*for debug*/
				System.out.println("now the Asymmetry Percentage is "
						+((Algor_DVHOP) this.algorithm).getPercentage());
			}
		}
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
	

}
