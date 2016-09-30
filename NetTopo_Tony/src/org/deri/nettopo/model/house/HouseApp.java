package org.deri.nettopo.model.house;

import javax.swing.JFrame;
import java.applet.Applet;

import javax.vecmath.Point3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.applet.JMainFrame;

import javax.media.j3d.*;

/**
 * It is designed as a subclass of applet. The main entry is located in.
 * 
 * @author 
 * 
 */
public class HouseApp extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HouseApp() {
		setLayout(new BorderLayout());

		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();

		// Create a Canvas3D object and add it to the frame
		Canvas3D canvas = new Canvas3D(config);
		add(canvas, BorderLayout.CENTER);

		// Create a SimpleUniverse object to mange the "view" branch
		SimpleUniverse u = new SimpleUniverse(canvas);
		u.getViewingPlatform().setNominalViewingTransform();

		// Create the "content" branch
		BranchGroup bg_root = new BranchGroup();

		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		BranchGroup labyrinth = new House(this).getBG();
		tg.addChild(labyrinth);

		/* enbales mouse rotate */
		MouseRotate rotate = new MouseRotate();
		rotate.setTransformGroup(tg);
		tg.addChild(rotate);
		rotate.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));

		/* enbales mouse zoom */
		MouseZoom zoom = new MouseZoom();
		zoom.setTransformGroup(tg);
		tg.addChild(zoom);
		zoom.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));

		/* enbales mouse translate */
		MouseTranslate translate = new MouseTranslate();
		translate.setTransformGroup(tg);
		tg.addChild(translate);
		translate
				.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));

		bg_root.addChild(tg);
		bg_root.compile();

		u.addBranchGraph(bg_root);
	}

	/**
	 * main entry of the whole application
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JMainFrame(new HouseApp(), 800, 800);
		frame.setTitle("Wireless Sensor Network 3D Modeling");
	}
}
