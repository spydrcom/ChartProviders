
package net.myorb.bfo;

// CalcLib charting
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.SegmentCollector;
import net.myorb.charting.DisplayGraphTypes.NamedSegment;
import net.myorb.charting.DisplayGraphSegmentTools;
import net.myorb.charting.DisplayGraphProperties;
import net.myorb.charting.PlotLegend;

// BFO
import org.faceless.graph2.Key;
import org.faceless.graph2.TextStyle;
import org.faceless.graph2.AxesGraph;
import org.faceless.graph2.ScatterSeries;
import org.faceless.graph2.LineSeries;
import org.faceless.graph2.BarSeries;
import org.faceless.graph2.Style;
import org.faceless.graph2.Axis;

// JRE AWT
import java.awt.Color;

/**
 * support for an implementation of the chart library using BFO
 * @author Michael Druckman
 */
public class ChartLibSupport extends BfoPrimitives
{


	/**
	 * @param chartTitle the chart title
	 * @param graph the graph object holding the data
	 * @param domainTitle the x-axis title
	 * @param rangeTitle the y-axis title
	 * @return graph with axis titles
	 */
	public static AxesGraph axisChart
		(
			String chartTitle, AxesGraph graph,
			String domainTitle, String rangeTitle
		)
	{
		BfoPrimitives.setTitle (graph, chartTitle);
		//PSET CHART AXIS_TITLE_STYLE Default 18 BLACK
		TextStyle axisStyle = getStyle ("AXIS_TITLE_STYLE", KindOfStyle.TEXT).getTextStyle ();
		graph.getAxis (Axis.BOTTOM).setLabel (domainTitle, axisStyle);
		graph.getAxis (Axis.LEFT).setLabel (rangeTitle, axisStyle);
		return graph;
	}
	public static Image axisChartDisplay
		(
			String chartTitle,
			PlotCollection funcPlots, String[] expression,
			String domainTitle, String rangeTitle
		)
	{
		return getImageFor
		(
			axisChart (chartTitle, cvtToMultiSeries (expression, funcPlots), domainTitle, rangeTitle)
		);
	}
	static AxesGraph getCollection (PlotCollection funcPlots, String[] expressions)
	{
		if (expressions.length > 1) { return cvtToMultiSeries (expressions, funcPlots); }
		else { return cvtToMultiSeries (expressions[0], funcPlots); }
	}


	/*
	 * single function plot
	 */


	/**
	 * a simple single series plot
	 * @param name the name of the plot
	 * @param points the data for the plot
	 * @return the BFO Graph object
	 */
	public static AxesGraph cvtToMultiSeries (String name, PlotCollection points)
	{
		AxesGraph graph = new AxesGraph ();
		addPlot (points.get (0), name, 0, null, graph);
		graph.setBackWallPaint (Color.WHITE);
		setAxis (graph, false, true);
		return graph;
	}


	/**
	 * a simple single series plot
	 * @param title a title for the plot
	 * @param functionPlot the data for the plot
	 * @param profile the function profile text
	 * @param parameter name for parameter
	 */
	public void showPlot (String title, PlotCollection functionPlot, String profile, String parameter)
	{
		showFrame (title, axisChart (title, cvtToMultiSeries (profile, functionPlot), parameter, profile));
	}


	/*
     * line plots for multiple functions using BFO
     */


	/**
	 * tabulate multiple series
	 * @param names a name for each series
	 * @param points the plot data to be drawn
	 * @return the BFO Graph object
	 */
	public static AxesGraph cvtToMultiSeries (String[] names, PlotCollection points)
	{
		int color = 0;
		DisplayGraphProperties.setPalate ();
		AxesGraph graph = new AxesGraph ();

		StyleWrapper properties = getStyle ("KEY_STYLE", KindOfStyle.KEY);
		Key key = new Key (properties.getStyle ());

		for (int i = 0; i < names.length; i++)
		{
			SegmentManager s = addPlot
				(points.get (i), names[i], color, key, graph);
			color = s.nextColor;
		}

		graph.setBackWallPaint (Color.WHITE);
		graph.addKey (key, properties.getAlign ().getValue ());
		setAxis (graph, false, true);
		return graph;
	}


