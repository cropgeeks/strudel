package sbrn.mapviewer.gui.components;

import sbrn.mapviewer.data.*;

public class ResultsTableEntry
{

	public Feature targetFeature = null;
	public Feature homologFeature = null;
	private Link link;



	public String getTargetFeatureName()
	{
		return targetFeature.getName();
	}

	public String getTargetFeatureStart()
	{
		return String.valueOf(targetFeature.getStart());
	}

	public String getTargetFeatureMap()
	{
		return targetFeature.getOwningMap().getName();
	}

	public String getHomologFeatureName()
	{
		if(homologFeature != null)
			return homologFeature.getName();

		return "";
	}

	public String getHomologFeatureMapset()
	{
		if(homologFeature != null)
			return homologFeature.getOwningMapSet().getName();

		return "";
	}

	public String getHomologFeatureMap()
	{
		if(homologFeature != null)
			return homologFeature.getOwningMap().getName();

		return "";
	}

	public String getHomologFeatureStart()
	{
		if(homologFeature != null)
			return String.valueOf(homologFeature.getStart());

		return "";
	}

	public String getLinkEValue()
	{
		if(link != null)
		{
			return String.valueOf(link.getScore());
		}

		return "";
	}

	public String getHomologFeatureAnnotation()
	{
		if(homologFeature != null)
			return homologFeature.getAnnotation();

		return "";
	}

	public Feature getHomologFeature()
	{
		return homologFeature;
	}

	public void setHomologFeature(Feature homologFeature)
	{
		this.homologFeature = homologFeature;
	}

	public Link getLink()
	{
		return link;
	}

	public void setLink(Link link)
	{
		this.link = link;
	}

	public void setTargetFeature(Feature targetFeature)
	{
		this.targetFeature = targetFeature;
	}

	public Feature getTargetFeature()
	{
		return targetFeature;
	}


}
