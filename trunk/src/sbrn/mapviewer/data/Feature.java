package sbrn.mapviewer.data;

public class Feature
{
	// Feature types
	public final static int GENERIC = 0;
	
	public final static int MARKER = 1;
	public final static int SSR = 2;
	public final static int SNP = 3;
	//etc
	
	
	private String name;
	
	private float start, stop;
	
	
	public Feature(String name)
	{
		this.name = name;
	}
	
	public String getName()
		{ return name; }
	
	public void setStart(float start)
		{ this.start = start; }
	
	public float getStart()
		{ return start; }
}