package sbrn.mapviewer.gui;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

public class Scroller extends JScrollBar implements AdjustmentListener
{
	
	WinMain winMain;
	
	public Scroller(WinMain winMain)
	{
		this.winMain = winMain;
		addAdjustmentListener(this);
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{		
		// if(e.getValueIsAdjusting())
		// return;
		
		if (e.getSource().equals(winMain.leftCanvasScroller))
		{
			winMain.mainCanvas.moveGenomeViewPort(winMain.mainCanvas.targetGMapSet, e.getValue());
		}
		if (e.getSource().equals(winMain.rightCanvasScroller))
		{
			winMain.mainCanvas.moveGenomeViewPort(winMain.mainCanvas.referenceGMapSet, e.getValue());
		}	
	}
	
}
