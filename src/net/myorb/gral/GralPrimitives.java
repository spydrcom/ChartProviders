
package net.myorb.gral;

// CalcLib chart . DisplayGraphLibraryInterface
import net.myorb.charting.DisplayGraphLibraryInterface.Portion;
import net.myorb.charting.DisplayGraphLibraryInterface.Portions;

//CalcLib chart properties
import net.myorb.charting.DisplayGraphProperties;
import net.myorb.charting.DisplayGraphSegmentTools;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.PlotLegend;

//GRAL graphics
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.ui.InteractivePanel;

//GRAL data
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;

//GRAL plots
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.legends.ValueLegend;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.plots.PlotArea;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.Plot;

// JRE Text
import java.text.Format;
import java.util.Arrays;

//JRE AWT
import java.awt.geom.Ellipse2D;
import java.awt.Component;
import java.awt.Color;

/**
 * atomic object property controls of the GRAL chart library
 * @author Michael Druckman
 */
public class GralPrimitives extends DisplayGraphSegmentTools
{


	/**
	 * use standard frame for GRAL
	 * @param title a title for the frame
	 * @param drawable the GRAL component being drawn
	 */
	public static void showFrame (String title, Drawable drawable)
	{
		showComponentFrame (title, toComponent (drawable));
	}
	public static void showComponentFrame (String title, Component component)
	{
		StandardFrame frame = new StandardFrame (title);
		frame.setDisplayComponent (component);
		resizeAndShow (frame);
	}
	public static void resizeAndShow (StandardFrame frame)
	{
		StyleWrapper properties =
		GralProperties.getStyle ("IMAGE", GralProperties.KindOfStyle.IMAGE);
		frame.show (properties.getW (), properties.getH ());
	}


	/**
	 * @param drawable the GRAL component being drawn
	 * @return equivalent AWT Component
	 */
	public static Component toComponent (Drawable drawable)
	{
		return new InteractivePanel (drawable);
	}


	/*
	 * portions as array
	 */


	/**
	 * @param portions a CalcLib Portions object
	 * @return an array of CalcLib Portion
	 */
	public static Portion [] getPortionsFrom (Portions portions)
	{
		return portions.toArray (new Portion [] {});
	}


	/*
	 * plot properties initialization
	 */


	/**
	 * initialization for plot insets and title
	 * @param plot the plot being created
	 * @return the same plot object
	 */
	public static Plot startPlot (Plot plot)
	{
		setInsets (plot);
		plot.getTitle ().setText
			(DisplayGraphProperties.getTextProperty ("TITLE"));
		return plot;
	}


	/**
	 * initialization for plot labels and axis ranges
	 * @param dataSeries the data series included in plot
	 * @param xMax the maximum value along x axis
	 * @param yMax the maximum value along y axis
	 * @return the GRAL plot object
	 */
	public static Plot startPlot (DataSeries[] dataSeries, int xMax, int yMax)
	{
		Plot plot = startPlot (new XYPlot (dataSeries));

        setRenderers
        (
        	plot,
        	DisplayGraphProperties.getTextProperty ("XAXIS_LABEL"),
        	DisplayGraphProperties.getTextProperty ("YAXIS_LABEL"),
        	false, true
        );

        plot.getAxis (XYPlot.AXIS_X).setRange (0, xMax);
        plot.getAxis (XYPlot.AXIS_Y).setRange (0, yMax);

        return plot;
	}


	/**
	 * renderer and legend for bar chart
	 * @param plot the plot object holding bar chart data
	 * @return the plot after configuration changes
	 */
	public static Plot configured (Plot plot)
	{
        int n = 0;
        for (DataSource source : plot.getData ())
        {
        	ExtendedBarRenderer.setPointRendering
        	(
        		plot, source,
        		getPropertiesFor (n++)
        	);
        }
        orientLegend (plot.getLegend (), Orientation.HORIZONTAL);
        plot.setLegendVisible (true);
        return plot;
	}


	/*
	 * populate a data table from a point series
	 */


