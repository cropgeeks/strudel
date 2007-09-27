package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;

public class Genome
{
	//number of chromosomes in this genome
	int numChromos;
	//name
	String name;
	//chromosome x positions
	int xPosition;
	//chromosome y positions
	int[] yPositions;
	//colour for rendering the chromos in
	Color colour;
	
	public Genome(int numChromos, String name, Color colour)
	{
		this.numChromos = numChromos;
		this.name = name;
		this.colour = colour;
		yPositions = new int[numChromos];
	}

}
