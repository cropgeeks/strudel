package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.font.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class HomologResultsTable extends JTable
{
	
	//===============================================vars=========================================		
	
	HyperlinkCellRenderer hyperlinkCellRenderer = new HyperlinkCellRenderer();
	
	//===============================================c'tor=========================================	
	
	public HomologResultsTable()
	{
		//configure table for selections
		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
	}
	
	//===============================================methods=========================================	
	
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		if (column == 3)
		{
			return hyperlinkCellRenderer;
		}
		
		return super.getCellRenderer(row, column);
	}
	

	//===============================================inner classes=========================================
	
	class HyperlinkCellRenderer extends JLabel implements TableCellRenderer
	{
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			// get the index of the selected row but check for changes due to filtering
			int modelRow = table.convertRowIndexToModel(row);
			
			// set the font up so it's blue and underlined to make it look like a hyperlink
			Map<TextAttribute, Integer> attributes = new Hashtable<TextAttribute, Integer>();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			Font font = new Font(attributes);
			setFont(font);
			setForeground(Color.blue);
			
			// this is what we want to print to the cell
			setText((String) table.getModel().getValueAt(modelRow, column));
			
			// selection colors etc
			if (isSelected)
			{
				setBackground(table.getSelectionBackground());
				setOpaque(true);
			}
			else
			{
				setOpaque(false);
			}
			
			
			return this;
		}
		
	}

	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
}
