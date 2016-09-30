package org.deri.nettopo.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TestListInShell extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			TestListInShell shell = new TestListInShell(display);
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
	public TestListInShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		Label lblResult = new Label(this, SWT.NONE);
		lblResult.setBounds(63, 32, 32, 17);
		lblResult.setText("result");
		
		Combo combo = new Combo(this, SWT.NONE);
		for(int i=0;i<10;++i)
		combo.add("µÚ"+i+"¸ö");
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			Combo ctemp=(Combo)e.getSource();
			System.out.println(ctemp.getSelectionIndex());
			}
		});
		combo.setBounds(38, 55, 88, 25);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(180, 169);
		this.setLocation(200, 200);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
