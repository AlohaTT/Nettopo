package org.deri.nettopo.app;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.SWT;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.viewers.TableViewer;

import impl.RunTasksThread;

import java.io.*;
import java.util.*;

import org.deri.nettopo.app.wizard.*;
import org.deri.nettopo.app.table.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.localization.DVHopNode;
import org.deri.nettopo.util.*;
import org.deri.nettopo.util.Property;
import org.deri.nettopo.xml.ReadXML;
import org.deri.nettopo.algorithm.*;
import org.deri.nettopo.algorithm.tpgf.function.StaticFunction;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.image.*;

import view.CreateTasks;

/**
 * the entry of the application. singleton pattern.
 * 
 * @author Lei Shu, Chun Wu, Yuanbo Han
 */
public class NetTopoApp {

	private static final NetTopoApp APP = new NetTopoApp(); // @jve:decl-index=0:

	// private static final int LEFT_SASH_DEFAULT_LEFT = 200; //left this final
	// for further application

	private static final int LEFT_SASH_MIN_LEFT = 100;

	private static final int LEFT_SASH_MIN_RIGHT = 200;

	private static final int RIGHT_SASH_DEFAULT_RIGHT = 200;

	private static final int RIGHT_SASH_MIN_RIGHT = 100;

	private static final int RIGHT_SASH_MIN_LEFT = 200;

	private static final int BOTTOM_SASH_DEFAULT_BOTTOM = 150;

	private static final int BOTTOM_SASH_MIN_BOTTOM = 50;

	private static final int BOTTOM_SASH_MIN_TOP = 50;

	private static final Point origin = GlobalConstants.origin;

	private static final int ARROW_LENGTH = 10;

	private static final int ARROW_WIDTH = 5;

	private final int mobi_INTERVAL = 1; // seconds

	private final int func_INTERVAL = 5;

	private static ArrayList<WirelessSensorNetwork> allTasks = null;

	private static final String[] FILTER_NAMES = { "NetTopo Files (*.wsn)",
			"All Files (*.*)" };

	private static final String[] FILTER_EXTS = { "*.wsn", "*.*" };

	/**
	 * the only wsn used in the NetTopoApp
	 */
	private WirelessSensorNetwork wsn = null; // @jve:decl-index=0:

	/**
	 * the object is responsible for painting nodes
	 */
	private Painter painter = null; // @jve:decl-index=0:

	private Thread visualizer = null;

	/**
	 * this timer is used to start and stop the painting mobile thread
	 */
	private Timer timer_mobility = null;

	/**
	 * this timer is to be used in algorithm function
	 */
	private Timer timer_func = null;

	private TimerTask timertask_func = null;

	private File file_wsn = null; // @jve:decl-index=0:

	private FileOutputStream fos = null;

	private ObjectOutputStream oos = null;

	private ObjectInputStream ois = null;
	/**
	 * if the canvas is modified by users. Such as move nodes, use mobility etc.
	 */
	private boolean isFileModified = false;

	private Image img_newFile, img_saveFile, img_openFile, img_printFile;

	private Image img_buffer = null;

	private Image img_copyOfBuffer = null;

	private boolean nodeFocused = false;

	private VNode currentSelectedNode = null;

	private boolean districtAreaExist = false;

	private Vector<DistrictedArea> districtedAreas = new Vector<DistrictedArea>();

	private boolean attractorFieldExist = false;

	private AttractorField aField = null;

	private VGas currentSelectedGas = null;// add by Dennis

	private boolean gasFocused = false;// add by dennis whether the gas is
	// focused

	/**
	 * this is for the mobility
	 */
	private int mobileNodeNum = 0;

	/**
	 * this is to see whether the mobility is in pause
	 */
	private boolean isPause = false;

	/**
	 * main display in the app
	 */
	private Display display = null;

	/**
	 * main shell in the app
	 */
	private Shell sh_main = null;// shell �޸ĳ�static

	private Menu mb_main = null;

	/**
	 * submenu_mobility is the submenu for file
	 */
	private Menu submenu_file = null;

	/**
	 * menu of parallel
	 */

	/**
	 * simulation dir
	 */
	private String simulationPath = null;

	/**
	 * submenu_mobility is the submenu for edit
	 */
	private Menu submenu_edit = null;

	/**
	 * submenu_mobility is the submenu for node
	 */
	private Menu submenu_node = null;

	/**
	 * submenu_mobility is the submenu for network
	 */
	private Menu submenu_network = null;

	/**
	 * submenu_mobility is the submenu for algorithm
	 */
	private Menu submenu_algorithm = null;

	/**
	 * 
	 */
	private Menu submenu_distribute = null;

	/**
	 * submenu_mobility is the submenu for help
	 */
	private Menu submenu_help = null;

	/**
	 * submenu_mobility is the submenu for new of file
	 */
	private Menu submenu_new = null;

	/**
	 * submenu_Gas is the submenu for gas
	 */
	private Menu submenu_gas = null;// add by Dennis

	/**
	 * submenu_mobility is the submenu for mobility of file
	 */
	private Menu submenu_mobility = null;

	private Menu submenu_stop = null;

	private ToolBar tb_main = null;

	private Sash sash_h = null;

	private Sash sash_v_left = null;

	private Sash sash_v_right = null;

	private int sash_v_left_length = 3;

	private int sash_v_right_length = 3;

	private Canvas canv_main = null;

	private Composite cmp_main = null;

	private Composite cmp_graph = null;

	private CTabFolder ctf_output = null;

	private CTabFolder ctf_left = null;

	private CTabFolder ctf_right = null;

	private Text txt_log = null;

	private MenuItem mi_file, mi_edit, mi_node, mi_network, mi_gas,
			mi_algorithm, mi_mobility, mi_help,

			mi_file_new, mi_file_close, mi_file_open, mi_file_save,
			mi_file_saveAs, mi_file_printSetup, mi_file_printView,
			mi_file_print, mi_file_exit,

			mi_edit_search, mi_gas_createOne, mi_gas_createGas,

			mi_node_createOne, mi_node_createNodes, mi_node_killOneNode,
			mi_node_killNodes,

			mi_new_network,

			mi_algorithm_stop,

			mi_network_visualize, mi_network_stop, mi_network_clearup,

			mi_mobility_randomwaypoint, mi_mobility_stop,
			mi_mobility_pauseMobility, mi_mobility_resumeMobility,
			mi_mobility_stopMobility, mi_mobility_resetColor,
			mi_mobility_removeDistrictedArea, mi_mobility_removeAttractorField,
			mi_distribute, mi_result, mi_createTasks,
			mi_parallelRun;

	private ArrayList<MenuItem> mi_algorithms = new ArrayList<MenuItem>();

	private ArrayList<MenuItem> mi_parallels = new ArrayList<MenuItem>();

	private TableViewer tv_prop = null;

	private Table tb_prop = null;

	/**
	 * this is the only way to get an object of NetTopoApp
	 * 
	 * @return
	 */
	public static NetTopoApp getApp() {
		return APP;
	}

	/**
	 * there is only one wsn existing in one NetTopoApp
	 * 
	 * @return
	 */
	public WirelessSensorNetwork getNetwork() {
		return wsn;
	}

	public void setNetwork(WirelessSensorNetwork wsn) {
		this.wsn = wsn;
	}

	public boolean isFileModified() {
		return isFileModified;
	}

	public void setFileModified(boolean isFileModified) {
		this.isFileModified = isFileModified;
	}

	public int getMobi_INTERVAL() {
		return mobi_INTERVAL;
	}

	public int getFunc_INTERVAL() {
		return func_INTERVAL;
	}

	public void setIsPause(boolean pause) {
		this.isPause = pause;
	}

	public boolean getIsPause() {
		return this.isPause;
	}

	public Timer getTimer_Mobility() {
		return timer_mobility;
	}

	public void setTimer_Mobility(Timer timer) {
		this.timer_mobility = timer;
	}

	public Timer getTimer_func() {
		return timer_func;
	}

	public void setTime_func(Timer time_func) {
		this.timer_func = time_func;
	}

	public TimerTask getTimertask_func() {
		return timertask_func;
	}

	public void setTimertask_func(TimerTask timertask_func) {
		this.timertask_func = timertask_func;
	}

	public Shell getSh_main() {
		return sh_main;
	}

	public void setSh_main(Shell sh_main) {
		this.sh_main = sh_main;
	}

	public Painter getPainter() {
		return this.painter;
	}

	public Display getDisplay() {
		return this.display;
	}

	public Canvas getCanv_main() {
		return canv_main;
	}

	public void setCurrentSelectedNode(VNode node) {
		this.currentSelectedNode = node;
	}

	public VNode getCurrentSelectedNode() {
		return this.currentSelectedNode;
	}

	public boolean isDistrictAreaExist() {
		return districtAreaExist;
	}

	public void setDistrictAreaExist(boolean districtAreaExist) {
		this.districtAreaExist = districtAreaExist;
	}

	public boolean isAttractorFieldExist() {
		return attractorFieldExist;
	}

	public void setAttractorFieldExist(boolean attractorFieldExist) {
		this.attractorFieldExist = attractorFieldExist;
	}

	public void addDistrictedArea(DistrictedArea area) {
		if (districtedAreas != null && area != null) {
			districtedAreas.add(area);
			setDistrictAreaExist(true);
		}
	}

	// add by dennis
	public void setCurrentSelectedGas(VGas gas) {
		this.currentSelectedGas = gas;
	}

	public VGas getCurrentSelectedGas() {
		return this.currentSelectedGas;
	} // add by dennis

	public Vector<DistrictedArea> getDistrictedAreas() {
		return districtedAreas;
	}

	public void setDistrictedAreas(Vector<DistrictedArea> areas) {
		districtedAreas = areas;
	}

	public AttractorField getAField() {
		return aField;
	}

	public void setAField(AttractorField field) {
		if (field != null) {
			aField = field;
			setAttractorFieldExist(true);
		}
	}

