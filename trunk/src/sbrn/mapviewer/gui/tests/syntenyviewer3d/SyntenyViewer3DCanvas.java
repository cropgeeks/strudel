package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.tests.mainGui.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import com.sun.j3d.utils.pickfast.behaviors.*;
import com.sun.j3d.utils.universe.*;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 * 
 * A 3D chromosome viewer.
 */
public class SyntenyViewer3DCanvas extends Canvas3D
{

	// ==================================vars=============================

	// infrastructure
	private SimpleUniverse su = null;
	private BranchGroup scene = null;

	// array of colours used to colour the lines of links
	private Color3f[] colours = null;

	// data files
	private File referenceData;
	private File targetData;
	private File compData;
	private File[] otherMapFiles;

	// this Mapset holds all the data we want to compare against
	private MapSet referenceMapset = null;
	// this Mapset holds the data we want to find out about
	private MapSet targetMapset = null;
	// this link set holds the all the possible links between all chromos in the tagret set and all chromos in the reference set
	private LinkSet links = null;
	// a subset of all links which contains only those links that involve the central chromosome
	private LinkSet centralChromoLinkSet = null;
	// this array holds subsets of the centralChromoLinkSet which represent the sets of links between
	// the central chromo and each of the peripheral chromos
	private LinkSet[] linkSubsets = null;
	// the central chromosome in the standing stone circle arrangement
	private ChromoMap centralChromo = null;
	// the index of the central chromosome in the target data map set
	// hard code the central chromo for now
	private int centralChromoIndex = 0;

	// cylinder metrics
	private final float cylinderRadius = 0.05f;
	private final float cylinderHeight = 1.0f;
	// this is the radius of the circle the cylinders are to be placed in
	private final float circleRadius = 2.0f;
	// the positions of the peripheral cylinders only (the central cylinder is always at 0,0,0)
	public float[][] cylinderPositions = null;

	// this is the initial position of the camera/viewer's eye
	//a rotation will be applied to this position to create the correct viewing angle so the user looks down onto the object
//	private Point3f initialViewPoint = new Point3f(0.2f, 5.0f, 4.0f);
//	int viewingAngle = -55;
	private Point3f initialViewPoint = new Point3f(0.0f, 1.0f, 7.0f);
	int viewingAngle = 0;

	// object for adding lights etc
	private Decorator dec = null;

	// this is the root of the object part of the scene
	private BranchGroup objRoot = null;
	// this holds all the objects
	private TransformGroup wholeObj = null;

	// this holds all the rotation interpolators for the cylinders
	private RotationInterpolator[] interpolators = null;

	// this holds the TGs that contain the peripheral (reference) chromosomes
	private TransformGroup[] peripheralCylTGs = null;
	// same for the central chromo
	private TransformGroup centralChromoTG = null;

	// this hashtable will take cylinder objects (marker rings) as keys and the corresponding label for each marker as value
	// we have a single hashtable for all the markers in this particular view of the application
	private Hashtable markerNamesHashT = new Hashtable();
	private Hashtable markerPositionsHashT = new Hashtable();

	// holds the arrays of links between the central chromo and each peripheral one
	private Shape3D[] linkArrays = null;
	// indicates whether the link arrays are inverted or not
	private boolean[] linksInverted = null;

	MapViewerFrame frame;

	BranchGroup chromoLabels;
	LinkedList<BranchGroup> markerBranchGroups;
	LinkedList<BranchGroup> linkBranchGroups;

	// ==================================c'tor=============================

	public SyntenyViewer3DCanvas(MapViewerFrame frame, File referenceData, File targetData, File compData,
					File[] otherMapFiles, MapSet referenceMapset, MapSet targetMapset,
					int centralChromoIndex, LinkSet links)
	{
		super(getGraphicsConfig());

		this.frame = frame;
		this.referenceData = referenceData;
		this.targetData = targetData;
		this.compData = compData;
		this.otherMapFiles = otherMapFiles;
		this.referenceMapset = referenceMapset;
		this.targetMapset = targetMapset;
		this.centralChromoIndex = centralChromoIndex;
		this.links = links;

		loadData();

		int numChromos = referenceMapset.size();
		// indicates whether the link arrays are inverted or not
		linksInverted = new boolean[numChromos];
		dec = new Decorator();
		colours = dec.generateColours(numChromos);
		su = new SimpleUniverse(this);
		scene = createSceneGraph();
		su.addBranchGraph(scene);
	}

