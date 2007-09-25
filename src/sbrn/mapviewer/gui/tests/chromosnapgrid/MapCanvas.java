package sbrn.mapviewer.gui.tests.chromosnapgrid;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 * This class represents the View of an MVC architecture for moving genetic maps around a canvas 
 *  where the maps redraw themselves from coordinate 0,0 every time their paint method is called.
 *  
 *  @author Micha Bayer, Scottish Crop Research Institute
 * 
 */
public class MapCanvas extends JPanel
{
//	============================variables==================================
	/*the array of maps to be drawn*/
	private Map [] maps = null;
	/*the corresponding array with the maps' x positions*/
	private int [] mapX = null;
	/*the corresponding array with the maps' y positions*/
	private int [] mapY = null;

//	============================constructors==================================
	
	/**
	 * @param _maps the array of maps to be drawn
	 * @param _mapX the corresponding array with the maps' x positions
	 * @param _mapY the corresponding array with the maps' y positions
	 * @param _mil mouse listener
	 * @param _mml mouse listener
	 */
	public MapCanvas(Map [] _maps,  int [] _mapX, int [] _mapY, MouseInputListener _mil, MouseMotionListener _mml)
	{
		this.maps = _maps;
		this.addMouseListener(_mil);
		this.addMouseMotionListener(_mml);
		this.mapX = _mapX;
		this.mapY = _mapY;
	}
	
//	============================methods==================================	

	public void paintComponent(Graphics g)
	{
		////System.out.println("painting "+ maps.length + " maps");
		Graphics2D g2 = (Graphics2D) g;

		//draw the maps
		for(int i = 0; i < maps.length; i++)
		{
			//move the origin of the Graphics object to the desired current position of the map 
			g2.translate(mapX[i],mapY[i]);
			//the map draws itself from 0,0 always
			////System.out.println("drawing map at " + mapX[i] + "," + mapY[i]);
			maps[i].paintMap(g2);
			//now move it back to 0,0 to preserve the overall coordinate system
			g2.translate(-mapX[i],-mapY[i]);
		}
	}
	
//	============================accessors==================================
	
	public int[] getMapX()
	{
		return mapX;
	}

	public void setMapX(int[] mapX)
	{
		this.mapX = mapX;
	}

	public int[] getMapY()
	{
		return mapY;
	}

	public void setMapY(int[] mapY)
	{
		this.mapY = mapY;
	}
	
//--------------------------------------------------------------------------------------------------------------------------------------
}//end class
