package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import sbrn.mapviewer.data.ChromoMap;

public class MapArea
{

	//the chromosome that this area is part of
	Chromosome chromosome;
	//the corresponding feature map
	ChromoMap chromoMap;
	//the start position of the area in units of chromosome (base pairs or centimorgans), relative to the chromosome start
	int start;
	//the end position of the area in units of chromosome (base pairs or centimorgans), relative to the chromosome start
	int end;
		
	public MapArea(Chromosome chromosome, int start, int end, ChromoMap chromoMap)
	{
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.chromoMap = chromoMap;
	}
	
}
