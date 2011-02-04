package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class GenomeLabelPanel extends JPanel implements MouseListener
{

	int fontHeight = 13;
	BufferedImage exportBuffer;
	LinkedList<JLabel> labels = new LinkedList<JLabel>();

	public GenomeLabelPanel()
	{
		setBackground(Colors.genomeLabelPanelColour);
		setPreferredSize(new Dimension(10, 30));
		
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
			label.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
			
			label.addMouseListener(this);
			
			labels.add(label);
		}
	}
	
	public void reinititalise()
	{
		labels = new LinkedList<JLabel>();
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
	
	public void resetSelectedMapset()
	{
		//un-highlight all labels
		for(JLabel label : labels)
			label.setForeground(Color.BLACK);
		Strudel.winMain.zoomControlPanel.selectedSet = null;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{		
		//find out which label was clicked
		JLabel  labelClicked =  (JLabel)e.getComponent();

		//retrieve the mapset object
		int selectedIndex = labels.indexOf(labelClicked);
		GMapSet selectedSet = Strudel.winMain.dataSet.gMapSets.get(selectedIndex);
		
		selectGMapSet(selectedSet);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{	
	}

	public void selectGMapSet(GMapSet selectedSet)
	{
		//un-highlight all labels
		resetSelectedMapset();
		
		JLabel selectedLabel = null;
		//highlight  the selected label
		for(JLabel label : labels)
		{
			if(label.getText().equals(selectedSet.name))
			{
				selectedLabel = label;
				break;
			}
		}
		selectedLabel.setForeground(Color.RED);
		
		//update the zoom control panel
		Strudel.winMain.zoomControlPanel.updateControlsToMapsetSettings(selectedSet);
	}
}
