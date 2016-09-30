package org.deri.nettopo.model.house;



import javax.media.j3d.*;
import javax.swing.JOptionPane;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import javax.vecmath.AxisAngle4f;

import java.awt.event.KeyEvent;
import java.awt.AWTEvent;
import java.applet.Applet;
import java.util.Enumeration;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;

import java.util.*;

/**
 * House class encapsulate the content branch scene graph which is composed of
 * background node, wall segment, floor, ball and light.
 * 
 * @author
 * 
 */
public class House {

	/**
	 * A data structure containing the row index and column index of the grid in
	 * the labyrinth
	 * 
	 * @author
	 * 
	 */
	class LocationIndex {
		int xIndex = 0;

		int yIndex = 0;
	}

	/* The length of labyrinth side */
	private static final float SIDE_LEN = 1.4f;

	/* The height of the labyrinth wall */
	private static final float WALL_HEIGHT = 0.24f;

	/* The width of the labyrinth wall */
	private static final float WALL_WIDTH = 0.01f;

	/* The height of the labyrinth floor */
	private static final float FLOOR_HEIGHT = 0.005f;

	/* Content branch scene graph */
	private BranchGroup bg_labyrinth = null;

	/* Structure of the labyrinth. */
	private HouseStructure structure = null;

	/* The grid numbers of each side of labyrinth */
	private int gridBase;

	/* The grid length */
	private float gridLen;

	/* Sensor node raius */
	private float ballRadius;

	private Applet labyrinthApp;

	/* node - location map<TransformGroup, Vector3f> */
	private Map<TransformGroup,Point3f> nodesLoc;

	/*
	 * The curent location of the ball which is described as index of grid row
	 * and column
	 */
	LocationIndex ballLoc;

	/**
	 * Constructor method
	 * 
	 * @param applet
	 *            Caller of this class
	 */
	public House(Applet applet) {
		bg_labyrinth = new BranchGroup();

		labyrinthApp = applet;
		structure = HouseStrctureFactory.getStructure(1);
		gridBase = structure.getGridBase();
		gridLen = SIDE_LEN / gridBase;
		ballRadius = (gridLen - WALL_WIDTH) / 4;
		ballLoc = new LocationIndex();
		ballLoc.xIndex = 9;
		ballLoc.yIndex = 0;
		nodesLoc = new HashMap<TransformGroup,Point3f>();

		createBackGround();
		createLight();
		SharedGroup wallSegment = createWallSegment();
		createWalls(wallSegment);
		createFloor();

		/* Set the sensor nodes' location */
		Point3f p1 = getLocationByIndex(0, 0);
		Point3f p2 = getLocationByIndex(0, 9);
		Point3f p3 = getLocationByIndex(3, 5);
		Point3f p4 = getLocationByIndex(5, 0);
		Point3f p5 = getLocationByIndex(7, 9);
		Point3f p6 = getLocationByIndex(8, 5);
		//Point3f p7 = getLocationByIndex(9, 0);
		//Point3f p7 = getLocationByIndex(6, 3);
		Point3f p7 = getLocationByIndex(4, 8);

		/* Create sensor nodes and put them in a specific position */
		TransformGroup tg_sink = createSinkNode(p7);

		/* Add a behavior to a sensor node */
		NodeMoveBehavior behavior = new NodeMoveBehavior(tg_sink);
		behavior.setSchedulingBounds(new BoundingSphere());
		bg_labyrinth.addChild(behavior);

		/* Create dash lines to indicate the connection between sensor nodes */
//		Shape3D line_1_4 = createLine(p1, p4);
//		Shape3D line_2_3 = createLine(p2, p3);
//		Shape3D line_3_4 = createLine(p3, p4);
//		
//		Shape3D line_4_7 = createLine(p4, p7);
//		Shape3D line_5_6 = createLine(p5, p6);
//		Shape3D line_6_7 = createLine(p6, p7);
		
		Shape3D line_1_4 = createLine(p1, p3);
		Shape3D line_2_3 = createLine(p2, p7);
		Shape3D line_3_4 = createLine(p3, p7);
		
		Shape3D line_4_7 = createLine(p4, p3);
		Shape3D line_5_6 = createLine(p5, p7);
		Shape3D line_6_7 = createLine(p6, p5);

		/* Add lines to root */
		bg_labyrinth.addChild(line_1_4);
		bg_labyrinth.addChild(line_2_3);
		bg_labyrinth.addChild(line_3_4);
		bg_labyrinth.addChild(line_4_7);
		bg_labyrinth.addChild(line_5_6);
		bg_labyrinth.addChild(line_6_7);

	}