	/**
	 * create data table and populate
	 * @param points the points to be added to data table
	 * @return the populated data table
	 */
	public static DataTable newDataTableFor (Point.Series points)
	{
		DataTable table = newDataTable (2);
		for (Point p : points) table.add (p.x, valueFor (p));
		return table;
	}


	/**
	 * make series from points
	 * @param points the points making up the series
	 * @param called the name of the series
	 * @return a GRAL DataSeries object
	 */
	public static DataSeries newDataSeriesFor (Point.Series points, String called)
	{ return new DataSeries (called, newDataTableFor (points), 0, 1); }


	/*
	 * border
	 */


	/**
	 * set style of border
	 * @param plotArea the GRAL plot area for the XY plot
	 */
	public static void setBorder (PlotArea plotArea)
	{
		GralProperties.getStyle
			("GRAL_BORDER", GralProperties.KindOfStyle.BORDER)
		.setBorder (plotArea);
	}


	/*
	 * axis, line and point renders
	 */


	/**
	 * add title(s)
	 * @param chartTitle the title
	 * @param dataCollection the XY plot object
	 * @param domainTitle x-axis caption
	 * @param rangeTitle y-axis caption
	 * @return the component
	 */
	public static Drawable axisChart
		(
			String chartTitle, XYPlot dataCollection,
			String domainTitle, String rangeTitle
		)
	{
		setInsets (dataCollection);
		dataCollection.getTitle ().setText (chartTitle);
		setRenderers (dataCollection, domainTitle, rangeTitle, true, true);
		setBorder (dataCollection.getPlotArea ());
		return dataCollection;
	}


	/**
	 * add labels to X and Y axis
	 * @param dataCollection the XY plot object
	 * @param domainTitle x-axis caption text
	 * @param rangeTitle y-axis caption text
	 * @param xAxisVisible TRUE = show ticks
	 * @param yAxisVisible TRUE = show ticks
	 */
	public static void setRenderers
		(
			Plot dataCollection,
			String domainTitle, String rangeTitle,
			boolean xAxisVisible, boolean yAxisVisible
		)
	{
		LinearRenderer2D x, y;
	    dataCollection.setAxisRenderer (XYPlot.AXIS_X, x = newRenderer (domainTitle));
	    dataCollection.setAxisRenderer (XYPlot.AXIS_Y, y = newRenderer (rangeTitle));
	    x.setTicksVisible (xAxisVisible); y.setTicksVisible (yAxisVisible);
	}


	/**
	 * construct renderer for holding axis label
	 * @param title a label for the axis being rendered
	 * @return a renderer holding the label
	 */
	public static LinearRenderer2D newRenderer (String title)
	{
		LinearRenderer2D renderer = new LinearRenderer2D ();
		renderer.setLabel (new Label (title)); renderer.setIntersection (-Double.MAX_VALUE);
		return renderer;
	}


	/**
	 * set point and line rendering objects
	 * @param plot the GRAL plot being rendered
	 * @param data one of the GRAL data series for a plot
	 * @param color the color for this plot
	 */
	public static void setRendering (XYPlot plot, DataSeries data, Color color)
	{
		setPointRendering (plot, data, 1, color);
		setLineRendering (plot, data, color);
	}


	/**
	 * GRAL point rendering
	 * @param plot the GRAL XY plot
	 * @param data the GRAL data series
	 * @param sizeMultiplier a multiple of the minimum point size
	 * @param color the AWT color
	 */
	public static void setPointRendering
	(XYPlot plot, DataSeries data, int sizeMultiplier, Color color)
	{
        PointRenderer points = new DefaultPointRenderer2D ();
		double lo = -1.0 * sizeMultiplier, hi = 2.0 * sizeMultiplier;
		Ellipse2D.Double ellipse = new Ellipse2D.Double (lo, lo, hi, hi);
        points.setShape (ellipse); points.setColor (color);
        plot.setPointRenderers (data, points);
	}


	/**
	 * GRAL line rendering
	 * @param plot the GRAL XY plot
	 * @param data the GRAL data series
	 * @param color the AWT color
	 */
	public static void setLineRendering (XYPlot plot, DataSeries data, Color color)
	{
        LineRenderer lines = new DefaultLineRenderer2D ();
        plot.setLineRenderers (data, lines);
        lines.setColor (color);
	}


