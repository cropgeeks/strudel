package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Alpha;
import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
/**
 * Behaviour class that listens for mouse clicks on chromosomes that are then processed for inversion.
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class InvertRequestDetector extends Behavior
{

//===================================vars========================================
	
	private PickCanvas pickCanvas;
	private PickResult pickResult;
	private Primitive pickedNode;
	private BranchGroup objRoot;
	private RotationInterpolator[] interpolators = null;
	private TransformGroup[] transformGroups = null;
	int tgIndex = -1; 
	private SyntenyViewer3D sv3d = null;

//===================================c'tor========================================
	
	public InvertRequestDetector(SyntenyViewer3D _sv3d, 
					Canvas3D canvas,
					BranchGroup _objRoot,
					RotationInterpolator[] _interpolators, 
					TransformGroup[] _transformGroups)
	{
		this.objRoot = _objRoot;
		this.interpolators = _interpolators;
		this.transformGroups = _transformGroups;
		this.sv3d = _sv3d;

		pickCanvas = new PickCanvas(canvas, objRoot);
		pickCanvas.setTolerance(1.0f);
		pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
	}

//	===================================methods========================================	
	
	public void initialize()
	{
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED));
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 * Called when the appropriate stimulus is received
	 */
	public synchronized void  processStimulus(Enumeration criteria)
	{
		WakeupCriterion wakeup;
		AWTEvent[] event;
		int eventId;		
		while (criteria.hasMoreElements())
		{
			wakeup = (WakeupCriterion) criteria.nextElement();			
			if (wakeup instanceof WakeupOnAWTEvent)
			{
				event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();				
				for (int ii = 0; ii < event.length; ii++)
				{
					eventId = event[ii].getID();				
					if (eventId == MouseEvent.MOUSE_CLICKED)
					{
						int x = ((MouseEvent) event[ii]).getX();
						int y = ((MouseEvent) event[ii]).getY();

						
						try
						{
							pickCanvas.setShapeLocation(x, y);
						}
						catch (NullPointerException e){}		

						try
						{
							pickResult = pickCanvas.pickClosest();
						}
						catch (NullPointerException e){}

						if (pickResult != null)
						{
							pickedNode = ((Primitive) pickResult.getNode(PickResult.PRIMITIVE));
														
							try
							{
								//a chromosome has been picked
								//we now have to set the appropriate Alpha object on the RotationInterpolator
								Cylinder cyl = (Cylinder)pickedNode;							
								TransformGroup tg = (TransformGroup)cyl.getParent().getParent();	
								
								//find the index of this transform group in the array passed in
								//retrieve the corresponding interpolator object
								//set its alpha from null to the new Alpha object
								for(int i = 0; i< transformGroups.length; i++)
								{
									if(tg.equals(transformGroups[i]))
									{
										tgIndex = i;

									}
								}

								//the following resumes the Alpha object, which was paused straight after its creation
								//it then checks for the actual alpha value of the object and pauses it again when this is 1
								Alpha alpha = interpolators[tgIndex].getAlpha();
								AlphaActivator aa = new AlphaActivator(sv3d,alpha,tgIndex);
								aa.start();
							}
							catch (CapabilityNotSetException e)
							{

							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		//reset things
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED));
		pickedNode= null;
		pickResult = null;
	}	
	
	//----------------------------------------------------------------------------------------------------------------------------
}//end class
