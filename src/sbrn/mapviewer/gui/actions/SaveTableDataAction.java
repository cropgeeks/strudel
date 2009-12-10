package sbrn.mapviewer.gui.actions;

import java.awt.event.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.components.*;
import scri.commons.file.*;
import scri.commons.gui.*;

public class SaveTableDataAction extends AbstractAction
{

	//===========================================methods===========================================

	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save table data as");
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		File outputFile = new File("tableData.txt");
		fc.setSelectedFile(outputFile);

		while (fc.showSaveDialog(Strudel.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			// Confirm overwrite
			if (file.exists())
			{
				String msg = file.getAbsolutePath() + " already exists.\nContinuing will "
				+ "overwrite this file with your new file.";
				String[] options = new String[] { "Overwrite", "Rename", "Cancel" };

				int response = TaskDialog.show(msg, TaskDialog.WAR, 0, options);

				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return;
			}

			try
			{
				//write table data to file
				FileUtils.writeFile(file, extractResultsTableData());

				if(file.exists())
					TaskDialog.info("The data was successfully saved  to " + file, "Close");
				else
					TaskDialog.error("Error: data has not been saved.","Close");

			}
			catch (NullPointerException npx)
			{
				TaskDialog.error("File could not be saved -- access denied.", "Close");
			}
			catch (IOException e1)
			{
				TaskDialog.error("An internal error has prevented the data "
								+ "from being exported correctly.\n\nError details: "
								+ e1.getMessage(), "Close");
			}

			return;
		}

	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------

	private String extractResultsTableData()
	{
		//get the table model first
		TableModel model = Strudel.winMain.ffResultsPanel.resultsTable.getModel();
		return ((HomologResultsTableModel)model).getAllDataInTabFormat();
	}


	//------------------------------------------------------------------------------------------------------------------------------------------------------

}
