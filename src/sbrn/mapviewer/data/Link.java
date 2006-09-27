package sbrn.mapviewer.data;

/**
 * Represents a link between two features.
 */
public class Link
{
	private Feature feature1;
	private Feature feature2;	
	
	public Link(Feature feature1, Feature feature2)
	{
		this.feature1 = feature1;
		this.feature2 = feature2;
	}
	
	public Feature getFeature1()
		{ return feature1; }
	
	public Feature getFeature2()
		{ return feature2; }
	
	public String toString()
		{ return feature1 + " and " + feature2; }
}