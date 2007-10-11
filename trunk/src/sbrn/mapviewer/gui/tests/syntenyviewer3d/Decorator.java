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
	 * Returns an array of colours the length of numColours
	 */
	public Color3f [] generateColours(int numColours)
	{
		int red=0;
		int green = 0;
		int blue =0;
		
		//the amount by which we want to increment the values for each colour channel 
		int increment = 255/(numColours/3);
		
		Color3f [] colours = new Color3f[numColours];
		//make a colour gradient by initially ramping up the red only, then the green and then the blue
		for (int i = 0; i < colours.length; i++)
		{
		
			if(i<colours.length/3)
			{
				red+= increment;
			}
			if(i>= colours.length/3 && i<((colours.length/3)*2))
			{
				red = 0;
				green+=increment;
			}
			if(i>=((colours.length/3)*2))
			{
				red = 0;
				green = 0;
				blue+= increment;
			}
			
			colours[i] = new Color3f(new Color(red,green,blue));
		}	

		return colours;
	}
	
	
//---------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * Creates a 2d label that is always readable from whatever angle 
	 */
	public void createFlagLabel(String text, TransformGroup targetTransformGroup, Vector3f vec)
	{

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

		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		bg.addChild(tg);
		targetTransformGroup.addChild(bg);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Returns a random int that is less than a certain value
	 * 
	 * @param max --
	 *                the maximum to be returned
	 * @return the random int
	 */
	private int getRandomInt(int max)
	{
		int rand = -1;

		while (rand > max || rand < 0)
		{
			rand = (int) (Math.random() * 100);
		}

		return rand;
	}
// 	---------------------------------------------------------------------------------------------------------------------	
	
}//end class
