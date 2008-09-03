package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	//this is where we hold the genome data
	public DataContainer dataContainer;

	//Swing components that make up the GUI
	//this is where we paint the genomes
	public static MainCanvas mainCanvas;
	public ZoomControlPanel zoomControlPanel;
	public ControlPanel controlPanel;
	public AnnotationPanel targetAnnotationPanel;
	public AnnotationPanel referenceAnnotationPanel;
	public OverviewCanvas targetOverviewCanvas;
	public OverviewCanvas referenceOverviewCanvas;
	public Scroller leftCanvasScroller;
	public Scroller rightCanvasScroller;

	//the controller for the whole application
	FatController fatController;

	public WinMain()
	{
		//load the data for testing
		//TODO remove hard coded data loading once user import options are available
		dataContainer = new DataContainer();
		dataContainer.loadData();

		//get the GUI assembled
		setupComponents();
		pack();

		//GUI bits and pieces
		setTitle("Map Viewer");
		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);


		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.isFirstRun || Prefs.guiWinMainX > (scrnW-50) || Prefs.guiWinMainY > (scrnH-50))
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		// Window listeners are added last so they don't interfere with the
		// maximization from above
		addListeners();
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainWidth  = getSize().width;
					Prefs.guiWinMainHeight = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
			}
		});
	}

	private void setupComponents()
	{
		//the fat controller
		fatController = new FatController(this);

		//this panel contains everything else in a borderlayout
		JPanel topContainerPanel = new JPanel(new BorderLayout());
		//this panel contains the main canvas, the annotation panel below it, and
		//the 2 scrollers left and right of the canvas
		JPanel mainPanel = new JPanel(new BorderLayout());
		//this panel contains the two overview panels and the control panel
		JPanel leftPanel = new JPanel(new BorderLayout());
		//a panel for the two overview canvases
		JPanel overViewsContainerPanel = new JPanel(new GridLayout(1,2));
		//the panel at the bottom of the main canvas -- contains annotation and zoom control panels
		JPanel bottomPanel = new JPanel(new BorderLayout());

		//scroll bars for the canvas
		leftCanvasScroller = new Scroller(this);
		rightCanvasScroller = new Scroller(this);
		mainPanel.add(leftCanvasScroller,BorderLayout.WEST);
		mainPanel.add(rightCanvasScroller,BorderLayout.EAST);

		//this is the main canvas which we render the genomes on
		mainCanvas = new MainCanvas(dataContainer.targetMapset, dataContainer.referenceMapset, this, dataContainer.links);
		mainPanel.add(mainCanvas, BorderLayout.CENTER);

		//add mousehandler
		MouseHandler mouseHandler = new MouseHandler(this);
		mainCanvas.addMouseListener(mouseHandler);
		mainCanvas.addMouseMotionListener(mouseHandler);
		mainCanvas.addMouseWheelListener(mouseHandler);
		leftCanvasScroller.addMouseListener(mouseHandler);
		rightCanvasScroller.addMouseListener(mouseHandler);

		//the panel with the zoom control sliders
		zoomControlPanel = new ZoomControlPanel(this);
//		zoomControlPanel.setPreferredSize(new Dimension(800, 100));
//		add(zoomControlPanel,BorderLayout.SOUTH);

		//the panels for displaying annotation info
		targetAnnotationPanel = new AnnotationPanel(this,dataContainer.targetMapset);
		referenceAnnotationPanel = new AnnotationPanel(this,dataContainer.referenceMapset);
		JPanel annotationContainerPanel = new JPanel(new GridLayout(1,2));
		annotationContainerPanel.add(targetAnnotationPanel);
		annotationContainerPanel.add(referenceAnnotationPanel);

		//now stick both the zoom controls and the annotation panels in the bottom panel and add it
		bottomPanel.add(zoomControlPanel, BorderLayout.NORTH);
		bottomPanel.add(annotationContainerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel,BorderLayout.SOUTH);

		//the control panel
		controlPanel = new ControlPanel(this);

		//the overviews for the two genomes
		targetOverviewCanvas = new OverviewCanvas(this,mainCanvas.targetGMapSet);
		referenceOverviewCanvas = new OverviewCanvas(this,mainCanvas.referenceGMapSet);
		targetOverviewCanvas.setPreferredSize(new Dimension(0,250));
		referenceOverviewCanvas.setPreferredSize(new Dimension(0,220));
		overViewsContainerPanel.add(targetOverviewCanvas);
		overViewsContainerPanel.add(referenceOverviewCanvas);

		//put it all together
		leftPanel.add(overViewsContainerPanel,BorderLayout.NORTH);
		leftPanel.add(controlPanel,BorderLayout.CENTER);

		topContainerPanel.add(leftPanel, BorderLayout.WEST);
		topContainerPanel.add(mainPanel, BorderLayout.CENTER);
		this.add(topContainerPanel);
	}
}
