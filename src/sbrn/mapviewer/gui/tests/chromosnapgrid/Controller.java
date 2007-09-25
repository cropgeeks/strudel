package sbrn.mapviewer.gui.tests.chromosnapgrid;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;


/**
 * This class represents the controller of an MVC architecture for moving genetic maps around a canvas 
 *  where the maps redraw themselves from coordinate 0,0 every time their paint method is called.
 *  
 * It also includes snap to grid functionality so users can align the tops of the maps. 
 *  
 *  @author Micha Bayer, Scottish Crop Research Institute
 * 
 */
public class Controller extends JFrame  implements MouseInputListener, MouseMotionListener
{
//	============================variables==================================
	
	/*frame size in x*/
	private static int frameXDimension = 500;
	/*frame size in y*/
	private static int frameYDimension = 300;
	
	/*array of the maps we want to display*/
	private Map [] maps = new Map [3];
	
	/*these hold the current coordinates for the top left hand corner of each map in the array*/
	private int [] mapX = new int[] {50,150,250};
	private int [] mapY = new int [] {50,50,50};
	
	/*the JPanel that draws the maps*/
	private MapCanvas canv = null;
	
	/*the extended area of each map that should be clickable
	includes the map's "backbone", the space between marker bars etc*/
	private Rectangle [] rects = null;
	
	/*the array index of the map that has been selected for repositioning*/
	private int selectedMap = -1;
	
	/*this specifies how far down the length of the selected map's "backbone" the user clicked
	needed to avoid sudden shift upon redraw of map when it has been moved*/
	private int selectedMapYOffset = 0; 
	
	/*indicates whether the map should be snapped to an invisible grid*/
	private boolean snapToGrid = true;
	
	/*the size of a square cell in the grid*/
	private int cellSize = 35; 

//	============================constructors==================================
	
	public Controller()
	{		
		//initialise maps
		for(int i = 0; i< maps.length; i++)
		{
			maps[i] = new Map(100, 7);
		}	

		canv = new MapCanvas(maps, mapX, mapY, this, this);
		this.getContentPane().add(canv);
		rects = new Rectangle[maps.length];
		initRects();
	}

//	============================methods==================================	
	
	public static void main(String [] args)
	{		
		Controller con = new Controller();
		con.setSize(frameXDimension, frameYDimension);
		con.setVisible(true);
		con.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
//	---------------------------------------------------------------------------------------------------------------------------
	
	private void initRects()
	{
		//populate the rectangles array that represents the clickable spaces for each map
		for (int i = 0; i< maps.length; i++)
		{
			//get the length of the map - this will the size of the rect along y axis
			int mapLength = maps[i].getMapLength();
			//get length of marker bar - this will the size of the rect along x axis
			int markerLength = maps[i].getMarkerLength();
			rects[i] = new Rectangle(mapX[i]-markerLength, mapY[i], markerLength*2, mapLength);			
		}	
	}
	
//	---------------------------------------------------------------------------------------------------------------------------
	
	private void repositionMap(MouseEvent e, boolean _snapToGrid)
	{

		int x = e.getX();
		int y = e.getY();
		int markerLength = maps[selectedMap].getMarkerLength();

		//reposition map by setting its coordinates appropriately		
		if(_snapToGrid)
		{
			//work out nearest snap point in grid - x
			x = calcSnapPoint(x);			
			//adjust for where along the backbone of the map we picked it up
			y = y-selectedMapYOffset;
//			work out nearest snap point in grid - y
			y = calcSnapPoint(y);			
		}
		//this gets executed while we are dragging, rather than dropping
		//don't want to snap to grid here but need to adjust for where along the backbone of the map we picked it up
		//otherwise we get jumps along y axis
		else
		{
			y = y-selectedMapYOffset;
		}

		mapX[selectedMap] = x;
		mapY[selectedMap] = y;
		//subtract a whole markerlength from x because we want the selection rectangle to be slightly bigger
		//than just the map itself to make it easier to pick 
		rects[selectedMap].setLocation(x-markerLength,mapY[selectedMap]);

	}
	
//-----------------------------------------------------------------------------------------------------------------------------------
	
	/**Calculates the nearest point to snap the map in the grid; works for both x and y coords
	 * @param _value -- the current x or y value
	 * @return int - the adjusted x or y value (snapped to grid)
	 */
	private int calcSnapPoint(int _value)
	{
		int snappedValue = -1; 
		
		//work out nearest snap point in grid - x
		int quotient = _value/cellSize; 
		double result = (double)_value/cellSize;
		double remainder = result-quotient;
		//if the remainder is greater than or equal to 0.5, round up to the nearest snap point value
		if(remainder >= 0.5)
		{
			//round up the quotient
			double adjustedQuotient = Math.ceil(result);
			//now multiply the cellsize by the result without the remainder
			snappedValue = ((int)adjustedQuotient)*cellSize; 
		}
		else //round down
		{
			double adjustedQuotient = Math.floor(result);
			//now multiply the cellsize by the result without the remainder
			snappedValue = ((int)adjustedQuotient)*cellSize; 
		}
		return snappedValue;
	}
	

//	========================mouse listener methods==================================	
	
	public void mouseDragged(MouseEvent e)
	{
		if(selectedMap != -1)
		{
			//reposition map by setting its coordinates appropriately
			repositionMap(e,false);			
		        //redraw the maps
		        this.repaint();
		}		
	}
//	---------------------------------------------------------------------------------------------------------------------------	
	public void mousePressed(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		for(int i= 0; i< rects.length; i++)
		{
			if(rects[i].contains(x,y))
			{
				selectedMap = i;
				//get top l.h. Y coordinate of this rect
				int topY = (int)rects[i].getY();
				selectedMapYOffset = y - topY;                  
			}
		}  
	}
//	---------------------------------------------------------------------------------------------------------------------------	
	public void mouseReleased(MouseEvent e)
	{
		if(selectedMap != -1)
		{
			//reposition map by setting its coordinates appropriately
			repositionMap(e,snapToGrid);		
			
			//reset things so no map is selected
		        selectedMap = -1;
		        selectedMapYOffset = 0;
		        
		        //redraw the maps
		        this.repaint();
		}
	}
//========================currently unused==================================	
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}	
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
//	---------------------------------------------------------------------------------------------------------------------------	

}//end class
