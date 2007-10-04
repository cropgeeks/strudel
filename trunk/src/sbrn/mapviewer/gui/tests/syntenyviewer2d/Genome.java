package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;
/**
 * Class representing a graphical representation of a genome. Assumes all chromosomes laid out vertically in a column.
 */
public class Genome
{
	//name
	String name;
	//colour for rendering the genome in
	Color colour;
	//an array containing all the chromosomes in the genome
	Chromosome [] chromosomes;
	//the position of the genome on the x axis of the canvas 
	int xPosition;
	
	public Genome(int numChromos, String name, Color colour)
	{
		this.name = name;
		this.colour = colour;	
		chromosomes = new Chromosome[numChromos];
	}

}
