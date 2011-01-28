package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class FoundFeaturesResultsPanel extends JPanel
{
	//===========================================vars===========================================

	GChromoMap previousMap = null;

	public ResultsTable resultsTable = null;
	JLabel resultsLabel = null;
	JLabel closeButton = null;

	//===========================================curve'tor===========================================

	/** Creates new form MTFindFeaturesResultsPanel */
	public FoundFeaturesResultsPanel()
	{
		super(new BorderLayout());

		String title = "Click on a row to highlight a homolog (multiple selection: Ctrl-click). Click on a homolog name to show annotation in a web browser: ";
		setBorder(BorderFactory.createTitledBorder(title));

		//this button closes the results table panel and resets the main canvas view to the original settings
		closeButton = new JLabel();
		closeButton.setIcon(Icons.getIcon("FILECLOSE"));
		JPanel buttonPanel = new JPanel(new BorderLayout());
		this.add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.add(closeButton, BorderLayout.EAST);
		closeButton.setToolTipText("Close results table and reset view");
		closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		closeButton.addMouseListener(new MouseInputAdapter()
						{
							@Override
							public void mouseClicked(MouseEvent e)
							{
								Strudel.winMain.fatController.resetMainCanvasView();
							}

							@Override
							public void mouseEntered(MouseEvent e)
							{
								closeButton.setIcon(Icons.getIcon("FILECLOSEHIGHLIGHTED"));
							}
							@Override
							public void mouseExited(MouseEvent e)
							{
								closeButton.setIcon(Icons.getIcon("FILECLOSE"));
							}
						}
		);

		//the results table
		resultsTable = new ResultsTable();

		JScrollPane scrollPane = new JScrollPane(resultsTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		this.add(scrollPane, BorderLayout.CENTER);
		//set the table up for sorting
		resultsTable.setAutoCreateRowSorter(true);
	}

	//===========================================methods===========================================

	public JTable getFFResultsTable()
	{
		return resultsTable;
	}



	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public JLabel getResultsLabel()
	{
		return resultsLabel;
	}


	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}//end class
