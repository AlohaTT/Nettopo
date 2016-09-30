package org.deri.nettopo.app.wizard.page;

import java.util.HashMap;
import java.util.Iterator;

import org.deri.nettopo.mobility.models.RandomWaypoint;
import org.deri.nettopo.util.AttractorField;
import org.deri.nettopo.util.DistrictedArea;
import org.deri.nettopo.util.Util;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Page_RandomWaypointAttr extends WizardPage {
	private Composite composite = null;
	private Text[] txt_attrs = null;
	private Label[] lbl_attrs = null;
	private String[] params_names = null;
	private boolean[] attrs_valid = null;
	private RandomWaypoint randomWaypoint = null;
	private HashMap<String, String> errors = null;

	public Page_RandomWaypointAttr() {
		super("RandomWaypoint");
		this.setTitle("Attributes:");
		this.setPageComplete(false);
		this.randomWaypoint = new RandomWaypoint();
		this.errors = new HashMap<String, String>();
		this.randomWaypoint.setModelName("RandomWaypoint");
	}

	public void createControl(Composite parent) {
		this.composite = new Composite(parent, SWT.NONE);
		this.composite.setLayout(new GridLayout(2, false));
		this.setControl(composite);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			if (lbl_attrs != null || txt_attrs != null) {
				for (int i = 0; i < params_names.length; i++) {
					txt_attrs[i].dispose();
					lbl_attrs[i].dispose();
				}
			}

			params_names = RandomWaypoint.getAttrsNames();
			txt_attrs = new Text[params_names.length];
			lbl_attrs = new Label[params_names.length];
			attrs_valid = new boolean[params_names.length];
			for (int i = 0; i < params_names.length; i++) {
				attrs_valid[i] = false;
			}

			for (int index = 0; index < params_names.length; index++) {

				lbl_attrs[index] = new Label(composite, SWT.NONE);
				lbl_attrs[index].setText(params_names[index]);
				lbl_attrs[index].setData(params_names[index]);
				lbl_attrs[index].setLayoutData(new GridData());
				if (params_names[index].equals(params_names[0])) {
					addNodeID(this.composite, index);
				} else if (params_names[index].equals(params_names[4])) {
					addAttractorFields(this.composite, index);
				} else if (params_names[index].equals(params_names[5])) {
					addDistrictedAreaWidget(this.composite, index);
				} else {
					txt_attrs[index] = new Text(composite, SWT.BORDER);
					txt_attrs[index].setData(new Integer(index));
					GridData data = new GridData();
					data.horizontalIndent = 5;
					txt_attrs[index].setLayoutData(data);
					txt_attrs[index].addFocusListener(new FocusListener() {
						public void focusGained(FocusEvent event) {
							int pos = ((Integer) ((Text) event.widget)
									.getData()).intValue();
							setMessage(RandomWaypoint.getHelpInfo()[pos]);
						}

						public void focusLost(FocusEvent event) {
							setMessage(null);
						}
					});
					txt_attrs[index].addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent event) {
							Text txt_arg = (Text) event.widget;
							int current = ((Integer) txt_arg.getData())
									.intValue();
							setPageAction(params_names[current], txt_arg
									.getText().trim(), current);
						}
					});
				}
			}
			/* set the focus in the first text area when the page is first shown */
			composite.addPaintListener(new PaintListener() {
				boolean firstTime = true;

				public void paintControl(PaintEvent e) {
					if (firstTime) {
						if (txt_attrs[0] != null) {
							txt_attrs[0].setFocus();
							firstTime = false;
						}
					}
				}
			});
			composite.layout();
		}
		super.setVisible(visible);
	}

	private void addNodeID(Composite parentComposite, final int suffix) {
		Composite nodeIDCmp = new Composite(parentComposite, SWT.NONE);
		nodeIDCmp.setLayoutData(new GridData());
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 0;
		nodeIDCmp.setLayout(layout);

		final Text txt_attrs_nodeID = new Text(nodeIDCmp, SWT.BORDER);
		txt_attrs_nodeID.setLayoutData(new GridData());
		txt_attrs_nodeID.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text txt_arg = (Text) event.widget;
				setPageAction(params_names[0], txt_arg.getText().trim(), suffix);
			}
		});
		txt_attrs_nodeID.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				setMessage(RandomWaypoint.getHelpInfo()[suffix]);
			}

			public void focusLost(FocusEvent event) {
				setMessage(null);
			}
		});

		final Button ALL = new Button(nodeIDCmp, SWT.CHECK);
		ALL.setText("ALL");
		ALL.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				txt_attrs_nodeID.setText("");
				txt_attrs_nodeID.clearSelection();
				if (ALL.getSelection()) {
					txt_attrs_nodeID.setEditable(false);
					setPageAction(params_names[0], "ALL", suffix);
				} else {
					txt_attrs_nodeID.setEditable(true);
					setPageAction(params_names[0], txt_attrs_nodeID.getText()
							.trim(), suffix);
				}
			}
		});

	}

	private void addDistrictedAreaWidget(Composite parentComposite,
			final int suffix) {
		Composite districtedCmp = new Composite(parentComposite, SWT.NONE);
		districtedCmp.setLayoutData(new GridData());
		GridLayout layout = new GridLayout(4, false);
		layout.marginLeft = 0;
		districtedCmp.setLayout(layout);

		final Text txt_attrs_districtedArea = new Text(districtedCmp, SWT.BORDER);
		txt_attrs_districtedArea.setLayoutData(new GridData());
		txt_attrs_districtedArea.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text txt_arg = (Text) event.widget;
				String data = (String)txt_arg.getData();
				setPageAction(params_names[5] + data, txt_arg.getText().trim(), suffix);
				
			}
		});
		txt_attrs_districtedArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				setMessage(RandomWaypoint.getHelpInfo()[suffix]);
			}

			public void focusLost(FocusEvent event) {
				setMessage(null);
			}
		});

		Button NO = new Button(districtedCmp, SWT.RADIO);
		NO.setText("NO");
		NO.setSelection(true);
		NO.addPaintListener(new PaintListener() {
			boolean firstTime = true;

			public void paintControl(PaintEvent event) {
				if (firstTime) {
					txt_attrs_districtedArea.setData("NO");
					txt_attrs_districtedArea.setEditable(false);
					setPageAction(params_names[5], "false", suffix);
					firstTime = false;
				}
			}
		});
		NO.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent event) {
				txt_attrs_districtedArea.setText("");
				txt_attrs_districtedArea.setData("NO");
				txt_attrs_districtedArea.setEditable(false);
				txt_attrs_districtedArea.clearSelection();
				setPageAction(params_names[5], "false", suffix);
			}
		});
		Button CIRCLE = new Button(districtedCmp, SWT.RADIO);
		CIRCLE.setText("CIRCLE");
		CIRCLE.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				txt_attrs_districtedArea.setEditable(true);
				txt_attrs_districtedArea.setData("CIRCLE");
				txt_attrs_districtedArea.setFocus();
				setPageAction(params_names[5] + ((Button)event.widget).getText().trim(),txt_attrs_districtedArea.getText().trim(), suffix);
			}
		});
		Button RECTANGLE = new Button(districtedCmp, SWT.RADIO);
		RECTANGLE.setText("RECTANGLE");
		RECTANGLE.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				txt_attrs_districtedArea.setEditable(true);
				txt_attrs_districtedArea.setData("RECTANGLE");
				txt_attrs_districtedArea.setFocus();
				setPageAction(params_names[5] + ((Button)event.widget).getText().trim(),txt_attrs_districtedArea.getText().trim(), suffix);
			}
		});
	}

	private void addAttractorFields(Composite parentComposite, final int suffix) {
		Composite attractorCmp = new Composite(parentComposite, SWT.NONE);
		attractorCmp.setLayoutData(new GridData());
		GridLayout layout = new GridLayout(4, false);
		layout.marginLeft = 0;
		attractorCmp.setLayout(layout);

		final Text txt_attrs_attractorFields = new Text(attractorCmp,
				SWT.BORDER);
		txt_attrs_attractorFields.setLayoutData(new GridData());
		txt_attrs_attractorFields.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text txt_arg = (Text) event.widget;
				setPageAction(params_names[4], txt_arg.getText().trim(), suffix);
			}
		});
		txt_attrs_attractorFields.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				setMessage(RandomWaypoint.getHelpInfo()[suffix]);
			}

			public void focusLost(FocusEvent event) {
				setMessage(null);
			}
		});

		Button NO = new Button(attractorCmp, SWT.RADIO);
		NO.setText("NO");
		NO.setSelection(true);
		NO.addPaintListener(new PaintListener() {
			boolean firstTime = true;

			public void paintControl(PaintEvent event) {
				if (firstTime) {
					txt_attrs_attractorFields.setEditable(false);
					setPageAction(params_names[4], "false", suffix);
					firstTime = false;
				}
			}
		});
		NO.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				txt_attrs_attractorFields.setText("");
				txt_attrs_attractorFields.setEditable(false);
				txt_attrs_attractorFields.clearSelection();
				setPageAction(params_names[4], "false", suffix);
			}
		});

		Button YES = new Button(attractorCmp, SWT.RADIO);
		YES.setText("YES");
		YES.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				txt_attrs_attractorFields.setEditable(true);
				txt_attrs_attractorFields.setFocus();
				setPageAction(params_names[4], txt_attrs_attractorFields.getText().trim(), suffix);
			}
		});
	}

	public String[] getAttrs() {
		return randomWaypoint.getAttrs();
	}

	public String[] getAttrsNames() {
		return params_names;
	}

	public void setPageAction() {
		if (Util.checkAllArgValid(attrs_valid)) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	public void setPageAction(String key, String value, int current) {
		boolean setSuccess = randomWaypoint.setAttrs(key, value);
		if (setSuccess) {
			if (key.startsWith(params_names[5])) {
				removeErrors(params_names[5]);
			} else {
				removeErrors(key);
			}
			setErrorMessage(getErrors());
			attrs_valid[current] = true;
		} else {
			String errorMsg = randomWaypoint.getErrorMessage();
			if (key.startsWith(params_names[5])) {
				addErrors(params_names[5], errorMsg);
			} else {
				addErrors(key, errorMsg);
			}
			setErrorMessage(getErrors());
			attrs_valid[current] = false;
		}
		setPageAction();
	}

	public String getErrors() {
		String currentErrors = "";
		Iterator<String> iter = errors.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			currentErrors += (key + ":");
			currentErrors += (errors.get(key) + "\n");
		}
		if ("".equals(currentErrors)) {
			return null;
		} else {
			return currentErrors;
		}
	}

	public void addErrors(String key, String error) {
		if (errors.keySet().contains(key)) {
			removeErrors(key);
		}
		errors.put(key, error);
	}

	public void removeErrors(String key) {
		if (errors.keySet().contains(key)) {
			errors.remove(key);
		}
	}

	public RandomWaypoint getRandomWaypoint() {
		return randomWaypoint;
	}
	
	public AttractorField getAttractorField(){
		return randomWaypoint.getAField();
	}
	
	public DistrictedArea getDistrictedArea(){
		return randomWaypoint.getDistrictedArea();
	}
	
}
