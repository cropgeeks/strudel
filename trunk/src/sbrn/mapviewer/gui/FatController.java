package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import javax.swing.table.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;

public class FatController
{
	
	// ===============================================vars===================================
	
	private WinMain winMain;
	public Vector<Feature> foundFeatures = new Vector<Feature>();
	public Vector<Feature> featuresInRange = new Vector<Feature>();
	public Vector<Feature> foundFeatureHomologs = new Vector<Feature>();
	public static GChromoMap invertMap = null;
	
	
	
	// ===============================================c'tors===================================
	
	public FatController(WinMain winMain)
	{
		this.winMain = winMain;
	}
	
	// ===============================================methods===================================
	
	// repaint the overview canvases
	public void updateOverviewCanvases()
	{
		for(OverviewCanvas overviewCanvas : winMain.overviewCanvases)
		{
			overviewCanvas.repaint();
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//update visible zoom info
	public void updateZoomControls()
	{
		for (ZoomControlPanel zoomControlPanel : winMain.zoomControlPanels)
		{
			zoomControlPanel.updateSlider();
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void changeBackgroundColour(String newColour)
	{
		Color colour = null;
		
		if (newColour.equals("black"))
		{
			colour = Color.BLACK;
		}
		else if (newColour.equals("light grey"))
		{
			colour = Color.LIGHT_GRAY;
		}
		else if (newColour.equals("dark grey"))
		{
			colour = Color.DARK_GRAY;
		}
		else if (newColour.equals("white"))
		{
			colour = Color.white;
		}
		
		// set all canvas backgrounds to the same colour
		winMain.mainCanvas.setBackground(colour);
		
		// update the display
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void initialisePositionArrays()
	{
		MapViewer.logger.finest("indexing position arrays");
		long startTime = System.nanoTime();
		// for all gmapsets
		for (GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				gChromoMap.initArrays();
			}
		}
		
		// update the display
		winMain.mainCanvas.updateCanvas(true);
		MapViewer.logger.finest(" done indexing position arrays");
		MapViewer.logger.finest("time taken (nanos) = " + (System.nanoTime() - startTime));
	}
	

	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public FoundFeatureTableModel makeFoundFeaturesDataModel(String [] featureNames)
	{
		LinkedList<Link> homologies = new LinkedList<Link>();

		//parse the strings out into the table model and populate as appropriate
		for (int i = 0; i < featureNames.length; i++)
		{
			//retrieve the Feature that corresponds to this name
			Feature f = Utils.getFeatureByName(featureNames[i].trim());
			if (f != null)
			{
				//get all the links this feature is involved in
				//for each link
				for (Link link : f.getLinks())
				{
					//create a new entry in the homologies list
					homologies.add(link);
				}
			}
		}
		
		return new FoundFeatureTableModel(homologies);
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void highlightRequestedFeature(Feature f)
	{		
		winMain.mainCanvas.drawFoundFeatures = true;
		
		//clear the found features
		if(foundFeatures != null)
			foundFeatures.clear();
		if(foundFeatureHomologs != null)
			foundFeatureHomologs.clear();

		//add the feature itself to the found features vector
		foundFeatures.add(f);
		
		for(Link link : f.getLinks())
		{
			//get both features from the link and put the homologue into the homologues vector
			
			//get the features of this link
			Feature f1 = link.getFeature1();
			Feature f2 = link.getFeature2();
			
			//check whether either of the features for this link are included in the highlightedfeatures list for its map
			if(!foundFeatures.contains(f1) && f1 != f)
			{
				foundFeatureHomologs.add(f1);
			}
			if(!foundFeatures.contains(f2) && f2 != f)
			{
				foundFeatureHomologs.add(f2);
			}
		}								
		
		// update the display
		winMain.mainCanvas.antiAlias = true;
		winMain.mainCanvas.updateCanvas(true);
	}
	
//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
//	restores the original view to what it looked like after loading the current dataset
	public void resetMainCanvasView()
	{	
		//hide the found features part of the split pane
		winMain.hideSplitPaneBottomHalf();
		winMain.splitPane.setDividerLocation(1.0);

		//clear the table model for the found features
		MapViewer.winMain.ffResultsPanel.getFFResultsTable().setModel(new DefaultTableModel());
				
		//clear the found features
		if(foundFeatures != null)
			foundFeatures.clear();
		if(foundFeatureHomologs != null)
			foundFeatureHomologs.clear();
		if(featuresInRange != null)
			featuresInRange.clear();		
		winMain.mainCanvas.drawFoundFeatures = false;
		winMain.mainCanvas.drawFoundFeaturesInRange = false;

		for(GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			//reset zoom on all mapsets
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet, 500);
			
			//reset selected maps
			gMapSet.selectedMaps.clear();
			
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{			
				//clear the outline
				gMap.drawHighlightOutline = false;
				
				//any inverted maps have to be flagged as non-inverted
				gMap.isPartlyInverted = false;
				
				//clear any highlighted regions
				gMap.highlightChromomapRegion = false;
			}			
		}	
		
		initialisePositionArrays();
		
		//repaint
		winMain.mainCanvas.updateCanvas(true);
	}
	
	
	
//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class






