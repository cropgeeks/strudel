package sbrn.mapviewer.gui.components;

import java.awt.*;
import javax.swing.*;
import sbrn.mapviewer.gui.*;

public class GenomeLabelPanel extends JPanel
{
	
	int fontHeight = 10;
	
	public GenomeLabelPanel()
	{
		setBackground(Colors.genomeLabelPanelColour);
		setPreferredSize(new Dimension(getWidth(), 20));
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g2 = (Graphics2D) graphics;
		
		//usual font stuff
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();

		//turn antialias on for prettier fonts
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		//work out label spacing
		int numGenomes = MapViewer.winMain.mainCanvas.gMapSetList.size();
		int labelInterval = getWidth() / numGenomes;
		int spacerLeft = labelInterval / 2;
		
		// draw the labels
		for (int i = 0; i < numGenomes; i++)
		{			
			String genomeName = MapViewer.winMain.mainCanvas.gMapSetList.get(i).name;
			int stringWidth = fm.stringWidth(genomeName);
			int x = (labelInterval * i) + spacerLeft - (stringWidth / 2);
			int y = (getHeight() / 2) + (fontHeight / 2);
			g2.drawString(genomeName.toUpperCase(), x, y);
		}	
		
		//turn antialias off again
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}