	/**
	 * get the branch groupd scene graph
	 * 
	 * @return
	 */
	public BranchGroup getBG() {
		bg_labyrinth.compile();
		return bg_labyrinth;
	}

	/**
	 * Get the current location of the ball
	 * 
	 * @return Location which is desribed as a vector
	 */
	private Point3f getLocationByIndex(int xIndex, int yIndex) {
		float x = -(gridBase / 2.0f - 0.5f) * gridLen + yIndex * gridLen;
		float y = (gridBase / 2.0f - 0.5f) * gridLen - xIndex * gridLen;
		return new Point3f(x, y, 0.0f);
	}

	/**
	 * Create the back ground node
	 * 
	 */
	private void createBackGround() {

		Background background = new Background();
		background.setApplicationBounds(new BoundingSphere(new Point3d(),
				Double.MAX_VALUE));

		BranchGroup backgroundGroup = new BranchGroup();
		// Set the texture for the background appearance
		Appearance backgroundAppearance = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(0.85f, 0.84f, 0.8f,
				ColoringAttributes.SHADE_FLAT);
		backgroundAppearance.setColoringAttributes(ca);

		// Create a detailed sphere with normal pointing inwards
		Sphere backgroundSphere = new Sphere(
				0.5f,
				Sphere.GENERATE_TEXTURE_COORDS | Sphere.GENERATE_NORMALS_INWARD,
				10, backgroundAppearance);
		backgroundGroup.addChild(backgroundSphere);
		background.setGeometry(backgroundGroup);

		bg_labyrinth.addChild(background);
	}

	/**
	 * create wall segment node which can be shared by other group nodes
	 * 
	 * @return
	 */
	private SharedGroup createWallSegment() {
		Appearance appearance = new Appearance();
		Material material = new Material();
		material.setDiffuseColor(new Color3f(0.3f, 0.4f, 0.4f));
		material.setAmbientColor(new Color3f(0.547f, 0.746f, 0.787f));
		material.setSpecularColor(new Color3f(0.5f, 0.5f, 0.5f));
		appearance.setMaterial(material);

		Box box = new Box(gridLen / 2, WALL_WIDTH / 2, WALL_HEIGHT / 2,
				Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS, appearance);
		SharedGroup sharedGroup = new SharedGroup();
		sharedGroup.addChild(box);

		// sharedGroup.addChild(light);

		return sharedGroup;
	}

	/**
	 * Wall is comopsed of multiple wall segments
	 * 
	 * @param wallSegment
	 */
	private void createWalls(SharedGroup wallSegment) {
		/* create horizontal walls */
		boolean horizontal[] = structure.getHorizontalSegments();
		for (int i = 0; i < horizontal.length; i++) {
			if (horizontal[i]) {
				int rowIndex = i / gridBase;
				int colIndex = i % gridBase;
				/* Compute the x and y coordinates of the offset */
				float x = -((gridBase / 2.0f - 0.5f) * gridLen) + colIndex
						* gridLen;
				float y = (gridBase / 2.0f) * gridLen - rowIndex * gridLen;

				Transform3D transform = new Transform3D();
				transform.setTranslation(new Vector3f(x, y, 0.0f));

				TransformGroup tg = new TransformGroup(transform);
				Link link = new Link();
				link.setSharedGroup(wallSegment);
				tg.addChild(link);
				bg_labyrinth.addChild(tg);
			}
		}

		/* create vertical walls */
		boolean vertical[] = structure.getVerticalSegements();
		for (int i = 0; i < vertical.length; i++) {
			if (vertical[i]) {
				int rowIndex = i % gridBase;
				int colIndex = i / gridBase;
				/* Compute the x and y coordinates of the offset */
				float x = -(gridBase / 2.0f) * gridLen + colIndex * gridLen;
				float y = (gridBase / 2.0f - 0.5f) * gridLen - rowIndex
						* gridLen;

				Transform3D transform = new Transform3D();
				transform.setRotation(new AxisAngle4f(0.0f, 0.0f, 1.0f,
						(float) (Math.PI / 2.0)));
				transform.setTranslation(new Vector3f(x, y, 0.0f));

				TransformGroup tg = new TransformGroup(transform);
				Link link = new Link();
				link.setSharedGroup(wallSegment);
				tg.addChild(link);
				bg_labyrinth.addChild(tg);
			}
		}
	}

