package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;

import javax.swing.*;

public class SyntenyViewer2DFrame extends JFrame
{
	public static void main(String[] args)
	{
		SyntenyViewer2DFrame comp = new SyntenyViewer2DFrame();
		Canvas2D canvas = new Canvas2D(comp);
		comp.add(canvas);
		comp.setSize(800, 600);
		comp.setLocationRelativeTo(null);
		comp.setBackground(Color.BLACK);
		comp.setVisible(true);
		comp.setTitle("Synteny Viewer 2D");
		comp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
