package sbrn.mapviewer.gui;

import java.awt.*;

import javax.swing.JFrame;


public class WinMain extends JFrame
{
	//this is where we paint the genomes
	public MainCanvas mainCanvas;
	
	//this is where we hold the genome data
	public DataContainer dataContainer;
	
	public WinMain()
	{
		//load the data for testing
		//TODO remove hard coded data loading once user import options are available
		dataContainer = new DataContainer();
		dataContainer.loadData();
		
		//get the GUI assembled
		setupComponents();
		
		//GUI bits and pieces
		setTitle("Map Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}
	
	private void setupComponents()
	{
		mainCanvas = new MainCanvas(dataContainer.targetMapset, dataContainer.referenceMapset);
		MouseHandler mouseHandler = new MouseHandler(this);
		mainCanvas.addMouseListener(mouseHandler);
		mainCanvas.addMouseMotionListener(mouseHandler);
		add(mainCanvas, BorderLayout.NORTH);
		mainCanvas.setPreferredSize(new Dimension(800, 600));
	}
}
