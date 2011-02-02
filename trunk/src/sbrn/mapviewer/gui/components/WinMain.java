package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.lang.management.*;
import java.util.*;
import java.text.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.io.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.Icons;
import scri.commons.gui.SwingUtils;

public class WinMain extends JFrame
{

	//	=================================================vars=====================================

	//this is where we hold all  our data from the current dataset
	public DataSet dataSet = null;

	//a list of the zoom control panels
	public LinkedList<ZoomControlPanel> zoomControlPanels = new LinkedList<ZoomControlPanel>();

	//++++++++++Swing components that make up the GUI: +++++++++++++

	//the thumbnail overviews and the dialog that contains them
	public LinkedList<OverviewCanvas> overviewCanvases = new LinkedList<OverviewCanvas>();
	public OverviewDialog overviewDialog = new OverviewDialog(this);

		//the tool bar at the top
	public ControlToolBar toolbar;

	public MenuFile mFile;
	public MenuExplore mExplore;
	public MenuView mView;
	public MenuAbout mAbout;
	public WinMainMenuBar menuBar;

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
	public JPanel navPanel;

	//the panel with the genome labels
	public GenomeLabelPanel genomeLabelPanel;

	//dialogs
	public FindFeaturesInRangeDialog ffInRangeDialog;
	public OpenFileDialog openFileDialog;
	public ConfigureViewSettingsDialog configureViewSettingsDialog;
	public AboutDialog aboutDialog = new AboutDialog(this, true);
	public GenomeLayoutDialog genomeLayoutDialog;
	public ColorSchemeChooserDialog colorChooserDialog = new ColorSchemeChooserDialog(this);

	//this panel displays hints for the user as to what to do in a given context
	public static HintPanel hintPanel;

	//	=================================================curve'tor=====================================

	public WinMain()
	{

		mFile = new MenuFile();
		mExplore = new MenuExplore();
		mView = new MenuView(this);
		mAbout = new MenuAbout();
		menuBar = new WinMainMenuBar(this);
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
		createMemoryTimer();

		setJMenuBar(menuBar);
	}

	//=================================================methods=====================================

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			@Override
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

