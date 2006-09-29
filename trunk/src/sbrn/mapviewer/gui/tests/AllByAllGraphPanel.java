package sbrn.mapviewer.gui.tests;

import java.awt.*;
import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

import sbrn.mapviewer.data.*;

class AllByAllGraphPanel extends JPanel
{
	private LinkSet linkset;
	private JFreeChart chart;
	
	AllByAllGraphPanel()
	{
		
	}
	
	void setLinkSet(LinkSet linkset, MapSet xAxisMapSet)
	{
		this.linkset = linkset;
		
		chart = ChartFactory.createXYLineChart(
			null,
			null, // xaxis title
			null, // yaxis title
			null,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);
		
		setChartData(xAxisMapSet);
				
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
		chart.setRenderingHints(rh);
		chart.removeLegend();

		XYPlot plot = chart.getXYPlot();
		
		XYDotRenderer dot = new XYDotRenderer();
		dot.setDotHeight(2);
		dot.setDotWidth(8);
		
		plot.setRenderer(dot);
		
//		plot.setDomainGridlinesVisible(false);
//		plot.setRangeGridlinesVisible(false);
		
        		
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
//		xAxis.setLowerBound(1);
//		xAxis.setUpperBound(data[data.length-1]);
		xAxis.setTickLabelFont(new JLabel().getFont());
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setLowerBound(0);
		
		yAxis.setTickLabelFont(new JLabel().getFont());
		
		ChartPanel panel = new ChartPanel(chart);
		
		
		setLayout(new BorderLayout());
		add(panel);
	}
	
	private void setChartData(MapSet xAxisMapSet)
	{
		XYSeries series = new XYSeries("");
		
		for (Link link: linkset)
		{
			float x, y;
			
			if (link.getFeature1().getOwningMapSet() == xAxisMapSet)
			{
				x = link.getFeature1().getStart();
				y = link.getFeature2().getStart();
			}
			else
			{
				x = link.getFeature2().getStart();
				y = link.getFeature1().getStart();
			}
			
			series.add(x, y);
		}
		
		XYSeriesCollection coll = new XYSeriesCollection(series);		
		chart.getXYPlot().setDataset(coll);
	}
}