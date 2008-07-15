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
	
	public OverviewCanvas targetOverviewCanvas;
	public OverviewCanvas referenceOverviewCanvas;
	
	
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
		//this panel contains everything else in a borderlayout
		JPanel topContainerPanel =  new JPanel(new BorderLayout());		
		//this panel contains the main canvas, the annotation panel below it, and 
		//the 2 scrollers left and right of the canvas
		JPanel mainPanel = new JPanel(new BorderLayout());		
		//this panel contains the two overview panels and the control panel
		JPanel leftPanel = new JPanel(new BorderLayout());
		//a panel for the two overview canvases
		JPanel overViewsContainer = new JPanel(new GridLayout(1,2));
		
		//scroll bars for the canvas
		leftCanvasScroller = new Scroller(this);
		rightCanvasScroller = new Scroller(this);
		mainPanel.add(leftCanvasScroller,BorderLayout.WEST);
		mainPanel.add(rightCanvasScroller,BorderLayout.EAST);
		
		//this is the main canvas which we render the genomes on
		mainCanvas = new MainCanvas(dataContainer.targetMapset, dataContainer.referenceMapset, this, dataContainer.links);
		mainPanel.add(mainCanvas, BorderLayout.CENTER);
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
		JPanel annotationPanelContainer = new JPanel(new GridLayout(1,2));
		annotationPanelContainer.add(targetAnnotationPanel);
		annotationPanelContainer.add(referenceAnnotationPanel);	
		annotationPanelContainer.setPreferredSize(new Dimension(800, 100));
		mainPanel.add(annotationPanelContainer,BorderLayout.SOUTH);

		//the control panel 
		ControlPanel controlPanel = new ControlPanel(this);
		controlPanel.setPreferredSize(new Dimension(250,400));
		
		//the overviews for the two genomes
		targetOverviewCanvas = new OverviewCanvas(this,mainCanvas.targetGMapSet);
		referenceOverviewCanvas = new OverviewCanvas(this,mainCanvas.referenceGMapSet);
		targetOverviewCanvas.setPreferredSize(new Dimension(125,250));
		referenceOverviewCanvas.setPreferredSize(new Dimension(125,250));
		overViewsContainer.add(targetOverviewCanvas);
		overViewsContainer.add(referenceOverviewCanvas);		
		
		//put it all together
		leftPanel.add(overViewsContainer,BorderLayout.NORTH);
		leftPanel.add(controlPanel,BorderLayout.CENTER);		
		topContainerPanel.add(leftPanel, BorderLayout.WEST);
		topContainerPanel.add(mainPanel, BorderLayout.CENTER);
		this.add(topContainerPanel);
	}
}
