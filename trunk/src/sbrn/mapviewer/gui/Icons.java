package sbrn.mapviewer.gui;

import java.lang.reflect.*;
import javax.swing.*;

public class Icons
{
	public static ImageIcon EXPORTIMAGE;
	public static ImageIcon FILEOPEN;
	public static ImageIcon HELP;
	public static ImageIcon OVERVIEW;
	public static ImageIcon RESET;
	public static ImageIcon ZOOM;
	public static ImageIcon FIND;

	private Icons()
	{
	}

	public static void initialize()
	{
		Icons icons = new Icons();
		Class c = icons.getClass();

		try
		{
			Field[] fields = c.getFields();
			for (Field field : fields)
			{
				if (field.getType() == ImageIcon.class)
				{
					String name = field.getName().toLowerCase() + ".png";

					ImageIcon icon = new ImageIcon(c.getResource("/res/icons/" + name));

					field.set(null, icon);
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to load one or more required icons.", e);
		}
	}
}