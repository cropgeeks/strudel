package sbrn.mapviewer;

public class Constants
{
	//types of genome represented by this app
	public static final int TARGET_GENOME = 1;
	public static final int REFERENCE_GENOME = 2;
	
	//size of the splitpane divider when visible
	public static final int SPLITPANE_DIVIDER_SIZE = 6;
	
	//the maximum zoom factor we will ever want to use
	public static final int MAX_ZOOM_FACTOR = 500;
	
	//the number of distance markers we want to draw on a chromosome -- fixed regardless of zoom factor
	public static final int  numDistanceMarkers = 30;
	
	//URLs for annotation info for the reference example files we provide with the app
	public static final String exampleRefGenome1BaseURL = "";
	public static final String exampleRefGenome2BaseURL = "http://rice.plantbiology.msu.edu/cgi-bin/gbrowse/rice/?name=";
	
}
