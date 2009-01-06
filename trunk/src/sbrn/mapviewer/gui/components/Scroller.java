package sbrn.mapviewer.gui.components;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class Scroller extends JScrollBar implements AdjustmentListener
{

	public Scroller()
	{
		addAdjustmentListener(this);
		this.setVisibleAmount(5);
		this.setValue(50);
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		for (GMapSet gMapSet : MapViewer.winMain.mainCanvas.gMapSetList)
		{
			//work out the new scroller position and the corresponding position on the mapset's genome in y
			int newCenterPoint = (int) (gMapSet.totalY * (e.getValue()/100.0f));
			MapViewer.winMain.mainCanvas.moveGenomeViewPort(gMapSet, newCenterPoint);
		} 

	}
}
