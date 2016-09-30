package org.deri.nettopo.app.wizard.page;

import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class Page_CKNTimes extends WizardPage {
	private Text text;

	/**
	 * Create the wizard.
	 */
	public Page_CKNTimes() {
		super("wizardPage");
		setTitle("Wizard Page title");
		setDescription("Please fill the blank with a number.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblCknRunningTimes = new Label(container, SWT.NONE);
		lblCknRunningTimes.setBounds(25, 75, 121, 17);
		lblCknRunningTimes.setText("CKN Running times");
		
		text = new Text(container, SWT.BORDER);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				
				if(!Util.isNumeric(text.getText()))
				{
					text.setText("");
					setDescription("You are requested to fill the blank with a number.");
				}
				else
				{
					Property.times=Integer.parseInt(text.getText());
					System.out.println("111111111111111111111111"+Property.times);
				}
				
			}
		});
		text.setBounds(152, 72, 73, 23);
	}
}
