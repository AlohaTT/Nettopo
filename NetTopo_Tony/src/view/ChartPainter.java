package view;


import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ChartPainter extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	protected Shell shell;
	private Image img =null;
	private Display display;
	private Canvas can;



	public static void startPaint(String key,String val) {
		try {
			Display display = Display.getDefault();
			ChartPainter shell = new ChartPainter(display,key,val);
			shell.open();
			if(!shell.isDisposed())
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
	 * @throws IOException 
	 */
	public ChartPainter(Display display,String key,String val) throws IOException {
		super(display, SWT.SHELL_TRIM| SWT.ON_TOP);
		BarChart.PaintChart(key,val);
		img = new Image(this.display, "./BarChart.png");
		createContents();
	}
	
	public ChartPainter(Display display){
		super(display, SWT.SHELL_TRIM| SWT.ON_TOP);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		this.display = Display.getDefault();
		//System.out.println(this.display.getBounds());
		shell = new Shell();
		shell.setBounds(200, 200, img.getBounds().width+80, img.getBounds().height+50);
		shell.setLayout(new FormLayout());
		shell.setText("SWT Application");
		this.can = new Canvas(shell, SWT.BORDER);
		can.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	public void open() {
		createContents();
		this.can.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				can.setBounds(0,0,img.getBounds().width,img.getBounds().height+3);
				e.gc.drawImage(img, 0, 0);
			}
		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		this.display.dispose();
	}

}
