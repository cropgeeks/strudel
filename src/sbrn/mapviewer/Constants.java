package sbrn.mapviewer;

public class Constants
{

	//size of the splitpane divider when visible
	public static final int SPLITPANE_DIVIDER_SIZE = 7;

	//the maximum zoom factor we will ever want to use
	public static final int MAX_ZOOM_FACTOR = 300;

	//the number of distance markers we want to draw on a chromosome -- fixed regardless of zoom factor
	public static final int  numDistanceMarkers = 25;

	//the maximum curvature coefficient for the links
	//this determines the shape of the curve, if any
	public static final float MAX_CURVEDLINK_COEFF = 0.3f;
	public static final float MAX_ANGLEDLINK_COEFF = 0.15f;
	public static final int LINKTYPE_CURVED = 0;
	public static final int LINKTYPE_STRAIGHT = 1;
	public static final int LINKTYPE_ANGLED = 2;

	//example data file paths
	public static final String exampleDataAllInOne = "data/BarleyRiceBrachySingleLineFormat.strudel";

	//example data descriptions
	public static final String exampleGenome1Description  = "Brachypodium distachyon 8x genome sequence; http://www.brachypodium.org/";
	public static final String exampleGenome2Description = "Barley Illumina SNPS consensus map UCR_20080416-2 (Close et al.); http://www.biomedcentral.com/1471-2164/10/582/abstract";
	public static final String exampleGenome3Description  = "Release 6 of the Rice Pseudomolecules and Genome Annotation, MSU Rice Genome Annotation Project; http://rice.plantbiology.msu.edu/";

	//URLs for annotation info for the reference example files we provide with the app
	public static final String exampleTargetGenomeBaseURL = "http://penguin.scri.ac.uk/paul/germinate/germinate_development/app/flapjack/flapjack_search/search.pl?marker=";
	public static final String exampleRefGenome1BaseURL = "";
	public static final String exampleRefGenome2BaseURL = "http://rice.plantbiology.msu.edu/cgi-bin/gbrowse/rice/?name=";

	//documentation and download site URL for the application
	public static final String strudelHomePage = "http://bioinf.scri.ac.uk/strudel/";
	public static final String strudelManualPage = "http://bioinf.scri.ac.uk/strudel/help/quickstart.shtml";
	public static final String strudelQuickStartPage = "http://bioinf.scri.ac.uk/strudel/help/quickstart.shtml";
	public static final String strudelPublicationsPage = "http://bioinf.scri.ac.uk/strudel/publications.shtml";
	public static final String strudelWhatsNewPage = "http://bioinf.scri.ac.uk/strudel/help/whatsnew.shtml";
	public static final String scriHTML = "http://www.scri.ac.uk";

	//these are the individual help links that are listed on the front page
	public static final String [] helpLabels = new String[]{"Data format and data loading", "General Controls", "View Settings", "Feature Exploration"};
	public static final String manual0 = "http://bioinf.scri.ac.uk/strudel/help/data.shtml";
	public static final String manual1 = "http://bioinf.scri.ac.uk/strudel/help/controls.shtml";
	public static final String manual2 = "http://bioinf.scri.ac.uk/strudel/help/view.shtml";
	public static final String manual3= "http://bioinf.scri.ac.uk/strudel/help/exploration.shtml";
}
