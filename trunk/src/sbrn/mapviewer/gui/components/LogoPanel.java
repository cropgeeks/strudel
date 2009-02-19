package sbrn.mapviewer.gui.components;

import java.awt.*;
import javax.swing.*;
import sbrn.mapviewer.gui.*;

public class LogoPanel extends JPanel
{
	private static ImageIcon logo = Icons.getIcon("SCRILARGE");
	
	LogoPanel(LayoutManager lm)
	{
		super(lm);
		setBackground(Color.white);
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		
		Graphics2D g = (Graphics2D) graphics;
		
		int w = getWidth();
		int h = getHeight();
		
		g.drawImage(logo.getImage(), 0, 0, w, w, null);
	}
}
