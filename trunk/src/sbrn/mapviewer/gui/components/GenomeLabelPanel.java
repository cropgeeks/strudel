package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;

public class GenomeLabelPanel extends JPanel
{

	int fontHeight = 11;
	BufferedImage exportBuffer;

	public GenomeLabelPanel()
	{
		setBackground(Colors.genomeLabelPanelColour);
		setPreferredSize(new Dimension(10, 20));
		
		initComponents();
	}
	
	public void initComponents()
	{
		int numGenomes = Strudel.winMain.dataSet.gMapSets.size();
		
		setLayout(new GridLayout(1, numGenomes));
		
		Font font = new Font("Sans-serif", Font.PLAIN, fontHeight);
		
		for (int i = 0; i < numGenomes; i++)
		{
			String genomeName = Strudel.winMain.dataSet.gMapSets.get(i).name;
			JLabel label = new JLabel(genomeName);
			add(label);
			label.setToolTipText(genomeName);
			label.setFont(font);
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
	}
	
	public void reinititalise()
	{
		removeAll();
		initComponents();
		repaint();
	}


	/**
	 * Draws the panel to a buffer for the exportImage code.
	 */
	public BufferedImage createExportBuffer()
	{
		exportBuffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = exportBuffer.createGraphics();

		g.setColor(Color.BLACK);
		setBackground(Colors.backgroundGradientEndColour);
		paintComponent(g);
		setBackground(Colors.genomeLabelPanelColour);
		return exportBuffer;
	}
}
