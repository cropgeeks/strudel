package sbrn.mapviewer.gui.actions;

import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.components.WinMain;
import scri.commons.gui.*;

public class ExportImageAction extends AbstractAction
{

	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save Image As");
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		// TODO: track current directories and offer a suitable filename
		fc.setSelectedFile(new File("mapviewer.png"));

		while (fc.showSaveDialog(Strudel.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			// Confirm overwrite
			if (file.exists())
			{
				String msg = file + " already exists.\nContinuing will "
				+ "overwrite this file with your new image.";
				String[] options = new String[] { "Overwrite", "Rename", "Cancel" };

				int response = TaskDialog.show(msg, TaskDialog.WAR, 0, options);

				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return;
			}

			try
			{
				BufferedImage main = WinMain.mainCanvas.getImageBuffer();
				BufferedImage label = Strudel.winMain.genomeLabelPanel.createExportBuffer();
				BufferedImage totalImage = new BufferedImage(main.getWidth(), main.getHeight()+label.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = totalImage.createGraphics();
				g.drawImage(main, null, 0, 0);
				g.drawImage(label, null, 0, main.getHeight());
				ImageIO.write(totalImage, "png", file);
				TaskDialog.info("The exported image was successfully saved "
								+ " to " + file, "Close");
			}
			catch (NullPointerException npx)
			{
				TaskDialog.error("File could not be saved -- access denied.", "Close");
			}
			catch (IOException e1)
			{
				TaskDialog.error("An internal error has prevented the image "
								+ "from being exported correctly.\n\nError details: "
								+ e1.getMessage(), "Close");
			}

			return;
		}

	}

}