	private void createFloor() {
		/* Defines the 8 vertices for the floor of the labyrinth */
		float[] coordinates = { -SIDE_LEN / 2, SIDE_LEN / 2, -WALL_HEIGHT / 2,
				SIDE_LEN / 2, SIDE_LEN / 2, -WALL_HEIGHT / 2, SIDE_LEN / 2,
				-SIDE_LEN / 2, -WALL_HEIGHT / 2, -SIDE_LEN / 2, -SIDE_LEN / 2,
				-WALL_HEIGHT / 2, -SIDE_LEN / 2, SIDE_LEN / 2,
				-WALL_HEIGHT / 2 - FLOOR_HEIGHT, SIDE_LEN / 2, SIDE_LEN / 2,
				-WALL_HEIGHT / 2 - FLOOR_HEIGHT, SIDE_LEN / 2, -SIDE_LEN / 2,
				-WALL_HEIGHT / 2 - FLOOR_HEIGHT, -SIDE_LEN / 2, -SIDE_LEN / 2,
				-WALL_HEIGHT / 2 - FLOOR_HEIGHT, };
		/* Defines the 24 indices for the floor */
		int[] coordIndices = { 0, 3, 2, 1, // front face
				4, 5, 6, 7, // back face
				4, 7, 3, 0, // left face
				1, 2, 6, 5, // right face
				4, 0, 1, 5, // top face
				7, 6, 2, 3 }; // bottom face
		float[] colors = { 0.0f, 0.0f, 0.0f, 0.8f, 0.8f, 0.8f };
		int[] colorIndices = { 0, 0, 0, 1 };

		IndexedQuadArray flat = new IndexedQuadArray(8,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3, 24);
		flat.setCoordinates(0, coordinates);
		flat.setCoordinateIndices(0, coordIndices);
		flat.setColors(0, colors);
		flat.setColorIndices(0, colorIndices);

		Appearance appearance = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(0.0f, 0.0f, 0.0f,
				ColoringAttributes.SHADE_FLAT);
		appearance.setColoringAttributes(ca);

		Shape3D floor = new Shape3D(flat, appearance);

		bg_labyrinth.addChild(floor);

	}

