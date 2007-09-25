package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import sbrn.mapviewer.data.ChromoMap;
import sbrn.mapviewer.data.LinkSet;
import sbrn.mapviewer.data.MapSet;
import sbrn.mapviewer.io.CMapLinkImporter;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickZoomBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;


/**
 * @author Micha Bayer, Scottish Crop Research Institute
 *
 *A 3D chromosome viewer. 
 */
public class SyntenyViewer3D extends Applet
{

//	==================================vars=============================	
	
	//window metrics
	private static int windowWidth = 1200;
	private static int windowHeight = 800;
	
	//infrastructure
	private Canvas3D canvas = null;
	private SimpleUniverse su = null;
	private BranchGroup scene = null;
	
	//array of colours used to colour the lines of links
	private Color3f [] colours = null;
	
	//data files
	private File referenceData = new File("E:/cmap_files/raw data/rice_pseudo4_os.maps");
	private File targetData = new File("E:/cmap_files/raw data/new_sxm_edited.maps");
	private File compData = new File("E:/cmap_files/raw data/barley_SNPS_vs_rice4_corr.data");
	private File [] otherMapFiles = new File []{new File("E:/cmap_files/raw data/new_owb_edited.maps"),
		new File("E:/cmap_files/raw data/new_mxb_edited.maps")};
	
	//this Mapset holds all the data we want to compare against
	private MapSet referenceMapset = null;
	//this Mapset holds the data we want to find out about
	private MapSet targetMapset = null;
	//this link set holds the all the possible links between all chromos in the tagret set and all chromos in the reference set
	private LinkSet links = null;
	
	//the central chromosome in the standing stone circle arrangement
	private ChromoMap centralChromo = null;
	
	//the index of the central chromosome in the target data map set
	//hard code the central chromo for now
	private int centralChromoIndex = 5;
	
	//cylinder metrics
	private final float cylinderRadius = 0.05f;
	private final float cylinderHeight = 1.0f;	
	// this is the radius of the circle the cylinders are to be placed in
	private final float circleRadius =2.0f;	
	//the positions of the peripheral cylinders only (the central cylinder is always at 0,0,0)
	public float [][]  cylinderPositions = null;
	
	//this is the initial position of the camera/viewer's eye
	private Point3f initialViewPoint = new Point3f(0.0f, 1.0f, 9.0f);
	
	//object for adding lights etc
	private Decorator dec = null;
	
//	this is the root of the object part of the scene
	private BranchGroup objRoot = null;
	//this holds all the objects
	private TransformGroup wholeObj = null;
	
	//this holds all the rotation interpolators for the cylinders
	private RotationInterpolator [] interpolators = null;
	
	//this holds the TGs that contain the peripheral (reference) chromosomes
	private TransformGroup [] peripheralCylTGs = null;
	//same for the central chromo
	private TransformGroup centralChromoTG = null;
	
	//this hashtable will take cylinder objects (marker rings) as keys and the corresponding label for each marker as value
	//we have a single hashtable for all the markers in this particular view of the application
	private Hashtable markerNamesHashT = new Hashtable();
	private Hashtable markerPositionsHashT = new Hashtable();
	
	//holds the arrays of links between the central chromo and each peripheral one
	private Shape3D [] linkArrays = null;
	//indicates whether the link arrays are inverted or not
	private boolean [] linksInverted = null;

	//a subset of all links which contains only those links that involve the central chromosome
	private LinkSet centralChromoLinkSet = null;
	//this array holds subsets of the centralChromoLinkSet which represent the sets of links between
	//the central chromo and each of the peripheral chromos
	private LinkSet [] linkSubsets = null;
	
//==================================c'tor=============================	
	
	public SyntenyViewer3D()
	{
		loadData();
		int numChromos = referenceMapset.size();
		//holds the arrays of links between the central chromo and each peripheral one
		linkArrays = new Shape3D [numChromos];
		//indicates whether the link arrays are inverted or not
		linksInverted = new boolean [numChromos];
		dec = new Decorator();
		colours = dec.generateColours();		
		setLayout(new BorderLayout());
		canvas = new Canvas3D(getGraphicsConfig());
		add("Center", canvas);		
		su = new SimpleUniverse(canvas);
		scene = createSceneGraph();
		su.addBranchGraph(scene);
		
		//System.out.println("\n\ndescribing group structure:\n");
		//describeScene(scene,0);
	}
	
//	==================================methods=============================	
	
