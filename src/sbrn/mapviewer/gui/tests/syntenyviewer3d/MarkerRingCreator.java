package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.util.Hashtable;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import sbrn.mapviewer.data.ChromoMap;
import sbrn.mapviewer.data.Feature;
import sbrn.mapviewer.data.Link;
import sbrn.mapviewer.data.LinkSet;

import com.sun.j3d.utils.geometry.Cylinder;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 *
 *This class is used to generate the markers on chromosomes in a 3D chromosome view. 
 */
public class MarkerRingCreator
{
	

//=========================================methods===========================================	

	/**
	 * Creates markers for a given chromosome
	 */
	public static TransformGroup createSpecificMarkerSet(ChromoMap cMap, 
					Hashtable namesHashT, 
					Hashtable positionsHashT, 
					float xCoord,
					float zCoord, 
					float cylinderRadius, 
					float cylinderHeight,
					LinkSet linkSet, 
					boolean isCentralChromo,
					boolean invert)
	{		
		Vector3f markervec = new Vector3f();
		TransformGroup wholeObj = new TransformGroup();
		
			//the y position of each marker is a float that is relative to the height of the cylinder, e.g. 
			//{cylinderHeight / 2 - 0.7167057515739517f
			//the position needs to be normalised with respect to the cylinder length as the values passed in are absolutes	
			//iterate over chromosome 
			for(Link link : linkSet.getLinks())
			{					
				try
				{
					Feature feat = null;
					//if this is the linkset from a central chromo we want to use feature1 of each link
					//else we want feature 2
					if(isCentralChromo)
						feat = link.getFeature1();
					else
						feat = link.getFeature2();					

					float featStart = feat.getStart();
					float mapEnd = cMap.getStop();
														
					//now calculate the actual y position 
					//this needs to take into account the map end point values
					//float yPos = (cylinderHeight / 2) - (float)(featStart/mapEnd);					
					float yPos;
					if(featStart == 0.0f )
					{
						yPos = 0.0f;
					}
					else
					{
						yPos = (cylinderHeight / 2) - (float)(featStart/mapEnd);	
					}
					
					if(invert)
					{

						//the normalised values range from -0.5 to 0.5
						// to invert the value just change the sign
						yPos = (-1)* yPos;
					}
			
					//place marker rings on the chromosome
					//use cylinders with minimal height and slightly bigger diameter than the parent cylinder to do this
					Appearance app = new Appearance();
					app.setColoringAttributes(new ColoringAttributes(0.7f, 0.0f, 0.0f, ColoringAttributes.NICEST));
					Cylinder cyl = new Cylinder(cylinderRadius+0.001f, 0.006f,Cylinder.ENABLE_APPEARANCE_MODIFY,app);
					//position it
					Point3f p = new Point3f(0.0f,yPos,0.0f);
					markervec.set(p);
					Transform3D t3d = new Transform3D();
					t3d.setTranslation(markervec);
					TransformGroup markerTG = new TransformGroup(t3d);
					markerTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
					markerTG.addChild(cyl);		
					wholeObj.addChild(markerTG);
									
					namesHashT.put(cyl,feat.getName());
					positionsHashT.put(cyl, new Point3f(xCoord, yPos, zCoord));
				}
				//catch(ArrayIndexOutOfBoundsException x){}
				//catch (NullPointerException npe){}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}

		return wholeObj;
	}
	
//	 ---------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * This method adds ALL the markers of a chromosome to the TransformGroup it returns; depending on the dataset this
	 * will likely cause OutOfmemoryErrors (and has been shown to do so already); retained for potential future use though	 * 
	 */
	public static TransformGroup createAllMarkers(ChromoMap cMap, Hashtable namesHashT, Hashtable positionsHashT, float xCoord,float zCoord, float cylinderRadius, float cylinderHeight)
	{
		Vector3f markervec = new Vector3f();
		TransformGroup wholeObj = new TransformGroup();
		
			//the y position of each marker is a float that is relative to the height of the cylinder, e.g. 
			//{cylinderHeight / 2 - 0.7167057515739517f
			//the position needs to be normalised with respect to the cylinder length as the values passed in are absolutes	
			//iterate over chromosome 
			for(Feature feat : cMap)
			{					
				try
				{
					
					//System.out.println("current feature = " + feat.getName());
					float featStart = feat.getStart();
					float mapEnd = cMap.getStop();
														
					//now calculate the actual y position 
					//this needs to take into account the map end point values
					//float yPos = (cylinderHeight / 2) - (float)(featStart/mapEnd);					
					float yPos;
					if(featStart == 0.0f )
					{
						yPos = 0.0f;
					}
					else
					{
						yPos = (cylinderHeight / 2) - (float)(featStart/mapEnd);	
					}
			
					//place marker rings on the chromosome
					//use cylinders with minimal height and slightly bigger diameter than the parent cylinder to do this
					Appearance app = new Appearance();
					app.setColoringAttributes(new ColoringAttributes(0.7f, 0.0f, 0.0f, ColoringAttributes.NICEST));
					Cylinder cyl = new Cylinder(cylinderRadius+0.001f, 0.006f,Cylinder.ENABLE_APPEARANCE_MODIFY,app);
					//position it
					Point3f p = new Point3f(0.0f,yPos,0.0f);
					markervec.set(p);
					Transform3D t3d = new Transform3D();
					t3d.setTranslation(markervec);
					TransformGroup markerTG = new TransformGroup(t3d);
					markerTG.addChild(cyl);		
					wholeObj.addChild(markerTG);
									
					namesHashT.put(cyl,feat.getName());
					positionsHashT.put(cyl, p);
				}
				//catch(ArrayIndexOutOfBoundsException x){}
				//catch (NullPointerException npe){}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}

		return wholeObj;
	}

// ---------------------------------------------------------------------------------------------------------------------	

	/**
	 * Inverts markers on a given chromosome
	 */
	public static void invertMarkers(int chromoIndex, TransformGroup [] peripheralCylTGs)
	{
		//need to recreate the structure of this branch of the scene graph
		TransformGroup rotateTG = (TransformGroup)peripheralCylTGs[chromoIndex].getChild(0);
		//this group has a set of other TGs as its child which in turn hold the cylinder objects for the markers
		TransformGroup markersTG = (TransformGroup)rotateTG.getChild(1);
		//iterate over all the children
		for(int i=0; i< markersTG.numChildren(); i++)
		{
			//get the next child TG
			TransformGroup tg = (TransformGroup)markersTG.getChild(i);
			//set its transform to reflect the new positions of the inverted markers
			Transform3D t3d = new Transform3D();
			tg.getTransform(t3d);
			Vector3f currentVec = new Vector3f();
			t3d.get(currentVec);

			float yPos = currentVec.y;
			//invert this value
			yPos = (-1)* yPos;
			//set it on the current transform
			currentVec.y = yPos;
			t3d.set(currentVec);
			tg.setTransform(t3d);
		}		
	}
	
//	---------------------------------------------------------------------------------------------------------------------

}//end class
