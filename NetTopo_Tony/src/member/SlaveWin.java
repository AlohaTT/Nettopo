package member;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class SlaveWin extends Shell {
	private Text MasterIP;
	private Button submit;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			SlaveWin shell = new SlaveWin(display);
			
//			 new Thread(new Runnable(){
//				 @Override
//				 public void run() {
//					 Display.getDefault().syncExec(new Runnable(){
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							NetTopoApp.getApp().run();
//						}
//					 });
//					}
//				 }).start();	
			 
			 
//			 while(NetTopoApp.getApp().getCmp_graph()==null);
			 
			 
//			 new Thread(new Runnable(){
//				 @Override
//				 public void run() {
//					 Display.getDefault().syncExec(new Runnable(){
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							Coordinate c=new Coordinate(400,400,0);
//							WirelessSensorNetwork wsn=new WirelessSensorNetwork();
//							wsn.setSize(c);
//							NetTopoApp.getApp().setNetwork(wsn);
//							NetTopoApp.getApp().cmd_repaintNetwork();
//						}
//						 
//					 });
//					 
//					}
//				 }).start();
			 
			 
			 
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
	 * 
	 * @param display
	 * @throws UnknownHostException
	 */
	public SlaveWin(Display display) throws UnknownHostException {
		super(display, SWT.SHELL_TRIM);

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(27, 86, 61, 17);
		lblNewLabel.setText("masterIP:");

		final Label warning = new Label(this, SWT.CENTER);
		warning.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		warning.setBounds(27, 112, 151, 17);

		MasterIP = new Text(this, SWT.BORDER);
		MasterIP.setBounds(94, 83, 84, 23);
		MasterIP.setText("172.27.35.9");
		submit = new Button(this, SWT.NONE);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// JOptionPane.showMessageDialog(null,
				// "信息消息","提示",JOptionPane.INFORMATION_MESSAGE);
				if (submit.getText().equals("Close")) {

					System.exit(0);
				}
				if (!isboolIp(MasterIP.getText())) {
					warning.setText("非法IP");
					// warning.setVisible(true);
				} else {
					// warning.setVisible(false);
					warning.setText("");
					final Slave slave = new Slave();
					int val = slave.testMaster(MasterIP.getText());
					switch (val) {
					case 1:
						warning.setText("Successful!");
						MasterIP.setEnabled(false);
						submit.setText("Close");
						try {
							new Thread(new Runnable() {	//新建一个线程防止卡死
								public void run() {
									try {
										slave.runTask();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										System.out.println("失去连接");
										System.exit(1);
									}
								}
							}).start();

						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						break;
					case -1:
						warning.setText("IP格式错误！");
						break;
					case -2:
						warning.setText("远程连接错误！");
						break;
					case -3:
						warning.setText("没有找到绑定对象！");
						break;
					default:
						System.out.println("error");
						break;
					}
				}

			}
		});

		submit.setBounds(54, 150, 80, 27);
		submit.setText("Connect");

		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager
				.getFont("微软雅黑", 14, SWT.NORMAL));
		lblNewLabel_1.setBounds(68, 10, 48, 27);
		lblNewLabel_1.setText("Slave");

		Label slaveIP = new Label(this, SWT.CENTER);
		slaveIP.setBounds(48, 43, 106, 17);

		String ip = InetAddress.getLocalHost().getHostAddress();
		slaveIP.setText(ip);

		createContents();
		
		
		
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				System.out.println("nnnnnnnnnn");
				System.exit(0);
			}
		});
		
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Slave");
		setSize(204, 250);

	}

	/**
	 * 判断是否为合法IP
	 * 
	 * @return the ip
	 */
	public boolean isboolIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
		// return matcher.find();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
