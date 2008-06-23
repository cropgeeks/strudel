package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;
/**
 * Class representing a graphical representation of a genome. Assumes all chromosomes laid out vertically in a column.
 */
public class Genome
{
	//name
	public String name;
	//colour for rendering the genome in
	Color colour;
	//an array containing all the chromosomes in the genome
	Chromosome [] chromosomes;
	//the position of the genome on the x axis of the canvas 
	int xPosition;
	//the vertical extent in pixels of the genome as drawn in the current view
	public int currentVerticalExtent;
	//a boolean indicating whether we want to darw the chromosomes or a detail section as a solid bar filling the whole canvas
	public boolean drawChromos = true;
	
	public Genome(int numChromos, String name, Color colour)
	{
		this.name = name;
		this.colour = colour;	
		chromosomes = new Chromosome[numChromos];
	}

}
