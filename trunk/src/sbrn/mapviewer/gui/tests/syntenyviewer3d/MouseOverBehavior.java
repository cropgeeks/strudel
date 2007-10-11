package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
/**
 * Behaviour class that listens for mouseover on marker rings (cylinders) and highlights these in a different colour as well
 * as pops up a label next to them 
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class MouseOverBehavior extends Behavior
{
	
//=======================================vars==============================
	
	private PickCanvas pickCanvas;	
	private PickResult pickResult;	
	private Appearance pickedShapeOldApp, cyanApp;
	private Primitive pickedNode;	
	private boolean isObjectSelectedBefore = false;
	private Primitive lastPickedNode = null;	
	private BranchGroup objRoot;	
	private BranchGroup labelBG = null;	
	private TransformGroup wholeObj  = null;	
	private Hashtable namesHashT = null;	
	private Hashtable positionsHashT = null;	
	private Shape3D [] linkArrays = null;
	boolean [] linksInverted = null;
	private TransformGroup[] transformGroups = null;
	SyntenyViewer3DCanvas canvas;
	
//========================================c'tor============================	
	
	public MouseOverBehavior(SyntenyViewer3DCanvas canvas, 
					Hashtable _namesHashT, 
					Hashtable _positionsHashT, 
					TransformGroup _wholeObj,
					BranchGroup _objRoot,
					TransformGroup[] _transformGroups)
	{

		this.namesHashT = _namesHashT;
		this.objRoot = _objRoot;
		this.wholeObj = _wholeObj;
		this.positionsHashT = _positionsHashT;
		this.transformGroups = _transformGroups;
		this.canvas = canvas;
		
		pickCanvas = new PickCanvas(canvas,objRoot);
		pickCanvas.setTolerance(1.0f);
		pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
		
		Color3f objColor = new Color3f(0.8f, 0.8f, 0.0f);
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
		
		cyanApp = new Appearance();
		objColor = new Color3f(0.9f, 0.9f, 0.9f);
		cyanApp.setMaterial(new Material(objColor, black, objColor, white, 10.0f));
		cyanApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.3f));
		
		linkArrays = canvas.getLinkArrays();
		linksInverted = canvas.getLinksInverted();
		
	}
	
//===========================================methods============================	
	
	public void initialize()
	{
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED));
		
	}
	
//------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void processStimulus(Enumeration criteria)
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
					if (eventId == MouseEvent.MOUSE_MOVED)
					{
						int x = ((MouseEvent) event[ii]).getX();
						int y = ((MouseEvent) event[ii]).getY();						
						pickCanvas.setShapeLocation(x, y);
						try
						{
							pickResult = pickCanvas.pickClosest();
						}
						catch (NullPointerException e)
						{
							e.printStackTrace();
						}
													
						if (pickResult != null && pickResult.getNode(PickResult.PRIMITIVE)!=null)
						{
							pickedNode = ((Primitive) pickResult.getNode(PickResult.PRIMITIVE));							
							//check whether this is the same object as before or not
							if(lastPickedNode != null)
							{
								//if this primitive picked is not the same as the previously picked primitive
								if(!lastPickedNode.equals(pickedNode))
								{
									isObjectSelectedBefore = false;
									try
									{
										//remove any existing marker labels
										if (labelBG != null && wholeObj.indexOfChild(labelBG)!=-1)
										{
											wholeObj.removeChild(labelBG);
										}
										lastPickedNode.setAppearance(pickedShapeOldApp);
									}
									catch (CapabilityNotSetException e)
									{
									}
								}
							}

							//if this is the object picked last
							if (isObjectSelectedBefore)
							{
								//do nothing
							}								
							else
							{
								//store original appearance
								pickedShapeOldApp = pickedNode.getAppearance();
								
								try
								{
//									set its colour to the highlight colour
									((Primitive) pickedNode).setAppearance(cyanApp);
									
									//pop up a flag with the markername
									//this has to come from the hashtable passed in
									
									//first find out which marker ring (cylinder) has been picked
									Cylinder cyl = (Cylinder)pickedNode;	
									//need to get the TG that it belongs to , 5 levels up
									//this is ugly as hell but there is probably no other way
									TransformGroup tg = (TransformGroup)cyl.getParent().getParent().getParent().getParent().getParent();	
									
									//work out where to place the label
									Point3f p = (Point3f)positionsHashT.get(cyl);
									Vector3f vec = null;
									//need to get the TG that it belongs to , 5 levels up
									//this is ugly as hell but there is probably no other way									
									TransformGroup centralChromoTG = (TransformGroup)cyl.getParent().getParent().getParent().getParent();	

									//check whether we have selected a marker on the central chromosome
									if(!centralChromoTG.equals(canvas.getCentralChromoTG()))
									{
										//find the index of this transform group in the array passed in
										int tgIndex = -1;
										for(int i = 0; i< transformGroups.length; i++)
										{
											if(tg.equals(transformGroups[i]))
											{
												tgIndex = i;
											}
										}
										
	
										//need to figure out whether this chromo is currently drawn inverted or not and
										//place the label accordingly
										if(linksInverted[tgIndex])
										{
											//if it is inverted we need to invert the y value only
											float y_inv = (-1)*p.y;
											Point3f p_inv = new Point3f(p.x, y_inv, p.z);
											vec = new Vector3f(p_inv);
										}
										else
										{
											vec = new Vector3f(p);
										}
									}
									else
									{
										vec = new Vector3f(p);
									}
									
									String mName = (String)namesHashT.get(cyl);
									createMarkerLabel(mName, wholeObj, vec);
								}
								catch (CapabilityNotSetException e)
								{
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
								//set flag to indicate it has been picked before
								isObjectSelectedBefore = true;	
								lastPickedNode = pickedNode;
							}
						}
						//this is executed when no primitive has been picked at all
						else
						{
							//remove any existing marker labels
							if (labelBG != null && wholeObj.indexOfChild(labelBG)!=-1)
							{
								wholeObj.removeChild(labelBG);
							}
							
							if(lastPickedNode!=null)
								try
								{
									lastPickedNode.setAppearance(pickedShapeOldApp);
								}
								catch (CapabilityNotSetException e)
								{
								}
							pickedNode = null;
							isObjectSelectedBefore = false;
						}
					}
				}
			}
		}
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED));
	}
	
	//----------------------------------------------------------------------------------------------------------------------------
	
	protected void createMarkerLabel(String text, TransformGroup m_tg, Vector3f vec)
	{
		LineArray line = new LineArray(2, LineArray.COORDINATES);
		line.setCoordinate(0, new Point3f(0.0f, 0.0f, 0.0f));
		line.setCoordinate(1, new Point3f(0.0f, 0.0f, 0.0f));
		
		Shape3D line3D = new Shape3D(line);
		TransformGroup labelTG = Label2D.getLabel(text, Color.RED,
												  Color.BLACK,
												  new Vector3f(0.1f, 0.0f, 0.0f), false);
												  
		TransformGroup tg = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.set(vec);
		tg.setTransform(t3d);
		tg.addChild(line3D);
		tg.addChild(labelTG);

		labelBG = new BranchGroup();
		labelBG.setCapability(BranchGroup.ALLOW_DETACH);
		labelBG.addChild(tg);
		labelBG.compile();

		m_tg.addChild(labelBG);		
	}
	
//	------------------------------------------------------------------------------------------------------------------------------------------		
}//end class
