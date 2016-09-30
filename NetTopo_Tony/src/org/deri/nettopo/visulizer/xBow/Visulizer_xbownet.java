package org.deri.nettopo.visulizer.xBow;

import java.util.Collection;
import java.util.Random;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;
import org.deri.nettopo.util.FormatVerifier;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.visulizer.Visulizer;

import java.io.*;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.*;
//import javax.xml.parsers.ParserConfigurationException;

import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.xbow.XBow_SensorNode;
import org.deri.nettopo.node.xbow.XBow_Sink;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

public class Visulizer_xbownet implements Visulizer {

	private static final int COUNTER = 7;

	private static final int DELAY = 7000; // delay of timer

	private String IP_Address;

	private String Port;

	private int Rate;

	private String Discription;

	private String[] names = { "IP Address", "Port", "Rate", "Discription" };

	private String errorMsg;

	private Socket xmlSocket = null;

	private InputStreamReader isr;

	private boolean connected;

	/* xbowNode - counter */
	private HashMap<Integer,Integer> nc;

	/* xbowNode - VNode ID */
	private HashMap<Integer,Integer> ni;

	private NetTopoApp app;

	private WirelessSensorNetwork wsn;

	private Painter painter;

	private Display display;

	private Timer timer;

	public Visulizer_xbownet() {
		nc = new HashMap<Integer,Integer>();
		ni = new HashMap<Integer,Integer>();
		this.IP_Address = "192.168.20.10";
		this.Port = "9005";
		this.Rate = 100;

		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
		painter = app.getPainter();
		display = app.getDisplay();

		timer = new Timer();
	}

	public String getIPAddress() {
		return IP_Address;
	}

	public String getPort() {
		return Port;
	}

	public int getRate() {
		return Rate;
	}

	public String getDiscription() {
		return Discription;
	}

	public String[] getAttrNames() {
		return names;
	}

	public boolean setAttrValue(String name, String value) {
		boolean isArgValid = true;
		if (name.equals(names[0])) {

			if (FormatVerifier.isIpAddress(value)) {
				IP_Address = value;
			} else {
				errorMsg = "Please input the correct IP address";
				isArgValid = false;
			}
		} else if (name.equals(names[1])) {
			if (FormatVerifier.isPositive(value)) {
				Port = value;
			} else {
				errorMsg = "Port must be a positive number";
				isArgValid = false;
			}
		} else if (name.equals(names[2])) {
			if (FormatVerifier.isPositive(value)) {
				Rate = Integer.parseInt(value);
			} else {
				errorMsg = "Rate must be a positive number";
				isArgValid = false;
			}
		} else if (name.equals(names[3])) {
			if (FormatVerifier.isStringEmpty(value)) {
				Discription = value;
			} else {
				errorMsg = "Please describe your location";
				isArgValid = false;
			}
		} else {
			errorMsg = "No such argument";
			isArgValid = false;
		}
		return isArgValid;
	}

	public String getAttrValue(String attrName) {

		int index = Util.indexOf(names, attrName);
		switch (index) {
		case 0:
			return String.valueOf(getIPAddress());
		case 1:
			return String.valueOf(getPort());
		case 2:
			return String.valueOf(getRate());
		case 3:
			return String.valueOf(getDiscription());
		default:
			return null;
		}
	}


	public String getAttrErrorDesciption() {
		return errorMsg;
	}

	protected boolean connect() {
		try {
			// setup the socket connection
			xmlSocket = new Socket(IP_Address, Integer.parseInt(Port));
			isr = new InputStreamReader(xmlSocket.getInputStream());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// app.notifyUser("hint", "Cannot connect to the server");
			return false;
		}
	}

	protected String getRawString() throws IOException {
		/*
		 * IMPORTANT Factors for getting data 1) when reading data from buffer,
		 * it is always good to read more data. For example, the actual length
		 * of an XML packet is around 2843, but set the buffer as 3200. If two
		 * sensor node 2843 * 2, but set the buffer as 6000. 2) the sampling
		 * rate is another important factor. Setting different sampling rate,
		 * getting different results.
		 */
		if (connected) {

			char[] input = new char[3000];
			// initialize this char[]
			// for (int j = 0; j < input.length; j++) {
			// input[j] = 0;
			// }
			isr.read(input);
			String rawStr = new String(input).trim();
			return rawStr;
		}
		return null;
	}

	protected String ProcessRawString(String Rowstring) {
		String s = Rowstring;
		String xmls = null;
		String bs;
		int indexS;
		int indexE;
		if (s != "") {
			indexS = s.indexOf("<?xml");
			indexE = s.indexOf("</MotePacket>");
			if (indexS < indexE) {
				if (indexS >= 0) {
					bs = s.substring(indexS, (indexE + 13));
					if (bs.length() > 2000) {
						xmls = bs;
					}
					if (bs.length() < 2000) {
						indexS = s.indexOf("<?xml", indexE);
						indexE = s.indexOf("</MotePacket>", indexS);
						if (indexS < indexE) {
							if (indexS >= 0) {
								bs = s.substring(indexS, (indexE + 13));
								if (bs.length() > 2000) {
									xmls = bs;
								}
							}
						}
					}
				}
			}
		}
		return xmls;
	}

