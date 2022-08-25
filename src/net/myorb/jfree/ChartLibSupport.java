
package net.myorb.jfree;

// CalcLib charting
import net.myorb.charting.PlotLegend;
import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphSegmentTools;
import net.myorb.charting.DisplayGraphProperties;

// JFreeChart
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

//JFreeChart axis/plot
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;

//JFreeChart render
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

//JFreeChart data
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import javax.swing.JComponent;
import java.awt.Color;

/**
 * layer of abstraction over JFreeChart
 * @author Michael Druckman
 */
public class ChartLibSupport
{


	/*
	 * JFree Chart boiler plate
	 */

    /**
     * JFreeChart boiler plate for bar chart
     * @param dataset the data management object
     * @param title the title for the chart
     * @return new bar chart instance
     */
    protected static JFreeChart createChart
    (CategoryDataset dataset, String title)
    {
        JFreeChart chart = ChartFactory.createBarChart
        (
            title,
            null /* x-axis label*/, 
            null /* y-axis label */,
            dataset
        );

        chart.setBackgroundPaint (Color.WHITE);
        chart.getLegend ().setFrame (BlockBorder.NONE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot ();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis ();
        rangeAxis.setStandardTickUnits (NumberAxis.createIntegerTickUnits ());
        BarRenderer renderer = (BarRenderer) plot.getRenderer ();
        renderer.setDrawBarOutline (false);

        return chart;
    }


	/**
	 * construct a chart with axis
	 * @param chartTitle title for the chart
	 * @param dataCollection XY plot collection for JFreeChart
	 * @param domainTitle the text for the x-axis
	 * @param rangeTitle the y-axis text
	 * @param colors display choices
	 * @return Swing component
	 */
	public static ChartPanel axisChart
		(
			String chartTitle,
			XYSeriesCollection[] dataCollection,
			String domainTitle, String rangeTitle,
			DisplayGraphTypes.Colors colors
		)
	{
		XYPlot plot = new XYPlot ();

		for (int i = 0; i < dataCollection.length; i++)
		{ mapDatasetToAxis (i, plot, dataCollection[i], true, false, colors); }
		labelAxis (plot, domainTitle, rangeTitle);

		JFreeChart chart = new JFreeChart
			(
				chartTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, true
			);
		return new ChartPanel (chart);
	}


    /*
	 * JFreeChart primitive conversions
	 */


	/**
	 * construct a chart with axis
	 * @param chartTitle title for the chart
	 * @param funcPlots collection of plots to include
	 * @param expressions identification of the series
	 * @param domainTitle the text for the x-axis
	 * @param rangeTitle the y-axis text
	 * @param colors display choices
	 * @return Swing component
	 */
	public static JComponent axisChart
		(
			String chartTitle,
			PlotCollection funcPlots, String[] expressions,
			String domainTitle, String rangeTitle,
			DisplayGraphTypes.Colors colors
		)
	{
		return axisChart (chartTitle, getCollection (funcPlots, expressions), domainTitle, rangeTitle, colors);
	}
	static XYSeriesCollection[] getCollection (PlotCollection funcPlots, String[] expressions)
	{
		if (expressions.length > 1) { return cvtToMultiSeries (expressions, funcPlots); }
		else { return cvtToMultiSeries (expressions[0], funcPlots); }
	}


	/**
	 * determine appropriate naming
	 * @param name the name of the series
	 * @param points the list of series points
	 * @return collection of series
	 */
	public static XYSeriesCollection[] cvtToMultiSeries
		(String name, DisplayGraphTypes.PlotCollection points)
	{
		if (points.size () > 1) return cvtToMultiSeries (points);
		else return cvtToMultiSeries (new String[]{name}, points);
	}


	/**
	 * build series collection from lists of points
	 * @param points the list of series points
	 * @return collection of series
	 */
	public static XYSeriesCollection[] cvtToMultiSeries (DisplayGraphTypes.PlotCollection points)
	{
		String[] names = new String[points.size ()];
		for (int i=0; i<points.size();i++) { names[i] = "f" + Integer.toString (i); }
		return cvtToMultiSeries (names, points);
	}


	/**
	 * collect multiple series
	 * @param names the names of the series
	 * @param points the list of series points
	 * @return the collection
	 */
	public static XYSeriesCollection[] cvtToMultiSeries
		(String[] names, DisplayGraphTypes.PlotCollection points)
	{
		int n = 0;
		XYSeriesCollection[] collection = new XYSeriesCollection[names.length];
		for (DisplayGraphTypes.Point.Series plot : points)
		{
			addPlot
			(
				plot, names[n], collection[n] = new XYSeriesCollection ()
			);
			n++;
		}
		return collection;
	}


	/**
	 * build series from list of points 
	 * @param name the name to be given to the series
	 * @param points the list of points for the series
	 * @return the series object
	 */
	public static XYSeries cvtToSeries (String name, DisplayGraphTypes.Point.Series points)
	{
		XYSeries series = new XYSeries (name);
		for (DisplayGraphTypes.Point p : points) if (!p.outOfRange) series.add (p.x, p.y);
		return series;
	}


	/**
	 * add series to collection
	 * @param points the list of series points
	 * @param named the name to be given to the series
	 * @param to the collection
	 */
	public static void addPlot
	(DisplayGraphTypes.Point.Series points, String named, XYSeriesCollection to)
	{
		DisplayGraphSegmentTools.getSegmentControl ().constructSegmentCollection
				(points, named, new SegmentManager (to));
	}


	/**
	 * convert display graph constructs to
	 *   XYSeries and XYSeriesCollection
	 */
	public static class SegmentManager
		implements DisplayGraphTypes.SegmentCollector
	{

		/**
		 * convert segment to series
		 * @param segment the segment
		 * @return the series result
		 */
		XYSeries toSeries (DisplayGraphTypes.NamedSegment segment)
		{
			XYSeries series = new XYSeries (segment.getName ());
			for (DisplayGraphTypes.Point p : segment) series.add (p.x, p.y);
			return series;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.charting.DisplayGraphAtomic.SegmentCollector#add(net.myorb.math.expressions.charting.DisplayGraphAtomic.NamedSegment)
		 */
		public void add (DisplayGraphTypes.NamedSegment segment)
		{
			collection.addSeries (toSeries (segment));
		}

		public SegmentManager
		(XYSeriesCollection collection)
		{ this.collection = collection; }
		XYSeriesCollection collection;

	}


	/**
	 * add series to collection
	 * @param series the series to be added
	 * @return the new collection
	 */
	public static XYSeriesCollection cvtToColl (XYSeries series)
	{
		XYSeriesCollection collection = new XYSeriesCollection ();
		collection.addSeries (series);
		return collection;
	}


	/*
	 * JFreeChart properties
	 */


	/**
	 * describe a plot of chart
	 * @param forSet data set ID
	 * @param ofPlot the plot object
	 * @param fromCollection the data collection
	 * @param renderLines request lines to be rendered
	 * @param renderShapes request shapes rendered
	 * @param colors the colors to render
	 */
	protected static void mapDatasetToAxis
		(
			int forSet, XYPlot ofPlot,
			XYSeriesCollection fromCollection,
			boolean renderLines, boolean renderShapes,
			DisplayGraphTypes.Colors colors
		)
	{
		DisplayGraphProperties.setPalate ();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer (renderLines, renderShapes);
		if (forSet >= PlotLegend.COLORS.length) throw new RuntimeException ("Excessive plot count request");
		renderer.setAutoPopulateSeriesPaint (false); renderer.setBasePaint (PlotLegend.COLORS[forSet]);

		ofPlot.setDataset (forSet, fromCollection);
		ofPlot.mapDatasetToDomainAxis (forSet, 0);
		ofPlot.mapDatasetToRangeAxis (forSet, 0);
		ofPlot.setRenderer (forSet, renderer);
	}


	/**
	 * labels for axis
	 * @param plot the plot object
	 * @param domainTitle the domain label
	 * @param rangeTitle the range label
	 */
	protected static void labelAxis (XYPlot plot, String domainTitle, String rangeTitle)
	{
		plot.setDomainAxis (0, new NumberAxis (domainTitle));
		plot.setRangeAxis (0, new NumberAxis (rangeTitle));
	}


    /**
     * scatter VS interpolation
     * @param dataPoints the data points for the scatter
     * @param funcPlot the plot of the interpolation function
     * @param title the title for the chart
     * @return the display component
     */
    public JComponent regressionPlotComponent
		(
			Point.Series dataPoints,
			Point.Series funcPlot,
			String title
		)
	{
    	XYSeriesCollection data = cvtToColl (cvtToSeries ("Data Points", dataPoints));
    	XYSeriesCollection interp = cvtToColl (cvtToSeries ("Interpolation", funcPlot));
    	return RegressionCharts.makeRegressionChart (title, data, interp, "X", "Y");
	}

}