	public int getMobileNodeNum() {
		return mobileNodeNum;
	}

	public void setMobileNodeNum(int mobileNodeNum) {
		this.mobileNodeNum = mobileNodeNum;
	}

	public void addMobileNodeNum(int num) {
		this.mobileNodeNum += num;
	}

	/**
	 * An empty private constructor that will be called in the NetTopoApp. The
	 * private is to make a singleton of App.
	 */
	private NetTopoApp() {
	}

	/**
	 * nofifyUser the content from the msg and will set title as text. an OK
	 * button and an information icon will be shown together.
	 * 
	 * @param text
	 *            the title
	 * @param msg
	 *            the content
	 */
	public void notifyUser(String text, String msg) {
		MessageBox mb = new MessageBox(sh_main, SWT.ICON_INFORMATION | SWT.OK);
		mb.setText(text);
		mb.setMessage(msg);
		mb.open();
	}

	/**
	 * canv_main will call redraw() if possible
	 */
	public void refresh() {
		if (!canv_main.isDisposed())
			canv_main.redraw();
		tv_prop.setInput(currentSelectedNode);

		if (isDistrictAreaExist()) {
			painter.paintDistrictedAreas(districtedAreas);
			mi_mobility_removeDistrictedArea.setEnabled(true);
		}
		if (isAttractorFieldExist()) {
			painter.paintAttractors(aField);
			mi_mobility_removeAttractorField.setEnabled(true);
		}

		if (!mi_mobility_stopMobility.isEnabled()) {
			mi_mobility_pauseMobility.setEnabled(false);
			mi_mobility_resumeMobility.setEnabled(false);
		}

		if (!mi_mobility_stopMobility.isEnabled()
				&& !mi_mobility_removeDistrictedArea.isEnabled()
				&& !mi_mobility_removeAttractorField.isEnabled()
				&& !mi_mobility_resetColor.isEnabled()
				&& !mi_mobility_pauseMobility.isEnabled()
				&& !mi_mobility_resumeMobility.isEnabled()) {
			mi_mobility_stop.setEnabled(false);
		}
	}

	/**
	 * if nodeFocused is true, then restoreImage()
	 * 
	 * @param needRepaint
	 *            if needRepaint
	 * @return img_buffer
	 */
	public Image getBufferImage(boolean needRepaint) {
		synchronized (APP) {
			if (needRepaint) { // dispose the image used last time and return a
								// new image
				if (img_buffer != null) {
					img_buffer.dispose();
				}
				img_buffer = new Image(Display.getCurrent(), wsn.getSize().x,
						wsn.getSize().y);
			}
			if (nodeFocused) {
				restoreImage();
				nodeFocused = false;
			}
			return img_buffer;
		}
	}

	// /**
	// * set the image
	// *
	// *
	// */
	// public void setBufferImage(Image image) {
	// synchronized (APP) {
	// img_buffer=image;
	// }
	// }

	/**
	 * current time will be added together with the log infor.
	 * 
	 * @param log
	 *            the information that will be added to the log
	 */
	public void addLog(String log) {
		java.util.Date current = new java.util.Date(System.currentTimeMillis());
		txt_log.append(log + "\t" + current + "\n");
	}

	/**
	 * copy the image data from img_buffer to img_copyOfBuffer. This change does
	 * not affect img_buffer
	 */
	private void bufferImage() {
		synchronized (APP) {
			if (img_copyOfBuffer != null)
				img_copyOfBuffer.dispose();
			img_copyOfBuffer = new Image(Display.getCurrent(), img_buffer,
					SWT.IMAGE_COPY);
		}
	}

	/**
	 * copy the image data form img_copyOfBuffer to img_buffer, and this change
	 * does not affect img_copyOfBuffer
	 */
	private void restoreImage() {
		synchronized (APP) {
			if (img_buffer != null)
				img_buffer.dispose();
			img_buffer = new Image(Display.getCurrent(), img_copyOfBuffer,
					SWT.IMAGE_COPY);
		}
	}

	/**
	 * create the images of new,open,save and print. img_buffer will be
	 * instanced with the width=1 and height=1.
	 */
	private void createImg() {
		img_newFile = new Image(Display.getCurrent(),
				MyImageLoader.class.getResourceAsStream("newFile.png")); // @jve:decl-index=0:
		img_openFile = new Image(Display.getCurrent(),
				MyImageLoader.class.getResourceAsStream("openFile.png")); // @jve:decl-index=0:
		img_saveFile = new Image(Display.getCurrent(),
				MyImageLoader.class.getResourceAsStream("saveFile.png")); // @jve:decl-index=0:
		img_printFile = new Image(Display.getCurrent(),
				MyImageLoader.class.getResourceAsStream("printFile.png"));

		img_buffer = new Image(Display.getCurrent(), 1, 1);
	}

	/**
	 * dispose the image of new, open, save and print.
	 */
	private void disposeImg() {
		img_newFile.dispose();
		img_openFile.dispose();
		img_saveFile.dispose();
		img_printFile.dispose();
	}

