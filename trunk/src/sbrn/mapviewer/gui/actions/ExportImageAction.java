package sbrn.mapviewer.gui.actions;

import java.awt.event.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import sbrn.mapviewer.*;
import scri.commons.gui.*;

public class ExportImageAction extends AbstractAction
{

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save Image As");
		fc.setAcceptAllFileFilterUsed(false);
		// TODO: track current directories and offer a suitable filename
		fc.setSelectedFile(new File("mapviewer.png"));
		
		while (fc.showSaveDialog(MapViewer.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			
			// Confirm overwrite
			if (file.exists())
			{
				String msg = file + " already exists.\nContinuing will "
				+ "overwrite this file with your new image.";
				String[] options = new String[] { "Overwrite", "Rename", "Cancel" };
				
				int response = TaskDialog.show(msg, MsgBox.WAR, 0, options);
				
				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return;
			}
			
			try
			{
				ImageIO.write(MapViewer.winMain.mainCanvas.getImageBuffer(), "png", file);
				TaskDialog.info("The exported image was successfully saved "
								+ " to " + file, "Close");
			}
			catch (Exception exception)
			{
				TaskDialog.error("An internal error has prevented the image "
								+ "from being exported correctly.\n\nError details: "
								+ exception.getMessage(), "Close");
			}
			
			return;
		}
		
	}
	
}
