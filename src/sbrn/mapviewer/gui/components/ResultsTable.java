package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.font.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

public class ResultsTable extends JTable
{
	
	//===============================================vars=========================================		
	
	HyperlinkCellRenderer hyperlinkCellRenderer = new HyperlinkCellRenderer();
	
	public boolean isFilterEvent = false;
	
	//===============================================curve'tor=========================================	
	
	public ResultsTable()
	{
		//configure table for selections
		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		
	        //for centering text in table
	        setDefaultRenderer(String.class, new LeftAlignedRenderer());
	        setDefaultRenderer(Integer.class, new LeftAlignedRenderer());
	        setDefaultRenderer(Float.class, new LeftAlignedRenderer());

	}
	
	//===============================================methods=========================================	
	
	public void addModelSpecificTableListeners()
	{
		//type of listener depends on the model we use for the results table
		if(getModel() instanceof sbrn.mapviewer.gui.components.HomologResultsTableModel)
		{
			HomologResultsTableListener homologResultsTableListener = new HomologResultsTableListener(this);
			addMouseMotionListener(homologResultsTableListener);
			addMouseListener(homologResultsTableListener);
			getSelectionModel().addListSelectionListener(homologResultsTableListener);
		}
		else if (getModel() instanceof  sbrn.mapviewer.gui.components.LinklessFeatureTableModel)
		{
			FoundFeaturesResultsTableListener foundFeaturesResultsTableListener = new FoundFeaturesResultsTableListener(this);
			addMouseMotionListener(foundFeaturesResultsTableListener);
			addMouseListener(foundFeaturesResultsTableListener);
			getSelectionModel().addListSelectionListener(foundFeaturesResultsTableListener);
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		boolean isURLColumn = false;
		if(getModel().getClass().getName().equals("sbrn.mapviewer.gui.components.HomologResultsTableModel"))
		{
			if (column == ((HomologResultsTableModel)getModel()).findColumn(HomologResultsTableModel.homologColumnLabel) ||
							column == ((HomologResultsTableModel)getModel()).findColumn(HomologResultsTableModel.targetNameColumnLabel))
				isURLColumn = true;
		}
		else if (getModel().getClass().getName().equals("sbrn.mapviewer.gui.components.LinklessFeatureTableModel"))
		{
			if (column == ((LinklessFeatureTableModel)getModel()).findColumn(LinklessFeatureTableModel.featureNameColumnLabel))
				isURLColumn = true;
		}
		
		if(isURLColumn)
			return hyperlinkCellRenderer;
		
		return super.getCellRenderer(row, column);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void addFeaturesFromSelectedMap(GChromoMap selectedMap)
	{
		//extract the list of features we need to insert 
		LinkedList<Link> newFeatures = new LinkedList<Link>();
		
		//the table's model
		HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel)getModel();
		
		//now find all features on the map that occur in the interval between these two coords
		for (Feature f : selectedMap.chromoMap.getFeatureList())
		{
			if(f.getStart() > selectedMap.relativeTopY && f.getStart() < selectedMap.relativeBottomY)
			{
				boolean featureExists = false;
				//first check this feature is not already contained in the table
				for(Link link : homologResultsTableModel.homologies)
				{
					if(link.getFeature1() != null)
					{
						if(link.getFeature1().equals(f))
							featureExists = true;
					}
					if(link.getFeature2() != null)
					{
						if(link.getFeature2().equals(f))
							featureExists = true;
					}
				}
				
				//if the feature is not in the table yet
				if(!featureExists)
				{
					//include this feature but turn it into half a link for now to make it match the data type used by the table
					Link link = new Link(null,f);
					newFeatures.add(link);
				}
				else
				{
					MapViewer.logger.finest("feature already exists -- not added" );
				}
			}
			
		}		
		MapViewer.logger.fine("num new features extracted = " + newFeatures.size());
		
		//add the new features/links to the table's data model
		homologResultsTableModel.homologies.addAll(0, newFeatures);
		
		//now fire a table change event to update the table
		homologResultsTableModel.fireTableRowsInserted(0, newFeatures.size()-1);
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
			setHorizontalAlignment(LEFT);
			
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
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	    /*
	 * Center the text
	 */
	class LeftAlignedRenderer extends DefaultTableCellRenderer
	{
		public LeftAlignedRenderer()
		{
			setHorizontalAlignment(LEFT);
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			return this;
		}
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//applies a regular epxression based filter to the results table
	@SuppressWarnings({"unchecked"})
	public void newFilter(String filterExpression, int index)
	{
		isFilterEvent = true;
		
		RowFilter<TableModel, Object> rf = null;
		String expr = "^" + filterExpression;
		
		try
		{
			rf = RowFilter.regexFilter(expr, index);
		}
		catch (java.util.regex.PatternSyntaxException e)
		{
			return;
		}
		
		if(filterExpression.equals("<none>"))
			((DefaultRowSorter<TableModel, Integer>) getRowSorter()).setRowFilter(null);
		else
			((DefaultRowSorter<TableModel, Integer>) getRowSorter()).setRowFilter(rf);
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void initColumnSizes()
	{
		TableColumn column = null;
		for (int i = 0; i < getColumnModel().getColumnCount(); i++)
		{
			// this is the maxWidth for entire column, header included
			int maxWidth = 0;
			
			// get the font metrics for this table
			FontMetrics fm = getFontMetrics(getFont());
			
			// get the string width for the data header for this column
			int headerWidth = fm.stringWidth(getColumnName(i));
			if (headerWidth > maxWidth)
				maxWidth = headerWidth;
			
			// get the data in this column and check their width
			for (int j = 0; j < getModel().getRowCount(); j++)
			{
				String cellContent = getModel().getValueAt(j, i).toString();
				int cellWidth = fm.stringWidth(cellContent);
				if (cellWidth > maxWidth)
					maxWidth = cellWidth;
			}
			column = getColumnModel().getColumn(i);
			column.setPreferredWidth(maxWidth);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public int getTotalTableWidth()
	{
		int width = 0;
		TableColumn column = null;
		for (int i = 0; i < getColumnModel().getColumnCount(); i++)
		{
			column = getColumnModel().getColumn(i);
			width += column.getPreferredWidth();
		}
		return width;
	}
	
	//	---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
}
