package org.deri.nettopo.app.wizard.page;

import java.util.HashMap;

import org.deri.nettopo.util.*;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.topology.*;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class Page_TopoAttributes extends WizardPage {
	Text[] txt_args;
	Label[] lbl_args;
	Topology topology;
	Coordinate[] coordinates;
	String[] argNames;
	boolean[] attrValid;

//	/********** add **************/
//	private Text[] txt_step;
//	private Text[] txt_max;
//	boolean isRandom = false;
	private HashMap<String, Integer> nodes = null;
	/********** add **************/
	Composite composite;

	
	
	
	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public Page_TopoAttributes() {
		super("TopoAttr");
		setDescription("Please set the arguments of this topology.");
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
//		/*************************************** 修改 ******************************************/
//		Page_TopoType page_topoType = (Page_TopoType) getWizard().getPage(
//				"TopoType");
//		// 可以通过Wizard来获取前面页面的对象
//		topology = page_topoType.getTopology();
//		if (topology.getClass().getName().contains("Random"))
//			isRandom = true;
//		if (Property.isTPGF && isRandom) {
//			gridLayout.numColumns = 4;
//			nodes = new HashMap<String, Integer>();
//
//		} else
//			gridLayout.numColumns = 2;
//
//		/*************************************** 修改 ******************************************/
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);

		setControl(composite);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			if (lbl_args != null) {
				for (int i = 0; i < argNames.length; i++) {
					lbl_args[i].dispose();
					txt_args[i].dispose();
				}
			}

			/*
			 * dynamically build the page controls based on user's choose in the
			 * last page
			 */
			Page_TopoType page_topoType = (Page_TopoType) getWizard().getPage(
					"TopoType");
			topology = page_topoType.getTopology();//在前一个页面已经实例化

			Page_NodeAttributes page_NodeAttributes = (Page_NodeAttributes) getWizard().getPage("NodeAttributes");
			Property.nodeAttr=page_NodeAttributes.gethMap();
			
			argNames = topology.getArgNames();

			attrValid = new boolean[argNames.length];
			txt_args = new Text[argNames.length];
			lbl_args = new Label[argNames.length];

//			/********** add ***************/
//			if (Property.isTPGF && isRandom) {
//				txt_step = new Text[argNames.length];
//				txt_max = new Text[argNames.length];
//			}
//			/********** add ***************/

			for (int i = 0; i < argNames.length; i++) {
				attrValid[i] = false;
			}

			for (int index = 0; index < argNames.length; index++) {
				lbl_args[index] = new Label(composite, SWT.NONE);
				lbl_args[index].setText(argNames[index]);
				txt_args[index] = new Text(composite, SWT.BORDER);
				txt_args[index].setData(new Integer(index));
				txt_args[index].addModifyListener(new ModifyListener() {

					public void modifyText(ModifyEvent e) {
						Text txt_arg = (Text) e.widget;
						int index = ((Integer) txt_arg.getData()).intValue();
						boolean setSuccess = topology.setArgValue(
								argNames[index], txt_arg.getText().trim());
						/*
						 * check if the current argument is valid and set
						 * successfully
						 */
						if (setSuccess) {
							setErrorMessage(null);
						} else {
							setErrorMessage(topology.getArgErrorDescription());
							setPageComplete(false);
						}
						attrValid[index] = setSuccess;
						/* check if all arguments are valid */
						if (!Property.isCreateTasks&&Util.checkAllArgValid(attrValid)) {/***********************alert****************/
							coordinates = topology.getCoordinates();
							try {
								if (checkCoordinates(coordinates)) {
									setErrorMessage(null);
									setPageComplete(true);
								} else {
									setPageComplete(false);
								}
							} catch (CoordinateNotFoundException ex) {
								ex.printStackTrace();
								return;
							}
						}
						else if(Property.isCreateTasks)
						{
							setErrorMessage(null);
							setPageComplete(true);
						}
					}
				});

			}
			composite.layout();
			/* set the focus in the first text area when the page is first shown */
			composite.addPaintListener(new PaintListener() {
				boolean firstTime = true;
				public void paintControl(PaintEvent e) {
					if (firstTime) {
						if (txt_args[0] != null) {
							txt_args[0].setFocus();
							firstTime = false;
						}
					}
				}
			});

		}
		super.setVisible(visible);
	}

	public Coordinate[] getCoordinates() {
		return coordinates;
	}

	/* check if any coordinate is duplicate or out of range */
	private boolean checkCoordinates(Coordinate[] coordinates)
			throws CoordinateNotFoundException {
		if (coordinates == null || coordinates.length < 1) {
			throw new CoordinateNotFoundException("No coordinate generated");
		}

		/* check if any coordinate is out of range */
		for (int i = 0; i < coordinates.length; i++) {
			if (!coordinates[i].withinRange(NetTopoApp.getApp().getNetwork()
					.getSize())) {
				setErrorMessage("Some node's coodinate is out of range");
				return false;
			}
		}

		/* check if there exist any duplicate coordinate in the array */
		if (coordinates.length > 1) {
			for (int i = 0; i < coordinates.length; i++) {
				for (int j = i + 1; j < coordinates.length; j++) {
					if (coordinates[i].equals(coordinates[j])) {
						setErrorMessage("Sorry, some nodes' coordinate is duplicate");
						return false;
					}
				}
			}
		}

		/*
		 * check if any coordinate is duplicate with already exist ones in the
		 * network
		 */
		for (int i = 0; i < coordinates.length; i++) {
			if (NetTopoApp.getApp().getNetwork()
					.hasDuplicateCoordinate(coordinates[i])) {
				setErrorMessage("Sorry, some nodes' coordinate is duplicate");
				return false;
			}
		}
		return true;
	}

	public HashMap<String, Integer> getNodes() {
		return nodes;
	}

	public void setNodes(HashMap<String, Integer> nodes) {
		this.nodes = nodes;
	}
	
	
	

}