	/*
     * regression plots using BFO
     */


	/**
	 * build an XY plot of the regression
	 * @param data the source points for the regression
	 * @param interpolation the interpolation function plot
	 * @return the XY plot with data points and the regression plot
	 */
	public static Image cvtToRegression
	(Point.Series data, Point.Series interpolation)
	{
		AxesGraph graph = new AxesGraph ();
		ScatterSeries dataSeries = getScatterSeries ("Data");
		LineSeries interpolationSeries = new LineSeries ("Interpolation");
		dataSeries.setStyle (new Style (Color.RED)); interpolationSeries.setStyle (new Style (Color.BLUE));
		populate (dataSeries, data); populate (interpolationSeries, interpolation);
		addKey (graph, dataSeries, interpolationSeries);
		setAxis (graph, false, true); 
		return getImageFor (graph);
	}


	/*
     * bar charts using BFO
     */


	/**
	 * construct FFT bar chart
	 * @param title a title for the plot
	 * @param levels the height of bars
	 * @return the component
	 */
	public static Image makeBarChart
		(
			String title,
			Point.Series levels
		)
	{
		AxesGraph graph = new AxesGraph ();
		BarSeries series = new BarSeries ("Levels");
		int fullCount = levels.size (), countPer = fullCount / 9, n = 0;
		for (int i = 0; i < 9; i++)
		{
			double sum = 0;
			for (int j = 0; j < countPer; j++)
			{ sum += levels.get (n++).y; }
			series.set (BARS[i], sum);
		}
		graph.addSeries (series);
		setAxis (graph, false, true);
		return getImageFor (graph);
	}
	static final String[] BARS = {"L", "L+", "L++", "M-", "M", "M+", "H--", "H-", "H"};


	/*
	 * segment management
	 */


	/**
	 * add series to collection
	 * @param points the list of series points
	 * @param named the name to be given to the series
	 * @param startingColor the legend color to start with
	 * @param key the legend being built for this graph
	 * @param to the graph being built
	 * @return a Segment Manager
	 */
	public static SegmentManager addPlot
		(
			Point.Series points, String named,
			int startingColor, Key key, AxesGraph to
		)
	{
		SegmentManager s;
		DisplayGraphSegmentTools.getSegmentControl ().constructSegmentCollection
		(points, named, s = new SegmentManager (to, key, startingColor));
		return s;
	}


	/**
	 * convert display graph constructs to
	 *   LineSeries and AxesGraph BFO objects
	 */
	public static class SegmentManager
		implements SegmentCollector
	{

		/**
		 * convert segment to series
		 * @param segment the segment
		 * @return the series result
		 */
		LineSeries toSeries (NamedSegment segment)
		{
			LineSeries series =
				new LineSeries (segment.getName ());
			for (Point p : segment) series.set (p.x, p.y);
			setStyleFor (series, PlotLegend.COLORS[nextColor++]);
			if (key != null) key.addSeries (series, keyTextProperties.getTextStyle ());
			return series;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.charting.DisplayGraphAtomic.SegmentCollector#add(net.myorb.math.expressions.charting.DisplayGraphAtomic.NamedSegment)
		 */
		public void add (NamedSegment segment)
		{
			graph.addSeries (toSeries (segment));
		}

		public SegmentManager (AxesGraph graph, Key key, int nextColor)
		{ this.graph = graph; this.nextColor = nextColor; this.key = key; }
		StyleWrapper keyTextProperties = getStyle ("KEY_TEXT_STYLE", KindOfStyle.TEXT);
		int nextColor; AxesGraph graph; Key key;

	}


}

