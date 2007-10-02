package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import javax.media.j3d.Alpha;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;

/**
 * This is a thread class which activates a paused alpha and keeps it running until it has reached 
 * its maximum alpha value, then pauses it again.
 * 
 * @author Micha Bayer, SCRI
 */
public class AlphaActivator extends Thread
{
//=======================================vars===============================
	
	//the Alpha object concerned
	private Alpha alpha = null;
	
	//the SyntenyViewer3D object the Alpha belongs to
	private SyntenyViewer3DCanvas sv3d = null;
	
	//the index of the TransformGroup that the target object hangs off from
	private int tgIndex = -1;

//=======================================c'tor===============================
	
	
	public AlphaActivator(SyntenyViewer3DCanvas _sv3d, Alpha _alpha, int _tgIndex)
	{
		this.alpha = _alpha;
		this.sv3d = _sv3d;
		this.tgIndex = _tgIndex;
	}
	
//	=======================================methods===============================
	
	/**In this method we resume the Alpha object and wait for it to peak at a value of 1
	while it is increasing we continuously check whether its value is still increasing
	when it stops to increase we pause the alpha again
	*/
	public void run()
	{
		Shape3D [] linkArrays = sv3d.getLinkArrays();
		boolean [] linksInverted = sv3d.getLinksInverted();
		linkArrays[tgIndex].setGeometry(null);
		
		alpha.resume();
		float previousVal = -1; 
		float currentVal = alpha.value();
		boolean decliningAlpha = false;
		while(!decliningAlpha)
		{
			currentVal = alpha.value();
			if(previousVal > currentVal)
			{	
				decliningAlpha = true;
			}
			previousVal = currentVal;
		}
		alpha.pause();
		

		//invert links between chromosomes
		//first check whether they have been previously inverted
		LineArray linkArray = null;
		if(linksInverted[tgIndex])
		{
			//this redraws the links without inversion i.e. restores to how they were initially
			linkArray = sv3d.makeLineArray(tgIndex,false);
			linksInverted[tgIndex] = false;
		}
		else
		{
			//this draws them upside down
			linkArray = sv3d.makeLineArray(tgIndex,true);
			linksInverted[tgIndex] = true;		
		}
		linkArrays[tgIndex].setGeometry(linkArray);		
		
		//invert the markers
		sv3d.invertMarkers(tgIndex);

	}
//---------------------------------------------------------------------------------------------------------------	
}//end class
