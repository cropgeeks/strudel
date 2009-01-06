package sbrn.mapviewer.gui.components;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

public class HomologResultsTable extends JTable
{
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
			setText("<html><u><font color=blue>" + (String) table.getModel().getValueAt(row, column) +
							"</font></u></html>");
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
