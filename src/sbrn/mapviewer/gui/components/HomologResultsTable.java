package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.font.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

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
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void addFeaturesFromSelectedMap(GChromoMap selectedMap, float canvasIntervalTopY, float canvasIntervalBottomY)
	{
		//extract the list of features we need to insert 
		LinkedList<Link> newFeatures = new LinkedList<Link>();
		
		//first convert the canvas coords to feature positions on the chromomap
		int chromoIntervalStartPos  = Math.round(((canvasIntervalTopY  - selectedMap.y) / selectedMap.height) * selectedMap.chromoMap.getStop());
		int chromoIntervalEndPos  = Math.round(((canvasIntervalBottomY  - selectedMap.y) / selectedMap.height) * selectedMap.chromoMap.getStop());

		//the table's model
		FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel)getModel();
		
		//now find all features on the map that occur in the interval between these two coords
		for (Feature f : selectedMap.chromoMap.getFeatureList())
		{
			if(f.getStart() > chromoIntervalStartPos && f.getStart() < chromoIntervalEndPos)
			{
				boolean featureExists = false;
				//first check this feature is not already contained in the table
				for(Link link : foundFeatureTableModel.homologies)
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
		foundFeatureTableModel.homologies.addAll(0, newFeatures);
		
		//now fire a table change event to update the table
		foundFeatureTableModel.fireTableRowsInserted(0, newFeatures.size()-1);
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