	protected xBowPacket getxBowPacket(String xmlstr) {
		if (xmlstr == null)
			return null;
		xBowPacket xp = new xBowPacket();
		try {
			DocumentBuilderFactory domfac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dombuilder = domfac.newDocumentBuilder();

			// Create instance of input source
			InputSource ins = new InputSource();
			// Initialize this input source as xmls
			ins.setCharacterStream(new StringReader(xmlstr));
			// Pass xmls stream to XML Parser
			Document doc = dombuilder.parse(ins);

			// Get the root element of XML packet
			Element root = doc.getDocumentElement();

			// Get the first level Node list
			NodeList fields = root.getChildNodes();

			// Get all fields' name
			for (int i = 0; i < fields.getLength(); i++) {
				Element field = (Element) fields.item(i);
				String name;
				Element nameEle = (Element) field.getElementsByTagName("Name")
						.item(0);
				name = nameEle.getTextContent();
				String value;
				Element valueEle = (Element) field.getElementsByTagName(
						"ConvertedValue").item(0);
				value = valueEle.getTextContent();
				if (name.equals("amtype")) {
					xp.setAmType(Integer.parseInt(value));
				} else if (name.equals("nodeid")) {
					xp.setNodeID(Integer.parseInt(value));
				} else if (name.equals("parent")) {
					xp.setParentID(Integer.parseInt(value));
				} else if (name.equals("group")) {
					xp.setGroup(Integer.parseInt(value));
				} else if (name.equals("voltage")) {
					xp.setVoltage(Integer.parseInt(value));
				} else if (name.equals("humid")) {
					xp.setHumid(Integer.parseInt(value));
				} else if (name.equals("humtemp")) {
					xp.setTemp(Integer.parseInt(value));
				} else if (name.equals("prtemp")) {
					xp.setPreTemp(Float.parseFloat(value));
				} else if (name.equals("press")) {
					xp.setPress(Float.parseFloat(value));
				}
			}
		} catch (Exception e) {
			errorMsg = e.toString();
		}

		if (xp.getAmType() != 11) // filter out the packet in which the nodeid equals 0
			return null;
		return xp;
	}

	protected Coordinate getCoordinate() {

		Coordinate c;
		/* get a random coordinate */
		Random random = new Random(System.currentTimeMillis());
		c = new Coordinate(random.nextInt(wsn.getSize().x), random.nextInt(wsn
				.getSize().y), 0);

		/* get another random coordiante if there exists one */
		while (wsn.hasDuplicateCoordinate(c)) { //
			c.setX(random.nextInt(wsn.getSize().x));
			c.setY(random.nextInt(wsn.getSize().y));
		}
		return c;
	}

	protected XBow_Sink addSinkNode() {

		XBow_Sink sink = new XBow_Sink();
		Coordinate c = getCoordinate();
		try {
			wsn.addNode(sink, c);
		} catch (DuplicateCoordinateException ex) {
			ex.printStackTrace();
		}
		return sink;
	}

