package sbrn.mapviewer;

import java.io.*;

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
	
	//the maximum curvature coefficient for the links
	//this determines the shape of the curve, if any
	public static final float MAX_CURVATURE_COEFF = 0.3f;
	
	//example data file paths	
	public static final String exampleTargetData =  "data/barleyExampleData.txt";
	public static final String exampleRefGenome1FeatData =  "data/artificial_genome.txt";
	public static final String exampleRefGenome1HomData =  "data/artificial_genome_homology data.txt";
	public static final String exampleRefGenome2FeatData =  "data/rice_TIGR_5.txt";
	public static final String exampleRefGenome2HomData =  "data/barleyRiceHomologyData.txt";
	
	//URLs for annotation info for the reference example files we provide with the app
	public static final String exampleRefGenome1BaseURL = "";
	public static final String exampleRefGenome2BaseURL = "http://rice.plantbiology.msu.edu/cgi-bin/gbrowse/rice/?name=";
	
}
