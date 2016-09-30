package org.deri.nettopo.display;

import java.util.ArrayList;
import java.util.List;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.xml.ReadXML;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

public class ShowResultShell extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static Painter painter;
	

	public static void startPaint(Display display) {
		try {
			ShowResultShell shell = new ShowResultShell(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public ShowResultShell(Display display) throws Exception {
		super(display, SWT.SHELL_TRIM|SWT.TOP);
		
		Combo combo = new Combo(this, SWT.READ_ONLY);
		List<String> filename=ReadXML.getTasksName();
		if(filename==null||filename.size()==0);
		else
		{
			for(String task:filename)
			{
				combo.add(task);
			}
			combo.select(0);
			ArrayList<WirelessSensorNetwork> allTasks=ReadXML.getAllTasksList();
			WirelessSensorNetwork wsnNow=allTasks.get(0);
			NetTopoApp.getApp().setNetwork(wsnNow);
			Coordinate c =wsnNow.getSize();
			if (c.z == 0) { // 2D
				painter = PainterFactory.getInstance("2D");
				//NetTopoApp.getApp().createCanv_main();
			} else { // 3D
				painter = PainterFactory.getInstance("3D");
			}
		}
		
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo tempCom=(Combo)e.getSource();
				ArrayList<WirelessSensorNetwork> allTasks=null;
				try {
					allTasks = ReadXML.getAllTasksList();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(allTasks==null||allTasks.size()==0)
				return;
				WirelessSensorNetwork wsnNow=allTasks.get(tempCom.getSelectionIndex());
				NetTopoApp.getApp().setNetwork(wsnNow);
				Coordinate c =wsnNow.getSize();
				if (c.z == 0) { // 2D
					painter = PainterFactory.getInstance("2D");
					//NetTopoApp.getApp().createCanv_main();
				} else { // 3D
					painter = PainterFactory.getInstance("3D");
				}
			}
		});
		combo.setBounds(43, 56, 88, 25);
		
		Label lblChoseTheTask = new Label(this, SWT.NONE);
		lblChoseTheTask.setBounds(29, 24, 131, 17);
		lblChoseTheTask.setText("Chose the Task Name");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(176, 149);
		setLocation(700,200);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
