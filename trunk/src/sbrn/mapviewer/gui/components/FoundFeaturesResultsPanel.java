package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.actions.*;
import sbrn.mapviewer.gui.entities.*;

public class FoundFeaturesResultsPanel extends JPanel
{
	//===========================================vars===========================================	
	
	GChromoMap previousMap = null;
	
	HomologResultsTable resultsTable = null;
	JLabel resultsLabel = null;
	
	//===========================================c'tor===========================================		
	
	/** Creates new form MTFindFeaturesResultsPanel */
	public FoundFeaturesResultsPanel()
	{
		super(new BorderLayout());
		
		String title = "Click on a row to highlight a homolog. Click on a homolog name to show annotation in a web browser: ";
		setBorder(BorderFactory.createTitledBorder(title));
		
		//this button closes the results table panel and resets the main canvas view to the original settings
		JLabel closeButton = new JLabel();
		closeButton.setIcon(Icons.getIcon("FILECLOSE"));
		JPanel buttonPanel = new JPanel(new BorderLayout());
		this.add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.add(closeButton, BorderLayout.EAST);
		closeButton.setToolTipText("Close results table and reset view");
		closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		closeButton.addMouseListener(new MouseInputAdapter()
						{
							public void mouseClicked(MouseEvent e)
							{
								MapViewer.winMain.fatController.resetMainCanvasView();
							}
						}
		);
		
		//the results table
		resultsTable = new HomologResultsTable();
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
	
	public int getTotalTableWidth()
	{
		int width = 0;
		TableColumn column = null;
		for (int i = 0; i < resultsTable.getColumnModel().getColumnCount(); i++)
		{
			column = resultsTable.getColumnModel().getColumn(i);
			width += column.getPreferredWidth();
		}
		return width;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void initColumnSizes()
	{
		TableColumn column = null;
		for (int i = 0; i < resultsTable.getColumnModel().getColumnCount(); i++)
		{
			// this is the maxWidth for entire column, header included
			int maxWidth = 0;
			
			// get the font metrics for this table
			FontMetrics fm = resultsTable.getFontMetrics(resultsTable.getFont());
			
			// get the string width for the data header for this column
			int headerWidth = fm.stringWidth(resultsTable.getColumnName(i));
			if (headerWidth > maxWidth)
				maxWidth = headerWidth;
			
			// get the data in this column and check their width
			for (int j = 0; j < resultsTable.getModel().getRowCount(); j++)
			{
				String cellContent = resultsTable.getModel().getValueAt(j, i).toString();
				int cellWidth = fm.stringWidth(cellContent);
				if (cellWidth > maxWidth)
					maxWidth = cellWidth;
			}
			column = resultsTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(maxWidth);
		}
	}

	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public JLabel getResultsLabel()
	{
		return resultsLabel;
	}

	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}//end class
