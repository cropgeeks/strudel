// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;

public class HintPanel extends JPanel
{
	private static JPanel panel;
	private static JLabel label;
	private static JLabel closeLabel;

	private ImageIcon closeIcon1, closeIcon2;

	public HintPanel()
	{
		label = new JLabel(" ", JLabel.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

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
				Prefs.showHintPanel = false;
				Strudel.winMain.configureViewSettingsDialog.viewSettingsPanel.getHintPanelCheckBox().setSelected(false);
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
}