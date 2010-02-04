// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
			public void mouseExited(MouseEvent e) {
				closeLabel.setIcon(closeIcon1);
			}
			public void mouseEntered(MouseEvent e) {
				closeLabel.setIcon(closeIcon2);
			}
			public void mouseClicked(MouseEvent e)
			{
				// TODO: Call setVisible(false) and update prefs/toolbar state
				System.out.println("hide me");
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