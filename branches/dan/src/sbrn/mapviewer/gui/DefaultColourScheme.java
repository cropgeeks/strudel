package sbrn.mapviewer.gui;

import java.awt.Color;
import java.util.ArrayList;

public class DefaultColourScheme
{
	//genomes and chromosomes
	protected Color genomeColour;
	protected Color outlineColour;
	protected Color invertedChromosomeColour;
	protected Color invertedChromosomeHighlightColour;
	protected Color chromosomeHighlightColour;

	//colours for links
	protected Color linkColour;
	protected Color strongEmphasisLinkColour;
	protected Color mildEmphasisLinkColour;

	//various labels
	protected Color chromosomeIndexColour;

	//features
	protected Color featureColour;
	protected Color highlightedFeatureColour;
	protected Color featureLabelColour;
	protected Color highlightedFeatureLabelColour;

	//backgrounds
	protected Color highlightedFeatureLabelBackgroundColour;

	//main canvas
	protected Color backgroundGradientStartColour;
	protected Color backgroundGradientEndColour;

	public DefaultColourScheme()
	{
		//genomes and chromosomes
		genomeColour = Prefs.strudelGenomeColour;
		outlineColour = Prefs.strudelOutlineColour;
		invertedChromosomeColour = Prefs.strudelInvertedChromosomeColour;
		invertedChromosomeHighlightColour = Prefs.strudelInvertedChromosomeHighlightColour;
		chromosomeHighlightColour = Prefs.strudelChromosomeHighlightColour;

		//colours for links
		linkColour = Prefs.strudelLinkColour;
		strongEmphasisLinkColour = Prefs.strudelStrongEmphasisLinkColour;
		mildEmphasisLinkColour =  Prefs.strudelMildEmphasisLinkColour;

		//various labels
		chromosomeIndexColour = Prefs.strudelChromosomeIndexColour;

		//features
		featureColour = Prefs.strudelFeatureColour;
		highlightedFeatureColour = Prefs.strudelHighlightedFeatureColour;
		featureLabelColour = Prefs.strudelFeatureLabelColour;
		highlightedFeatureLabelColour = Prefs.strudelHighlightedFeatureLabelColour;

		//backgrounds
		highlightedFeatureLabelBackgroundColour = Prefs.strudelHighlightedFeatureLabelBackgroundColour;

		//main canvas
		backgroundGradientStartColour = Prefs.strudelBackgroundGradientStartColour;
		backgroundGradientEndColour = Prefs.strudelBackgroundGradientEndColour;
	}

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
		mildEmphasisLinkColour = Color.WHITE;

		//various labels
		chromosomeIndexColour = Color.white;

		//features
		featureColour = new Color(180,180,180);
		highlightedFeatureColour = strongEmphasisLinkColour;
		featureLabelColour = Color.BLACK;
		highlightedFeatureLabelColour = Color.WHITE;

		//backgrounds
		highlightedFeatureLabelBackgroundColour = strongEmphasisLinkColour;