	private TransformGroup createSinkNode(Point3f place) {
		Appearance appearance = new Appearance();
		Material material = new Material();
		material.setDiffuseColor(new Color3f(0.3f, 0.4f, 0.3f));
		material.setAmbientColor(new Color3f(0.8f, 0.1f, 0.1f));
		material.setSpecularColor(new Color3f(0.2f, 0.2f, 0.2f));
		material.setShininess(10);
		appearance.setMaterial(material);

		Sphere node = new Sphere(ballRadius, Sphere.GENERATE_NORMALS
				| Sphere.GENERATE_TEXTURE_COORDS, appearance);

		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3f(place.x, place.y, place.z));
		TransformGroup tg = new TransformGroup(transform);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.addChild(node);
		bg_labyrinth.addChild(tg);
		nodesLoc.put(tg, place);
		return tg;
	}

	private Shape3D createLine(Point3f p1, Point3f p2) {
		LineArray line = new LineArray(2, LineArray.COORDINATES);
		Point3f[] points = { p1, p2 }; // where these are the points you want
		// to draw between
		line.setCoordinates(0, points);
		Appearance lineAppearance = new Appearance();
		LineAttributes lineAttrib = new LineAttributes();
		lineAttrib.setLinePattern(LineAttributes.PATTERN_DASH);
		// lineAttrib.setLineAntialiasingEnable(true);
		// lineAttrib.setLineWidth(2f);
		lineAppearance.setLineAttributes(lineAttrib);
		ColoringAttributes colorAttrib = new ColoringAttributes(new Color3f(
				1.0f, 0.0f, 0.0f), ColoringAttributes.SHADE_FLAT);
		lineAppearance.setColoringAttributes(colorAttrib);
		Shape3D lineShape = new Shape3D(line, lineAppearance);
		return lineShape;
	}

	private void createLight() {
		/* Create a directional light with a bright white colour */
		/*
		 * PointLight light = new PointLight(); light.setColor(new
		 * Color3f(1.0f,1.0f,1.0f)); light.setPosition(new Point3f(0.0f, 0.0f,
		 * 0.5f)); light.setInfluencingBounds(new BoundingSphere(new Point3d(),
		 * Double.MAX_VALUE));
		 */
		AmbientLight light = new AmbientLight(new Color3f(0.4f, 0.4f, 0.4f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(),
				Double.MAX_VALUE));

		PointLight light2 = new PointLight();
		light2.setColor(new Color3f(0.8f, 0.8f, 0.8f));
		light2.setPosition(new Point3f(0.0f, 0.0f, 0.5f));
		light2.setInfluencingBounds(new BoundingSphere(new Point3d(),
				Double.MAX_VALUE));

		DirectionalLight light3 = new DirectionalLight(new Color3f(1.0f, 1.0f,
				1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
		light3.setInfluencingBounds(new BoundingSphere(new Point3d(),
				Double.MAX_VALUE));

		bg_labyrinth.addChild(light);
		bg_labyrinth.addChild(light2);
		// bg_labyrinth.addChild(light3);
	}

	/**
	 * Behavior node which acts as a key listener. When the navigation key is
	 * pressed, the ball can be moved correspondingly.
	 * 
	 * @author wupure
	 * 
	 */
	public class NodeMoveBehavior extends Behavior {
		private static final int LEFT = 37; // key code of left

		private static final int UP = 38; // key code of up

		private static final int RIGHT = 39; // key code of right

		private static final int DOWN = 40; // key code of down

		private TransformGroup tg;

		private Transform3D moveTrans = new Transform3D();

		public NodeMoveBehavior(TransformGroup tg) {
			setTransform(tg);
		}

		public void setTransform(TransformGroup tg) {
			this.tg = tg;
		}

		public void initialize() {
			// set initial wakeup condition
			this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
		}

		public void processStimulus(@SuppressWarnings("rawtypes") Enumeration criteria) {
			// decode event
			if (!criteria.hasMoreElements())
				return;
			WakeupOnAWTEvent condition = (WakeupOnAWTEvent) criteria.nextElement();
			AWTEvent[] events = condition.getAWTEvent();
			if (events.length <= 0)
				return;
			KeyEvent keyEvent = (KeyEvent) events[0];
			int keyCode = keyEvent.getKeyCode();

			moveBall(ballLoc, keyCode);

		}

		private void moveBall(LocationIndex loc, int direction) {
			int leftIndex = loc.yIndex * gridBase + loc.xIndex;
			int rightIndex = (loc.yIndex + 1) * gridBase + loc.xIndex;
			int upIndex = (loc.xIndex) * gridBase + loc.yIndex;
			int downIndex = (loc.xIndex + 1) * gridBase + loc.yIndex;
			boolean[] horizontal = structure.getHorizontalSegments();
			boolean[] vertical = structure.getVerticalSegements();
			switch (direction) {
			case LEFT:
				if (!vertical[leftIndex])
					loc.yIndex--;
				break;
			case UP:
				if (!horizontal[upIndex])
					loc.xIndex--;
				break;
			case RIGHT:
				if (!vertical[rightIndex])
					loc.yIndex++;
				break;
			case DOWN:
				if (!horizontal[downIndex])
					loc.xIndex++;
				break;
			}

			if (loc.yIndex >= gridBase) { // escape the labyrinth, win
				// show congratuation hint message
				javax.swing.JOptionPane.showMessageDialog(labyrinthApp,
						"You successfully escaped the labyrinth.",
						"Congratulations", JOptionPane.INFORMATION_MESSAGE);
				loc.xIndex = 0;
				loc.yIndex = 0;
			}
			Point3f p = getLocationByIndex(ballLoc.xIndex, ballLoc.yIndex);
			moveTrans.setTranslation(new Vector3f(p.x, p.y, p.z));
			tg.setTransform(moveTrans);
			this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
		}
	}

}
