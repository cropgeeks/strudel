package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class WinMain extends JFrame
{

//=================================================vars=====================================

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

//=================================================c'tor=====================================

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

		//this panel contains the main canvas and the annotation panel below it
		JPanel mainPanel = new JPanel(new BorderLayout());
		//a panel for the zoom controls
		JPanel zoomControlContainerPanel = new JPanel(new GridLayout(1,dataContainer.numRefGenomes+1));

		overviewDialog.createLayout();

		//this is the main canvas which we render the genomes on
		mainCanvas = new MainCanvas(dataContainer.targetMapset, dataContainer.referenceMapsets, this, dataContainer.linkSets);
		mainPanel.add(mainCanvas, BorderLayout.CENTER);

//		mainPanel.setBorder(BorderFactory.createCompoundBorder(
//			BorderFactory.createLineBorder(new Color(0, 45, 150)),
//			BorderFactory.createLineBorder(new Color(125, 133, 151), 1)));

		mainPanel.setBorder(BorderFactory.createLineBorder(new Color(125, 133, 151), 2));

		//add mousehandler
		MouseHandler mouseHandler = new MouseHandler(this);
		mainCanvas.addMouseListener(mouseHandler);
		mainCanvas.addMouseMotionListener(mouseHandler);
		mainCanvas.addMouseWheelListener(mouseHandler);


		//the panels with the zoom control sliders
		for (GMapSet gMapSet : mainCanvas.gMapSetList)
		{
			ZoomControlPanel zoomControlPanel = new ZoomControlPanel(this, gMapSet);
			zoomControlContainerPanel.add(zoomControlPanel);
//			zoomControlPanel.setPreferredSize(new Dimension(800, 100));
			zoomControlPanels.add(zoomControlPanel);
		}

		//the panels for displaying annotation info
//		targetAnnotationPanel = new AnnotationPanel(this,dataContainer.targetMapset);
//		referenceAnnotationPanel = new AnnotationPanel(this,dataContainer.referenceMapset);
//		JPanel annotationContainerPanel = new JPanel(new GridLayout(1,2));
//		annotationContainerPanel.add(targetAnnotationPanel);
//		annotationContainerPanel.add(referenceAnnotationPanel);

		//the control panel
		toolbar = new ControlToolBar(this);

		//the overviews for the genomes
		for (GMapSet gMapSet : mainCanvas.gMapSetList)
		{
			OverviewCanvas overviewCanvas = new OverviewCanvas(this,gMapSet);
			overviewCanvas.setPreferredSize(new Dimension(0,250));
//			overViewsContainerPanel.add(overviewCanvas);
			overviewDialog.addCanvas(overviewCanvas);
			overviewCanvases.add(overviewCanvas);
		}

		overviewDialog.setVisible(Prefs.guiOverviewVisible);

		add(toolbar, BorderLayout.NORTH);
		add(mainPanel);
		add(zoomControlContainerPanel, BorderLayout.SOUTH);
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

}//end class
