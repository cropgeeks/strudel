package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import sbrn.mapviewer.gui.tests.syntenyviewer3d.*;

public class SyntenyViewer3DPanel extends JPanel
{
	
	SyntenyViewer3DCanvas canvas;
	
	public SyntenyViewer3DPanel(File referenceData, File targetData, File compData, File[] otherMapFiles, Dimension frameSize)
	{	
		canvas = new SyntenyViewer3DCanvas( referenceData, targetData,compData, otherMapFiles);
		this.add(canvas,BorderLayout.CENTER);
	}

}
