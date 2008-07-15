package sbrn.mapviewer.gui;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

public class Scroller extends JScrollBar implements AdjustmentListener
{
	
	WinMain winMain;
	
	public Scroller(WinMain winMain)
	{
		setVisibleAmount(0);
		this.winMain = winMain;
		addAdjustmentListener(this);
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
//		 if(e.getValueIsAdjusting())
//		 return;
		
		System.out.println("scroller adjustmentValueChanged called");
		
		if (e.getSource().equals(winMain.leftCanvasScroller))
		{
			System.out.println("left scroller");
			winMain.mainCanvas.moveGenomeViewPort(winMain.mainCanvas.targetGMapSet, e.getValue());
		}
		else if (e.getSource().equals(winMain.rightCanvasScroller))
		{
			System.out.println("right scroller");
			winMain.mainCanvas.moveGenomeViewPort(winMain.mainCanvas.referenceGMapSet, e.getValue());
		}
	}
	
}
