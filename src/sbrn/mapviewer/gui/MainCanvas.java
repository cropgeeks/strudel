package sbrn.mapviewer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import sbrn.mapviewer.gui.entities.GChromoMap;

public class MainCanvas extends JPanel
{
	public MainCanvas()
	{
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.black);
	}
	
	// ============================methods==================================
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
				
		int x = 100;
		int y = 200;
		
		// draw the maps
		GChromoMap map = new GChromoMap();
		// move the origin of the Graphics object to the desired current position of the map
		g2.translate(x,y);
		// the map draws itself from 0,0 always
		map.paintMap(g2);
		// now move it back to 0,0 to preserve the overall coordinate system
		g2.translate(-x, -y);

	}
}