			@Override
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

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	//here we set up the components that we require at startup, before any data has been loaded
	public void setupInitialComponents()
	{
		//the fat controller
		fatController = new FatController(this);

		//dialogs we only want one instance of
		openFileDialog = new OpenFileDialog();
		configureViewSettingsDialog = new ConfigureViewSettingsDialog();

		//this panel contains the main canvas
		mainPanel = new JPanel(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		//this panel simply takes the place of the main canvas before we have loaded any data
		//it contains a number of linnks to help pages and also command like open file etc
		navPanel = NavPanel.getLinksPanel(this);
		navPanel.setOpaque(false);
		mainPanel.add(navPanel, BorderLayout.CENTER);

		//the control toolbar at the top of the GUI
		toolbar = new ControlToolBar(this);
		add(toolbar, BorderLayout.NORTH);

		//drag and drop support
		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(this, dropAdapter));

	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	//here we set up the components we cannot set up until data has been loaded
	public void setupRemainingComponents()
	{
		try
		{
			//control panel for found features
			foundFeaturesTableControlPanel = new FoundFeaturesTableControlPanel();

			//this panel contains the zoom controls and the search results panel below it
			bottomPanel = new JPanel(new BorderLayout());	
			//for some reason we need to set the bottom panel size to 0 to hide it
			bottomPanel.setMinimumSize(new Dimension(0,0));
			bottomPanel.setPreferredSize(new Dimension(0,0));
			//we also need to initially hide it because otherwise it keeps flashing up for a few seconds before the main canvas has been repainted
			bottomPanel.setVisible(false);

			//the popup menu we use when are over a chromosome
			chromoContextPopupMenu  = new ChromoContextPopupMenu();

			//this is the main canvas which we render the genomes on
			mainCanvas = new MainCanvas();
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
			//don't want the divider visible as we want to control this splitpane programmatically only
			splitPane.setOneTouchExpandable(false);
			splitPane.setDividerSize(0);
			//when we resize, give the extra weight to the top component (the canvas)
			splitPane.setResizeWeight(1.0);	
			splitPane.setDividerLocation(1.0f);

			//these dialogs can only be instantiated now because they rely on data having been loaded previously
			ffInRangeDialog = new FindFeaturesInRangeDialog();
			ffInRangeDialog.ffInRangePanel.initRemainingComponents();
			genomeLayoutDialog = new GenomeLayoutDialog();			

			//the hint panel
			hintPanel = new HintPanel();
			mainPanel.add(hintPanel, BorderLayout.NORTH);
			hintPanel.setIcons(Icons.getIcon("HELP12"), Icons.getIcon("FILECLOSEHIGHLIGHTED"), Icons.getIcon("FILECLOSE"));
			if(Prefs.showHintPanel)
				hintPanel.setVisible(true);
			else
				hintPanel.setVisible(false);

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
			
			validate();
			repaint();
			
			//now we can allow the bottom panel to be visible, although it has to stay hidden until we move the splitpane's divider location
			bottomPanel.setVisible(true);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	public void showBottomPanel(boolean repaintAndIndex)
	{
		//we want the results table's control panel to be fully visible
		int controlPanelHeight = (int)foundFeaturesTableControlPanel.getMinimumSize().getHeight();	
		int newDividerLocation = getHeight() - controlPanelHeight;
		splitPane.setDividerLocation(newDividerLocation);		
		
		//refresh the main canvas
		if(repaintAndIndex)
		{
			validate();
			mainCanvas.updateCanvas(true);		
		}
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	public void hideBottomPanel(boolean repaintAndIndex)
	{			
		splitPane.setDividerLocation(1.0f);
		if(repaintAndIndex)
			mainCanvas.updateCanvas(true);		

	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	//removes, reconfigures, and adds GUI components which are dependent on the number of genomes
	//needs to be done when users load different datasets in succession
	public void reinitialiseDependentComponents()
	{
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

		Strudel.winMain.ffInRangeDialog.ffInRangePanel.initRemainingComponents();

		//the labels with the genome names need to be updated
		genomeLabelPanel.repaint();
	}


	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	public void showStartPanel(boolean showStartPanel)
	{
		if(showStartPanel)
		{
			navPanel.setVisible(true);
//			logoPanel.setVisible(true);
			mainCanvas.setVisible(false);
		}
		else
		{
			navPanel.setVisible(false);
//			logoPanel.setVisible(false);
			mainCanvas.setVisible(true);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	private void initOverviewDialog()
	{
		//the overviews for the genomes
		for (GMapSet gMapSet : dataSet.gMapSets)
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
		zoomControlContainerPanel = new JPanel(new GridLayout(1, dataSet.gMapSets.size()));

		//if there is only one genome showing, we want a shorter zoom control that does not fill the width of  the entire canvas
		if(dataSet.gMapSets.size() == 1)
		{
			ZoomControlPanel zoomControlPanel = new ZoomControlPanel(this, dataSet.gMapSets.get(0), true);
			zoomControlPanel.zoomSlider.setMaximumSize(new Dimension(500, Short.MAX_VALUE));
			zoomControlContainerPanel.add(zoomControlPanel);
			zoomControlPanels.add(zoomControlPanel);
		}
		else
		{
			//the panels with the zoom control sliders
			for (GMapSet gMapSet : dataSet.gMapSets)
			{
				ZoomControlPanel zoomControlPanel = new ZoomControlPanel(this, gMapSet, false);
				zoomControlContainerPanel.add(zoomControlPanel);
				zoomControlPanels.add(zoomControlPanel);
			}
		}


	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	private void createMemoryTimer()
	{
		final DecimalFormat df = new DecimalFormat("0.00");
		final MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();
		final ThreadMXBean tBean = ManagementFactory.getThreadMXBean();

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				long used = mBean.getHeapMemoryUsage().getUsed()
					+ mBean.getNonHeapMemoryUsage().getUsed();

				String label = df.format(used/1024f/1024f) + "MB (" +
					(tBean.getThreadCount()-tBean.getDaemonThreadCount()) + ")";
				toolbar.memLabel.setText(label);
			}
		};

		javax.swing.Timer timer = new javax.swing.Timer(1000, listener);
		timer.setInitialDelay(0);
		timer.start();
	}

}//end class
