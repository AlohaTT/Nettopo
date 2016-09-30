package org.deri.nettopo.algorithm.ckn.function;

import java.io.IOException;
import main.MainApp;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.StaticFunction;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.app.wizard.Wizard_CreateCKNTasks;
import org.deri.nettopo.app.wizard.Wizard_CreateCKNTimes;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.DuplicateCoordinateException;
import org.deri.nettopo.util.Property;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;

public class CKN_TPGF_FindOnePath_Parallel implements AlgorFunc {

	public CKN_TPGF_FindOnePath_Parallel(Algorithm algorithm) {
		// this.algorithm = algorithm;
	}

	@Override
	public void run() {
		boolean flag = NetTopoApp.getApp().getNetwork().isPreparedForTPFG();
		int val = -Integer.MAX_VALUE;
		while ((IDialogConstants.CANCEL_ID != val) && (!flag)) {
			Wizard_CreateCKNTasks wizard = new Wizard_CreateCKNTasks();
			WizardDialog dlg = new WizardDialog(NetTopoApp.getApp()
					.getSh_main(), wizard);
			val = dlg.open();
			flag = NetTopoApp.getApp().getNetwork().isPreparedForTPFG();
			Property.isSensor = false;
		}
		if (flag) {
			Wizard_CreateCKNTimes wizard = new Wizard_CreateCKNTimes();
			WizardDialog dlg = new WizardDialog(NetTopoApp.getApp()
					.getSh_main(), wizard);
			val = dlg.open();
			if(val==IDialogConstants.OK_ID)
			create();
		}
		Property.isTPGF=false;
	}

	private void create() {
		java.util.List<WirelessSensorNetwork> tasks = null;
		try {
			if (Property.tasks == null || Property.tasks.size() == 0) {
				tasks = StaticFunction.createWSNLists(NetTopoApp.getApp()
						.getNetwork());
				Property.tasks = StaticFunction.copyTasks(tasks);
			} else
				tasks = StaticFunction.copyTasks(Property.tasks);
			System.out.println("********************tasks:" + tasks.size());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DuplicateCoordinateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			MainApp.master.setTasksList(tasks);
			try {
				MainApp.master.compute();
				//ChartPainter.startPaint("sensorNumber", "hopNum");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
