package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import sbrn.mapviewer.data.ChromoMap;

public class VisibleGenomeArea
{
	HashMap<Rectangle, Chromosome> chromosomeAreas;
	Vector<MapArea> mapAreas;
	
	// makes a new map containing rectangles as keys (areas visible on the canvas which form the bounds of a chromosome) and
	// Chromosomes as keys
	public void makeChromoAreaMap(Vector<Rectangle> rectangles, Vector<Chromosome> chromosomes)
	{
		chromosomeAreas = new HashMap<Rectangle, Chromosome>();
		for (int i = 0; i < rectangles.size(); i++)
		{
			chromosomeAreas.put(rectangles.get(i), chromosomes.get(i));
		}		
	}
	
	public void updateMapAreas()
	{
		mapAreas = new Vector<MapArea>();
		for (Rectangle rect : chromosomeAreas.keySet())
		{
			//make a new map area object
			int visibleLimitTopPx = (int) rect.getY();
			int visibleLimitBottomPx = (int) (rect.getY() + rect.getHeight());
			
			Chromosome chromosome = chromosomeAreas.get(rect);
			ChromoMap chromoMap = chromosome.chromoMap;
			
			//we now need to work out how the drawn region relates to an actual region on the chromosome
			int start = -1;
			int end = -1;
			
			MapArea mapArea = new MapArea(chromosome, start,  end, chromoMap);
		}
	}
	
	// checks each rectangle in the list of visible chromosomes for intersection with selectedRect
	// returns a map with Chromosomes that selectedRect intersects with (intersecting rectangles are keys)
	public HashMap<Rectangle, Chromosome> getIntersectingChromos(Rectangle selectedRect)
	{
		HashMap<Rectangle, Chromosome> map = new HashMap<Rectangle, Chromosome>();;

		
		for (Rectangle rectangle : chromosomeAreas.keySet())
		{
			if (selectedRect.intersects(rectangle))
			{
				//get the intersecting rectangle
				Rectangle intersect = selectedRect.intersection(rectangle);
				//add it to the map along with the chromosome
				map.put(intersect,chromosomeAreas.get(rectangle));
			}
		}
		
		System.out.println("intersecting chromos :");
		for (Chromosome chromosome : map.values())
		{
			System.out.println("chr. " + chromosome.genomeIndex);
		}
		
		return map;
	}
}
