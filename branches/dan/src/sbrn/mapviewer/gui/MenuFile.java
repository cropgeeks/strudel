package sbrn.mapviewer.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;
import sbrn.mapviewer.Strudel;
import sbrn.mapviewer.gui.components.HomologResultsTableModel;
import sbrn.mapviewer.gui.components.WinMain;
import sbrn.mapviewer.gui.dialog.OpenFileDialog;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.io.DataLoadUtils;
import scri.commons.file.FileUtils;
import scri.commons.gui.TaskDialog;

/**
 * Stores the methods which carry out the actions which are fired whenever the
 * File menu is interacted with.
 */
public class MenuFile
{
	public void loadData()
	{
	    //open the file dialog
	    OpenFileDialog openFileDialog = Strudel.winMain.openFileDialog;
	    openFileDialog.setLocationRelativeTo(Strudel.winMain);
	    openFileDialog.setVisible(true);

	    //clear the text fields, in case they had text showing previously
	    openFileDialog.openFilesPanel.getInputFileTF().setText("");
	}

	void loadExample()
	{
		Strudel.winMain.fatController.loadOwnData = false;
		DataLoadUtils.loadDataInThread(null, false);
	}

	public void openFile(File file)
	{
		Strudel.winMain.fatController.recentFileLoad = true;
		DataLoadUtils.loadDataInThread(file.toString(), false);
	}

	void exportImage()
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


	public void saveTableData()
	{
		saveResults("Save table data as", "tableData.txt", extractResultsTableData());
	}

	public void saveMapOrder()
	{
		saveResults("Save map order as", "mapOrder.txt", extractMapOrder());
	}

	//saves results in text format to a file
	private void saveResults(String fileChooserTitle, String fileName, String data)
	{
	    JFileChooser fc = new JFileChooser();
	    fc.setDialogTitle(fileChooserTitle);
	    fc.setCurrentDirectory(new File(System.getProperty("user.home")));
	    File outputFile = new File(fileName);
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
			    FileUtils.writeFile(file, data);

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

	//gets the table data from the table and returns them in tab delimited format for saving out to file
	private String extractResultsTableData()
	{
		//get the table model first
		TableModel model = Strudel.winMain.ffResultsPanel.resultsTable.getModel();
		return ((HomologResultsTableModel)model).getAllDataInTabFormat();
	}

	//extracts the order of all maps in their mapsets and formats them for saving to a tab del text file
	private String extractMapOrder()
	{
		StringBuilder sb = new StringBuilder();

		//print header with mapsets' names first
		for(GMapSet gMapSet : Strudel.winMain.dataContainer.gMapSets)
		{
			sb.append(gMapSet.name + "\t");
		}

		//line break
		sb.append("\n");

		//iterate over the mapsets and maps
		for (int i = 0; i < Strudel.winMain.dataContainer.maxChromos; i++)
		{
			for(GMapSet gMapSet : Strudel.winMain.dataContainer.gMapSets)
			{
				GChromoMap gMap  = null;
				if(gMapSet.gMaps.size() > i)
				{
					gMap  = gMapSet.gMaps.get(i);
					//output map name
					sb.append(gMap.name + "\t");
				}
				else
				{
					//if there is no map for this row, just output an empty String and tab char
					sb.append(" \t");
				}
			}
			//line break
			sb.append("\n");
		}

		return sb.toString();
	}

}
