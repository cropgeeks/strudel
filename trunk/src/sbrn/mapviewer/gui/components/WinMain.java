package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.*;
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
	
	//a list of the zoom control panels
	public LinkedList<ZoomControlPanel> zoomControlPanels = new LinkedList<ZoomControlPanel>();
	
	//++++++++++Swing components that make up the GUI: +++++++++++++

	//the tool bar at the top
	public ControlToolBar toolbar;

	//the thumbnail overviews and the dialog that contains them
	public LinkedList<OverviewCanvas> overviewCanvases = new LinkedList<OverviewCanvas>();
	public OverviewDialog overviewDialog = new OverviewDialog(this);
	
	//the controller instance for the whole application
	public FatController fatController;
	
	//the mouse handler for the whole application
	public MouseHandler mouseHandler;
	
	//a context menu that activates when the user right-clicks on a chromosome
	public ChromoContextPopupMenu chromoContextPopupMenu;
	
	//the canvas for rendering the genomes
	public static MainCanvas mainCanvas;

	//this component shows the results of a feature search in a JTable
	public FoundFeaturesResultsPanel ffResultsPanel = new FoundFeaturesResultsPanel();
	//this is a control panel for it, showing just to its left
	public FoundFeaturesTableControlPanel foundFeaturesTableControlPanel;
	
	//this splitpane contains the main panel and the bottom panel
	public JSplitPane splitPane;
	public JPanel bottomPanel;
	
	//this panel contains the main canvas and/or the start panel
	public JPanel mainPanel;
	
	//a panel for the zoom controls
	public JPanel zoomControlContainerPanel;
	
	//this panel contains the genome labels and the zoom controls
	public JPanel zoomControlAndGenomelabelContainer;
	
	//this panel contains the results table and its control panel 
	public JPanel bottomPanelContainer;
	
	//this panel simply takes the place of the main canvas before we have loaded any data
	//just contains a simple label with instructions for how to load data
	public StartPanel startPanel;
	
	//a background panel with the SCRI logo
	public LogoPanel logoPanel;
		
	//the panel with the genome labels	
	public GenomeLabelPanel genomeLabelPanel;
	
	//dialogs
	public FindFeaturesDialog ffDialog;
	public FindFeaturesInRangeDialog ffInRangeDialog;
	public OpenFileDialog openFileDialog;
	public AboutDialog aboutDialog = new AboutDialog(this, true);

	
	
	//	=================================================c'tor=====================================
	
	public WinMain()
	{
		
		//get the GUI assembled as far as possible without the data loaded
		setupInitialComponents();
		pack();
		
		//GUI bits and pieces
		setTitle("Strudel " + Install4j.VERSION);
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
				
				if(mainCanvas !=null)
				{
					//refresh the main canvas
					MapViewer.winMain.validate();
					mainCanvas.redraw = true;
					MapViewer.winMain.mainCanvas.updateCanvas(true);				
				}
			}
			
			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
				
				if(mainCanvas !=null)
					mainCanvas.redraw = true;
			}
		});
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//here we set up the components that we require at startup, before any data has been loaded
	public void setupInitialComponents()
	{
		MapViewer.logger.fine("initing initial components in winMain");
		
		//the fat controller
		fatController = new FatController(this);
		
		//the file open dialog
		openFileDialog = new OpenFileDialog();
		
		//this panel contains the main canvas
		mainPanel = new JPanel(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		
		//this panel simply takes the place of the main canvas before we have loaded any data
		//just contains a simple label with instructions for how to load data
		startPanel = new StartPanel();
		logoPanel = new LogoPanel(new BorderLayout());
		mainPanel.add(logoPanel, BorderLayout.CENTER);
		logoPanel.add(startPanel, BorderLayout.CENTER);
		startPanel.setOpaque(false);
		
		//the control toolbar at the top of the GUI
		toolbar = new ControlToolBar(this);
		add(toolbar, BorderLayout.NORTH);

	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//here we set up the components we cannot set up until data has been loaded
	public void setupRemainingComponents()
	{
		MapViewer.logger.fine("initing remaining components in winMain");
		
		try
		{		
			//control panel for found features
			foundFeaturesTableControlPanel = new FoundFeaturesTableControlPanel();
			
			//the popup menu we use when are over a chromosome
			chromoContextPopupMenu  = new ChromoContextPopupMenu();
			
			//this panel contains the zoom controls and the search results panel below it
			bottomPanel = new JPanel(new BorderLayout());
			
			//this is the main canvas which we render the genomes on
			mainCanvas = new MainCanvas();
			//add this but hide the start panel first -- the main canvas is going to take its place instead
//			showStartPanel(false);
			MapViewer.logger.fine("adding main canvas");
			mainPanel.add(mainCanvas, BorderLayout.CENTER);	

			//add mousehandler
			mouseHandler = new MouseHandler(this);
			mainCanvas.addMouseListener(mouseHandler);
			mainCanvas.addMouseMotionListener(mouseHandler);
			mainCanvas.addMouseWheelListener(mouseHandler);
			
			//the dialog which displays thumbnails of the genomes
			overviewDialog.createLayout();
			
			//the panel with the genome labels	
			genomeLabelPanel = new GenomeLabelPanel();
			
			//initialise the zoom controls and the overview dialog
			initZoomControls();			
			initOverviewDialog();
			
			//this splitpane contains the main panel and the bottom panel
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, bottomPanel);
			splitPane.setOneTouchExpandable(true);
			//hide the bottom component and any sign of the split pane initially
			hideSplitPaneBottomHalf();
			
			//these dialogs can only be instantiated now because they rely on data having been loaded previously
			ffDialog = new FindFeaturesDialog();
			ffInRangeDialog = new FindFeaturesInRangeDialog();
			
			//assemble everything
			
			//this panel contains the genome labels and the zoom controls
			zoomControlAndGenomelabelContainer = new JPanel(new BorderLayout());
			zoomControlAndGenomelabelContainer.add(genomeLabelPanel,BorderLayout.NORTH);
			zoomControlAndGenomelabelContainer.add(zoomControlContainerPanel, BorderLayout.SOUTH);		
			mainPanel.add(zoomControlAndGenomelabelContainer, BorderLayout.SOUTH);
			
			//this panel contains the results table and its control panel 
			bottomPanelContainer = new JPanel(new BorderLayout());
			bottomPanelContainer.add(foundFeaturesTableControlPanel, BorderLayout.WEST);
			bottomPanelContainer.add(ffResultsPanel, BorderLayout.CENTER);
			bottomPanel.add(bottomPanelContainer,BorderLayout.CENTER);
			
			add(splitPane, BorderLayout.CENTER);
			
			//add a property change listener to the split pane so we know to repaint the canvas when it gets resized
			splitPane.addPropertyChangeListener( new PropertyChangeListener () 
			{			
				public void propertyChange(PropertyChangeEvent evt) 
				{
					MapViewer.logger.finest("========splitpane propertyChange");
					MapViewer.logger.finest("splitpane resized -- updating canvas");
					MapViewer.logger.finest("canvas size before resize event = " + MapViewer.winMain.mainCanvas.getHeight());
					//refresh the main canvas
					MapViewer.winMain.validate();
					MapViewer.winMain.mainCanvas.updateCanvas(true);
					MapViewer.logger.finest("canvas size after resize event = " + MapViewer.winMain.mainCanvas.getHeight());
				}			
			});
			
			repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void hideSplitPaneBottomHalf()
	{
		bottomPanel.setMinimumSize(new Dimension(0,0));
		bottomPanel.setPreferredSize(new Dimension(0,0));
		splitPane.setResizeWeight(1.0);
		splitPane.setDividerSize(0);
		
		//refresh the main canvas
		MapViewer.winMain.validate();
		MapViewer.winMain.mainCanvas.updateCanvas(true);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//removes, reconfigures, and adds GUI components which are dependent on the number of genomes
	//needs to be done when users load different datasets in succession
	public void reinitialiseDependentComponents()
	{
		MapViewer.logger.finest("reinitialiseDependentComponents()");
		
		//remove existing components
		zoomControlAndGenomelabelContainer.remove(zoomControlContainerPanel);
		for(OverviewCanvas overviewCanvas : overviewCanvases)
		{
			overviewDialog.remove(overviewCanvas);
		}
		
		//clear lists with the corresponding objects
		zoomControlPanels.clear();
		overviewCanvases.clear();
		
		//reinstate everything
		//the panels with the zoom control sliders
		initZoomControls();
		foundFeaturesTableControlPanel.setupGenomeFilterCombo();
		zoomControlAndGenomelabelContainer.add(zoomControlContainerPanel, BorderLayout.CENTER);		
		
		initOverviewDialog();
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void showStartPanel(boolean showStartPanel)
	{
		if(showStartPanel)
		{
			startPanel.setVisible(true);
			logoPanel.setVisible(true);
			mainCanvas.setVisible(false);
		}
		else
		{
			startPanel.setVisible(false);
			logoPanel.setVisible(false);
			mainCanvas.setVisible(true);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
		
	private void initOverviewDialog()
	{
		//the overviews for the genomes
		for (GMapSet gMapSet : dataContainer.gMapSetList)
		{
			OverviewCanvas overviewCanvas = new OverviewCanvas(this,gMapSet);
			overviewCanvas.setPreferredSize(new Dimension(0,250));
			overviewDialog.add(overviewCanvas);
			overviewCanvases.add(overviewCanvas);
		}
		overviewDialog.setVisible(Prefs.guiOverviewVisible);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void initZoomControls()
	{
		MapViewer.logger.finest("initZoomControls()");
		
		zoomControlContainerPanel = new JPanel(new GridLayout(1, dataContainer.gMapSetList.size()));

		//if there is only one genome showing, we want a shorter zoom control that does not fill the width of  the entire canvas
		if(dataContainer.gMapSetList.size() == 1)
		{				
			ZoomControlPanel zoomControlPanel = new ZoomControlPanel(this, dataContainer.gMapSetList.get(0), true);
			zoomControlPanel.zoomSlider.setMaximumSize(new Dimension(500, Short.MAX_VALUE));
			zoomControlContainerPanel.add(zoomControlPanel);
			zoomControlPanels.add(zoomControlPanel);
		}
		else
		{			
			//the panels with the zoom control sliders
			for (GMapSet gMapSet : dataContainer.gMapSetList)
			{
				ZoomControlPanel zoomControlPanel = new ZoomControlPanel(this, gMapSet, false);
				zoomControlContainerPanel.add(zoomControlPanel);
				zoomControlPanels.add(zoomControlPanel);
			}
		}
		

	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
}//end class
