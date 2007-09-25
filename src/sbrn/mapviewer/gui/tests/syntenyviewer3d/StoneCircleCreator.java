package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cylinder;
/**
 * Creates a standing stone circle type arrangement of cylinder objects
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class StoneCircleCreator
{
//=======================================vars===============================	
	
	private RotationInterpolator [] interpolators = null;
	
//==========================================methods==========================	
	
	/**
	 * Creates an an array of cylinders representing the chromosomes in a "standing stone circle" type arrangement;
	 * this does not include the central cylinder however
	 */
	public TransformGroup [] makeCylinderArray(float [][] cylinderPositions,float cylinderRadius, float cylinderHeight, BranchGroup objRoot)
	{	
		int numCyls = cylinderPositions.length;
		TransformGroup [] tGroups = new TransformGroup [numCyls];
		interpolators = new RotationInterpolator [numCyls];
		
		try
		{
			for(int i = 0; i< numCyls; i++)
			{
				//create new cylinder and a vector to hold its position data
				Cylinder cyl = new Cylinder(cylinderRadius, cylinderHeight);
				Vector3f vec = new Vector3f();
				
				//set up first TG for translation
				Transform3D translate = new Transform3D();
				vec.set(cylinderPositions[i]);
				translate.setTranslation(vec);
				TransformGroup translateTG = new TransformGroup(translate);
				tGroups[i] = translateTG;
				
				//set up second TG for rotation
				TransformGroup cylRotateTG = new TransformGroup();
				cylRotateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);	

				//put things together
				translateTG.addChild(cylRotateTG);
				cylRotateTG.addChild(cyl);	
				
				//make the rotator object
				interpolators[i]= RotatorCreator.setupRotation(vec,cylRotateTG,objRoot);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
		return tGroups;
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	/**
	 * Calculates the cylinder positions.
	 */
	public float [][]  makeCylinderPositions(int numCyls, float circleRadius)
	{
		//need an array with the same number of outer elements as datafiles and 3 inner elements
		float [][] cylinderPositions = new float [numCyls][3];
		
		//work out the polar angle for the segment enclosed by two chromosome positions
		double polarAngle = 360/(numCyls); 
		double currentAngle = polarAngle;
		
		float origX = 0.0f;
		float origZ = 0.0f;
		
		//first make coords for the central chromosome
		cylinderPositions[0] = new float [] {origX, 0.0f, origZ};
		
		//this positions the peripheral cylinders
		for(int i = 0; i< numCyls; i++)
		{
			double xCoord = origX +circleRadius*Math.cos(Math.toRadians(currentAngle));
			double zCoord = origZ + circleRadius*Math.sin(Math.toRadians(currentAngle));
			float [] coords = {(float)xCoord,0.0f, (float)zCoord};
			cylinderPositions[i] = coords; 
			currentAngle += polarAngle;
		}		
		return cylinderPositions;
	}
	
	//-------------------------------------------------------------------------------------------------------------------	

	public RotationInterpolator[] getInterpolators()
	{
		return interpolators;
	}
	
	//-------------------------------------------------------------------------------------------------------------------	

	public void setInterpolators(RotationInterpolator[] interpolators)
	{
		this.interpolators = interpolators;
	}
	
	//-------------------------------------------------------------------------------------------------------------------
}//end class
