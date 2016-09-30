package org.deri.nettopo.display;

import java.util.*;

import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.AttractorField;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DistrictedArea;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.GasConfiguration;
import org.deri.nettopo.gas.VGas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;


public class Painter_2D implements Painter {
//
	/**画失效节点
	 * @param id
	 */
	public void paintNodeByCoors(int id,RGB col) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			Coordinate c = wsn.getDeadCors().get(new Integer(id));
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				//VNode node = (VNode) wsn.getNodeByID(id);
				Color color = new Color(Display.getCurrent(), col);
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
				gc.dispose();
			}
		}
	}
	
	
	/**
	 * paint node by its id
	 */
	public void paintNode(int id) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			Coordinate c = wsn.getCoordianteByID(id);
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				VNode node = (VNode) wsn.getNodeByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
				gc.dispose();
			}
		}
	}

	/**
	 * paint node by its id, and set the color to rgb
	 */
	public void paintNode(int id, RGB rgb) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			Coordinate c = wsn.getCoordianteByID(id);
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				Color color = new Color(Display.getCurrent(), rgb);
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);

				color.dispose();
				gc.dispose();
			}
		}
	}

	/**
	 * paint node by its id within ids
	 */
	public void paintNodes(int[] ids) {
		for (int i = 0; i < ids.length; i++) {
			this.paintNode(ids[i]);
		}
	}

	/**
	 * paint all nodes in the wsn 修改过
	 */
	public void paintAllNodes() {
		Collection<VNode> allNodes = NetTopoApp.getApp().getNetwork()
				.getAllNodes();
		Iterator<VNode> Iter = allNodes.iterator();
		while (Iter.hasNext()) {
			VNode node1 = (VNode) Iter.next();
			//if (node1.getColor() != NodeConfiguration.DeadNodeRGB) {
				int id = node1.getID();
				this.paintNode(id);
			/*}else {
				continue;
			}*/
		}
	}

	/**
	 * repaint all nodes in the wsn
	 */
	public void rePaintAllNodes() {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(true);
			GC gc = new GC(img);

			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();

			Collection<VGas> allGas = wsn.getAllGas();
			Iterator<VGas> gas_it = allGas.iterator();
			while (gas_it.hasNext()) {
				VGas gas = (VGas) gas_it.next();
				int id = gas.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), gas.getColor());
				gc.setBackground(color);
				int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}

			Collection<VNode> allNodes = wsn.getAllNodes();
			Iterator<VNode> it = allNodes.iterator();
			while (it.hasNext()) {
				VNode node = (VNode) it.next();
				int id = node.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}

			gc.dispose();
		}
	}

	/**
	 * paint all nodes without clearing the image
	 */
	/**
	 * repaint all nodes in the wsn
	 */
	public void rePaintAllNodesWithoutClear() {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(false);
			GC gc = new GC(img);
			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();

			Collection<VNode> allNodes = wsn.getAllNodes();
			Iterator<VNode> it = allNodes.iterator();
			while (it.hasNext()) {
				VNode node = (VNode) it.next();
				int id = node.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}

			gc.dispose();
		}
	}

	/**
	 * paint the focus square around the node with coordinate
	 */
	public void paintNodeFocus(Coordinate c) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				int nodeRadius = NodeConfiguration.paintRadius;
				gc.drawRectangle(c.x - nodeRadius - 1, c.y - nodeRadius - 1,
						2 * (nodeRadius) + 1, 2 * (nodeRadius) + 1);
				gc.dispose();
			}
		}
	}

	/**
	 * paint the focus square around the node with id
	 */
	public void paintNodeFocus(int id) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			Coordinate c = wsn.getCoordianteByID(id);
			if (c != null) {
				this.paintNodeFocus(c);
			}
		}
	}

	/**
	 * remove the node with the id
	 * 
	 * @param id
	 */
	public void removeNode(int id) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			if (app.getCurrentSelectedNode() != null
					&& app.getCurrentSelectedNode().getID() == id) {
				app.setCurrentSelectedNode(null);
			}
			wsn.deleteNodeByID(id);
			this.rePaintAllNodes();
		}
	}

	/**
	 * remove the node with the coordinate
	 * 
	 * @param coordinate
	 */
	public void removeNode(Coordinate c) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			int id = 0;
			int[] ids = wsn.getAllNodesID();
			for (int i = 0; i < ids.length; i++) {
				if (wsn.getCoordianteByID(i).equals(c)) {
					id = i;
					this.removeNode(id);
				}
			}
		}
	}

	/**
	 * remove the focus square around the node with id
	 */
	public void removeNodeFocus(int id) {
		Coordinate c = NetTopoApp.getApp().getNetwork().getCoordianteByID(id);
		this.removeNodeFocus(c);
	}

	/**
	 * remove the focus square around the node with coordinate c
	 */
	public void removeNodeFocus(Coordinate c) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			if (c != null) {
				Image img = app.getBufferImage(false);
				GC gc = new GC(img);
				int nodeRadius = NodeConfiguration.paintRadius;
				Color color = new Color(Display.getCurrent(),
						NodeConfiguration.WhiteColorRGB);
				gc.setForeground(color);
				gc.drawRectangle(c.x - nodeRadius - 1, c.y - nodeRadius - 1,
						2 * (nodeRadius) + 1, 2 * (nodeRadius) + 1);
				gc.dispose();
			}
		}
	}

	/**
	 * remove all nodeFocus
	 */
	public void removeAllNodeFocus() {

	}

	/**
	 * paint connection between id1 and id2. with the color of (119, 211, 217)
	 */
	public void paintConnection(int id1, int id2) {
		paintConnection(id1, id2, NodeConfiguration.ConnectionColorRGB);
	}

	/**
	 * paint connection between id1 and id2. with the color of rgb
	 */
	public void paintConnection(int id1, int id2, RGB rgb) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Image img = app.getBufferImage(false);
			GC gc = new GC(img);
			Coordinate c1 = wsn.getCoordianteByID(id1);
			Coordinate c2 = wsn.getCoordianteByID(id2);
			Color color = new Color(Display.getCurrent(), rgb);
			gc.setForeground(color);
			if (c1 != null && c2 != null) {
				gc.drawLine(c1.x, c1.y, c2.x, c2.y);
			}

			color.dispose();
			gc.dispose();
		}
	}

	/**
	 * 
	 * @param path
	 *            it is generated by the RandomWaypoint(). For every path,the
	 *            first element is the id. The others are x and y ...
	 */
	public void paintMobileNodes(double[][] path) {
		if (NetTopoApp.getApp().getTimer_Mobility() == null) {
			NetTopoApp.getApp().setTimer_Mobility(new Timer());
		}
		Timer timer = NetTopoApp.getApp().getTimer_Mobility();
		for (int i = 0; i < path.length; i++) {
			timer.schedule(new PainterMobileNodeTask(path[i]), 0, NetTopoApp
					.getApp().getMobi_INTERVAL() * 1000);
		}
	}

	/**
	 * paint the districtedArea for the mobility
	 */
	public void paintDistrictedArea(DistrictedArea area) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Coordinate c = null;
			int width = 0;
			int height = 0;
			int type = 2;
			if (area != null) {
				c = area.getCenter();
				width = (int) area.getWidth();
				height = (int) area.getHeight();
				type = area.getType();
			}
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				Color color = new Color(Display.getCurrent(),
						NodeConfiguration.DistrictedAreaColor);
				gc.setForeground(color);
				gc.setLineStyle(SWT.LINE_DOT);
				if (type == 0) {
					gc.drawOval(c.x - width, c.y - height, width * 2,
							height * 2);
				} else if (type == 1) {
					gc.drawRectangle(new Rectangle(c.x - width, c.y - height,
							width * 2, height * 2));
				}

				color.dispose();
				gc.dispose();
			}
		}
	}

	/**
	 * paint more than one districtedArea
	 */
	public void paintDistrictedAreas(Vector<DistrictedArea> areas) {
		Iterator<DistrictedArea> iter = areas.iterator();
		while (iter.hasNext()) {
			DistrictedArea area = iter.next();
			// System.out.println(Arrays.toString(area.getDistrictedArea()));
			this.paintDistrictedArea(area);
		}
	}

	/**
	 * remove the districtedArea generated from the mobility
	 */
	public void removeDistrictedArea(DistrictedArea area) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Coordinate c = null;
			int width = 0;
			int height = 0;
			int type = 2;
			if (area != null) {
				c = area.getCenter();
				width = (int) area.getWidth();
				height = (int) area.getHeight();
				type = area.getType();
			}
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				Color color = new Color(Display.getCurrent(),
						NodeConfiguration.WhiteColorRGB);
				gc.setForeground(color);
				if (type == 0) {
					gc.drawOval(c.x - width, c.y - height, width * 2,
							height * 2);
				} else if (type == 1) {
					gc.drawRectangle(new Rectangle(c.x - width, c.y - height,
							width * 2, height * 2));
				}

				app.setDistrictAreaExist(false);
				color.dispose();
				gc.dispose();
			}
		}
	}

	/**
	 * remove more than one districtedArea generated from the mobility
	 */
	public void removeDistrictedAreas(Vector<DistrictedArea> areas) {
		Iterator<DistrictedArea> iter = areas.iterator();
		while (iter.hasNext()) {
			DistrictedArea area = iter.next();
			this.removeDistrictedArea(area);
		}
	}

	/**
	 * paint the attractors
	 */
	public void paintAttractors(AttractorField aFields) {
		NetTopoApp app = NetTopoApp.getApp();
		Coordinate[] c = null;
		if (aFields != null) {
			c = aFields.getCoordinates();
			synchronized (app) {
				if (c != null) {
					Image img = NetTopoApp.getApp().getBufferImage(false);
					GC gc = new GC(img);
					int attractorRadius = NodeConfiguration.AttractorRadius;
					Color color = new Color(Display.getCurrent(),
							NodeConfiguration.AttractorFieldsColor);
					gc.setBackground(color);
					for (int i = 0; i < c.length; i++) {
						gc.fillOval(c[i].x - attractorRadius, c[i].y
								- attractorRadius, 2 * attractorRadius,
								2 * attractorRadius);
					}

					gc.dispose();
				}
			}
		}
	}

	/**
	 * remove the attractors
	 */

	public void removeAttractors(AttractorField aFields) {
		NetTopoApp app = NetTopoApp.getApp();
		Coordinate[] c = null;
		if (aFields != null) {
			c = aFields.getCoordinates();
			synchronized (app) {
				if (c != null) {
					Image img = NetTopoApp.getApp().getBufferImage(false);
					GC gc = new GC(img);
					int attractorRadius = NodeConfiguration.AttractorRadius;
					Color color = new Color(Display.getCurrent(),
							NodeConfiguration.WhiteColorRGB);
					gc.setBackground(color);
					for (int i = 0; i < c.length; i++) {
						gc.fillOval(c[i].x - attractorRadius, c[i].y
								- attractorRadius, 2 * attractorRadius,
								2 * attractorRadius);
					}

					gc.dispose();
				}
			}
		}
	}

	public void paintRec(Coordinate start, Coordinate end) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(true);
			GC gc = new GC(img);
			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Collection<VNode> allNodes = wsn.getAllNodes();
			Iterator<VNode> it = allNodes.iterator();
			while (it.hasNext()) {
				VNode node = (VNode) it.next();
				int id = node.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);

				color.dispose();
			}
			Color color = new Color(Display.getCurrent(), new RGB(0, 0, 0));
			gc.setBackground(color);
			gc.drawRectangle(Math.min(start.x, end.x),
					Math.min(start.y, end.y), Math.abs(start.x - end.x),
					Math.abs(start.y - end.y));
			color.dispose();
			gc.dispose();
		}
	}

	public void paintCircle(Coordinate c1, String val) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(true);
			GC gc = new GC(img);
			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Collection<VNode> allNodes = wsn.getAllNodes();
			Iterator<VNode> it = allNodes.iterator();
			while (it.hasNext()) {
				VNode node = (VNode) it.next();
				int id = node.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);

				color.dispose();
			}
			Color color = new Color(Display.getCurrent(), new RGB(0, 0, 0));
			gc.setBackground(color);
			int r = Integer.parseInt(val);
			gc.drawOval(c1.x - r, c1.y - r, r * 2, r * 2);
			color.dispose();
			gc.dispose();
		}
	}

	@Override
	public void paintArea(List<int[]> p) {
		// TODO Auto-generated method stub
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(true);
			GC gc = new GC(img);
			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Collection<VNode> allNodes = wsn.getAllNodes();
			Iterator<VNode> it = allNodes.iterator();
			Collection<VGas> allGas = wsn.getAllGas();
			Iterator<VGas> gas_it = allGas.iterator();
			while (it.hasNext()) {
				VNode node = (VNode) it.next();
				int id = node.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}

			while (gas_it.hasNext()) {
				VGas gas = (VGas) gas_it.next();
				int id = gas.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), gas.getColor());
				gc.setBackground(color);
				int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}

			Color color = new Color(Display.getCurrent(),
					GasConfiguration.GasBoundaryColor);
			gc.setBackground(color);
			for (int[] pol : p) {
				gc.fillPolygon(pol);
			}
			color.dispose();
			gc.dispose();
		}
	}

	@Override
	public void paintGas(int id) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();

			Coordinate g_c = wsn.getCoordianteByID(id);
			Image img = NetTopoApp.getApp().getBufferImage(false);
			GC gc = new GC(img);
			VGas gas = (VGas) wsn.getGasByID(id);
			Color color = new Color(Display.getCurrent(), gas.getColor());
			gc.setBackground(color);
			if (g_c != null) {
				int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;

				gc.fillOval(g_c.x - radius, g_c.y - radius, 2 * radius,
						2 * radius);

				color.dispose();
				gc.dispose();
			}
			Collection<VNode> sensorNodes = wsn.getNodes(
					"org.deri.nettopo.node.tpgf.SensorNode_TPGF", true);
			if (sensorNodes.size() > 0) {
				for (VNode vn : sensorNodes) {
					// /for (int v = 0; v < nodes.length; v++) {
					int idv = vn.getID();
					Coordinate n_c = wsn.getCoordianteByID(idv);
					double dis_gn = n_c.distance(g_c);
					int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
					if (dis_gn < radius) {
						vn.setColor(NodeConfiguration.NodeInGasRGB);// //-------------------------mark---------------------------;

					}/*
					 * else{
					 * nodes[v].setColor(NodeConfiguration.SensorNodeColorRGB);
					 * }
					 */

				}
				color.dispose();
				gc.dispose();

			}
		}
	}

	@Override
	public void paintGas(int id, RGB rgb) {
		// TODO Auto-generated method stub
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();

			Coordinate g_c = wsn.getCoordianteByID(id);
			Image img = NetTopoApp.getApp().getBufferImage(false);
			GC gc = new GC(img);
			VGas gas = (VGas) wsn.getGasByID(id);
			Color color = new Color(Display.getCurrent(), gas.getColor());
			gc.setBackground(color);
			if (g_c != null) {

				int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
				gc.fillOval(g_c.x - radius, g_c.y - radius, 2 * radius,
						2 * radius);

				color.dispose();
				gc.dispose();
			}
			Collection<VNode> sensorNodes = wsn.getNodes(
					"org.deri.nettopo.node.tpgf.SensorNode_TPGF", true);
			if (sensorNodes.size() > 0) {
				for (VNode vn : sensorNodes) {
					// /for (int v = 0; v < nodes.length; v++) {
					int idv = vn.getID();
					Coordinate n_c = wsn.getCoordianteByID(idv);
					double dis_gn = n_c.distance(g_c);
					int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
					if (dis_gn < radius) {
						vn.setColor(NodeConfiguration.NodeInGasRGB);

					}/*
					 * else{
					 * nodes[v].setColor(NodeConfiguration.SensorNodeColorRGB);
					 * }
					 */

				}
				color.dispose();
				gc.dispose();
			}
		}
	}

	@Override
	public void paintMobileGas(double[][] path) {
		// TODO Auto-generated method stub

		if (NetTopoApp.getApp().getTimer_Mobility() == null) {
			NetTopoApp.getApp().setTimer_Mobility(new Timer());
		}
		Timer timer = NetTopoApp.getApp().getTimer_Mobility();
		for (int i = 0; i < path.length; i++) {
			timer.schedule(new PainterMobileGasTask(path[i]), 0, NetTopoApp
					.getApp().getMobi_INTERVAL() * 1000);
		}

	}

	@Override
	public void paintGasFocus(int id) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			Coordinate c = wsn.getCoordianteByID(id);
			VGas vg = wsn.getGasByID(id);
			if (c != null) {
				int gasRadius = Integer.parseInt(vg.getAttrValue("Radius"));// -------------------------mark---------------------------
				this.paintGasFocus(c, gasRadius);
			}
		}

	}

	@Override
	public void paintGasFocus(Coordinate c, int gasRadius) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			if (c != null) {
				Image img = NetTopoApp.getApp().getBufferImage(false);
				GC gc = new GC(img);
				// int gasRadius = GasConfiguration.paintGRadius;
				gc.drawRectangle(c.x - gasRadius - 1, c.y - gasRadius - 1,
						2 * (gasRadius) + 1, 2 * (gasRadius) + 1);
				gc.dispose();
			}
		}

	}

	@Override
	public void removeGas(int id) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			if (app.getCurrentSelectedGas() != null
					&& app.getCurrentSelectedGas().getID() == id) {
				app.setCurrentSelectedGas(null);

			}
			wsn.deleteGasByID(id);
			this.rePaintAllGas();
		}

	}

	@Override
	public void removeGas(Coordinate c) {
		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			int id = 0;
			int[] ids = wsn.getAllGasID();
			for (int i = 0; i < ids.length; i++) {
				if (wsn.getCoordianteByID(i).equals(c)) {
					id = i;
					this.removeGas(id);
				}
			}

		}

	}

	@Override
	public void removeGasFocus(int id) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			if (app.getCurrentSelectedGas() != null
					&& app.getCurrentSelectedGas().getID() == id) {
				app.setCurrentSelectedGas(null);
			}
			wsn.deleteGasByID(id);
			this.rePaintAllGas();
		}

	}

	@Override
	public void removeGasFocus(Coordinate c, int gasRadius) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			if (c != null) {
				Image img = app.getBufferImage(false);
				GC gc = new GC(img);
				// int gasRadius = GasConfiguration.paintGRadius;
				Color color = new Color(Display.getCurrent(),
						GasConfiguration.WhiteGColorRGB);
				gc.setForeground(color);
				gc.drawRectangle(c.x - gasRadius - 1, c.y - gasRadius - 1,
						2 * (gasRadius) + 1, 2 * (gasRadius) + 1);
				gc.dispose();
			}
		}

	}

	@Override
	public void removeAllGasFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rePaintAllGas() {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(true);
			GC gc = new GC(img);

			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Collection<VGas> allGas = wsn.getAllGas();
			Iterator<VGas> it = allGas.iterator();
			while (it.hasNext()) {
				VGas gas = (VGas) it.next();
				int id = gas.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), gas.getColor());
				gc.setBackground(color);
				int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();

				Collection<VNode> sensorNodes = wsn.getAllNodes();
				if (sensorNodes.size() > 0) {
					for (VNode vn : sensorNodes) {
						// /for (int v = 0; v < nodes.length; v++) {
						if (!(vn instanceof SensorNode))
							continue;
						int idv = vn.getID();
						Coordinate n_c = wsn.getCoordianteByID(idv);
						double dis_gn = n_c.distance(c);
						if (dis_gn < radius) {
							vn.setColor(NodeConfiguration.NodeInGasRGB);// //-------------------------mark---------------------------;

						}/*
						 * else{
						 * nodes[v].setColor(NodeConfiguration.SensorNodeColorRGB
						 * ); }
						 */

					}
				}
			}
			gc.dispose();
		}

	}

	@Override
	public void rePaint() {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			Image img = app.getBufferImage(true);
			GC gc = new GC(img);

			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Collection<VNode> allNodes = wsn.getAllNodes();
			Iterator<VNode> node_it = allNodes.iterator();
			Collection<VGas> allGas = wsn.getAllGas();
			Iterator<VGas> gas_it = allGas.iterator();
			while (gas_it.hasNext()) {
				VGas gas = (VGas) gas_it.next();
				int id = gas.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), gas.getColor());
				gc.setBackground(color);
				int radius = Integer.parseInt(gas.getAttrValue("Radius"));// -------------------------mark---------------------------;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}
			while (node_it.hasNext()) {
				VNode node = (VNode) node_it.next();
				int id = node.getID();
				Coordinate c = wsn.getCoordianteByID(id);
				Color color = new Color(Display.getCurrent(), node.getColor());
				gc.setBackground(color);
				int radius = NodeConfiguration.paintRadius;
				gc.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
				color.dispose();
			}
			gc.dispose();
		}

	}

	@Override
	public void paintBoundary() {

		Collection<VGas> allGas = NetTopoApp.getApp().getNetwork().getAllGas();
		Iterator<VGas> Iter = allGas.iterator();

		while (Iter.hasNext()) {
			int id = Iter.next().getID();
			this.paintOneBoundary(id);
		}

	}

	private void paintOneBoundary(int id) {
		// TODO Auto-generated method stub

		NetTopoApp app = NetTopoApp.getApp();
		synchronized (app) {
			WirelessSensorNetwork wsn = app.getNetwork();
			Coordinate g_c = wsn.getCoordianteByID(id);
			int radius = Integer.parseInt(wsn.getGasByID(id).getAttrValue(
					"Radius"));

			Image img = NetTopoApp.getApp().getBufferImage(false);
			GC gc = new GC(img);
			Color color = new Color(Display.getCurrent(),
					NodeConfiguration.ColorOfBoundaryLine);
			gc.setForeground(color);
			if (g_c != null) {
				gc.drawOval(g_c.x - radius, g_c.y - radius, 2 * radius,
						2 * radius);
				color.dispose();
				gc.dispose();
			}
		}
	}

	@Override
	public void paintAllGas() {
		Collection<VGas> allGas = NetTopoApp.getApp().getNetwork().getAllGas();
		Iterator<VGas> Iter = allGas.iterator();
		while (Iter.hasNext()) {
			int id = Iter.next().getID();
			this.paintGas(id);
		}
	}

}
