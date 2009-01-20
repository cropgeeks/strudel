package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.font.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

public class HomologResultsTable extends JTable
{
	public HomologResultsTable()
	{
	}
	
	HyperlinkCellRenderer hyperlinkCellRenderer = new HyperlinkCellRenderer();
	
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		if (column == 3)
		{
			return hyperlinkCellRenderer;
		}
		
		return super.getCellRenderer(row, column);
	}
	
	class HyperlinkCellRenderer extends JLabel implements TableCellRenderer
	{

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			//get the index of the selected row but check for changes due to filtering
			int modelRow = table.convertRowIndexToModel(row);

			//set the font up so it's blue and underlined to make it look like a hyperlink
			Map attributes = new Hashtable<Integer, Integer>();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			Font font = new Font(attributes);
			setFont(font);
			setForeground(Color.blue);
			
			//this is what we want to print to the cell
			setText((String) table.getModel().getValueAt(modelRow, column));
			
			//selection colors etc
			if(isSelected)
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
	
}
