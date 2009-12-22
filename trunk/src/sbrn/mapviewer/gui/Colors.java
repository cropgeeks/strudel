package sbrn.mapviewer.gui;

import java.awt.*;

public class Colors
{

	//genomes and chromosomes
	public static Color genomeColour = new Color(0, 50, 155);//blue
	public static Color outlineColour = Color.white;
	public static Color invertedChromosomeColour = new Color(0,36,18);//green
	public static Color invertedChromosomeHighlightColour = new Color(0,59,43);//light green
	public static Color chromosomeHighlightColour = new Color(80,0,0); // red

	//colours for links
	public static Color linkColour = new Color(120,120,120);
	public static Color strongEmphasisLinkColour = new Color(130,0, 0);
	public static Color mildEmphasisLinkColour =  Color.WHITE;

	//various labels
	public static Color chromosomeIndexColour = Color.white;
	public static Color genomeLabelPanelColour = new Color(170, 170, 170);
	public static Color distanceMarkerColour = new Color(140,140,150);

	//features
	public static Color featureColour = new Color(180,180,180);
	public static Color highlightedFeatureColour = strongEmphasisLinkColour;
	public static Color featureLabelColour = Color.BLACK;
	public static Color highlightedFeatureLabelColour = Color.WHITE;

	//backgrounds
	public static Color highlightedFeatureLabelBackgroundColour = strongEmphasisLinkColour;
	public static Color distanceMarkerBackgroundColour = new Color(70,70,70);

	//regions
	public static Color panZoomRectOutlineColour = Color.red;
	public static Color panZoomRectFillColour = new Color(1f, 1f, 1f, 0.25f);
	public static Color selectionRectOutlineColour = Color.green;
	public static Color selectionRectFillColour = new Color(1f, 1f, 1f, 0.25f);

	//overview canvas
	public static Color overviewCanvasTransparentPaint = new Color(1,1,1,0.25f);
	public static Color overviewCanvasSelectionRectColour = Color.red;
	public static Color  overviewCanvasBackgroundColour = Color.white;

	//main canvas
	public static Color mainCanvasBackgroundColour = Color.black;
	public static Color backgroundGradientStartColour = Color.black;
	public static Color backgroundGradientEndColour = new Color(110, 110, 110);

}
