// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class HintPanel extends JPanel
{
	private static JPanel panel;
	private static JLabel label;
	private static JLabel closeLabel;

	private ImageIcon closeIcon1, closeIcon2;

	//strings to display -- store them all here rather than all over the code
	public static final String clickSpecificChromoStr = "Click this chromosome to see its homologs, Alt-click it to fit it on screen";
	public static final String zoomInStr = "Shift-click and drag on the chromosome to outline a region for further zooming, or use the zoom sliders";
	public static final String overviewStr = "Click on a chromosome to see all its homologs or Ctrl-click two or more to see their homologs. " +
	"You can also right-click on a chromosome and choose '"
	+ Strudel.winMain.chromoContextPopupMenu.selectAllChromosStr + "'.";
	public static final String altClickStr = "Double-click to fill the screen with this chromosome";


	public HintPanel()
	{
		label = new JLabel(" ", JLabel.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
		setLabel(HintPanel.clickSpecificChromoStr);

		closeLabel = new JLabel();
		closeLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));

		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				closeLabel.setIcon(closeIcon1);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				closeLabel.setIcon(closeIcon2);
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Strudel.winMain.mView.showHint();
				setVisible(Prefs.showHintPanel);
			}
		});

		panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(242,242,189));
		panel.add(label);
		panel.add(closeLabel, BorderLayout.EAST);

		setLayout(new BorderLayout());
		add(panel);

		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(0, 0, 2, 0),
			BorderFactory.createLineBorder(new Color(167, 166, 170))));
	}

	public void setIcons(ImageIcon labelIcon, ImageIcon closeIcon, ImageIcon closeIconGS)
	{
		label.setIcon(labelIcon);
		closeLabel.setIcon(closeIconGS);

		closeIcon1 = closeIconGS;
		closeIcon2 = closeIcon;
	}

	public static void setLabel(String str)
	{
		label.setText(str);
	}

	public static void clearLabel()
	{
		label.setText(" ");
	}

	public static void upDate()
	{
		setLabel(HintPanel.overviewStr);

		GChromoMap selectedMap = Strudel.winMain.mouseHandler.mouseOverHandler.selectedMap;
		if(selectedMap != null)
		{
			if(selectedMap.owningSet.zoomFactor >= selectedMap.owningSet.singleChromoViewZoomFactor)
				HintPanel.setLabel(HintPanel.zoomInStr);
			else if(selectedMap.highlight && Strudel.winMain.mainCanvas.drawLinks)
				HintPanel.setLabel(HintPanel.altClickStr);
		}
		Strudel.winMain.hintPanel.repaint();
	}




}