package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.wizard.Wizard_CreateNodes;
import org.deri.nettopo.app.wizard.Wizard_CreateOneNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.topology.Topology;
import org.deri.nettopo.topology.simpletopo.Topo_Arangement;
import org.deri.nettopo.topology.simpletopo.Topo_Random_One;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CreateTasks extends Shell {
	private Text taskName;
	private Text length;
	private Text width;
	private Text high;
	private static Shell shell;

	private Text[] txt_attrsTopo;
	private Label[] lbl_attrsTopo;
	private String[] attrNamesTopo;

	private Text[] txt_attrs;
	private Label[] lbl_attrs;
	private String[] attrNames;

	private Composite nodeMessage;
	private Composite topoMessage;
	private Label lblNodeName;
	private Label topoName;
	private Label lblTopo;
	private Label nodeName;
	private Combo allNodesList;
	private Combo aName;
	private Combo algorName;
	private Combo tasksList;		//所有任务下拉框
	private Button TwoRadioButton;
	private Button ThreeRadioButton;
	
	private List<VNode> vnodeList = null;
	
	
	private List<Topology> topoList = null;
	private Button btnUpdate;
	public static String simPath;
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void Start(Display display,String spath) {
		try {
			//Display display = Display.getDefault();
			simPath=spath;
			if(shell!=null)
				{
				if(!shell.isDisposed())
					shell.dispose();
				}
			shell = new CreateTasks(display);
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
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 */
	public CreateTasks(Display display) throws InstantiationException,
			IllegalAccessException, IOException {
		super(display, SWT.SHELL_TRIM);
		vnodeList = new ArrayList<VNode>();
		topoList = new ArrayList<Topology>();
		Group general = new Group(this, SWT.NONE);
		general.setText("General Message");
		general.setBounds(10, 76, 444, 118);

		Label lblName = new Label(general, SWT.NONE);
		lblName.setBounds(10, 29, 78, 17);
		lblName.setText("Tasks Name:");

		taskName = new Text(general, SWT.BORDER);
		taskName.setBounds(94, 26, 73, 23);

		Label lblAlgorithmname = new Label(general, SWT.NONE);
		lblAlgorithmname.setBounds(10, 71, 101, 17);
		lblAlgorithmname.setText("AlgorithmName:");

		algorName = new Combo(general, SWT.READ_ONLY);

		algorName.setBounds(118, 68, 73, 25);

		 aName = new Combo(general, SWT.READ_ONLY);
		aName.setBounds(206, 68, 198, 25);
		aName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		algorName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Algorithm algorithm = null;
				Combo combo = (Combo) e.getSource();
				String name = (String) combo.getData(combo.getSelectionIndex()
						+ "");
				try {
					algorithm = (Algorithm) Class.forName(name).newInstance();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				AlgorFunc[] functions = algorithm.getFunctions();
				if (functions == null)
					return;
				aName.removeAll();
				for (int i = 0; i < functions.length; i++) { // 算法方法名字
					final String funcName = functions[i].getClass().getName();

					InputStream res_func = AlgorFunc.class
							.getResourceAsStream("function.properties");
					Properties prop = new Properties();
					try {
						prop.load(res_func);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					String funcDiscription = prop.getProperty(funcName);
					if (funcDiscription == null) {
						continue;
					}

					aName.add(funcDiscription);
					aName.setData("" + (aName.getItemCount() - 1), funcName);
					aName.select(0);
				}
			}
		});

		/** 多级下拉框列表 **********************************************/
		InputStream res = Algorithm.class
				.getResourceAsStream("Algorithm.properties");
		BufferedReader br = new BufferedReader(new InputStreamReader(res));
		String property;

		while ((property = br.readLine()) != null) {

			if (property.trim().startsWith("#")) {
				continue;/* #是注释符，将算法从菜单中注释掉 */
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

			algorName.add(description);
			algorName.setData("" + (algorName.getItemCount() - 1), name);

		}
		/** 多级下拉框列表 **********************************************/
		Group grpNwtwork = new Group(this, SWT.NONE);
		grpNwtwork.setText("NetWork");
		grpNwtwork.setBounds(10, 203, 444, 80);

		final Label lable_high = new Label(grpNwtwork, SWT.NONE);
		lable_high.setText("High:");
		lable_high.setVisible(false);
		lable_high.setBounds(268, 47, 42, 17);

		TwoRadioButton = new Button(grpNwtwork, SWT.RADIO);
		TwoRadioButton.setSelection(true);
		TwoRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				high.setVisible(false);
				lable_high.setVisible(false);
			}
		});
		TwoRadioButton.setBounds(123, 24, 86, 17);
		TwoRadioButton.setText("2D");

		ThreeRadioButton = new Button(grpNwtwork, SWT.RADIO);
		ThreeRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				high.setVisible(true);
				lable_high.setVisible(true);
			}
		});
		ThreeRadioButton.setBounds(226, 24, 97, 17);
		ThreeRadioButton.setText("3D");

		Label lblDimension = new Label(grpNwtwork, SWT.NONE);
		lblDimension.setBounds(10, 24, 61, 17);
		lblDimension.setText("Dimension");

		Label lblNewLabel = new Label(grpNwtwork, SWT.NONE);
		lblNewLabel.setBounds(57, 47, 48, 17);
		lblNewLabel.setText("Length:");

		length = new Text(grpNwtwork, SWT.BORDER);
		length.setBounds(111, 44, 35, 23);

		Label lblWidth = new Label(grpNwtwork, SWT.NONE);
		lblWidth.setText("Width:");
		lblWidth.setBounds(166, 47, 42, 17);

		width = new Text(grpNwtwork, SWT.BORDER);
		width.setBounds(214, 44, 35, 23);

		high = new Text(grpNwtwork, SWT.BORDER);
		high.setBounds(316, 44, 35, 23);
		high.setVisible(false);

		Group grpNode = new Group(this, SWT.NONE);
		grpNode.setText("Nodes");
		grpNode.setBounds(10, 289, 444, 257);

		nodeMessage = new Composite(grpNode, SWT.NONE);
		nodeMessage.setBounds(10, 76, 234, 170);

		topoMessage = new Composite(grpNode, SWT.NONE);
		topoMessage.setBounds(250, 76, 184, 170);

		allNodesList = new Combo(grpNode, SWT.READ_ONLY);
		allNodesList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnUpdate.setText("Update");	//有可能操作没结束
				showNodeMessage(
						vnodeList.get(allNodesList.getSelectionIndex()),
						topoList.get(allNodesList.getSelectionIndex()),SWT.READ_ONLY);
			}
		});
		allNodesList.setBounds(10, 18, 132, 60);

		lblNodeName = new Label(grpNode, SWT.NONE);
		lblNodeName.setBounds(25, 49, 47, 17);
		lblNodeName.setText("Name:");
		lblNodeName.setVisible(false);

		lblTopo = new Label(grpNode, SWT.NONE);
		lblTopo.setBounds(250, 49, 38, 17);
		lblTopo.setText("Topo:");
		lblTopo.setVisible(false);

		topoName = new Label(grpNode, SWT.NONE);
		topoName.setBounds(294, 49, 140, 17);
		topoName.setVisible(false);

		Button addNode = new Button(grpNode, SWT.NONE);
		addNode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES|SWT.NO|SWT.CANCEL);
				mb.setText("Message");
				mb.setMessage("Yes for Create nodes, and No for Create a node.");
				int returnValueFirst = mb.open();
				if (returnValueFirst == SWT.YES) {			//create nodes
					Property.isCreateTasks = true;
					Wizard_CreateNodes wizard = new Wizard_CreateNodes();
					WizardDialog dlg = new WizardDialog(shell, wizard);
					int returnValue = dlg.open();
					if (returnValue == IDialogConstants.OK_ID) {
						// final VNode node=wizard.getVnode();
						final VNode dataNode = wizard.getDataVnode();
						final Topology topoNode = wizard.getTopology();
						showNodeMessage(dataNode, topoNode,SWT.READ_ONLY);
						vnodeList.add(dataNode);
						topoList.add(topoNode);
						String[] nodeNameReal=dataNode.getClass().getName().split("\\.");
						allNodesList.add(nodeNameReal[nodeNameReal.length-1]);
						allNodesList.select(allNodesList.getItemCount() - 1);
					}
					Property.isCreateTasks = false;
				}
				else if(returnValueFirst == SWT.NO)
				{
					Property.isCreateOneTask = true;
					Wizard_CreateOneNode wizardCreateOneNode = new Wizard_CreateOneNode();
					WizardDialog dlgCreateOneNode = new WizardDialog(shell, wizardCreateOneNode);
					int returnValueCON = dlgCreateOneNode.open();
					if (returnValueCON == IDialogConstants.OK_ID) {
						final VNode dataNodeCOM = wizardCreateOneNode.getDataVnode();//获取节点属性
						boolean createType = wizardCreateOneNode.getCreateType();
						Topology topo = null;
						
						if(createType)
							{
							topo = new Topo_Random_One();
							topoList.add(topo );
							}
						else
							{
							 topo = new Topo_Arangement();
							Coordinate cor =wizardCreateOneNode.getOneCoordinate(); 
							topo.setArgValue("x", cor.x+"");
							topo.setArgValue("y", cor.y+"");
							topo.setArgValue("z", cor.z+"");
							topoList.add(topo);
							}
						showNodeMessage(dataNodeCOM, topo,SWT.READ_ONLY);
						String[] nodeNameReal=dataNodeCOM.getClass().getName().split("\\.");
						allNodesList.add(nodeNameReal[nodeNameReal.length-1]);
					}
					Property.isCreateOneTask = false;
				}
				else
				return;
				
			}
		});
		addNode.setBounds(154, 17, 90, 25);
		addNode.setText("Add Node");

		nodeName = new Label(grpNode, SWT.NONE);
		nodeName.setBounds(78, 47, 132, 23);
		nodeName.setVisible(false);
		
		btnUpdate = new Button(grpNode, SWT.NONE);
		btnUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn=(Button)e.widget;
				int index=allNodesList.getSelectionIndex();
				if(btn.getText().equals("Update"))
				{
					
					showNodeMessage(vnodeList.get(index),topoList.get(index),SWT.BORDER);
					btn.setText("Save");
				}
				else if(btn.getText().equals("Save"))
				{
					showNodeMessage(vnodeList.get(index),topoList.get(index),SWT.READ_ONLY);
					btn.setText("Update");
				}
				else
					return;
				
			}
		});
		btnUpdate.setBounds(249, 17, 90, 25);
		btnUpdate.setText("Update");
		
				Button delNode = new Button(grpNode, SWT.NONE);
				delNode.setBounds(345, 17, 90, 25);
				delNode.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (allNodesList.getText() == null
								|| allNodesList.getText().equals(""))
							return;
						int index = allNodesList.getSelectionIndex();
						vnodeList.remove(index);
						topoList.remove(index);
						allNodesList.remove(index);
						if (allNodesList.getItemCount() == 0) {
							lblTopo.setVisible(false);
							topoName.setVisible(false);
							nodeName.setVisible(false);
							lblNodeName.setVisible(false);

							if (lbl_attrs != null) {
								for (int i = 0; i < attrNames.length; i++) {
									lbl_attrs[i].dispose();
									txt_attrs[i].dispose();
								}
							}

							if (lbl_attrsTopo != null) {
								for (int i = 0; i < attrNamesTopo.length; i++) {
									lbl_attrsTopo[i].dispose();
									txt_attrsTopo[i].dispose();
								}
							}

						} else {
							allNodesList.select(0);
							showNodeMessage(vnodeList.get(0), topoList.get(0),SWT.READ_ONLY);
						}
					}
				});
				delNode.setText("Delete Node");
		
		Button submit = new Button(this, SWT.NONE);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(verityFilling())
				{
					try {
						
						boolean isValid = createXML();
						if(isValid)
						{
						warningWin("A task is created.");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					System.out.println("verityFilling:false");
				}
			}
		});
		submit.setBounds(55, 552, 80, 27);
		submit.setText("Add Tasks");
		
		Button cancelBtn = new Button(this, SWT.NONE);
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		cancelBtn.setBounds(310, 552, 80, 27);
		cancelBtn.setText("Close");
		
		
		Group grpSimulationTasks = new Group(this, SWT.NONE);
		grpSimulationTasks.setText("Simulation");
		grpSimulationTasks.setBounds(10, 10, 444, 60);
		
		Label lblTasks = new Label(grpSimulationTasks, SWT.NONE);
		lblTasks.setBounds(227, 25, 40, 17);
		lblTasks.setText("Tasks:");
		
		 tasksList = new Combo(grpSimulationTasks, SWT.READ_ONLY);
		List<String> fileNameList = Util.getXMLFileName(simPath);
		for(String filename:fileNameList)
		{
			tasksList.add(filename);
		}
		if(tasksList.getItemCount()!=0)
			tasksList.select(0);
		tasksList.setBounds(268, 22, 100, 25);
		
		Label lblDirectionary = new Label(grpSimulationTasks, SWT.NONE);
		lblDirectionary.setBounds(10, 25, 82, 17);
		lblDirectionary.setText("Directionary:");
		
		Label lblSimDir = new Label(grpSimulationTasks, SWT.NONE);
		lblSimDir.setBounds(98, 25, 115, 17);
		lblSimDir.setText(simPath);
		
		Button btnNewButton = new Button(grpSimulationTasks, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tasksList.getItemCount()==0)
					return;
						List<String> filesPath=Util.getXMLFilePath(simPath);
						File file= new File(filesPath.get(tasksList.getSelectionIndex()));
						if(file.exists())
						{
							file.delete();
							tasksList.remove(tasksList.getSelectionIndex());
						}
			}
		});
		btnNewButton.setBounds(374, 20, 58, 27);
		btnNewButton.setText("Delete");
		
		Button btn_clear = new Button(this, SWT.NONE);
		btn_clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allNodesList.removeAll();
				length.setText("");
				width.setText("");
				high.setText("");
				taskName.setText("");
				algorName.select(-1);
				aName.select(-1);
				//aName.setText("");
				Control[]  ctrNode =  nodeMessage.getChildren();
				for(Control ct:ctrNode)
				{
					ct.dispose();
				}
				
				Control[]  ctrTopo =  topoMessage.getChildren();
				for(Control ct:ctrTopo)
				{
					ct.dispose();
				}
				
				lblNodeName.setVisible(false);
				nodeName.setVisible(false);
				lblTopo.setVisible(false);
				topoName.setVisible(false);
			}
		});
		btn_clear.setBounds(182, 552, 80, 27);
		btn_clear.setText("Clear");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(490, 643);

	}

	private void showNodeMessage(VNode dataNode, Topology topoNode,int swtConf) {

		String[] topoNameShow = topoNode.getClass().getName().split("\\.");
		topoName.setText(topoNameShow[topoNameShow.length - 1]);
		String[] nodeNameshow = dataNode.getClass().getName().split("\\.");
		nodeName.setText(nodeNameshow[nodeNameshow.length - 1]);
		GridLayout gridLayout = new GridLayout();
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		GridLayout gridLayoutTopo = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayoutTopo.numColumns = 2;
		nodeMessage.setLayout(gridLayout);
		topoMessage.setLayout(gridLayoutTopo);

		if (lbl_attrs != null) {
			for (int i = 0; i < attrNames.length; i++) {
				lbl_attrs[i].dispose();
				txt_attrs[i].dispose();
			}
		}
		attrNames = dataNode.getAttrNames();
		lbl_attrs = new Label[attrNames.length];
		txt_attrs = new Text[attrNames.length];
		for (int index = 0; index < attrNames.length; index++) {
			lbl_attrs[index] = new Label(nodeMessage, SWT.NONE);
			lbl_attrs[index].setText(attrNames[index] + ":");
			lbl_attrs[index].setLayoutData(gridData);
			txt_attrs[index] = new Text(nodeMessage, swtConf);
			txt_attrs[index].setLayoutData(gridData);
			txt_attrs[index].setText(dataNode.getAttrValue(attrNames[index])); 
			txt_attrs[index].setData(attrNames[index]);
			txt_attrs[index].addModifyListener(new ModifyListener(){
				boolean isFirstTime=true;
				@Override
				public void modifyText(ModifyEvent e) {
					// TODO Auto-generated method stub
					if(isFirstTime)
					{
						isFirstTime=false;
						return;
					}
					Text text=(Text)e.widget;
					VNode node=vnodeList.get( allNodesList.getSelectionIndex());
					node.setAttrValue(text.getData().toString(), text.getText());
				}
				
			});
		}
		nodeMessage.layout();

		if (lbl_attrsTopo != null) {
			for (int i = 0; i < attrNamesTopo.length; i++) {
				lbl_attrsTopo[i].dispose();
				txt_attrsTopo[i].dispose();
			}
		}
		attrNamesTopo = topoNode.getArgNames();
		lbl_attrsTopo = new Label[attrNamesTopo.length];
		txt_attrsTopo = new Text[attrNamesTopo.length];
		for (int index = 0; index < attrNamesTopo.length; index++) {
			lbl_attrsTopo[index] = new Label(topoMessage, SWT.NONE);
			lbl_attrsTopo[index].setText(attrNamesTopo[index] + ":");
			lbl_attrsTopo[index].setLayoutData(gridData);
			txt_attrsTopo[index] = new Text(topoMessage,swtConf);
			txt_attrsTopo[index].setLayoutData(gridData);
			txt_attrsTopo[index].setText(topoNode
					.getArgValue(attrNamesTopo[index]));
			txt_attrsTopo[index].setData(attrNames[index]);
			txt_attrsTopo[index].setLayoutData(gridData);
			txt_attrsTopo[index].addModifyListener(new ModifyListener(){
				boolean isFirstTime=true;
				@Override
				public void modifyText(ModifyEvent e) {
					// TODO Auto-generated method stub
					if(isFirstTime)
					{
						isFirstTime=false;
						return;
					}
					Text text=(Text)e.widget;
					Topology topo=topoList.get(allNodesList.getSelectionIndex());
					topo.setArgValue(text.getData().toString(), text.getText());
				}
			});
			
		}
		topoMessage.layout();

		lblTopo.setVisible(true);
		topoName.setVisible(true);
		nodeName.setVisible(true);
		lblNodeName.setVisible(true);

	}

	private boolean verityFilling()
	{
		if(taskName.getText().trim().equals(""))
		{
			warningWin("You should name the tasks");
			return false;
		}
	
		if(TwoRadioButton.getSelection())
		{
			if(!Util.isNumeric(length.getText()))
				{
				warningWin("Length should be a non-negative number");
				return false;
				}
			if(!Util.isNumeric(width.getText()))
			{
				warningWin("Width should be a non-negative number");
				return false;
			}
		}
		if(ThreeRadioButton.getSelection())
		{
			if(!Util.isNumeric(length.getText()))
			warningWin("Length should be a non-negative number");
			if(!Util.isNumeric(width.getText()))
			warningWin("width should be a non-negative number");
			if(!Util.isNumeric(high.getText()))
				warningWin("high should be a non-negative number");
			return false;
		}
		
		if(aName.getText().trim().equals(""))
		{
			warningWin("AlgorithmName should not be empty.");
			return false;
		}
		else
		{
			System.out.println("(aName.getText().trim():"+aName.getText().trim());
		}
		return true;
	}
	
	public boolean createXML() throws IOException
	{
		if(contains(taskName.getText().trim()))
		{

			MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES
					| SWT.NO );
			mb.setText("Save");
			mb.setMessage("There is a task named"+taskName.getText().trim()+"already, do you want to cover it?");
			int returnValue = mb.open();
			if (returnValue == SWT.YES) {
				System.out.println("Yes");
			} else if (returnValue == SWT.NO) {
				System.out.println("NO");
				return false;
			}
		}
		
		Element root = DocumentHelper.createElement("Configuration");
        Document document2 = DocumentHelper.createDocument(root);

        // 添加属性
       // root2.addAttribute("WSN", "zhangsan");
        // 添加子节点:add之后就返回这个元素
        Element wsn = root.addElement("WirelessSensorNetwork");
        	Element width=wsn.addElement("Length");
        	width.setText(length.getText());
        	Element length=wsn.addElement("Width");
        	length.setText(width.getText());
        	Element hight=wsn.addElement("Hight");
        	if(ThreeRadioButton.getSelection())
        	{
        		hight.setText(high.getText());
        	}
        	else
        		hight.setText("0");
        	VNode vnode=null;
        	Topology topo=null;
        	String[] attrName;
        	for(int i=0,len=vnodeList.size();i<len;++i)
        	{	vnode=vnodeList.get(i);
        		 Element node = root.addElement("Node");
        	        node.addAttribute("name", nodeName.getText());
        	        node.addAttribute("class",vnode.getClass().getName() );
        	        	Element property=node.addElement("Property");
        	        	attrName=vnode.getAttrNames();
        	        	String str=null;
        	        	for(int j=0,lenj=attrName.length;j<lenj;++j)
        	        	{
        	        		str=attrName[j].replaceAll(" ", "_");
        	        		Element elementsOfProperty=property.addElement(str);
        	        		elementsOfProperty.setText(vnode.getAttrValue(attrName[j]));
        	        	}
        	        	Element topology=node.addElement("Topology");
        	        	topo=topoList.get(i);	
        	        	topology.addAttribute("name", topoName.getText());
        	        	topology.addAttribute("class", topo.getClass().getName());
        	        	attrName=topo.getArgNames();
        	        	for(int k=0,lenk=attrName.length;k<lenk;++k)
        	        	{
        	        		str=attrName[k].replaceAll(" ", "_");
        	        		Element elementsOfProperty=topology.addElement(str);
        	        		elementsOfProperty.setText(topo.getArgValue(attrName[k]));
        	        	}
        	}        	
        	        
        	    Element algorithm = root.addElement("Algorithm");
        	    String[] algorNameReal= aName.getText().split("\\.");
        	    algorithm.addAttribute("name", algorNameReal[algorNameReal.length-1].replaceAll(" ", "_"));
        	    algorithm.addAttribute("class", aName.getData(""+aName.getSelectionIndex()).toString());
        	     System.out.println("XML has been created!");   		
        OutputFormat format = new OutputFormat("    ", true);// 设置缩进为4个空格，并且另起一行为true
        XMLWriter xmlWriter = new XMLWriter(new FileWriter(simPath+"\\"+taskName.getText().trim()+".xml"),
                format);
        System.out.println("---------:"+simPath+taskName.getText().trim());
        xmlWriter.write(document2);
        xmlWriter.flush();
        xmlWriter.close();
        tasksList.add(taskName.getText().trim());
        if(tasksList.getItemCount()==1)
        	tasksList.select(0);
        return true;
	}
	
	private boolean contains(String name)
	{
		File file=new File(simPath);
		File[] allExistFile=file.listFiles();
		for(File f:allExistFile)
		{
			String fileName=f.getName();
			if(fileName.endsWith(".xml")&&name.equals(fileName.substring(0,fileName.indexOf("."))))
			{
				return true;
			}
		}
		return false;
	}
	
	
	private void warningWin(String waringMessage)
	{
		MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES);
		mb.setText("Message");
		mb.setMessage(waringMessage);
		int returnValue = mb.open();
		if (returnValue == SWT.YES) {
			return;
		}
		return ;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
