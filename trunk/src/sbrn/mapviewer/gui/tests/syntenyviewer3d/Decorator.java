package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.awt.Color;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 *
 *This class is used to add features into the 3D chromosome view such as lighting and labels
 */
public class Decorator
{
	
//==============================methods===============================
	
	/**
	 * Adds directional light to a scene 
	 */
	public void createLights(BranchGroup graphRoot)
	{
		
		// Create a bounds for the light source influence
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
		
		// Set up the global, ambient light
		Color3f alColor = new Color3f(1.0f, 1.0f, 1.0f);
		AmbientLight aLgt = new AmbientLight(alColor);
		aLgt.setInfluencingBounds(bounds);
		graphRoot.addChild(aLgt);
		
		// Set up the directional (infinite) light source
		Color3f lColor1 = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f lDir1 = new Vector3f(0.0f,0.0f, -50.0f);
		DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
		lgt1.setInfluencingBounds(bounds);
		graphRoot.addChild(lgt1);

	}
	
	
	// ---------------------------------------------------------------------------------------------------------------------	

	/**
	 * Returns an array of colours
	 */
	public Color3f [] generateColours()
	{
		Color3f [] colours = new Color3f []{
						new Color3f(Color.CYAN),
						new Color3f(Color.YELLOW),
						new Color3f(Color.RED),
						new Color3f(Color.WHITE),
						new Color3f(Color.MAGENTA),
						new Color3f(Color.ORANGE),
						new Color3f(Color.PINK),
						new Color3f(Color.GREEN),
						new Color3f(Color.BLUE),
						new Color3f(0.5f,0.9f,0.2f),						
						new Color3f(0.2f,0.5f,0.9f),
						new Color3f(0.0f,0.5f,0.5f),
						new Color3f(0.5f,0.5f,0.0f),
						new Color3f(0.5f,0.0f,0.5f),
						new Color3f(0.1f,0.3f,0.6f),
						new Color3f(0.6f,0.3f,0.1f),
						new Color3f(0.3f,0.6f,0.1f),
						new Color3f(0.6f,0.8f,1.0f),
						new Color3f(1.0f,0.8f,0.6f)
						};

		return colours;
	}
	
	
//---------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * Creates a 2d label that is always readable from whatever angle 
	 */
	public void createFlagLabel(String text, BranchGroup bg, TransformGroup m_tg, Vector3f vec)
	{
		if (bg != null)
			bg.detach();
		
		LineArray line = new LineArray(2, LineArray.COORDINATES);
		line.setCoordinate(0, new Point3f(0.0f, 0.0f, 0.0f));
		line.setCoordinate(1, new Point3f(0.0f, 0.0f, 0.0f));
		
		Shape3D line3D = new Shape3D(line);
		TransformGroup labelTG = Label2D.getLabel(text, Color.BLACK,
												  new Color(19,130,51),
												  new Vector3f(0.0f, 0.6f, 0.0f), false);
												  
		TransformGroup tg = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.set(vec);
		tg.setTransform(t3d);
		tg.addChild(line3D);
		tg.addChild(labelTG);

		bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.addChild(tg);
		m_tg.addChild(bg);
	}
// 	---------------------------------------------------------------------------------------------------------------------	
	
}//end class