	/**
	 * use renderer to compute position represented by value
	 * @param renderer the axis renderer to use for the computation
	 * @param axis the access to the axis being described by the value
	 * @param value the value that is to be represented
	 * @param dimension the dimension to be described
	 * @return the computed position of the value
	 */
	public static double computePoint (AxisRenderer renderer, Axis axis, double value, int dimension)
	{
		return renderer.getPosition (axis, value, true, false).get (dimension);
	}


	/*
	 * Data table
	 */


	/**
	 * a table for collecting data
	 * @param columns the count of columns
	 * @return a configured data table
	 */
	public static DataTable newDataTable (int columns)
	{
        return new DataTable (doubleDataType (columns));
	}


	/**
	 * @param columns the count of columns
	 * @return the sized array of class objects
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	public static Class<? extends Comparable<?>>[] doubleDataType (int columns)
	{
		Class[] classes = new Class[columns];
		Arrays.fill (classes, Double.class);
		return classes;
	}


	/*
	 * Data series
	 */


	/**
	 * translate data to series
	 * @param data the tables of plot data
	 * @param names the names for each of the plots
	 * @return the series object for the data collected
	 */
	public static DataSeries[] getSeriesFor (DataTable[] data, String[] names)
	{
		DataSeries[] series = new DataSeries[data.length];
		for (int i = 0; i < series.length; i++) series[i] = new DataSeries (names[i], data[i], 0, 1);
		return series;
	}


	/**
	 * populate a data table per segment to be included in plot
	 * @param segments the list of segments to plot
	 * @return a data table per segment
	 */
	public static DataTable[] getSegmentData (SegmentList segments)
	{
		DataTable[] data =
			new DataTable[segments.size ()];
		for (int segNo = 0; segNo < data.length; segNo++)
		{ data[segNo] = newDataTableFor (segments.get (segNo)); }
		return data;
	}


	/*
     * text legends for bar/pie charts using GRAL
     */


	/**
	 * @param legend the legend being configured
	 * @param labelColumn the column of the data table holding the label index
	 * @param formatter the label formatter for text in the legend
	 * @param orientation the orientation for the display
	 */
	public static void prepareLegend
		(
			ValueLegend legend, int labelColumn,
			Format formatter, Orientation orientation
		)
	{
		legend.setLabelColumn (labelColumn);
		orientLegend (legend, orientation);
		legend.setLabelFormat (formatter);
	}


	/**
	 * @param legend the legend being configured
	 * @param orientation the orientation for the display
	 */
	public static void orientLegend
		(
			Legend legend,
			Orientation orientation
		)
	{
		StyleWrapper legendProperties =
			GralProperties.getStyle ("GRAL_LEGEND", GralProperties.KindOfStyle.LEGEND);
		orientLegend (legend, orientation, legendProperties.getOrientation ());
		legend.setAlignmentX (legendProperties.getXalign ());
		legend.setAlignmentY (legendProperties.getYalign ());
	}
	public static void orientLegend
		(
			Legend legend,
			Orientation orientation,
			String override
		)
	{
		if (override != null)
		{ orientation = Orientation.valueOf (override); }
		legend.setOrientation (orientation);
	}


	/*
	 * Insets
	 */


	/**
	 * use common spacing on displays
	 * @param plot the plot object to adjust
	 */
	public static void setInsets (Plot plot)
	{
		StyleWrapper insetProperties =
		GralProperties.getStyle ("GRAL_INSETS", GralProperties.KindOfStyle.INSETS);
		plot.setInsets (insetProperties.getInsets ());
	}


	/*
	 * Properties
	 */


	/**
	 * @param barIndex the index into the chart for this bar
	 * @return the renderer to use for this bar index
	 */
	public static ExtendedBarRenderer.Properties getPropertiesFor (int barIndex)
	{
		StyleWrapper barProperties =
		GralProperties.getStyle ("GRAL_BARS", GralProperties.KindOfStyle.BARS);
		barProperties.setColor (PlotLegend.COLORS[barIndex]);
		return barProperties;
	}


}