	// ==================================methods=============================
	/**
	 * Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	 */
	private void loadData()
	{
		try
		{
			// the chromosome positioned in the centre of the stone circle arrangement
			centralChromo = targetMapset.getMap(centralChromoIndex);
			// now work out which of the links we actually need for this particular central chromo
			centralChromoLinkSet = LinkSetManager.makeCentralChromoLinkSet(links, centralChromo);
			// make a new array for the sets of links between the central chromo and the peripheral ones
			linkSubsets = new LinkSet[referenceMapset.size()];

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a scene graph and returns it in the shape of a single BranchGroup object
	 */
	private BranchGroup createSceneGraph()
	{
		System.out.println("creating scene graph for 3D view");

		objRoot = new BranchGroup();
		wholeObj = new TransformGroup();

		// set the appropriate capabilities
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objRoot.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
		wholeObj.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		wholeObj.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wholeObj.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wholeObj.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		wholeObj.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

		try
		{
			//addWhiteBackground();
			
			// this creates an ambient plus a directional light source to provide some shading
			dec.createLights(objRoot);

			// position the central cylinder and the peripheral ones
			makeCylinders();

			// this places a label on each chromosome with its name
			createChromoLabels();

			// this creates the links between chromosomes
			createLinks();

			// this creates rings on each chromosome that represent features
			createMarkers();

			// add the whole Object to the root
			objRoot.addChild(wholeObj);

			BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);

			// this allows us to set the initial camera view point
			Vector3f translate = new Vector3f();
			Transform3D T3D = new Transform3D();
			TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();
			translate.set(initialViewPoint);
			T3D.rotX(Math.toRadians(viewingAngle));
			//T3D.rotY(viewingAngle);
			T3D.setTranslation(translate);				
			vpTrans.setTransform(T3D);

			// now add the various behaviours
			// rotation of the whole stone circle object
			PickRotateBehavior behavior1 = new PickRotateBehavior(objRoot, this, bounds);
			behavior1.setTolerance(50.0f);
			objRoot.addChild(behavior1);

			// zooming of the whole stone circle object
			PickZoomBehavior behavior2 = new PickZoomBehavior(objRoot, this, bounds);
			objRoot.addChild(behavior2);

			// sideways translation of the whole stone circle object
			PickTranslateBehavior behavior3 = new PickTranslateBehavior(objRoot, this, bounds);
			objRoot.addChild(behavior3);

			// this behaviour listens for mouse clicks that invert single cylinders/chromosomes
			InvertRequestDetector ird = new InvertRequestDetector(this, objRoot, interpolators, peripheralCylTGs);
			ird.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
			objRoot.addChild(ird);

			// this behaviour listens for mouseover on marker rings and then highlights them and pops up a label with the name of the marker
			MouseOverBehavior mo_behavior = new MouseOverBehavior(this, markerNamesHashT, markerPositionsHashT, wholeObj, objRoot, peripheralCylTGs);
			mo_behavior.setSchedulingBounds(bounds);
			objRoot.addChild(mo_behavior);

			// Let Java 3D perform optimizations on this scene graph.
			objRoot.compile();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return objRoot;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Makes a LineArray object that corresponds to the set of links between the central chromo and the peripheral chromo at index chromosomeIndex in the array of peripheral chromos
	 */
	public LineArray makeLineArray(int chromosomeIndex, boolean invert)
	{
		// work out the marker positions and draw the links
		LinkSet subSet = LinkSetManager.makeSpecificLinkSet(chromosomeIndex, centralChromoLinkSet,
						referenceMapset, linkSubsets);
		float[][] markerPositions = LinkSetManager.calcLinkPositions(invert, subSet, centralChromo,
						cylinderHeight);
		LineArray linkArray = LinkCreator.createLinks(cylinderPositions, markerPositions, chromosomeIndex,
						colours[chromosomeIndex]);
		return linkArray;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	// this creates the links between chromosomes
	private void createLinks()
	{
		linkBranchGroups = new LinkedList<BranchGroup>();
		// holds the arrays of links between the central chromo and each peripheral one
		linkArrays = new Shape3D[referenceMapset.size()];

		// for each combination of the central chromosome and a peripheral one
		for (int i = 0; i < referenceMapset.size(); i++)
		{
			// make a new line array
			LineArray linkArray = makeLineArray(i, false);
			Shape3D s3d = new Shape3D(linkArray);
			s3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

			// attach this to a new branch group so it can be detached again as needed
			BranchGroup linesBg = new BranchGroup();
			Utils.setDetachRelatedCapabilities(linesBg);
			linesBg.addChild(s3d);
			linkBranchGroups.add(linesBg);

			// add this to the graph
			wholeObj.addChild(linesBg);
			linkArrays[i] = s3d;
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Inverts all the markers of the chromosome at chromoIndex in the array of TransformGroups holding the peripheral cylinders/chromosomes
	 */
	public void invertMarkers(int chromoIndex)
	{
		MarkerRingCreator.invertMarkers(chromoIndex, this.peripheralCylTGs);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Creates rings on each chromosome that represent features (markers)
	 */
	private void createMarkers()
	{
		try
		{
			markerBranchGroups = new LinkedList<BranchGroup>();

			// central cylinder
			// create markers and attach to the existing TG that holds the cylinder
			// need to insert a branchgroup in between though so we can detach and update this
			BranchGroup centralChromoMarkersBG = new BranchGroup();
			Utils.setDetachRelatedCapabilities(centralChromoMarkersBG);
			TransformGroup centralChromoMarkersTG = MarkerRingCreator.createSpecificMarkerSet(
							centralChromo, markerNamesHashT, markerPositionsHashT, 0.0f,
							0.0f, cylinderRadius, cylinderHeight, centralChromoLinkSet,
							true, false);
			Utils.setChildRelatedCapabilities(centralChromoMarkersTG);
			centralChromoMarkersBG.addChild(centralChromoMarkersTG);
			centralChromoTG.addChild(centralChromoMarkersBG);
			markerBranchGroups.add(centralChromoMarkersBG);

			// iterate over the reference mapset (i.e. the peripheral chromos)
			ChromoMap chromoMap = null;
			TransformGroup rotateTG = null;
			for (int i = 0; i < referenceMapset.size(); i++)
			{
				chromoMap = referenceMapset.getMap(i);
				// create markers and attach to the existing TG that holds the cylinder
				// need to insert a branchgroup in between though so we can detach and update this
				rotateTG = (TransformGroup) peripheralCylTGs[i].getChild(0);
				BranchGroup peripheralChromoBG = new BranchGroup();
				Utils.setDetachRelatedCapabilities(peripheralChromoBG);
				TransformGroup peripheralChromoMarkersTG = MarkerRingCreator.createSpecificMarkerSet(
								chromoMap, markerNamesHashT, markerPositionsHashT,
								cylinderPositions[i][0], cylinderPositions[i][2],
								cylinderRadius, cylinderHeight, linkSubsets[i], false,
								linksInverted[i]);
				Utils.setChildRelatedCapabilities(peripheralChromoMarkersTG);
				peripheralChromoBG.addChild(peripheralChromoMarkersTG);
				rotateTG.addChild(peripheralChromoBG);
				markerBranchGroups.add(peripheralChromoBG);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Places a label on each chromosome with its name
	 */
	private void createChromoLabels()
	{
		chromoLabels = new BranchGroup();
		chromoLabels.setCapability(BranchGroup.ALLOW_DETACH);
		chromoLabels.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		chromoLabels.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		TransformGroup chromoLabelsTG = new TransformGroup();

		// objects for the chromosome labels
		Vector3f chrLabelvec = new Vector3f();
		Transform3D chrLabelTransf = new Transform3D();
		ChromoMap chromoMap = null;

		// place the label for the central cylinder
		chrLabelvec.set(0.0f, 0.0f, 0.0f);
		chrLabelTransf.setTranslation(chrLabelvec);
		dec.createFlagLabel(centralChromo.getName(), chromoLabelsTG, chrLabelvec);

		// iterate over the reference mapset (i.e. the peripheral chromos)
		for (int i = 0; i < referenceMapset.size(); i++)
		{
			chromoMap = referenceMapset.getMap(i);
			chrLabelvec.set(cylinderPositions[i]);
			chrLabelTransf.setTranslation(chrLabelvec);
			dec.createFlagLabel(chromoMap.getName(), chromoLabelsTG, chrLabelvec);
		}

		chromoLabels.addChild(chromoLabelsTG);
		wholeObj.addChild(chromoLabels);

	}

	// ---------------------------------------------------------------------------------------------------------------------

	public void updateView()
	{
		// set the new central chromo
		centralChromo = targetMapset.getMap(centralChromoIndex);
		System.out.println("updating view to use centralChromo " + centralChromo.getName());

		// update the link set which contains all the links from the current central chromo to the reference ones
		centralChromoLinkSet = LinkSetManager.makeCentralChromoLinkSet(links, centralChromo);

		// now update all the objects that are affected by the change of central chromo
		updateChromoLabels();
		updateLinks();
		updateMarkers();
	}

	private void updateChromoLabels()
	{
		System.out.println("updating chromosome labels in 3D view");
		chromoLabels.detach();
		createChromoLabels();
	}

	private void updateLinks()
	{
		System.out.println("updating links in 3D view");
		// iterate over the link branchgroups and detach them all
		for (BranchGroup bg : linkBranchGroups)
		{
			bg.detach();
		}
		// now recreate them
		createLinks();
	}

	private void updateMarkers()
	{
		System.out.println("updating markers in 3D view");
		// iterate over the marker branchgroups and detach them all
		for (BranchGroup bg : markerBranchGroups)
		{
			bg.detach();
		}
		// now recreate them
		createMarkers();
	}

	// ---------------------------------------------------------------------------------------------------------------------
	/**
	 * Creates the central and peripheral cylinders
	 */
	private void makeCylinders()
	{
		// make central cylinder first
		Cylinder cyl = new Cylinder(cylinderRadius, cylinderHeight);
		// this holds the position data
		Vector3f vec = new Vector3f(0.0f, 0.0f, 0.0f);
		Transform3D translate = new Transform3D();
		translate.setTranslation(vec);
		centralChromoTG = new TransformGroup(translate);
		Utils.setChildRelatedCapabilities(centralChromoTG);
		centralChromoTG.addChild(cyl);
		wholeObj.addChild(centralChromoTG);

		// make the standing stone circle of cylinders representing the chromosomes
		StoneCircleCreator stc = new StoneCircleCreator();
		cylinderPositions = stc.makeCylinderPositions(referenceMapset.size(), circleRadius);
		peripheralCylTGs = stc.makeCylinderArray(cylinderPositions, cylinderRadius, cylinderHeight, objRoot);
		for (int i = 0; i < peripheralCylTGs.length; i++)
		{
			Utils.setChildRelatedCapabilities(peripheralCylTGs[i]);
			wholeObj.addChild(peripheralCylTGs[i]);
		}
		interpolators = stc.getInterpolators();
	}

	// ---------------------------------------------------------------------------------------------------------------------	
	
	public void addWhiteBackground()
	{
		BoundingSphere boundingSphere = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		Background background = new Background(new Color3f(Color.white));
		background.setApplicationBounds(boundingSphere);
		objRoot.addChild(background);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	// get a nice graphics config
	private static GraphicsConfiguration getGraphicsConfig()
	{
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
		GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
						template);
		return gcfg;
	}

	// ---------------------------------------------------------------------------------------------------------------------
	/**
	 * Recursive method that traverses the entire scene graph and prints out all the objects attached to it
	 */
	private void describeScene(Group group, int numTabs)
	{
		Enumeration e = group.getAllChildren();

		while (e.hasMoreElements())
		{
			Object obj = e.nextElement();
			Class c = obj.getClass();
			Class superC = c.getSuperclass();

			// print appropriate number of tab characters, then the class name
			for (int i = 0; i < numTabs; i++)
			{
				System.out.print("   ");
			}
			System.out.println(c.getName());

			if (superC != null)
			{
				if (superC.getName().equals("javax.media.j3d.Group"))
				{
					numTabs++;
					describeScene((Group) obj, numTabs);
				}
			}
		}
	}

	// ==================================accessors===============================

	public void setCentralChromo(ChromoMap centralChromo)
	{
		this.centralChromo = centralChromo;
	}

	public void setCentralChromoIndex(int i)
	{
		centralChromoIndex = i;
	}

	public Shape3D[] getLinkArrays()
	{
		return linkArrays;
	}

	public void setLinkArrays(Shape3D[] linkArrays)
	{
		this.linkArrays = linkArrays;
	}

	public boolean[] getLinksInverted()
	{
		return linksInverted;
	}

	public void setLinksInverted(boolean[] linksInverted)
	{
		this.linksInverted = linksInverted;
	}

	public TransformGroup[] getPeripheralCylTGs()
	{
		return peripheralCylTGs;
	}

	public TransformGroup getCentralChromoTG()
	{
		return centralChromoTG;
	}

	// ---------------------------------------------------------------------------------------------------------------------

} // end of class

