package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Creates RotationInterpolator objects for a given TransformGroup and any objects hanging off it
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class RotatorCreator
{
	
//========================================methods========================
	
	public static RotationInterpolator setupRotation(Vector3f vec, TransformGroup rotateTG,BranchGroup objRoot)
	{
		//this is needed to set up the rotation of the cylinder over its current position
		Transform3D rotate = new Transform3D();
		AxisAngle4d ang = new AxisAngle4d(new Vector3d(vec), Math.toRadians(90));			
		float [] currentPos = new float [3];
		vec.get(currentPos);
		rotate.setRotation(ang);		
		
		//set up the Alpha object and store it 
		Alpha alpha = new Alpha(-1,1000);
		alpha.setStartTime(System.currentTimeMillis());
		alpha.pause();				
		
		//set up the interpolators
		RotationInterpolator rotator = new RotationInterpolator(alpha, rotateTG);				
		rotator.setTransformAxis(rotate);
		rotator.setMinimumAngle((float)Math.toRadians(0));
		rotator.setMaximumAngle((float)Math.toRadians(180));
		rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),100));
		
		//add the rotator to the object root
		objRoot.addChild(rotator);
		
		return rotator;		
	}
//	------------------------------------------------------------------------------------------------------------------------------------------	
}//end class
