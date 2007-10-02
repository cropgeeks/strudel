package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.util.Hashtable;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * Test class
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class TestObjectInverter extends Applet
{
	
	private SimpleUniverse u = null;
	private SyntenyViewer3DCanvas c = null;
	private static RotationInterpolator [] interpolators = new RotationInterpolator [2];
	private TransformGroup [] transFormGroups = new TransformGroup [2];
	private Alpha [] alphas = new Alpha [2];
	
	public BranchGroup createSceneGraph()
	{
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		
		// Create the TransformGroup node and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at run time. Add it to
		// the root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.addChild(objTrans);
		
		float cylinderRadius = 0.05f;
		float cylinderHeight = 1.0f;
		Appearance app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(0.4f, 0.35f, 0.8f, ColoringAttributes.NICEST));
		
		Hashtable chromoPositionsHashT = new Hashtable();	
		float[][] cylinderPositions = new float [2][3];
		cylinderPositions[0] = new float [] {0.0f, 0.0f, 0.0f};
		cylinderPositions[1] = new float [] {0.3f, 0.0f, 0.0f};
		
		for (int i = 0; i < cylinderPositions.length; i++)
		{
			Cylinder cyl = new Cylinder(cylinderRadius, cylinderHeight);
			
			//new line array
			LineArray axisZLines=new LineArray(4,LineArray.COORDINATES|LineArray.COLOR_3);
			objRoot.addChild(new Shape3D(axisZLines));			      
			Point3f z0=new Point3f(0.0f,0.0f,0.0f);
			Point3f z1=new Point3f(0.5f,0.0f,0.0f);
			Point3f z2=new Point3f(0.0f,0.5f,0.0f);
			Point3f z3=new Point3f(0.5f,0.5f,0.0f);
			axisZLines.setCoordinate(0,z0);
			axisZLines.setCoordinate(1,z1);
			axisZLines.setCoordinate(2,z2);
			axisZLines.setCoordinate(3,z3);
			
			//this holds the position data
			Vector3f vec = new Vector3f();
			
			//set up first TG for translation
			Transform3D translate = new Transform3D();
			vec.set(cylinderPositions[i]);
			translate.setTranslation(vec);
			TransformGroup translateTG = new TransformGroup(translate);
			transFormGroups[i] = translateTG;
			
			//set up second TG for rotation
			Transform3D rotate = new Transform3D();
			rotate.rotX(Math.toRadians(90));
			TransformGroup rotateTG = new TransformGroup();
			rotateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			//set up the Alpha object and store it 
			Alpha alpha = new Alpha(-1,1000);
			alpha.setStartTime(System.currentTimeMillis());
			alpha.pause();
			
			alphas[i] = alpha; 
			//set up the interpolator
			RotationInterpolator rotator = new RotationInterpolator(alpha, rotateTG);

			rotator.setTransformAxis(rotate);
			rotator.setMinimumAngle((float)Math.toRadians(0));
			rotator.setMaximumAngle((float)Math.toRadians(180));
			rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),100));
			interpolators[i]= rotator;

			//put together the tree
			objRoot.addChild(rotator);
			objRoot.addChild(translateTG);
			translateTG.addChild(rotateTG);
			rotateTG.addChild(cyl);			

			//store cylinder position
			chromoPositionsHashT.put(cyl, cylinderPositions[i]);
		}
			
		Decorator dec = new Decorator();
		dec.createLights(objRoot);
		
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		
		InvertRequestDetector ird = new InvertRequestDetector(c,objRoot,interpolators,transFormGroups);

		ird.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
		objRoot.addChild(ird);
		
		// Have Java 3D perform optimizations on this scene graph.
		objRoot.compile();
		
		return objRoot;
	}

	public void init()
	{
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		
		c = new SyntenyViewer3DCanvas(config);
		add("Center", c);
		
		// Create a simple scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph();
		u = new SimpleUniverse(c);
		
		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();
		
		u.addBranchGraph(scene);
	}
	
	public void destroy()
	{
		u.cleanup();
		System.exit(0);
	}
	
	//
	// The following allows ObjectInverter to be run as an application
	// as well as an applet
	//
	public static void main(String[] args)
	{		
		new MainFrame(new TestObjectInverter(), 256, 256);
	}
//------------------------------------------------------------------------------------------------------------------
}//end class