		//main canvas
		backgroundGradientStartColour = Color.black;
		backgroundGradientEndColour = new Color(110, 110, 110);
	}

	public void setCustomColourPreferences()
	{
		Prefs.strudelGenomeColour = genomeColour;
		Prefs.strudelInvertedChromosomeColour = invertedChromosomeColour;
		Prefs.strudelInvertedChromosomeHighlightColour = invertedChromosomeHighlightColour;
		Prefs.strudelChromosomeHighlightColour = chromosomeHighlightColour;
		Prefs.strudelLinkColour = linkColour;
		Prefs.strudelStrongEmphasisLinkColour = strongEmphasisLinkColour;
		Prefs.strudelMildEmphasisLinkColour = mildEmphasisLinkColour;
		Prefs.strudelChromosomeIndexColour = chromosomeIndexColour;
		Prefs.strudelFeatureColour = featureColour;
		Prefs.strudelHighlightedFeatureColour = highlightedFeatureColour;
		Prefs.strudelFeatureLabelColour = featureLabelColour;
		Prefs.strudelHighlightedFeatureLabelColour = highlightedFeatureLabelColour;
		Prefs.strudelHighlightedFeatureLabelBackgroundColour = highlightedFeatureLabelBackgroundColour;
		Prefs.strudelBackgroundGradientStartColour = backgroundGradientStartColour;
		Prefs.strudelBackgroundGradientEndColour = backgroundGradientEndColour;
	}

	public ArrayList<ColourInfo> getColours()
	{
		ArrayList<ColourInfo> colours = new ArrayList<ColourInfo>();

		colours.add(new ColourInfo(genomeColour, "Chromosome"));
		colours.add(new ColourInfo(invertedChromosomeColour, "Inverted Chromosome"));
		colours.add(new ColourInfo(invertedChromosomeHighlightColour, "Inverted Chromosome Highlight"));
		colours.add(new ColourInfo(chromosomeHighlightColour, "Chromosome Highlight"));

		colours.add(new ColourInfo(linkColour, "Link"));
		colours.add(new ColourInfo(strongEmphasisLinkColour, "Strong Emphasis Link"));
		colours.add(new ColourInfo(mildEmphasisLinkColour, "Mild Emphasis Link"));

		colours.add(new ColourInfo(chromosomeIndexColour, "Chromosome Index"));

		colours.add(new ColourInfo(featureColour, "Feature"));
		colours.add(new ColourInfo(highlightedFeatureColour, "Highlighted Feature"));
		colours.add(new ColourInfo(featureLabelColour, "Feature Label"));
		colours.add(new ColourInfo(highlightedFeatureLabelColour, "Highlighted Feature Label"));

		colours.add(new ColourInfo(highlightedFeatureLabelBackgroundColour, "Highlighted Feature Label Background"));

		colours.add(new ColourInfo(backgroundGradientStartColour, "Background Gradient Start"));
		colours.add(new ColourInfo(backgroundGradientEndColour, "Background Gradient End"));

		return colours;
	}

	public void setSchemeColours(ArrayList<ColourInfo> colours)
	{
		genomeColour = colours.get(0).colour;
		invertedChromosomeColour = colours.get(1).colour;
		invertedChromosomeHighlightColour = colours.get(2).colour;
		chromosomeHighlightColour = colours.get(3).colour;
		linkColour = colours.get(4).colour;
		strongEmphasisLinkColour = colours.get(5).colour;
		mildEmphasisLinkColour = colours.get(6).colour;
		chromosomeIndexColour = colours.get(7).colour;
		featureColour = colours.get(8).colour;
		highlightedFeatureColour = colours.get(9).colour;
		featureLabelColour = colours.get(10).colour;
		highlightedFeatureLabelColour = colours.get(11).colour;
		highlightedFeatureLabelBackgroundColour = colours.get(12).colour;
		backgroundGradientStartColour = colours.get(13).colour;
		backgroundGradientEndColour = colours.get(14).colour;
	}

	public void setColours()
	{
		Colors.genomeColour = genomeColour;
		Colors.invertedChromosomeColour = invertedChromosomeColour;
		Colors.invertedChromosomeHighlightColour = invertedChromosomeHighlightColour;
		Colors.chromosomeHighlightColour = chromosomeHighlightColour;

		Colors.linkColour = linkColour;
		Colors.strongEmphasisLinkColour = strongEmphasisLinkColour;
		Colors.mildEmphasisLinkColour = mildEmphasisLinkColour;

		Colors.chromosomeIndexColour = chromosomeIndexColour;

		Colors.featureColour = featureColour;
		Colors.highlightedFeatureColour = highlightedFeatureColour;
		Colors.featureLabelColour = featureLabelColour;
		Colors.highlightedFeatureLabelColour = highlightedFeatureLabelColour;

		Colors.highlightedFeatureLabelBackgroundColour = highlightedFeatureLabelBackgroundColour;

		Colors.backgroundGradientStartColour = backgroundGradientStartColour;
		Colors.backgroundGradientEndColour = backgroundGradientEndColour;
	}
	
	public Color getBackgroundGradientEndColour()
	{
		return backgroundGradientEndColour;
	}

	public void setBackgroundGradientEndColour(Color backgroundGradientEndColour)
	{
		this.backgroundGradientEndColour = backgroundGradientEndColour;
	}

	public Color getBackgroundGradientStartColour()
	{
		return backgroundGradientStartColour;
	}

	public void setBackgroundGradientStartColour(Color backgroundGradientStartColour)
	{
		this.backgroundGradientStartColour = backgroundGradientStartColour;
	}

	public Color getChromosomeHighlightColour()
	{
		return chromosomeHighlightColour;
	}

	public void setChromosomeHighlightColour(Color chromosomeHighlightColour)
	{
		this.chromosomeHighlightColour = chromosomeHighlightColour;
	}

	public Color getChromosomeIndexColour()
	{
		return chromosomeIndexColour;
	}

	public void setChromosomeIndexColour(Color chromosomeIndexColour)
	{
		this.chromosomeIndexColour = chromosomeIndexColour;
	}

	public Color getFeatureColour()
	{
		return featureColour;
	}

	public void setFeatureColour(Color featureColour)
	{
		this.featureColour = featureColour;
	}

	public Color getFeatureLabelColour()
	{
		return featureLabelColour;
	}

	public void setFeatureLabelColour(Color featureLabelColour)
	{
		this.featureLabelColour = featureLabelColour;
	}

	public Color getGenomeColour()
	{
		return genomeColour;
	}

	public void setGenomeColour(Color genomeColour)
	{
		this.genomeColour = genomeColour;
	}

	public Color getHighlightedFeatureColour()
	{
		return highlightedFeatureColour;
	}

	public void setHighlightedFeatureColour(Color highlightedFeatureColour)
	{
		this.highlightedFeatureColour = highlightedFeatureColour;
	}

	public Color getHighlightedFeatureLabelBackgroundColour()
	{
		return highlightedFeatureLabelBackgroundColour;
	}

	public void setHighlightedFeatureLabelBackgroundColour(Color highlightedFeatureLabelBackgroundColour)
	{
		this.highlightedFeatureLabelBackgroundColour = highlightedFeatureLabelBackgroundColour;
	}

	public Color getHighlightedFeatureLabelColour()
	{
		return highlightedFeatureLabelColour;
	}

	public void setHighlightedFeatureLabelColour(Color highlightedFeatureLabelColour)
	{
		this.highlightedFeatureLabelColour = highlightedFeatureLabelColour;
	}

	public Color getInvertedChromosomeColour()
	{
		return invertedChromosomeColour;
	}

	public void setInvertedChromosomeColour(Color invertedChromosomeColour)
	{
		this.invertedChromosomeColour = invertedChromosomeColour;
	}

	public Color getInvertedChromosomeHighlightColour()
	{
		return invertedChromosomeHighlightColour;
	}

	public void setInvertedChromosomeHighlightColour(Color invertedChromosomeHighlightColour)
	{
		this.invertedChromosomeHighlightColour = invertedChromosomeHighlightColour;
	}

	public Color getLinkColour()
	{
		return linkColour;
	}

	public void setLinkColour(Color linkColour)
	{
		this.linkColour = linkColour;
	}

	public Color getMildEmphasisLinkColour()
	{
		return mildEmphasisLinkColour;
	}

	public void setMildEmphasisLinkColour(Color mildEmphasisLinkColour)
	{
		this.mildEmphasisLinkColour = mildEmphasisLinkColour;
	}

	public Color getOutlineColour()
	{
		return outlineColour;
	}

	public void setOutlineColour(Color outlineColour)
	{
		this.outlineColour = outlineColour;
	}

	public Color getStrongEmphasisLinkColour()
	{
		return strongEmphasisLinkColour;
	}

	public void setStrongEmphasisLinkColour(Color strongEmphasisLinkColour)
	{
		this.strongEmphasisLinkColour = strongEmphasisLinkColour;
	}

	public static class ColourInfo
	{
		public Color colour;
		public String name;

		ColourInfo(Color colour, String name)
		{
			this.colour = colour;
			this.name = name;
		}

		public String toString()
		{
			return name;
		}
	}

	public String toString()
	{
		return "Strudel";
	}
}
