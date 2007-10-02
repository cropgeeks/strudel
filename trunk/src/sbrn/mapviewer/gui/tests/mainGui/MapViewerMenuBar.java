package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.event.*;
import javax.swing.*;


public class MapViewerMenuBar extends JMenuBar implements ActionListener
{

	JMenuItem openFileItem;
	MapViewerFrame frame;
	
	public MapViewerMenuBar(MapViewerFrame frame)
	{
		this.frame = frame;
		init();
	}

	private void init()
	{

		// the File Menu
		JMenu fileMenu = new JMenu("File");
		this.add(fileMenu);

		// the Open File item
		openFileItem = new JMenuItem("Open...");
		openFileItem.addActionListener(this);
		fileMenu.add(openFileItem);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if(src.equals(openFileItem))
		{
			System.out.println("open file selected");
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(frame);
		}
	}

}
