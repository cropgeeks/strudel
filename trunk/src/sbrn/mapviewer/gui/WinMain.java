package sbrn.mapviewer.gui;

import java.awt.*;

import javax.swing.*;


public class WinMain extends JFrame
{
	//this is where we paint the genomes
	public static MainCanvas mainCanvas;
	
	//this is where we hold the genome data
	public DataContainer dataContainer;
	
	public ZoomControlPanel zoomControlPanel;
	
	public AnnotationPanel targetAnnotationPanel;
	public AnnotationPanel referenceAnnotationPanel;
	
	//scroll bars for the canvas
	Scroller leftCanvasScroller;
	Scroller rightCanvasScroller;
	
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
//		setExtendedState(this.MAXIMIZED_BOTH);
	}
	
	private void setupComponents()
	{
		//scroll bars for the canvas
		leftCanvasScroller = new Scroller(this);
		rightCanvasScroller = new Scroller(this);
		add(leftCanvasScroller,BorderLayout.WEST);
		add(rightCanvasScroller,BorderLayout.EAST);
		
		//this is the main canvas which we render the genomes on
		mainCanvas = new MainCanvas(dataContainer.targetMapset, dataContainer.referenceMapset, this, dataContainer.links);
		add(mainCanvas, BorderLayout.CENTER);
		mainCanvas.setPreferredSize(new Dimension(800, 600));
		
		//add mousehandler
		MouseHandler mouseHandler = new MouseHandler(this);
		mainCanvas.addMouseListener(mouseHandler);
		mainCanvas.addMouseMotionListener(mouseHandler);
		mainCanvas.addMouseWheelListener(mouseHandler);
		leftCanvasScroller.addMouseListener(mouseHandler);
		rightCanvasScroller.addMouseListener(mouseHandler);
		
		//the panel with the zoom control sliders
		zoomControlPanel = new ZoomControlPanel(this);
		//zoomControlPanel.setPreferredSize(new Dimension(800, 100));
//		add(zoomControlPanel,BorderLayout.SOUTH);
		
		//the panels for displaying annotation info
		targetAnnotationPanel = new AnnotationPanel(this,dataContainer.targetMapset);
		referenceAnnotationPanel = new AnnotationPanel(this,dataContainer.referenceMapset);
//		targetAnnotationPanel.setBackground(Color.green);
//		referenceAnnotationPanel.setBackground(Color.red);
		JPanel annotationPanelContainer = new JPanel(new GridLayout(1,2));
		annotationPanelContainer.add(targetAnnotationPanel);
		annotationPanelContainer.add(referenceAnnotationPanel);		
		annotationPanelContainer.setPreferredSize(new Dimension(800, 150));
		add(annotationPanelContainer,BorderLayout.SOUTH);

	}
}
