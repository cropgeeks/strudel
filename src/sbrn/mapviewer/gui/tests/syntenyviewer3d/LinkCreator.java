package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 *
 *This class is used to generate a set of lines between two cylinders. 
 */
public class LinkCreator
{

//=========================================methods===========================================	
	
	/**
	 * Creates a Shape3D object which represents the lines drawn between chromosomes
	 */
	public static LineArray createLinks(float[][] cylinderPositions, float[][] markerPositions, int iteration,Color3f lineColour)
	{
		LineArray linkGeom = null;
		
		try
		{
			//the number of verteces (line end points) - 2 per link
			int arraySize = markerPositions[0].length*2;
			
			//the number of links to draw
			int numLinks = markerPositions[0].length;
			
			//if there are no links between this pair of chromosomes, return null
			if( arraySize == 0)
			{
				return null;
			}		
			
			//this object represents the actual lines
			 linkGeom = new LineArray(arraySize, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
			int vertecesDrawn = 0;
			
			// for every two vertices in the array
			// set the position on the central cylinder (line startpoint)
			// set the position on the other cylinder (line endpoint)
			for(int i = 0; i< numLinks; i++)
			{					
					//line start point
					// in the case of the hub&rim model the central cylinder (the hub) can take all the line start points
					linkGeom.setCoordinate(vertecesDrawn, new Point3f(0.0f,
									markerPositions[0][i], 0.0f));
					linkGeom.setColor(vertecesDrawn,lineColour);	
					vertecesDrawn++;
										
					// line end point
					linkGeom.setCoordinate(vertecesDrawn, new Point3f(cylinderPositions[iteration][0],
									markerPositions[1][i], cylinderPositions[iteration][2]));
					linkGeom.setColor(vertecesDrawn,lineColour);	
					vertecesDrawn++;				
			}

		}
		catch(Exception e)
		{

			e.printStackTrace();
		}
		
		return linkGeom;
	}

// ---------------------------------------------------------------------------------------------------------------------	

}//end class
