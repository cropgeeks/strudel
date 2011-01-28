package sbrn.mapviewer.gui;

import java.awt.Color;

public class PrintColourScheme extends DefaultColourScheme
{
	public PrintColourScheme()
	{
		//genomes and chromosomes
		genomeColour = Prefs.printGenomeColour;
		outlineColour = Prefs.printOutlineColour;
		invertedChromosomeColour = Prefs.printInvertedChromosomeColour;
		invertedChromosomeHighlightColour = Prefs.printInvertedChromosomeHighlightColour;
		chromosomeHighlightColour = Prefs.printChromosomeHighlightColour;

		//colours for links
		linkColour = Prefs.strudelLinkColour;
		strongEmphasisLinkColour = Prefs.printStrongEmphasisLinkColour;
		mildEmphasisLinkColour =  Prefs.printMildEmphasisLinkColour;

		//various labels
		chromosomeIndexColour = Prefs.printChromosomeIndexColour;

		//features
		featureColour = Prefs.printFeatureColour;
		highlightedFeatureColour = Prefs.printHighlightedFeatureColour;
		featureLabelColour = Prefs.printFeatureLabelColour;
		highlightedFeatureLabelColour = Prefs.printHighlightedFeatureLabelColour;

		//backgrounds
		highlightedFeatureLabelBackgroundColour = Prefs.printHighlightedFeatureLabelBackgroundColour;

		//main canvas
		backgroundGradientStartColour = Prefs.printBackgroundGradientStartColour;
		backgroundGradientEndColour = Prefs.printBackgroundGradientEndColour;
	}

	@Override
	public void resetToDefault()
	{
		//genomes and chromosomes
		genomeColour = new Color(0, 50, 155);//blue
		outlineColour = Color.white;
		invertedChromosomeColour = new Color(0,36,18);//green
		invertedChromosomeHighlightColour = new Color(0,59,43);//light green
		chromosomeHighlightColour = new Color(80,0,0); // red

		//colours for links
		linkColour = new Color(120,120,120);
		strongEmphasisLinkColour = new Color(130,0, 0);
		mildEmphasisLinkColour = Color.gray;

		//various labels
		chromosomeIndexColour = Color.black;

		//features
		featureColour = new Color(180,180,180);
		highlightedFeatureColour = strongEmphasisLinkColour;
		featureLabelColour = Color.BLACK;
		highlightedFeatureLabelColour = Color.gray;

		//backgrounds
		highlightedFeatureLabelBackgroundColour = strongEmphasisLinkColour;

		//main canvas
		backgroundGradientStartColour = Color.white;
		backgroundGradientEndColour = Color.white;
	}

	@Override
	public void setCustomColourPreferences()
	{
		Prefs.printGenomeColour = genomeColour;
		Prefs.printInvertedChromosomeColour = invertedChromosomeColour;
		Prefs.printInvertedChromosomeHighlightColour = invertedChromosomeHighlightColour;
		Prefs.printChromosomeHighlightColour = chromosomeHighlightColour;
		Prefs.printLinkColour = linkColour;
		Prefs.printStrongEmphasisLinkColour = strongEmphasisLinkColour;
		Prefs.printMildEmphasisLinkColour = mildEmphasisLinkColour;
		Prefs.printChromosomeIndexColour = chromosomeIndexColour;
		Prefs.printFeatureColour = featureColour;
		Prefs.printHighlightedFeatureColour = highlightedFeatureColour;
		Prefs.printFeatureLabelColour = featureLabelColour;
		Prefs.printHighlightedFeatureLabelColour = highlightedFeatureLabelColour;
		Prefs.printHighlightedFeatureLabelBackgroundColour = highlightedFeatureLabelBackgroundColour;
		Prefs.printBackgroundGradientStartColour = backgroundGradientStartColour;
		Prefs.printBackgroundGradientEndColour = backgroundGradientEndColour;
	}

	@Override
	public String toString()
	{
		return "Print";
	}
}
