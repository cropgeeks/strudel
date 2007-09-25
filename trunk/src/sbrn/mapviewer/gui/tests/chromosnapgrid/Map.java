package sbrn.mapviewer.gui.tests.chromosnapgrid;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * This class represents the data model of an MVC architecture for moving genetic maps around a canvas 
 *  where the maps redraw themselves from coordinate 0,0 every time their paint method is called. Maps need to know
 *  how to draw themselves and do so, starting at coordinate 0,0, every time the paintMap method is invoked.  
 *  
 *  @author Micha Bayer, Scottish Crop Research Institute
 * 
 */
public class Map
{
//============================variables==================================
	
	//all of these hard coded for now pending design of proper classes
	
	/*the length of the map's "backbone" in pixels*/
	private int mapLength = 100;
	/*the number of marker features to be displayed on the map*/
	private int numMarkers = 7;
	/*the name of the map*/
	private String name = "map";
	/*the length of the map's marker in pixels; assumes this is a horizontal cross bar*/
	private int markerLength = 10; 

//	============================constructors==================================
	
	/**
	 * @param mapLength -- the length of the map's "backbone" in pixels
	 * @param numMarkers -- the number of marker features to be displayed on the map
	 */
	public Map(int mapLength, int numMarkers)
	{
		this.mapLength = mapLength;
		this.numMarkers = numMarkers;
	}

//	============================methods==================================	
	/**
	 * Draws the map from coordinate 0,0 given the current position of the Graphics object
	 */
	public void paintMap(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		//draw the map
		g2.drawString(name, 0,0);
		g2.drawLine(0, 0,0,mapLength);
		
		//draw the markers onto the backbone
		int currentY = 5; 
		for(int j = 0; j< numMarkers; j++)
		{
			g2.drawLine(0-markerLength/2, currentY,0+markerLength/2, currentY);
			currentY += mapLength/numMarkers;
		}
	}	

//================================accessors================================	
	
	public int getMapLength()
	{
		return mapLength;
	}
	public void setMapLength(int length)
	{
		this.mapLength = length;
	}
	public int getNumMarkers()
	{
		return numMarkers;
	}
	public void setNumMarkers(int numMarkers)
	{
		this.numMarkers = numMarkers;
	}


	public int getMarkerLength()
	{
		return markerLength;
	}


	public void setMarkerLength(int markerLength)
	{
		this.markerLength = markerLength;
	}
//	------------------------------------------------------------------------------------------------------------------------------
}//end class