	protected void setTimer() {
		timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Iterator<Integer> it = ni.values().iterator();
				while (it.hasNext()) {
					Integer id = (Integer) it.next();
					wsn.deleteNodeByID(id.intValue());
				}
				ni.clear();
				nc.clear();
			}
		}, DELAY);
	}

	protected void updateWSN(xBowPacket xp) {
		if (xp == null)
			return;

		setTimer();

		XBow_SensorNode xbownode = new XBow_SensorNode(xp.getxBowNodeID(), xp
				.getParentID());

		/* add new node if the node does not exist in sensor network */
		if (!nc.containsKey(Integer.valueOf(xbownode.getxBowNodeID()))) {
			Coordinate c = getCoordinate();
			try {
				xbownode.setAmType(xp.getAmType());
				xbownode.setGroup(xp.getGroup());
				xbownode.setVoltage(xp.getVoltage());
				xbownode.setHumid(xp.getHumid());
				xbownode.setTemp(xp.getTemp());
				xbownode.setPreTemp(xp.getPreTemp());
				xbownode.setPress(xp.getPress());
				wsn.addNode(xbownode, c);
				ni.put(Integer.valueOf(xbownode.getxBowNodeID()), Integer
						.valueOf(xbownode.getID()));
			} catch (DuplicateCoordinateException ex) {
				ex.printStackTrace();
			}
		} else { // update the parent id of the current xbownode in case of
			// topology change
			int xBowNodeID = xbownode.getxBowNodeID();
			int vNodeID = ((Integer)ni.get(Integer.valueOf(xBowNodeID))).intValue();
			XBow_SensorNode node = (XBow_SensorNode) wsn.getNodeByID(vNodeID);
			node.setParentID(xbownode.getParentID());
			node.setAmType(xp.getAmType());
			node.setGroup(xp.getGroup());
			node.setVoltage(xp.getVoltage());
			node.setHumid(xp.getHumid());
			node.setTemp(xp.getTemp());
			node.setPreTemp(xp.getPreTemp());
			node.setPress(xp.getPress());
		}

		// Reset the counter or add a new counter
		nc.put(Integer.valueOf(xbownode.getxBowNodeID()), Integer
				.valueOf(COUNTER));

		/* Temporarily store the id of nodes that with counter value 0 */
		ArrayList<Integer> tempID = new ArrayList<Integer>();

		/* Decrease counter of all other nodes by one */
		Iterator<Integer> it = nc.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			if (key.intValue() != xbownode.getxBowNodeID()) {
				Integer counter = (Integer) nc.get(key);
				nc.put(key, Integer.valueOf(counter.intValue() - 1));

				/* check if any node's counter equals 0 */
				if (counter.intValue() == 0) {
					// delete the node and all its children from wsn
					wsn.deleteNodeByID(((Integer) ni.get(key)).intValue());
					tempID.add(key);
				}
			}
		}

		/* update the two hash maps */
		for (int i = 0; i < tempID.size(); i++) {
			nc.remove(tempID.get(i));
			ni.remove(tempID.get(i));
		}
	}

	protected void updateCanvas() {
		painter.rePaintAllNodes();
		Collection<VNode> sensorNodes = wsn.getNodes(
				"org.deri.nettopo.node.xbow.XBow_SensorNode", true);
		XBow_SensorNode[] nodes = new XBow_SensorNode[sensorNodes.size()];
		nodes = (XBow_SensorNode[]) sensorNodes.toArray(nodes);

		if (nodes.length > 0) {
			nodes = (XBow_SensorNode[]) sensorNodes.toArray(nodes);

			for (int i = 0; i < nodes.length; i++) {
				for (int j = 0; j < nodes.length; j++) {
					int id_i = nodes[i].getID();
					int id_j = nodes[j].getID();
					if (id_i != id_j) {
						if (nodes[i].getParentID() == nodes[j].getxBowNodeID()) {
							painter.paintConnection(id_i, id_j);
						}
					}
				}
			}
		}

		/*
		 * Update the canvas in NetTopoApp, but never call app.refresh()
		 * directly
		 */
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				/* let the display call this method */
				app.refresh();
			}
		});

	}

	public void run() {
		String str = "";
		xBowPacket xp;

		if (connected = connect()) {
			if (display.isDisposed())
				return;
			display.asyncExec(new Runnable() {
				public void run() {
					/* let the display call this method */
					app.refresh();
				}
			});

			try {
				while (connected) {
					str = getRawString();
					System.out.println(str);
					str = ProcessRawString(str);
					// System.out.println(str);
					xp = getxBowPacket(str);
					
					updateWSN(xp);

					if (xp != null) {
						System.out.print(xp.getxBowNodeID());
						System.out.print(" parent: ");
						System.out.print(xp.getParentID());
						System.out.print(" Counters: " + nc.get(Integer.valueOf(1)) + " "
								+ nc.get(Integer.valueOf(2)) + " " + nc.get(Integer.valueOf(3)) + " " + nc.get(Integer.valueOf(4))
								+ " " + nc.get(Integer.valueOf(5)) + " " + nc.get(Integer.valueOf(6)));
						System.out.println();

						if (xp.getParentID() == 126)
							System.out.print(str);
					}
					updateCanvas();

					// System.out.println("NodeID: " + xp.getNodeID());
					// System.out.println("ParentID: " + xp.getParentID());

					try {
						Thread.sleep(Rate);
					} catch (InterruptedException e) {
						xmlSocket.close();
						timer.cancel();
						return;
					}
				}
			} catch (Exception ex) {
				// notify the user that the connection crashed.
				ex.printStackTrace();
				if (display.isDisposed())
					return;
				display.asyncExec(new Runnable() {
					public void run() {
						/* let the display call this method */
						app.notifyUser("Error", "Connection crashed");
					}
				});

				return;
			}
		} else {
			// connection failed
			// app.notifyUser("hint", "Cannot connect to the server");
			if (display.isDisposed())
				return;
			display.asyncExec(new Runnable() {
				public void run() {
					/* let the display call this method */
					app.notifyUser("Hint", "Cannot connect to the server");
				}
			});
			return;
		}
	}

	class CheckPacketTask extends TimerTask {
		public void run() {

		}
	}
}