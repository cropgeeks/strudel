package sbrn.mapviewer.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class StatusBar extends JLabel
{
	public final String DEFAULT_TEXT = "Idle";
	
	public StatusBar()
	{
		super();
		super.setPreferredSize(new Dimension(100, 16));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setDefaultText();
	}
	
	public void setMessage(String message)
	{
		setText(" " + message);
	}
	
	public void setDefaultText()
	{
		setText(DEFAULT_TEXT);
	}
}
