package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class WinMain extends JFrame
{
	
	//	=================================================vars=====================================
	
	//this is where we hold the genome data
	public DataContainer dataContainer;
	
	//Swing components that make up the GUI
	public static MainCanvas mainCanvas;
	public LinkedList<ZoomControlPanel> zoomControlPanels = new LinkedList<ZoomControlPanel>();
	public ControlToolBar toolbar;
	public AnnotationPanel targetAnnotationPanel;
	public AnnotationPanel referenceAnnotationPanel;
	public LinkedList<OverviewCanvas> overviewCanvases = new LinkedList<OverviewCanvas>();
	
	public OverviewDialog overviewDialog = new OverviewDialog(this);
	
	//the controller for the whole application
	public FatController fatController;
	
	public ChromoContextPopupMenu chromoContextPopupMenu;
	
	//this component shows the results of a feature search in a JTable
	public FoundFeaturesResultsPanel ffResultsPanel = new FoundFeaturesResultsPanel();
	//this is a control panel for it, showing just to its left
	public FoundFeaturesTableControlPanel foundFeaturesTableControlPanel = new FoundFeaturesTableControlPanel();
	
	//this splitpane contains the main panel and the bottom panel
	public JSplitPane splitPane = null;
	JPanel bottomPanel = null;
	
	//a panel for the zoom controls
	public JPanel zoomControlContainerPanel;
	
	//this panel contains the genome labels and the zoom controls
	JPanel zoomControlAndGenomelabelContainer;
	
	
	//	=================================================c'tor=====================================
	
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
		setTitle("Map Viewer - " + Install4j.VERSION);
		setIconImage(Icons.getIcon("MAPVIEWER").getImage());
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
	
	//=================================================methods=====================================
	
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
				
				mainCanvas.redraw = true;
			}
			
			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
				
				mainCanvas.redraw = true;
			}
		});
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void setupComponents()
	{
		//the fat controller
		fatController = new FatController(this);
		
		//the popup menu we use when are over a chromosome
		chromoContextPopupMenu  = new ChromoContextPopupMenu();
		
		//this panel contains the main canvas
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		//this panel contains the zoom controls and the search results panel below it
		bottomPanel = new JPanel(new BorderLayout());
		
		//a panel for the zoom controls
		zoomControlContainerPanel = new JPanel(new GridLayout(1,dataContainer.numRefGenomes+1));
		
		overviewDialog.createLayout();
		
		//this is the main canvas which we render the genomes on
		mainCanvas = new MainCanvas(dataContainer.targetMapset, dataContainer.referenceMapsets, this, dataContainer.linkSets);
		mainPanel.add(mainCanvas, BorderLayout.CENTER);
//		mainPanel.setBorder(BorderFactory.createLineBorder(new Color(125, 133, 151), 2));
		
		//add mousehandler
		MouseHandler mouseHandler = new MouseHandler(this);
		mainCanvas.addMouseListener(mouseHandler);
		mainCanvas.addMouseMotionListener(mouseHandler);
		mainCanvas.addMouseWheelListener(mouseHandler);
		
		//the panel with the genome labels	
		GenomeLabelPanel genomeLabelPanel = new GenomeLabelPanel();
		zoomControlContainerPanel.add(genomeLabelPanel);
		
		//the panels with the zoom control sliders
		for (GMapSet gMapSet : mainCanvas.gMapSetList)
		{
			ZoomControlPanel zoomControlPanel = new ZoomControlPanel(this, gMapSet);
			zoomControlContainerPanel.add(zoomControlPanel);
			zoomControlPanels.add(zoomControlPanel);
		}
		
		//the control panel
		toolbar = new ControlToolBar(this);
		
		//the overviews for the genomes
		for (GMapSet gMapSet : mainCanvas.gMapSetList)
		{
			OverviewCanvas overviewCanvas = new OverviewCanvas(this,gMapSet);
			overviewCanvas.setPreferredSize(new Dimension(0,250));
			overviewDialog.addCanvas(overviewCanvas);
			overviewCanvases.add(overviewCanvas);
		}
		overviewDialog.setVisible(Prefs.guiOverviewVisible);
		
		//this splitpane contains the main panel and the bottom panel
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, bottomPanel);
		splitPane.setOneTouchExpandable(true);
		//hide the bottom component and any sign of the split pane initially
		hideSplitPaneBottomHalf();
		
		//assemble everything
		
		//this panel contains the genome labels and the zoom controls
		zoomControlAndGenomelabelContainer = new JPanel(new BorderLayout());
		zoomControlAndGenomelabelContainer.add(genomeLabelPanel,BorderLayout.NORTH);
		zoomControlAndGenomelabelContainer.add(zoomControlContainerPanel, BorderLayout.CENTER);		
		mainPanel.add(zoomControlAndGenomelabelContainer, BorderLayout.SOUTH);
		//hide this panel initially until the data has been loaded
		zoomControlAndGenomelabelContainer.setVisible(false);
		
		//this panel contains the results table and its control panel 
		JPanel bottomPanelContainer = new JPanel(new BorderLayout());
		bottomPanelContainer.add(foundFeaturesTableControlPanel, BorderLayout.WEST);
		bottomPanelContainer.add(ffResultsPanel, BorderLayout.CENTER);
		bottomPanel.add(bottomPanelContainer,BorderLayout.CENTER);
		
		add(toolbar, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		
		//add a property change listener to the plit pane so we know to repaint the canvas when it gets resized
		splitPane.addPropertyChangeListener( new PropertyChangeListener () 
		{			
			public void propertyChange(PropertyChangeEvent evt) 
			{
				MapViewer.winMain.mainCanvas.updateCanvas(true);
			}			
		});
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void hideSplitPaneBottomHalf()
	{
		bottomPanel.setMinimumSize(new Dimension(0,0));
		bottomPanel.setPreferredSize(new Dimension(0,0));
		splitPane.setResizeWeight(1.0);
		splitPane.setDividerSize(0);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
}//end class