	/**
	 * this will create the main menu at the top of the application
	 */
	private void createMenu_main() {
		/* create the menu bar */
		mb_main = new Menu(sh_main, SWT.BAR);
		sh_main.setMenuBar(mb_main);

		mi_file = new MenuItem(mb_main, SWT.CASCADE);
		mi_file.setText("&File");
		mi_edit = new MenuItem(mb_main, SWT.CASCADE);
		mi_edit.setText("&Edit");
		mi_node = new MenuItem(mb_main, SWT.CASCADE);
		mi_node.setText("&Node");
		mi_network = new MenuItem(mb_main, SWT.CASCADE);
		mi_network.setText("Net&work");
		mi_algorithm = new MenuItem(mb_main, SWT.CASCADE);
		mi_algorithm.setText("&Algorithm");
		mi_mobility = new MenuItem(mb_main, SWT.CASCADE);
		mi_mobility.setText("&Mobility");

		// mi_parallel = new MenuItem(mb_main, SWT.CASCADE);
		// mi_parallel.setText("&Paralel");

		mi_distribute = new MenuItem(mb_main, SWT.CASCADE);
		mi_distribute.setText("&Distribute");

		mi_gas = new MenuItem(mb_main, SWT.CASCADE);
		mi_gas.setText("&Gas");

		mi_help = new MenuItem(mb_main, SWT.CASCADE);
		mi_help.setText("&Help");
		/* Create the File submenu */
		submenu_file = new Menu(mi_file);
		mi_file.setMenu(submenu_file);

		mi_file_new = new MenuItem(submenu_file, SWT.CASCADE);
		mi_file_new.setText("&New");

		/*********************** �����ļ��� *******************/
		mi_file_open = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_open.setText("&Open File");
		mi_file_open.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_openFile();
			}
		});

		/*********************** ����"�ر�" *******************/
		mi_file_close = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_close.setText("&Close");
		mi_file_close.setEnabled(false);
		mi_file_close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_closeFile();
			}
		});

		new MenuItem(submenu_file, SWT.SEPARATOR);

		/*********************** �ļ���Ŀ¼������ *******************/
		mi_file_save = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_save.setText("&Save");
		mi_file_save.setEnabled(false);
		mi_file_save.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_saveFile();
			}
		});

		/*********************** �ļ���Ŀ¼������ *******************/
		mi_file_saveAs = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_saveAs.setText("Save &As");
		mi_file_saveAs.setEnabled(false);
		mi_file_saveAs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_saveAs();
			}
		});

		new MenuItem(submenu_file, SWT.SEPARATOR);

		mi_file_printSetup = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_printSetup.setText("Page Set&up");
		mi_file_printSetup.setEnabled(false);

		mi_file_printView = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_printView.setText("PrintPre&view");
		mi_file_printView.setEnabled(false);

		mi_file_print = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_print.setText("&Print...");
		mi_file_print.setEnabled(false);

		new MenuItem(submenu_file, SWT.SEPARATOR);

		mi_file_exit = new MenuItem(submenu_file, SWT.PUSH);
		mi_file_exit.setText("E&xit");
		mi_file_exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cancelThreads();
				display.dispose();

				/***************** add ****************/
				System.exit(0);
			}
		});

		/* Create the New submenu in the File submenu */
		submenu_new = new Menu(mi_file_new);
		mi_file_new.setMenu(submenu_new);

		mi_new_network = new MenuItem(submenu_new, SWT.PUSH);
		mi_new_network.setText("Wireless Sensor Network");
		mi_new_network.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_newNetwork();
			}
		});

		/* Create the Edit submenu */
		submenu_edit = new Menu(mi_edit);
		mi_edit.setMenu(submenu_edit);

		mi_edit_search = new MenuItem(submenu_edit, SWT.PUSH);
		mi_edit_search.setText("Search Node");
		mi_edit_search.setEnabled(false);
		mi_edit_search.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_searchNode();
			}
		});

		/* Create the Node submenu */
		submenu_node = new Menu(mi_node);
		mi_node.setMenu(submenu_node);

		mi_node_createOne = new MenuItem(submenu_node, SWT.PUSH);
		mi_node_createOne.setText("Create A Node");
		mi_node_createOne.setEnabled(false);
		mi_node_createOne.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_newNode();
			}
		});

		mi_node_createNodes = new MenuItem(submenu_node, SWT.PUSH);
		mi_node_createNodes.setText("Create Nodes");
		mi_node_createNodes.setEnabled(false);
		mi_node_createNodes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_newNodes();
			}
		});

		mi_node_killNodes = new MenuItem(submenu_node, SWT.PUSH);
		mi_node_killNodes.setText("Kill Nodes");
		mi_node_killNodes.setEnabled(false);
		mi_node_killNodes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_killNodes();
			}
		});

		mi_node_killOneNode = new MenuItem(submenu_node, SWT.PUSH);
		mi_node_killOneNode.setText("Kill One Node");
		mi_node_killOneNode.setEnabled(false);
		mi_node_killOneNode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_killOneNode();
			}
		});

		/* Create the Network submenu */
		submenu_network = new Menu(mi_network);
		mi_network.setMenu(submenu_network);

		mi_network_visualize = new MenuItem(submenu_network, SWT.PUSH);
		mi_network_visualize.setText("Visualize Network");
		mi_network_visualize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_visualizeNetwork();
			}
		});

		mi_network_stop = new MenuItem(submenu_network, SWT.PUSH);
		mi_network_stop.setText("Stop Visualization");
		mi_network_stop.setEnabled(false);
		mi_network_stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (visualizer != null) {
					visualizer.interrupt();
					visualizer = null;
				}
				mi_network_stop.setEnabled(false);
			}
		});

		mi_network_clearup = new MenuItem(submenu_network, SWT.PUSH);
		mi_network_clearup.setText("Clear all");
		mi_network_clearup.setEnabled(false);
		mi_network_clearup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				clearAll();
			}
		});

		/* Dynamically create the Algorithm submenu */
		/* ���µ��㷨�����ļ�algorithm.properties���棬�����ڲ˵��п�����Ҫ����Ӧ���� */
		submenu_algorithm = new Menu(mi_algorithm);
		mi_algorithm.setMenu(submenu_algorithm);

		try {

			InputStream res = Algorithm.class
					.getResourceAsStream("algorithm.properties");
			BufferedReader br = new BufferedReader(new InputStreamReader(res));
			String property;

			while ((property = br.readLine()) != null) {

				if (property.trim().startsWith("#")) {
					continue;/* #��ע�ͷ������㷨�Ӳ˵���ע�͵� */
				}
				final String name;
				String description;
				int index = property.indexOf("=");
				if ((index != -1)) {
					name = property.substring(0, index).trim();
					description = property.substring(index + 1).trim();
				} else {
					name = property.trim();
					description = "";
				}

				Algorithm algorithm;
				try {
					algorithm = (Algorithm) Class.forName(name).newInstance();
				} catch (ClassNotFoundException ex) {
					continue;
				}

				MenuItem algor = new MenuItem(submenu_algorithm, SWT.CASCADE);
				algor.setText(description);
				algor.setEnabled(false);
				mi_algorithms.add(algor);

				Menu submenu_algorFunc = new Menu(algor);
				algor.setMenu(submenu_algorFunc);
				AlgorFunc[] functions = algorithm.getFunctions();

				if (functions == null)
					continue;
				for (int i = 0; i < functions.length; i++) { // �㷨��������
					final String funcName = functions[i].getClass().getName();
					// final AlgorFunc function = (AlgorFunc) functions[i]
					// .getClass().newInstance();
					final AlgorFunc function = functions[i];
					InputStream res_func = AlgorFunc.class
							.getResourceAsStream("function.properties");
					Properties prop = new Properties();
					prop.load(res_func);

					MenuItem mi_algorFunc = new MenuItem(submenu_algorFunc,
							SWT.PUSH);
					String funcDiscription = prop.getProperty(funcName);
					if (funcDiscription == null) {
						continue;
					}
					mi_algorFunc.setText(funcDiscription);
					mi_algorFunc.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							Property.isTPGF = false;
							function.run();

						}
					});
				}

			}
			res.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * Create the Gas submenu
		 * 
		 * @author Dennis
		 */
		submenu_gas = new Menu(mi_gas);
		mi_gas.setMenu(submenu_gas);

		mi_gas_createOne = new MenuItem(submenu_gas, SWT.PUSH);
		mi_gas_createOne.setText("Create One Gas");
		mi_gas_createOne.setEnabled(false);
		mi_gas_createOne.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_newOneGas();
			}
		});

		mi_gas_createGas = new MenuItem(submenu_gas, SWT.PUSH);
		mi_gas_createGas.setText("Create Gas");
		mi_gas_createGas.setEnabled(false);
		mi_gas_createGas.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_newGas();
			}
		});

		submenu_distribute = new Menu(mi_distribute);
		mi_distribute.setMenu(submenu_distribute);

		mi_createTasks = new MenuItem(submenu_distribute, SWT.PUSH);
		mi_createTasks.setText("Create tasks");
		mi_createTasks.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				boolean isOK = chooseDir();
				if (isOK) {
					CreateTasks.Start(display, simulationPath);
				} else
					return;
				mi_parallelRun.setEnabled(true);
			}
		});

		mi_parallelRun = new MenuItem(submenu_distribute, SWT.PUSH);
		mi_parallelRun.setText("Running the tasks");
		mi_parallelRun.setEnabled(false);
		mi_parallelRun.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					addLog("It is running ,please wait.");

					/**
					 * ��� ��/image/ Ŀ¼
					 */
					Util.deleteDir(new File("./image/"));
					boolean isFinish = cmd_parallelRun();
					if (!isFinish) {
						addLog("Some thing is worng with slaves.   #101"); // 101�Ŵ���
						return;
					}
					cmd_createResultsMenuItem();
					addLog("All tasks has been finished , you can check it in the submenu of distribute ->show the results");
					mi_result.setEnabled(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

		mi_result = new MenuItem(submenu_distribute, SWT.CASCADE);
		mi_result.setText("Show the results");
		mi_result.setEnabled(false);
		mi_result.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					cmd_result();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// submenu_parallel = new Menu(mi_parallel);
		// mi_parallel.setMenu(submenu_parallel);
		// try {
		// InputStream res = Algorithm.class
		// .getResourceAsStream("parallelAlgorithm.properties");
		// BufferedReader br = new BufferedReader(new InputStreamReader(res));
		// String property;
		//
		// while ((property = br.readLine()) != null) {
		//
		// if (property.trim().startsWith("#")) {
		// continue;/* #��ע�ͷ������㷨�Ӳ˵���ע�͵� */
		// }
		// final String name;
		// String description;
		// int index = property.indexOf("=");
		// if ((index != -1)) {
		// name = property.substring(0, index).trim();
		// description = property.substring(index + 1).trim();
		// } else {
		// name = property.trim();
		// description = "";
		// }
		//
		// Algorithm algorithm;
		// try {
		// algorithm = (Algorithm) Class.forName(name).newInstance();
		// } catch (ClassNotFoundException ex) {
		// continue;
		// }
		//
		// MenuItem algor = new MenuItem(submenu_parallel, SWT.CASCADE);
		// algor.setText(description);
		// algor.setEnabled(false);
		// mi_parallels.add(algor);
		//
		// Menu submenu_algorFunc = new Menu(algor);
		// algor.setMenu(submenu_algorFunc);
		// AlgorFunc[] functions = algorithm.getFunctions();
		//
		// if (functions == null)
		// continue;
		// for (int i = 0; i < functions.length; i++) { // �㷨��������
		// final String funcName = functions[i].getClass().getName();
		// // final AlgorFunc function = (AlgorFunc) functions[i]
		// // .getClass().newInstance();
		// final AlgorFunc function = functions[i];
		// InputStream res_func = AlgorFunc.class
		// .getResourceAsStream("function.properties");
		// Properties prop = new Properties();
		// prop.load(res_func);
		//
		// MenuItem mi_algorFunc = new MenuItem(submenu_algorFunc,
		// SWT.PUSH);
		// String funcDiscription = prop.getProperty(funcName);
		// if (funcDiscription == null) {
		// continue;
		// }
		// mi_algorFunc.setText(funcDiscription);
		// mi_algorFunc.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// MainApp.master.setTopoTypeAndFunction(name + ":"
		// + funcName);// ��¼�±����õķ���
		// System.out.println("name:" + name + ":" + funcName);
		// Property.isTPGF = true;
		// function.run();
		// }
		// });
		// }
		// }
		// res.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		mi_algorithm_stop = new MenuItem(submenu_algorithm, SWT.PUSH);
		mi_algorithm_stop.setText("Stop Algorithm");
		mi_algorithm_stop.setEnabled(false);
		mi_algorithm_stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_stopAlgorithm();
			}
		});

		/* Create the mobility submenu */
		submenu_mobility = new Menu(mi_mobility);
		mi_mobility.setMenu(submenu_mobility);

		mi_mobility_randomwaypoint = new MenuItem(submenu_mobility, SWT.PUSH);
		mi_mobility_randomwaypoint.setText("RandomWaypoint");
		mi_mobility_randomwaypoint.setEnabled(false);
		mi_mobility_randomwaypoint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cmd_randomwaypoint();
			}
		});

		mi_mobility_stop = new MenuItem(submenu_mobility, SWT.CASCADE);
		mi_mobility_stop.setText("Stop");
		mi_mobility_stop.setEnabled(false);

		submenu_stop = new Menu(mi_mobility_stop);
		mi_mobility_stop.setMenu(submenu_stop);

		mi_mobility_pauseMobility = new MenuItem(submenu_stop, SWT.PUSH);
		mi_mobility_pauseMobility.setText("Pause Mobility");
		mi_mobility_pauseMobility.setEnabled(true);
		mi_mobility_pauseMobility.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cmd_pauseMobility();
			}
		});

		mi_mobility_resumeMobility = new MenuItem(submenu_stop, SWT.PUSH);
		mi_mobility_resumeMobility.setText("Resume Mobility");
		mi_mobility_resumeMobility.setEnabled(false);
		mi_mobility_resumeMobility.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cmd_resumeMobility();
			}
		});

		mi_mobility_stopMobility = new MenuItem(submenu_stop, SWT.PUSH);
		mi_mobility_stopMobility.setText("Stop Mobility");
		mi_mobility_stopMobility.setEnabled(false);
		mi_mobility_stopMobility.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cmd_stopMobility();
			}
		});

		mi_mobility_resetColor = new MenuItem(submenu_stop, SWT.PUSH);
		mi_mobility_resetColor.setText("Reset Mobile Color");
		mi_mobility_resetColor.setEnabled(false);
		mi_mobility_resetColor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cmd_resetColor();
			}
		});

		mi_mobility_removeDistrictedArea = new MenuItem(submenu_stop, SWT.PUSH);
		mi_mobility_removeDistrictedArea.setText("Remove DistrictedArea");
		mi_mobility_removeDistrictedArea.setEnabled(false);
		mi_mobility_removeDistrictedArea
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						cmd_removeDistrictedArea();
					}
				});

		mi_mobility_removeAttractorField = new MenuItem(submenu_stop, SWT.PUSH);
		mi_mobility_removeAttractorField.setText("Remove AttractorField");
		mi_mobility_removeAttractorField.setEnabled(false);
		mi_mobility_removeAttractorField
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						cmd_removeAttractorField();
					}
				});

		/* Create the Help submenu */
		submenu_help = new Menu(mi_help);
		mi_help.setMenu(submenu_help);

	}

	/**
	 * This method initialises tb_main
	 */
	private void createTb_main() {
		tb_main = new ToolBar(sh_main, SWT.NONE);
		tb_main.setFont(new Font(Display.getDefault(), "Times New Roman", 9,
				SWT.NORMAL));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		tb_main.setLayoutData(data);

		ToolItem ti_newFile = new ToolItem(tb_main, SWT.PUSH);
		ti_newFile.setToolTipText("New");
		ti_newFile.setImage(img_newFile);
		ti_newFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_newNetwork();
			}
		});

		ToolItem ti_openFile = new ToolItem(tb_main, SWT.PUSH);
		ti_openFile.setToolTipText("Open");
		ti_openFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_openFile();
			}
		});
		ti_openFile.setImage(img_openFile);

		ToolItem ti_saveFile = new ToolItem(tb_main, SWT.PUSH);
		ti_saveFile.setToolTipText("Save");
		ti_saveFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cmd_saveFile();
			}
		});
		ti_saveFile.setImage(img_saveFile);

		ToolItem ti_printFile = new ToolItem(tb_main, SWT.PUSH);
		ti_printFile.setToolTipText("Print");
		ti_printFile.setImage(img_printFile);
		new ToolItem(tb_main, SWT.SEPARATOR);
		/*
		 * ToolItem ti_cursor = new ToolItem(tb_main, SWT.PUSH);
		 * ti_cursor.setImage(new
		 * Image(Display.getCurrent(),"./img/openFile.png"));
		 */
	}

	/**
	 * This method initialises cmp_main(main composite)
	 */
	private void createCmp_main() {
		cmp_main = new Composite(sh_main, SWT.NONE);
		cmp_main.setLayoutData(new GridData(GridData.FILL_BOTH));

		cmp_main.setLayout(new FormLayout());
		createSash_h();
		createSash_v_left();
		createSash_v_right();
		createCtf_left();
		createCtf_right();
		createCtf_log();
		createCmp_middle();
	}

	/**
	 * This method initialises sash_h
	 */
	private void createSash_h() {
		sash_h = new Sash(cmp_main, SWT.HORIZONTAL);
		final FormData fd_sash = new FormData();
		fd_sash.left = new FormAttachment(0, 0);
		fd_sash.right = new FormAttachment(100, 0);
		fd_sash.bottom = new FormAttachment(100, -BOTTOM_SASH_DEFAULT_BOTTOM);
		sash_h.setLayoutData(fd_sash);
		sash_h.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Rectangle cmpRect = cmp_main.getClientArea();
				int loc = 0;
				if (e.y > cmpRect.height - BOTTOM_SASH_MIN_BOTTOM) {
					loc = BOTTOM_SASH_MIN_BOTTOM;
				} else if (e.y < BOTTOM_SASH_MIN_TOP) {
					loc = cmpRect.height - BOTTOM_SASH_MIN_TOP;
				} else {
					loc = cmpRect.height - e.y;
				}
				fd_sash.bottom = new FormAttachment(100, -loc);
				cmp_main.layout();
			}
		});
	}

	/**
	 * create a sash which lies at the left part of the composite.
	 * 
	 * "createSash_v_left();" is created for further application. this sash
	 * won't be seen so far. if you want to see this sash widget, just modify
	 * the "fd_sash.left = new FormAttachment(0, -3);" -3 to 3.
	 */
	private void createSash_v_left() {
		sash_v_left = new Sash(cmp_main, SWT.VERTICAL);
		final FormData fd_sash = new FormData();
		fd_sash.left = new FormAttachment(0, -3);
		fd_sash.top = new FormAttachment(0, 0);
		fd_sash.bottom = new FormAttachment(sash_h, 0);
		sash_v_left.setLayoutData(fd_sash);

		sash_v_left.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Rectangle cmpRect = cmp_main.getClientArea();
				int loc = 0;
				if (e.x < LEFT_SASH_MIN_LEFT) { // left min width
					loc = LEFT_SASH_MIN_LEFT;
				} else if (e.x > cmpRect.width
						- (LEFT_SASH_MIN_RIGHT + sash_v_right_length)) { // right
																			// min
																			// width
					loc = cmpRect.width
							- (LEFT_SASH_MIN_RIGHT + sash_v_right_length);
				} else {
					loc = e.x;
				}
				sash_v_left_length = loc;
				fd_sash.left = new FormAttachment(0, loc);
				sash_v_left.setLayoutData(fd_sash);
				cmp_main.layout();
			}
		});
	}

	/**
	 * create a sash which lies at the right part of the composite
	 */
	private void createSash_v_right() {
		sash_v_right = new Sash(cmp_main, SWT.VERTICAL);
		final FormData fd_sash = new FormData();
		fd_sash.right = new FormAttachment(100, RIGHT_SASH_DEFAULT_RIGHT);
		fd_sash.top = new FormAttachment(0, 0);
		fd_sash.bottom = new FormAttachment(sash_h, 0);
		sash_v_right.setLayoutData(fd_sash);
		sash_v_right.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Rectangle cmpRect = cmp_main.getClientArea();
				int loc = 0;
				if (e.x < sash_v_left_length + RIGHT_SASH_MIN_LEFT) { // left
																		// min
																		// width
					loc = cmpRect.width
							- (sash_v_left_length + RIGHT_SASH_MIN_LEFT);
				} else if (e.x > cmpRect.width - RIGHT_SASH_MIN_RIGHT) { // right
																			// min
																			// width
					loc = RIGHT_SASH_MIN_RIGHT;
				} else {
					loc = cmpRect.width - e.x;
				}
				fd_sash.right = new FormAttachment(100, -loc);
				sash_v_right_length = loc;
				sash_v_right.setLayoutData(fd_sash);
				cmp_main.layout();
			}
		});
	}

	/**
	 * This method initialises ctf_left
	 */
	private void createCtf_left() {
		ctf_left = new CTabFolder(cmp_main, SWT.BORDER);

		ctf_left.setFont(new Font(display, "Times New Roman", 9, SWT.NORMAL));
		ctf_left.setMinimizeVisible(true);
		ctf_left.setTabHeight(20);
		ctf_left.setMaximizeVisible(true);

		FormData fd_ctf_left = new FormData();
		fd_ctf_left.left = new FormAttachment(0, 0);
		fd_ctf_left.right = new FormAttachment(sash_v_left, 0);
		fd_ctf_left.top = new FormAttachment(0, 0);
		fd_ctf_left.bottom = new FormAttachment(sash_h, 0);
		ctf_left.setLayoutData(fd_ctf_left);
	}

	/** This method initialises ctf_right */
	private void createCtf_right() {
		ctf_right = new CTabFolder(cmp_main, SWT.BORDER);

		ctf_right.setFont(new Font(display, "Times New Roman", 9, SWT.NORMAL));
		ctf_right.setMinimizeVisible(true);
		ctf_right.setTabHeight(20);
		ctf_right.setMaximizeVisible(true);

		FormData fd_ctf_right = new FormData();
		fd_ctf_right.left = new FormAttachment(sash_v_right, 0);
		fd_ctf_right.right = new FormAttachment(100, 0);
		fd_ctf_right.top = new FormAttachment(0, 0);
		fd_ctf_right.bottom = new FormAttachment(sash_h, 0);
		ctf_right.setLayoutData(fd_ctf_right);

		CTabItem cti_table = new CTabItem(ctf_right, SWT.NONE);
		cti_table.setText("Properties");
		cti_table.setControl(createCmp_nodeTable());
		ctf_right.setSelection(cti_table);
	}

	/**
	 * create a composite for nodeTable
	 * 
	 * @return Composite
	 */
	private Composite createCmp_nodeTable() {
		Composite cmp_nodeTable = new Composite(ctf_right, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		cmp_nodeTable.setLayout(gridLayout);

		tv_prop = new TableViewer(cmp_nodeTable);
		tv_prop.setContentProvider(new VNodeContentProvider());
		tv_prop.setLabelProvider(new VNodeLabelProvider());
		tb_prop = tv_prop.getTable();
		tb_prop.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn tc = new TableColumn(tb_prop, SWT.LEFT);
		tc.setText("Property");
		tc.setWidth(120);
		tc = new TableColumn(tb_prop, SWT.LEFT);
		tc.setText("Value");
		tc.setWidth(80);

		tb_prop.setHeaderVisible(true);
		tb_prop.setLinesVisible(true);
		return cmp_nodeTable;
	}

	/** This method initialises ctf_output */
	private void createCtf_log() {
		ctf_output = new CTabFolder(cmp_main, SWT.BORDER);
		ctf_output.setFont(new Font(display, "Times New Roman", 9, SWT.NORMAL));
		ctf_output.setMinimizeVisible(true);
		ctf_output.setTabHeight(20);
		ctf_output.setMaximizeVisible(true);

		FormData fd_ctf_output = new FormData();
		fd_ctf_output.left = new FormAttachment(0, 0);
		fd_ctf_output.right = new FormAttachment(100, 0);
		fd_ctf_output.top = new FormAttachment(sash_h, 0);
		fd_ctf_output.bottom = new FormAttachment(100, 0);
		ctf_output.setLayoutData(fd_ctf_output);

		CTabItem cti_log = new CTabItem(ctf_output, SWT.NONE);
		cti_log.setText("Log");
		cti_log.setControl(createCtr_log());
		ctf_output.setSelection(cti_log);
	}

	/**
	 * this method creates a composite for log
	 * 
	 * @return cmp_console
	 */
	private Composite createCtr_log() {
		Composite cmp_console = new Composite(ctf_output, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		cmp_console.setLayout(gridLayout);

		txt_log = new Text(cmp_console, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		txt_log.setEditable(false);
		txt_log.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		txt_log.setLayoutData(new GridData(GridData.FILL_BOTH));
		return cmp_console;
	}

	/* add by dennis */
	private void cmd_newOneGas() {
		Wizard_CreateOneGas wizard = new Wizard_CreateOneGas();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			expandGUI();
			notifyModified();
			refresh();
		}

	}

	private void cmd_newGas() {
		Wizard_CreateGas wizard = new Wizard_CreateGas();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			expandGUI();
			notifyModified();
			refresh();
		}
	}

	/**
	 * This method initialises cmp_graph
	 * 
	 * @return cmp_graph
	 */
	private void createCmp_middle() {
		cmp_graph = new Composite(cmp_main, SWT.BORDER);
		cmp_graph.setBackground(new Color(Display.getCurrent(), 187, 191, 196));

		FormData fd_cmp_graph = new FormData();
		fd_cmp_graph.left = new FormAttachment(sash_v_left, 0);
		fd_cmp_graph.right = new FormAttachment(sash_v_right, 0);
		fd_cmp_graph.top = new FormAttachment(0, 0);
		fd_cmp_graph.bottom = new FormAttachment(sash_h, 0);
		cmp_graph.setLayoutData(fd_cmp_graph);

		cmp_graph.setLayout(null);
	}

	/**
	 * This method initialises canv_main. this method will be called in the
	 * method of "cmd_newNetwork()"
	 */
	private void createCanv_main() {
		if (canv_main != null)
			canv_main.dispose();
		canv_main = new Canvas(cmp_graph, SWT.NO_BACKGROUND);
		canv_main.setBounds(new Rectangle(origin.x, origin.y, wsn.getSize().x
				+ origin.x, wsn.getSize().y + origin.y));

		canv_main.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				synchronized (APP) {
					Image tempImg = new Image(Display.getCurrent(), wsn
							.getSize().x + origin.x, wsn.getSize().y + origin.y);
					GC gc = new GC(tempImg);// reduce the flash of the screen
					/* Always paint the contents from the buffer image */
					gc.drawImage(img_buffer, origin.x, origin.y);

					/* paint the coordinate axes */
					gc.drawLine(origin.x, origin.y, wsn.getSize().x + origin.x,
							origin.y);
					gc.drawLine(origin.x, origin.y, origin.x, wsn.getSize().y
							+ origin.y);
					// gc.drawLine(100,100,100,400);

					/* paint the arrow on the coordinates axes */
					gc.drawLine(wsn.getSize().x + origin.x, origin.y,
							wsn.getSize().x + origin.x - ARROW_LENGTH, origin.y
									- ARROW_WIDTH);
					gc.drawLine(wsn.getSize().x + origin.x, origin.y,
							wsn.getSize().x + origin.x - ARROW_LENGTH, origin.y
									+ ARROW_WIDTH);
					gc.drawLine(origin.x, wsn.getSize().y + origin.y, origin.x
							- ARROW_WIDTH, wsn.getSize().y + origin.y
							- ARROW_LENGTH);
					gc.drawLine(origin.x, wsn.getSize().y + origin.y, origin.x
							+ ARROW_WIDTH, wsn.getSize().y + origin.y
							- ARROW_LENGTH);

					/* paitn the origin, length and width characters */
					gc.drawText("0", origin.x - 10, origin.y - 15, true);
					gc.drawText(String.valueOf(wsn.getSize().x),
							wsn.getSize().x + origin.x - 28, origin.y - 18,
							true);
					gc.drawText(String.valueOf(wsn.getSize().y), origin.x - 28,
							wsn.getSize().y + origin.y - 18, true);

					e.gc.drawImage(tempImg, 0, 0);

					gc.dispose();
					tempImg.dispose();
				}
			}
		});

		Listener listener = new Listener() {
			Coordinate original = new Coordinate();
			// Coordinate originalGas = new Coordinate();

			boolean pointInNode = false;

			boolean pointInGas = false;
			Point lastLeftPoint = null;

			boolean rightPressed = false;
			Point lastRightPoint = null;

			VNode moveNode = null;
			boolean nodeMoved = false; // if true,the only use is to notify that
										// the wsn has modified

			/********************* add **************/
			boolean gasMoved = false; // if true,the only use is to notify that
										// the wsn has modified
			VGas moveGas = null;

			/********************* add **************/

			public void handleEvent(Event event) {

				switch (event.type) {
				case SWT.MouseDown:
					if (event.button == 1) { // left button down

						/********************* add *****************/

						int id_minDis = -1;
						double minDis = Math.max(wsn.getSize().x,
								wsn.getSize().y); // large enough

						java.util.Iterator<VNode> n_it = wsn.getAllNodes()
								.iterator();
						while (n_it.hasNext()) {
							VNode node = (VNode) n_it.next();

							int id = node.getID();
							Coordinate n_c = wsn.getCoordianteByID(id);
							if (n_c != null) {
								Coordinate eventPos = new Coordinate(event.x
										- origin.x, event.y - origin.y, 0);
								// to plus origin.x and origin.y because the
								// nodesID and Coordinate generated
								// do not move with origin.x or origin.y. Just
								// the presentation move origin.x and origin.y.
								double ndis = n_c.distance(eventPos);
								if (ndis < NodeConfiguration.paintRadius) {

									if (ndis < minDis) {
										minDis = ndis;
										id_minDis = id;
										moveNode = node;
										Coordinate ncoord = wsn
												.getCoordianteByID(moveNode
														.getID());
										original.setCoord(ncoord);
									}
								}
							}
						}
						if (minDis <= NodeConfiguration.paintRadius) {
							// event coordinate is in some node
							pointInNode = true;

							lastLeftPoint = new Point(event.x, event.y);

							if (nodeFocused) {
								restoreImage();
							}
							bufferImage();
							painter.paintNodeFocus(id_minDis);

							nodeFocused = true;

							currentSelectedNode = wsn.getNodeByID(id_minDis);
							painter.paintCircle(new Coordinate(original.x,
									original.y), currentSelectedNode
									.getAttrValue("Max TR"));
							refresh();
						} else {// ------------------------------------------if
								// node is out of node clear the paints------
							wsn.resetAllNodesColor();
							painter.rePaintAllGas();
							painter.paintAllNodes();

							refresh();
							if (nodeFocused) {
								restoreImage(); // ���浱ǰ״̬��image
								nodeFocused = false;
								currentSelectedNode = null;
								refresh();
							}
						}
						if (gasFocused) {
							restoreImage();
							gasFocused = false;
							currentSelectedGas = null;
							refresh();
						}
						if (moveNode == null) { // ����movenode�����п����� gas
							int gid_minDis = -1;
							java.util.Iterator<VGas> g_it = wsn.getAllGas()
									.iterator();
							while (g_it.hasNext()) {
								VGas gas = (VGas) g_it.next();

								int id = gas.getID();

								Coordinate g_c = wsn.getCoordianteByID(id);

								if (g_c != null) {
									Coordinate eventPos = new Coordinate(
											event.x - origin.x, event.y
													- origin.y, 0);
									double gdis = g_c.distance(eventPos);
									int rad = Integer.parseInt(gas
											.getAttrValue("Radius"));// -------------------------mark---------------------------
									if (gdis < rad) {
										if (gdis < minDis) {
											minDis = gdis;
											gid_minDis = id;
											moveGas = gas;
											Coordinate gcoord = wsn
													.getCoordianteByID(moveGas
															.getID());
											original.setCoord(gcoord);
										}
									}
								}
							}

							if (moveGas != null) // ���ж����ڵ�
							{
								if (minDis <= Integer.parseInt(moveGas
										.getAttrValue("Radius"))) {
									// event coordinate is in gas but not in
									// node
									pointInGas = true;
									lastLeftPoint = new Point(event.x, event.y);

									if (gasFocused) {
										restoreImage(); // huifu the image
									}
									bufferImage(); // store the image into
													// buffer
									painter.paintGasFocus(gid_minDis);
									gasFocused = true;
									currentSelectedGas = wsn
											.getGasByID(gid_minDis);
									refresh();

								} else {
									painter.rePaintAllNodes();
									if (gasFocused) {
										restoreImage();
										gasFocused = false;
										currentSelectedGas = null;
										refresh();
									}

								}
							}
						}
					} else if (event.button == 3) {// right button down
						rightPressed = true;

						lastRightPoint = new Point(event.x, event.y);

					}
					break;

				case SWT.MouseMove:

					if (pointInNode) {
						int offset_x = event.x - lastLeftPoint.x;

						int offset_y = event.y - lastLeftPoint.y;

						Coordinate coordinate = wsn.getCoordianteByID(moveNode
								.getID());
						coordinate.x += offset_x;
						coordinate.y += offset_y;
						if (coordinate.x < 0)
							coordinate.x = 0;
						if (coordinate.y < 0)
							coordinate.y = 0;
						if (coordinate.x > wsn.getSize().x)
							coordinate.x = wsn.getSize().x;
						if (coordinate.y > wsn.getSize().y)
							coordinate.y = wsn.getSize().y;

						VNode node = wsn.getNodeByID(moveNode.getID());
						if (node == wsn.getNodes(
								"org.deri.nettopo.node.tpgf.SourceNode_TPGF",
								true)) {
							node.setColor(NodeConfiguration.SourceNodeColorRGB);
						}
						if (node == wsn.getNodes(
								"org.deri.nettopo.node.SinkNode", true)) {
							node.setColor(NodeConfiguration.SinkNodeColorRGB);
						}
						Collection<VGas> gas = wsn.getAllGas();

						if (gas.size() > 0) {
							for (VGas vg : gas) {
								int idv = vg.getID();
								Coordinate g_c = wsn.getCoordianteByID(idv);
								double dis_gn = coordinate.distance(g_c);
								int rad = Integer.parseInt(vg
										.getAttrValue("Radius"));// -------------------------mark---------------------------
								if (dis_gn < rad) {

									node.setColor(NodeConfiguration.NodeInGasRGB);
									break;
								} else {
									node.setColor(NodeConfiguration.SensorNodeColorRGB);
								}
							}
						}
						painter.rePaint();

						// painter.rePaintAllNodes();
						// repaint gas author Dennis
						// painter.rePaintAllGas();
						currentSelectedNode = moveNode;
						nodeMoved = true;
						refresh();
						lastLeftPoint.x = event.x;
						lastLeftPoint.y = event.y;
					} else if (pointInGas) { // ���ڶ����ڵ���
						int offset_x = event.x - lastLeftPoint.x;

						int offset_y = event.y - lastLeftPoint.y;

						Coordinate coordinate = wsn.getCoordianteByID(moveGas
								.getID());
						int rad = Integer.parseInt(moveGas
								.getAttrValue("Radius"));// -------------------------mark---------------------------
						Collection<VNode> sensorNodes = wsn.getNodes(
								"org.deri.nettopo.node.tpgf.SensorNode_TPGF",
								true);

						if (sensorNodes.size() > 0) {
							for (VNode vn : sensorNodes) {
								int idv = vn.getID();
								Coordinate n_c = wsn.getCoordianteByID(idv);
								double dis_gn = n_c.distance(coordinate);

								if (dis_gn < rad) {
									vn.setColor(NodeConfiguration.SensorNodeColorRGB);
									Collection<VGas> gas = wsn.getAllGas();
									if (gas.size() > 1) {
										for (VGas vg : gas) {
											if (vg.getID() != moveGas.getID()) {
												Coordinate g_c = wsn
														.getCoordianteByID(vg
																.getID());
												int radx = Integer
														.parseInt(vg
																.getAttrValue("Radius"));// -------------------------mark---------------------------
												if (n_c.distance(g_c) < radx) {
													vn.setColor(NodeConfiguration.NodeInGasRGB);
													break;
												}
											}
										}
									}
								}
							}
						}
						coordinate.x += offset_x;
						coordinate.y += offset_y;
						if (coordinate.x < 0)
							coordinate.x = 0;
						if (coordinate.y < 0)
							coordinate.y = 0;
						if (coordinate.x > wsn.getSize().x)
							coordinate.x = wsn.getSize().x;
						if (coordinate.y > wsn.getSize().y)
							coordinate.y = wsn.getSize().y;

						if (sensorNodes.size() > 0) {

							for (VNode vn : sensorNodes) {
								int idv = vn.getID();
								Coordinate n_c = wsn.getCoordianteByID(idv);
								double dis_gn = n_c.distance(coordinate);
								if (dis_gn < rad) {
									vn.setColor(NodeConfiguration.NodeInGasRGB);
								} /*
								 * else{
								 * vn.setColor(NodeConfiguration.SensorNodeColorRGB
								 * ); }
								 */
							}
						}
						painter.rePaint();

						// painter.rePaintAllNodes();
						// repaint gas author Dennis
						// painter.rePaintAllGas();
						currentSelectedGas = moveGas;
						gasMoved = true;
						refresh();
						lastLeftPoint.x = event.x;
						lastLeftPoint.y = event.y;
					}

					if (rightPressed) { // right button is pressed down
						int offset_x = event.x - lastRightPoint.x;
						int offset_y = event.y - lastRightPoint.y;
						Point pt = canv_main.getLocation();
						canv_main.setLocation(pt.x + offset_x, pt.y + offset_y);
					}
					break;
				case SWT.MouseUp:
					if (event.button == 1) { // ���
						if (pointInNode) {
							Coordinate coordinate = wsn
									.getCoordianteByID(moveNode.getID());
							if (wsn.duplicateWithOthers(coordinate)) {
								MessageBox mb = new MessageBox(sh_main,
										SWT.ICON_WARNING | SWT.OK);
								mb.setText("Warning");
								mb.setMessage("Duplicate coordinate. You cannot move the node to that coordinate");
								mb.open();
								coordinate.setCoord(original);
								painter.rePaintAllNodes();

								// by dennis
								// painter.rePaintAllGas();
								currentSelectedNode = moveNode;// ��������ҿ��£��������˾������ȡ���ʦ������Ҫ�ȳ�Ч��
								System.out.println("I have set it null");

								refresh();
							} else {
								if (nodeMoved)
									notifyModified();
								if (moveNode instanceof DVHopNode) {
									Coordinate c = ((DVHopNode) moveNode)
											.getCoordinate();
									c.setCoord(coordinate);
									/* fordebug */
									System.out.println("node "
											+ moveNode.getID()
											+ " has been moved to ( "
											+ c.getX() + "," + c.getY() + " )");
								}

							}
							moveNode = null;
							bufferImage();
						} else if (pointInGas) {
							Coordinate coordinate = wsn
									.getCoordianteByID(moveGas.getID());
							if (wsn.duplicateWithOthers(coordinate)) {
								MessageBox mb = new MessageBox(sh_main,
										SWT.ICON_WARNING | SWT.OK);
								mb.setText("Warning");
								mb.setMessage("Duplicate coordinate. You cannot move the gas to that coordinate");
								mb.open();
								coordinate.setCoord(original);
								painter.rePaintAllGas();

								// by dennis
								// painter.rePaintAllGas();
								currentSelectedGas = moveGas;
								refresh();
							} else {
								if (gasMoved)
									notifyModified();
							}
							bufferImage();
						}

						/*********************** dd ********************************/

						pointInGas = false;
						pointInNode = false;
					}

					if (event.button == 3) // right button up
						rightPressed = false;

					break;
				}
			}

		};

		canv_main.addListener(SWT.MouseDown, listener);
		canv_main.addListener(SWT.MouseUp, listener);
		canv_main.addListener(SWT.MouseMove, listener);

	}

	/**
	 * 
	 */

	public boolean isOpen() {
		if (sh_main == null)
			return false;
		return !sh_main.isDisposed();
	}

	/**
	 * this method will be called by the main entry
	 */
	public void run() {
		display = Display.getDefault();
		sh_main = new Shell(display);
		sh_main.setText("NetTopo");
		sh_main.setImage(new Image(Display.getCurrent(), MyImageLoader.class
				.getResourceAsStream("logo.jpg")));
		sh_main.setBounds(new Rectangle(0, 0, 1000, 900));
		sh_main.setLayout(new GridLayout());
		sh_main.setLocation(500, 100);// ����λ��
		sh_main.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				System.exit(0);
			}
		});
		createImg();
		createMenu_main();
		createTb_main();
		createCmp_main();
		sh_main.open();
		while (!sh_main.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		disposeImg();
		cancelThreads();
		display.dispose();
	}

	private void expandGUI() {
		FormData fd_sash_right = (FormData) sash_v_right.getLayoutData();
		fd_sash_right.right = new FormAttachment(100, -RIGHT_SASH_DEFAULT_RIGHT);
		cmp_main.layout();
		nodeFocused = false;
		tv_prop.setInput(null);

		expandMostGUI();
		expandAlgorithmGUI();
		expandKillGUI();
		expandMobilityGUI();
		expandSearchNodeGUI();
	}

	/**
	 * Expand the GUI when a network is created Relay the sash, set canvas and
	 * some menu items visible but not the mobility, search node. For there is
	 * no one node so far.
	 */
	private void expandMostGUI() {
		/* set some menu items visible */
		mi_file_close.setEnabled(true);
		mi_file_save.setEnabled(true);
		mi_file_saveAs.setEnabled(true);

		mi_node_createOne.setEnabled(true);
		mi_node_createNodes.setEnabled(true);
	}

	/** to expand the GUI for algorithm */
	private void expandAlgorithmGUI() {
		Iterator<MenuItem> it = mi_algorithms.iterator();
		while (it.hasNext()) {
			MenuItem item = it.next();
			item.setEnabled(true);
		}

		/************ mi_parallels ********************************************/
		Iterator<MenuItem> it1 = mi_parallels.iterator();
		while (it1.hasNext()) {
			MenuItem item = it1.next();
			item.setEnabled(true);
		}

		mi_gas_createOne.setEnabled(true);
		mi_gas_createGas.setEnabled(true);

		mi_algorithm_stop.setEnabled(true);
	}

	/** expand the kill GUI */
	public void expandKillGUI() {
		mi_node_killNodes.setEnabled(true);
		mi_node_killOneNode.setEnabled(true);
	}

	/** expand the GUI of mobility. */
	public void expandMobilityGUI() {
		mi_mobility_randomwaypoint.setEnabled(true);
	}

	/** expand the GUI of search */
	public void expandSearchNodeGUI() {
		mi_edit_search.setEnabled(true);
	}

	/** to shrink the GUI for algorithm */
	private void shrinkAlgorithmGUI() {
		Iterator<MenuItem> it = mi_algorithms.iterator();
		while (it.hasNext()) {
			MenuItem item = it.next();
			item.setEnabled(false);
		}

		/****************************** mi_parallels ***********************************/
		Iterator<MenuItem> it0 = mi_parallels.iterator();
		while (it0.hasNext()) {
			MenuItem item = it0.next();
			item.setEnabled(false);
		}

		mi_algorithm_stop.setEnabled(false);
	}

	/**
	 * set the property invisible dispose the main canvas set the most of the
	 * menu items disable
	 */
	private void shrinkGUI() {
		FormData fd_sash_right = (FormData) sash_v_right.getLayoutData();
		fd_sash_right.right = new FormAttachment(100, RIGHT_SASH_DEFAULT_RIGHT);
		cmp_main.layout();

		if (canv_main != null)
			canv_main.dispose();

		mi_file_close.setEnabled(false);
		mi_file_save.setEnabled(false);
		mi_file_saveAs.setEnabled(false);
		mi_edit_search.setEnabled(false);
		mi_node_createOne.setEnabled(false);
		mi_node_createNodes.setEnabled(false);
		mi_node_killNodes.setEnabled(false);
		mi_node_killOneNode.setEnabled(false);
		mi_network_stop.setEnabled(false);
		mi_network_clearup.setEnabled(false);
		shrinkAlgorithmGUI();

		mi_mobility_randomwaypoint.setEnabled(false);
		mi_mobility_stop.setEnabled(false);
		mi_mobility_pauseMobility.setEnabled(false);
		mi_mobility_resumeMobility.setEnabled(false);
		mi_mobility_stopMobility.setEnabled(false);
		mi_mobility_resetColor.setEnabled(false);
		mi_mobility_removeDistrictedArea.setEnabled(false);
		mi_mobility_removeAttractorField.setEnabled(false);
	}

	/**
	 * to notify user that the wsn is modified and set the save file enable
	 */
	private void notifyModified() {
		refresh();
		isFileModified = true;
		mi_file_save.setEnabled(true);
		addLog("Wireless Sensor Network has been modified.");
	}

	private void cmd_newNetwork() {
		if (isFileModified) {
			MessageBox mb = new MessageBox(sh_main, SWT.ICON_WARNING | SWT.YES
					| SWT.NO | SWT.CANCEL);
			mb.setText("Save");
			mb.setMessage("Do you want to save the current file?");
			int returnValue = mb.open();
			if (returnValue == SWT.YES) {
				cmd_saveFile();
			} else if (returnValue == SWT.CANCEL) {
				return;
			}
		}

		/* Open the corresponding wizard */
		Wizard_CreateNetwork wizard = new Wizard_CreateNetwork();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();

		if (returnValue == IDialogConstants.OK_ID) { // User finishes the wizard
			wsn = new WirelessSensorNetwork();
			isFileModified = false;
			file_wsn = null;
			try {
				if (oos != null) {
					oos.close();
					fos.close();
				}
				Coordinate c = wizard.getNetSize();
				wsn.setSize(c);
				if (c.z == 0) { // 2D
					painter = PainterFactory.getInstance("2D");
					createCanv_main();
				} else { // 3D
					painter = PainterFactory.getInstance("3D");
				}
				expandGUI();
				getBufferImage(true);// This is the only place to put the
										// bufferImage to wns.size()
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean cmd_repaintNetwork() { // paint the network in slaves screen.
		Coordinate c = wsn.getSize();
		if (c.z == 0) { // 2D
			painter = PainterFactory.getInstance("2D");
			createCanv_main();
		} else { // 3D
			painter = PainterFactory.getInstance("3D");
		}
		expandGUI();
		painter.rePaintAllNodes();

		refresh();
		return true;
	}

	private void cmd_openFile() {
		/* check if the file is modified first */
		if (isFileModified) {
			MessageBox mb = new MessageBox(sh_main, SWT.ICON_WARNING | SWT.YES
					| SWT.NO | SWT.CANCEL);
			mb.setMessage("Do you want to save the current file?");
			int returnValue = mb.open();
			if (returnValue == SWT.YES) {
				cmd_saveFile();
			} else if (returnValue == SWT.CANCEL) {
				return;
			}
		}
		/* Show the file-open dialog */
		FileDialog dlg = new FileDialog(sh_main, SWT.OPEN);
		dlg.setFilterNames(FILTER_NAMES);
		dlg.setFilterExtensions(FILTER_EXTS);
		String fn = dlg.open(); // get the path of the file name entered by user
		if (fn != null) { // user clicks the open rather than cancel
			try {
				File file = new File(fn);
				if (!file.exists()) {
					MessageBox mb = new MessageBox(sh_main,
							SWT.ICON_INFORMATION | SWT.OK);
					mb.setMessage("File " + fn + " does not exist.");
					mb.open();
					return;
				}

				/* read the WirelessSensorNetwork object in the file */
				FileInputStream fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				wsn = (WirelessSensorNetwork) ois.readObject();
				ois.close();
				fis.close();

				/* release the last file if possible */
				if (oos != null) {
					oos.close();
					fos.close();
				}
				isFileModified = false;
				file_wsn = file;

				Coordinate c = wsn.getSize();
				if (c.z == 0) { // 2D
					painter = PainterFactory.getInstance("2D");
					createCanv_main();
				} else { // 3D
					painter = PainterFactory.getInstance("3D");
				}

				String pictureName = file_wsn.getPath() + ".bmp";
				File picture = new File(pictureName);
				if (!picture.exists()) {
					expandGUI();
					painter.rePaintAllNodes();
					refresh();
					// this.addLog("The network from " + fn + " was opened.");
					MessageBox mb = new MessageBox(sh_main,
							SWT.ICON_INFORMATION | SWT.OK);
					mb.setMessage("Picture " + fn
							+ " does not exist. You may lose some data.");
					mb.open();
					return;
				}

				/* Load image data */
				synchronized (APP) {
					ImageLoader il = new ImageLoader();
					ImageData[] data = il.load(pictureName);
					if (img_buffer != null) {
						img_buffer.dispose();
					}
					if (data != null & data.length > 0)
						img_buffer = new Image(Display.getCurrent(), data[0]);
				}

				expandGUI();
				refresh();
				// this.addLog("The network from " + fn + " was opened.");
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageBox mb = new MessageBox(sh_main, SWT.ICON_INFORMATION
						| SWT.OK);
				mb.setMessage("NetTopo can not open "
						+ fn
						+ " because NetTopo does not support such kind of file or the file is broken.");
				mb.open();
				return;
			}
		}
	}

	private void cmd_closeFile() {
		if (isFileModified) {
			MessageBox mb = new MessageBox(sh_main, SWT.ICON_WARNING | SWT.YES
					| SWT.NO | SWT.CANCEL);
			mb.setMessage("Do you want to save the current file?");
			int returnValue = mb.open();
			if (returnValue == SWT.YES) {
				cmd_saveFile();
			} else if (returnValue == SWT.CANCEL) {
				return;
			}
		}
		try {
			if (oos != null) {
				oos.close();
				fos.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		file_wsn = null;
		isFileModified = false;
		shrinkGUI();
		cancelThreads();
		addLog("Network was closed.");
		refresh();
	}

	private void cmd_saveFile() {
		if (file_wsn == null) {
			cmd_saveAs();
		} else {
			try {
				if (!file_wsn.exists())
					file_wsn.createNewFile();
				if (oos != null) {
					oos.close();
					fos.close();
				}
				fos = new FileOutputStream(file_wsn);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(wsn);
				oos.flush();

				/* write the image data in the file */
				ImageLoader il = new ImageLoader();
				il.data = new ImageData[1];
				synchronized (APP) {
					il.data[0] = img_buffer.getImageData();
				}
				il.save(file_wsn.getPath() + ".bmp", SWT.IMAGE_BMP);

				isFileModified = false;
				mi_file_save.setEnabled(false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void cmd_saveAs() {
		SafeSaveDialog dlg = new SafeSaveDialog(sh_main);
		dlg.setFilterNames(FILTER_NAMES);
		dlg.setFilterExtensions(FILTER_EXTS);
		String fn = dlg.open(); // get the path of the file name entered by user
		if (fn != null) { // user clicks the save rather than cancel
			try {
				File file = new File(fn);
				if (!file.exists())
					file.createNewFile();
				if (oos != null) {
					oos.close();
					fos.close();
				}
				/* write the WirelessSensorNetwork object in the file */
				fos = new FileOutputStream(file);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(wsn);
				oos.flush();

				/* write the image data in the file */
				ImageLoader il = new ImageLoader();
				il.data = new ImageData[1];
				synchronized (APP) {
					il.data[0] = img_buffer.getImageData();
				}
				il.save(file.getPath() + ".bmp", SWT.IMAGE_BMP);

				/* update state */
				isFileModified = false;
				file_wsn = file;
				/* update GUI */
				mi_file_save.setEnabled(false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}

	private void cmd_searchNode() {
		InputDialog dlg = new InputDialog(sh_main, "Search Node By ID",
				"Please enter the node ID", "", new IInputValidator() {
					public String isValid(String input) {
						if (!FormatVerifier.isPositive(input)) {
							return "Node ID must be a positive integer.";
						} else {
							return null;
						}
					}
				});
		if (dlg.open() == Window.OK) {
			int nodeID = Integer.parseInt(dlg.getValue());
			if (wsn.getNodeByID(nodeID) != null) {
				if (nodeFocused) {
					restoreImage();
				}
				bufferImage();
				this.setCurrentSelectedNode(wsn.getNodeByID(nodeID));
				painter.paintNodeFocus(nodeID);
				nodeFocused = true;
				refresh();
			} else { // did not find the node
				if (nodeFocused) {
					restoreImage();
					refresh();
					nodeFocused = false;
				}

				MessageBox mb = new MessageBox(sh_main, SWT.ICON_INFORMATION
						| SWT.OK);
				mb.setText("Result");
				mb.setMessage("Node does not exist.");
				mb.open();
			}
		}
	}

	private void cmd_newNode() {
		Wizard_CreateOneNode wizard = new Wizard_CreateOneNode();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			expandGUI();
			notifyModified();
			refresh();
		}
	}

	private void cmd_createResultsMenuItem() {
		ReadXML.FILEPATH = CreateTasks.simPath;
		ArrayList<String> tasksName = ReadXML.getAllTasksName();
		Menu result = new Menu(mi_result);
		mi_result.setMenu(result);
		for (int i = 0, len = tasksName.size(); i < len; ++i) {
			String str = tasksName.get(i);
			MenuItem mi = new MenuItem(result, SWT.PUSH);
			mi.setText(str);
			mi.setData(i);
			mi.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					MenuItem tempMI = (MenuItem) e.getSource();
					try {
						System.out.println("in result");

						// ReadResult rr = new ReadResult();
						paintNetwork((int) tempMI.getData(), "./image/"
								+ tempMI.getText() + ".bmp");

						/** if pass the image , then ,this codes can be deleted ***/
						// ArrayList<ArrayList<Integer>> paths =
						// rr.getAllPaths(tempMI.getText(), null);
						// if (paths == null || paths.size() == 0)
						// return;
						// for (ArrayList<Integer> path : paths) {
						// for (int i = 0, len = path.size() - 1; i < len; ++i)
						// {
						// painter.paintNode(path.get(i),
						// NodeConfiguration.NodeInPathColor);
						// painter.paintConnection(path.get(i),
						// path.get(i + 1),
						// NodeConfiguration.ConnectionColorRGB);
						// }
						// painter.paintNode(path.get(path.size() - 2),
						// NodeConfiguration.NodeInPathColor);
						// painter.paintConnection(path.get(path.size() - 2),
						// path.get(path.size() - 1),
						// NodeConfiguration.ConnectionColorRGB);
						// }
						// refresh();
						/** if pass the image , then ,this codes can be deleted ***/

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
	}

	private void paintNetwork(int index, String pictureName) throws Exception // ���»�ͼ����slave�õ��Ľ���л�ͼ
	{
		WirelessSensorNetwork wsnNow = allTasks.get(index);
		setNetwork(wsnNow);
		System.out.println("wsn in master:" + wsnNow.toString());
		Coordinate c = wsnNow.getSize();
		if (c.z == 0) { // 2D
			painter = PainterFactory.getInstance("2D");
			NetTopoApp.getApp().createCanv_main();
		} else { // 3D
			painter = PainterFactory.getInstance("3D");
		}
		// setBufferImage(image);
		synchronized (APP) {
			ImageLoader il = new ImageLoader();
			ImageData[] data = il.load(pictureName);
			if (img_buffer != null) {
				img_buffer.dispose();
			}
			if (data != null & data.length > 0)
				img_buffer = new Image(Display.getCurrent(), data[0]);
		}
		expandGUI();
		// painter.rePaint();
		refresh();
	}

	private boolean cmd_parallelRun() throws Exception {// ����
		ReadXML.FILEPATH = CreateTasks.simPath;
		if (allTasks == null || allTasks.size() == 0 || ReadXML.isModified()) {
			allTasks = ReadXML.getAllTasksList();
		}
		RunTasksThread rtt = new RunTasksThread(allTasks);

		/**
		 * oooooooooooooooooooooooooooooooooooooooooooo
		 */
		allTasks = StaticFunction.copyAllTasks(allTasks);

		for (WirelessSensorNetwork w : allTasks) {
			System.out.println("NodeNumber:" + w.getAllSensorNodesID().length);
		}
		Thread th = new Thread(rtt);
		th.start();
		while (th.isAlive()) {
			Thread.sleep(500);
		}
		return rtt.isFinish();

	}

	private void cmd_result() {
		ShowResultShell.startPaint(getDisplay());
		painter = ShowResultShell.painter;
	}

	private void cmd_newNodes() {
		Wizard_CreateNodes wizard = new Wizard_CreateNodes();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			expandGUI();
			notifyModified();
			refresh();
		}
	}

	private void cmd_killNodes() {
		Wizard_KillNodes wizard = new Wizard_KillNodes();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			notifyModified();
		}
	}

	private void cmd_killOneNode() {
		Wizard_KillOneNode wizard = new Wizard_KillOneNode();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			notifyModified();
		}
	}

	private void cmd_visualizeNetwork() {
		if (wsn == null)
			cmd_newNetwork();

		if (wsn == null)
			return;
		/* Open the cooresponding wizard */
		Wizard_VisualizeNetwork wizard = new Wizard_VisualizeNetwork();
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) { // User finishes the
			// wizard
			Runnable run_vi = wizard.getRunnable();
			visualizer = new Thread(run_vi);
			visualizer.start();
			mi_network_stop.setEnabled(true);
			notifyModified();
		}
	}

	/** the timertask will probably run when it is cancelled */
	public void cmd_stopAlgorithm() {
		if (getTimer_func() != null && getTimertask_func() != null) {
			getTimertask_func().cancel();
			getTimer_func().cancel();
			getTimer_func().purge();
			setTimertask_func(null);
			setTime_func(null);
		}
		
		wsn.resetOriginal();
		wsn.resetAllSensorNodesActive();
		wsn.resetAllNodesColor();
		painter.rePaintAllNodes();
		this.addLog("the algorithm has been stopped. If it run once more, just press stop again.");
		refresh();
	}

	private void cmd_randomwaypoint() {
		Wizard_CreateRandomWaypointModel wizard = new Wizard_CreateRandomWaypointModel(
				1);
		WizardDialog dlg = new WizardDialog(sh_main, wizard);
		int returnValue = dlg.open();
		if (returnValue == IDialogConstants.OK_ID) {
			mi_mobility_stop.setEnabled(true);
			mi_mobility_stopMobility.setEnabled(true);
			mi_mobility_pauseMobility.setEnabled(true);
			mi_mobility_resetColor.setEnabled(true);
			addDistrictedArea(wizard.getDistrictedArea());
			setAField(wizard.getAttractorField());

			double[][] paths = wizard.getChangedPath();
			if (wsn.getSize().z == 0) {
				painter = PainterFactory.getInstance("2D");
			} else {
				painter = PainterFactory.getInstance("3D");
			}
			painter.paintMobileNodes(paths);
			notifyModified();
			refresh();
		}
	}

	private void cmd_pauseMobility() {
		this.setIsPause(true);
		mi_mobility_pauseMobility.setEnabled(false);
		mi_mobility_resumeMobility.setEnabled(true);
		this.addLog("the mobility is in pause.");
	}

	private void cmd_resumeMobility() {
		this.setIsPause(false);
		mi_mobility_pauseMobility.setEnabled(true);
		mi_mobility_resumeMobility.setEnabled(false);
		this.addLog("the mobility has been successfully resumed.");
	}

	private void cmd_stopMobility() {
		if (getCurrentSelectedNode() != null) {
			painter.removeNodeFocus(this.getCurrentSelectedNode().getID());
		}
		if (getTimer_Mobility() != null) {
			getTimer_Mobility().cancel();
			getTimer_Mobility().purge();
			setTimer_Mobility(null);
		}

		mi_mobility_stopMobility.setEnabled(false);
		mi_mobility_pauseMobility.setEnabled(false);
		mi_mobility_resumeMobility.setEnabled(false);
		mi_mobility_resetColor.setEnabled(true);
		setMobileNodeNum(0);
		this.addLog("the mobility has been stopped.");
	}

	private void cmd_resetColor() {
		wsn.resetAllNodesColor();
		painter.rePaintAllNodes();
		mi_mobility_resetColor.setEnabled(false);
		this.addLog("the colors of the mobile nodes have been reset to their original colors.");
		notifyModified();
		refresh();
	}

	private void cmd_removeDistrictedArea() {
		if (districtedAreas != null && isDistrictAreaExist()) {
			painter.removeDistrictedAreas(districtedAreas);
			this.setDistrictAreaExist(false);
			this.setDistrictedAreas(new Vector<DistrictedArea>());
			mi_mobility_removeDistrictedArea.setEnabled(false);
			this.addLog("the districted area(s) has(have) been removed.");
			notifyModified();
			refresh();
		}
	}

	private void cmd_removeAttractorField() {
		if (this.aField != null && this.attractorFieldExist) {
			painter.removeAttractors(this.getAField());
			this.setAField(null);
			this.attractorFieldExist = false;
			mi_mobility_removeAttractorField.setEnabled(false);
			this.addLog("the attractor field(s) has(have) been removed.");
			notifyModified();
			refresh();
		}
	}

	/**
	 * open dir
	 */
	private boolean chooseDir() {
		// �½��ļ��У�Ŀ¼���Ի���
		DirectoryDialog folderdlg = new DirectoryDialog(sh_main);
		// �����ļ��Ի���ı���
		folderdlg.setText("DIR selection");
		// ���ó�ʼ·��
		folderdlg.setFilterPath("SystemDrive");
		// ���öԻ�����ʾ�ı���Ϣ
		folderdlg
				.setMessage("Please choose the DIR contains tasks or where you want to hold new tasks.");
		// ���ļ��Ի��򣬷���ѡ���ļ���Ŀ¼
		String selecteddir = folderdlg.open();
		if (selecteddir == null) {
			return false;
		} else {
			simulationPath = selecteddir;
			return true;
			// System.out.println("��ѡ�е��ļ���Ŀ¼Ϊ��"+selecteddir);
		}
	}

	/** make the wsn to be empty */
	private void clearAll() {
		cancelThreads();
		wsn.deleteAllNodes();
		painter.rePaintAllNodes();
		notifyModified();
		this.addLog("The wsn has been set to empty.");
		currentSelectedNode = null;
		refresh();
	}

	/**
	 * cancel two timer object one for ckn one for mobility
	 */
	private void cancelThreads() {
		if (timer_mobility != null) {
			timer_mobility.cancel();
			timer_mobility.purge();
			setTimer_Mobility(null);
		}
		if (timer_func != null && timertask_func != null) {
			timertask_func.cancel();
			timer_func.cancel();
			timer_func.purge();
			setTimertask_func(null);
			setTime_func(null);
		}
		if (visualizer != null) {
			visualizer.interrupt();
			visualizer = null;
			mi_network_stop.setEnabled(false);
		}
	}

	/********************************* add shell getter ***************************************/

	synchronized public Composite getCmp_graph() {
		return cmp_graph;
	}

	/**
	 * Before this is run, be sure to set up the launch configuration
	 * (Arguments->VM Arguments) for the correct SWT library path in order to
	 * run with the SWT dlls. The dlls are located in the SWT plugin jar. For
	 * example, on Windows the Eclipse SWT 3.1 plugin jar is:
	 * installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
	 */
	public static void main(String[] args) {

		NetTopoApp.getApp().run();
	}

}