	public static void main(String[] args)
	{
		new MainFrame(new SyntenyViewer3D(),windowWidth, windowHeight);
	}


//	---------------------------------------------------------------------------------------------------------------------	
	/**
	 * Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	 */ 
	private void loadData()
	{
		try
		{
			DataLoader dLoader = new DataLoader();
			referenceMapset =  dLoader.loadMapData(referenceData);
			targetMapset =  dLoader.loadMapData(targetData);
			
			CMapLinkImporter limp = new CMapLinkImporter(compData);
			limp.addMapSet(referenceMapset);
			limp.addMapSet(targetMapset);
			
			MapSet [] otherMapSets = dLoader.loadOtherMapSets(otherMapFiles);
			for(int i = 0; i< otherMapSets.length; i++)
			{
				limp.addMapSet(otherMapSets[i]);
			}
			
			try
			{
				links = limp.loadLinkSet();
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}

			centralChromo = targetMapset.getMap(centralChromoIndex);
			
			//now work out which of the links we actually need for this particular central chromo
			centralChromoLinkSet = LinkSetManager.makeCentralChromoLinkSet(links,centralChromo);
			
			//make a new array for the sets of links between the central chromo and the peripheral ones
			linkSubsets = new LinkSet [referenceMapset.size()];
			
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}
	
//	---------------------------------------------------------------------------------------------------------------------		
	
	/**
	 * Creates a scene graph and returns it in the shape of a single BranchGroup object
	 */
	private BranchGroup createSceneGraph()
	{
		objRoot = new BranchGroup();
		wholeObj = new TransformGroup();
		
		//set the appropriate capabilities
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);	
		objRoot.setCapability(BranchGroup.ENABLE_PICK_REPORTING);		
		wholeObj.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		wholeObj.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wholeObj.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wholeObj.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		wholeObj.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		
		try
		{
			// this creates an ambient plus a directional light source to provide some shading
			dec.createLights(objRoot);
			
			//position the central cylinder and the peripheral ones
			makeCylinders();
			
			//this places a label on each chromosome with its name
			createChromoLabels();
			
			//this creates the links between chromosomes
			createLinks();
			
			//this creates rings on each chromosome that represent features
			createMarkers();
			
			//add the whole Object to the root
			objRoot.addChild(wholeObj);
						
			BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
			
			// this allows us to set the initial camera view point
			Vector3f translate = new Vector3f();
			Transform3D T3D = new Transform3D();
			TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();
			translate.set(initialViewPoint);
			T3D.setTranslation(translate);
			vpTrans.setTransform(T3D);
			
			//now add the various behaviours
			//rotation of the whole stone circle object
			PickRotateBehavior behavior1 = new PickRotateBehavior(objRoot, canvas, bounds);
			behavior1.setTolerance(50.0f);
			objRoot.addChild(behavior1);
			
			//zooming of the whole stone circle object
			PickZoomBehavior behavior2 = new PickZoomBehavior(objRoot, canvas, bounds);
			objRoot.addChild(behavior2);
			
			//sideways translation of the whole stone circle object
			PickTranslateBehavior behavior3 = new PickTranslateBehavior(objRoot, canvas, bounds);
			objRoot.addChild(behavior3);
			
			//this behaviour listens for mosue clicks that invert single cylinders/chromosomes
			InvertRequestDetector ird = new InvertRequestDetector(this,canvas,objRoot,interpolators,peripheralCylTGs );
			ird.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
			objRoot.addChild(ird);
			
			//this behaviour listens for mouseover on marker rings and then highlights them and pops up a label with the name of the marker
			MouseOverBehavior mo_behavior = new MouseOverBehavior(canvas,markerNamesHashT,markerPositionsHashT,wholeObj,objRoot, peripheralCylTGs, this);
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
	 * Makes a LineArray object that corresponds to teh set of links between thre central chromo and the 
	 * peripheral chromo at index chromosomeIndex in the array of peripheral chromos
	 */
	public LineArray makeLineArray(int chromosomeIndex, boolean invert)
	{
		//work out the marker positions and draw the links
		LinkSet subSet = LinkSetManager.makeSpecificLinkSet(chromosomeIndex,centralChromoLinkSet, referenceMapset, linkSubsets);
		float [][] markerPositions = LinkSetManager.calcLinkPositions(invert, subSet,centralChromo,cylinderHeight);
		LineArray linkArray = LinkCreator.createLinks(cylinderPositions,markerPositions,chromosomeIndex,colours[chromosomeIndex]);			
		return linkArray;
	}
	
//	---------------------------------------------------------------------------------------------------------------------	
	
	//this creates the links between chromosomes
	private void createLinks()
	{
		//for each combination of the central chromosome and a peripheral one
		for(int i =0; i< referenceMapset.size(); i++)
		{
			//make a new line array
			LineArray linkArray = makeLineArray(i,false);
			Shape3D s3d = new Shape3D(linkArray);
			//add this link array to the graph
			wholeObj.addChild(s3d);
			linkArrays[i] = s3d;
			s3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		}

	}

	
//	---------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Inverts all the markers of the chromosome at chromoIndex in the array of TransformGroups holding the
	 * peripheral cylinders/chromosomes
	 */
	public void invertMarkers(int chromoIndex)
	{
		MarkerRingCreator.invertMarkers(chromoIndex, this.peripheralCylTGs);
	}
	
	
//	---------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * Creates rings on each chromosome that represent features (markers)
	 */
	private void createMarkers()
	{
		try
		{	
			//central cylinder		
			//create markers and attach to the existing TG that holds the cylinder
			centralChromoTG.addChild(MarkerRingCreator.createSpecificMarkerSet(centralChromo,
							markerNamesHashT, 
							markerPositionsHashT, 
							0.0f,
							0.0f, 
							cylinderRadius, 
							cylinderHeight,
							centralChromoLinkSet, 
							true, 
							false));		
					
			//iterate over the reference mapset (i.e. the peripheral chromos)
			ChromoMap chromoMap = null;
			TransformGroup rotateTG = null;
			for(int i = 0; i< referenceMapset.size(); i++)
			{
				chromoMap = referenceMapset.getMap(i);
				//create markers and attach to the existing TG that holds the cylinder
				rotateTG = (TransformGroup)peripheralCylTGs[i].getChild(0);
				rotateTG.addChild(MarkerRingCreator.createSpecificMarkerSet(chromoMap,
								markerNamesHashT, 
								markerPositionsHashT, 
								cylinderPositions[i][0],
								cylinderPositions[i][2], 
								cylinderRadius, 
								cylinderHeight,
								linkSubsets[i], 
								false,
								linksInverted[i]));	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}				
	}
	
//	---------------------------------------------------------------------------------------------------------------------		
	
	/**
	 * Places a label on each chromosome with its name
	 */
	private void createChromoLabels()
	{
		//objects for the chromosome labels
		Vector3f chrLabelvec = new Vector3f();
		Transform3D chrLabelTransf = new Transform3D();
		ChromoMap chromoMap = null;

		//place the label for the central cylinder		
		chrLabelvec.set(0.0f,0.0f,0.0f);
		chrLabelTransf.setTranslation(chrLabelvec);
		dec.createFlagLabel(centralChromo.getName(), objRoot, wholeObj, chrLabelvec);
		
		//iterate over the reference mapset (i.e. the peripheral chromos)
		for(int i = 0; i< referenceMapset.size(); i++)
		{
			chromoMap = referenceMapset.getMap(i);
			chrLabelvec.set(cylinderPositions[i]);
			chrLabelTransf.setTranslation(chrLabelvec);
			dec.createFlagLabel(chromoMap.getName(), objRoot, wholeObj, chrLabelvec);			
		}		
	}
	
//	---------------------------------------------------------------------------------------------------------------------		
	/**
	 * Creates the central and peripheral cylinders 
	 */
	private void makeCylinders()
	{
		//make central cylinder first
		Cylinder cyl = new Cylinder(cylinderRadius, cylinderHeight);
		//this holds the position data
		Vector3f vec = new Vector3f(0.0f,0.0f,0.0f);		
		Transform3D translate = new Transform3D();
		translate.setTranslation(vec);
		centralChromoTG = new TransformGroup(translate);
		centralChromoTG.addChild(cyl);
		wholeObj.addChild(centralChromoTG);

		//make the standing stone circle of cylinders representing the chromosomes
		StoneCircleCreator stc = new StoneCircleCreator();
		cylinderPositions = stc.makeCylinderPositions(referenceMapset.size(), circleRadius);
		peripheralCylTGs =  stc.makeCylinderArray(cylinderPositions,cylinderRadius, cylinderHeight,objRoot);
		for (int i = 0; i< peripheralCylTGs.length; i++)
		{
			wholeObj.addChild(peripheralCylTGs[i]);
		}
		interpolators = stc.getInterpolators();
	}
	
//	---------------------------------------------------------------------------------------------------------------------	

	
	// get a nice graphics config
	private static GraphicsConfiguration getGraphicsConfig()
	{
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
		GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
		.getBestConfiguration(template);
		return gcfg;
	}
	
//	---------------------------------------------------------------------------------------------------------------------	
	/**
	 * Recursive method that traverses the entire scene graph and prints out all the objects attached to it
	 */
	private void describeScene(Group group, int numTabs)
	{		
		Enumeration e = group.getAllChildren() ;
		
		while( e.hasMoreElements()) 
		{
			Object obj = e.nextElement();
			Class c = obj.getClass();
			Class superC = c.getSuperclass();
			
			//print appropriate number of tab characters, then the class name
			for(int i = 0 ; i< numTabs; i++)
			{
				System.out.print("   ");
			}
			System.out.println(c.getName());
			
			if(superC != null)
			{
				if(superC.getName().equals("javax.media.j3d.Group"))
				{
					numTabs++;
					describeScene((Group)obj, numTabs);
				}
			}			
		}
	}
	
//==================================accessors===============================

	public void setCentralChromo(ChromoMap centralChromo)
	{
		this.centralChromo = centralChromo;
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
	
//	---------------------------------------------------------------------------------------------------------------------	

} // end of class
