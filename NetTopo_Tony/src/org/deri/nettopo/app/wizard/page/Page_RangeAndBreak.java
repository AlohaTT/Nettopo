package org.deri.nettopo.app.wizard.page;

import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Page_RangeAndBreak extends WizardPage {
	private Text text;
	private Text text_1;

	/**
	 * Create the wizard.
	 */
	public Page_RangeAndBreak() {
		super("wizardPage");
		setTitle("Wizard Page title");
		setDescription("Please fill the blacks");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblsensor = new Label(container, SWT.NONE);
		lblsensor.setBounds(0, 22, 112, 17);
		lblsensor.setText("\u5F53\u524D\u5DF2\u6709Sensor\u6570\u91CF");
		
		Label sensorNumber = new Label(container, SWT.NONE);
		sensorNumber.setBounds(128, 22, 61, 17);
		sensorNumber.setText(Property.TPGF_max+"");
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u8BF7\u9009\u62E9\u589E\u6216\u51CF\u8282\u70B9");
		label.setBounds(16, 45, 96, 17);
		
		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Property.flag=true;
			}
		});
		btnRadioButton.setBounds(128, 45, 45, 17);
		btnRadioButton.setText("\u589E\u52A0");
		
		Button btnRadioButton_1 = new Button(container, SWT.RADIO);
		btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Property.flag=false;
			}
		});
		btnRadioButton_1.setBounds(179, 45, 97, 17);
		btnRadioButton_1.setText("\u51CF\u5C11");
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setBounds(88, 71, 24, 17);
		label_1.setText("\u6B65\u957F");
		
		text = new Text(container, SWT.BORDER);//步长
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(Util.isNumeric(text.getText().trim()))
					{
					setDescription("You should fill in a number.");
					return ;
					}
				else
				{
					Property.TPGF_step=Integer.parseInt(text.getText().trim());
				}
			}
		});
		text.setBounds(128, 68, 73, 23);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(10, 99, 102, 17);
		lblNewLabel.setText("\u8FB9\u754CSensor\u8282\u70B9\u6570");
		
		text_1 = new Text(container, SWT.BORDER);//最值
		text_1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(Util.isNumeric(text_1.getText().trim()))
				{
				setDescription("You should fill in a number.");
				return ;
				}
			else
			{
				if(Property.flag)
				Property.TPGF_max=Integer.parseInt(text_1.getText().trim());
				else
					Property.TPGF_min=Integer.parseInt(text_1.getText().trim());
			}
			}
		});
		text_1.setBounds(128, 96, 73, 23);
	}
}
