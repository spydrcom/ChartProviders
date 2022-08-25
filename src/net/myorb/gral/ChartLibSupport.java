
package net.myorb.gral;

// CalcLib charting
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;

import net.myorb.charting.DisplayGraphLibraryInterface.Portions;
import net.myorb.charting.DisplayGraphProperties;
import net.myorb.charting.PlotLegend;

// GRAL graphics
import de.erichseifert.gral.graphics.Orientation;

// GRAL data
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;

// GRAL plots
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;

// JRE
import java.awt.Component;
import java.awt.Color;

/**
 * support for an implementation of the chart library using GRAL
 * @author Michael Druckman
 */
public class ChartLibSupport extends GralPrimitives
{


	/**
	 * build chart component
	 * @param funcPlots the collection of plot points
	 * @param title a title for the chart being built
	 * @param expressions the text of expressions being charted
	 * @param domainLabel a label for X axis
	 * @param rangeLabel a label for Y axis
	 * @return the chart component
	 */
	public static Component axisChartComponent
		(
			PlotCollection funcPlots, String title, String[] expressions, String domainLabel, String rangeLabel
		)
	{
		XYPlot plot = expressions.length==1 ?
			cvtToMultiSeries (expressions[0], funcPlots) : cvtToMultiSeries (expressions, funcPlots);
		return toComponent (axisChart (title, plot, domainLabel, rangeLabel));
	}


	/*
     * line plots for multiple functions using GRAL
     */


	/**
	 * a simple single series plot
	 * @param name the name of the plot
	 * @param points the data for the plot
	 * @return the GRAL XY plot object
	 */
	public static XYPlot cvtToMultiSeries (String name, PlotCollection points)
	{
		DataSeries series = null; DataTable data = newDataTable (2);
        for (Point p : points.get (0)) { data.add (p.x, valueFor (p)); }
        XYPlot plot = new XYPlot (series = new DataSeries (name, data, 0, 1));
        setRendering (plot, series, Color.BLUE);
		return plot;
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
     * regression plots using GRAL
     */


	/**
	 * build an XY plot of the regression
	 * @param data the source points for the regression
	 * @param interpolation the interpolation function plot
	 * @return the XY plot with data points and the regression plot
	 */
	public static Component cvtToRegression
		(Point.Series data, Point.Series interpolation)
	{
        return toComponent
        (
            styledRegressionPlot
            (
            	newDataSeriesFor (data, "Data"),
            	newDataSeriesFor (interpolation, "Interpolation")
            )
        );
	}


	/**
	 * set plot properties
	 * @param plot the GRAL XY plot
	 * @param dataSeries the GRAL data series for data points
	 * @param regressionSeries the GRAL data series for the regression plot
	 * @return the plot object
	 */
	public static XYPlot styledRegressionPlot
	(XYPlot plot, DataSeries dataSeries, DataSeries regressionSeries)
	{
		setInsets (plot);
		setRenderers (plot, "X", "f(X)", true, true);

		setPointRendering (plot, dataSeries, 4, Color.RED);
        setPointRendering (plot, regressionSeries, 1, Color.BLUE);
		setLineRendering (plot, regressionSeries, Color.BLUE);

		orientLegend
		(plot.getLegend (), Orientation.HORIZONTAL);
		setBorder (plot.getPlotArea ());
        plot.setLegendVisible (true);

        return plot;
	}


	/**
	 * create regression plot and set properties
	 * @param dataSeries the GRAL data series for data points
	 * @param regressionSeries the GRAL data series for the regression plot
	 * @return the plot object
	 */
	public static XYPlot styledRegressionPlot
	(DataSeries dataSeries, DataSeries regressionSeries)
	{
		return styledRegressionPlot
	    (
	    	new XYPlot
	        (
	        	dataSeries, regressionSeries
	        ),
	        dataSeries, regressionSeries
	    );
	}


	/*
     * bar/pie chart builders using GRAL
     */


	/**
	 * construct FFT bar chart
	 * @param title a title for the plot
	 * @param levels the height of bars
	 * @return the component
	 */
	public static Component makeTransformChart
		(
			String title,
			Point.Series levels
		)
	{
		BarPlot plot = new BarPlot (newDataTableFor (levels));
		axisChart ("FFT - " + title, plot, "", "");
		return toComponent (plot);
	}


	/**
	 * fulfill bar chart request
	 * @param title a title for the bar chart
	 * @param portions the portions per bar to plot
	 * @return the component to display
	 */
	public static Component makeBarChartComponent
		(String title, Portions portions)
	{
		DisplayGraphProperties.setPalate ();
		return toComponent (BarAndPieSupport.makeBarChart (title, portions));
	}


	/**
	 * fulfill pie chart request
	 * @param title a title for the pie chart
	 * @param portions the portions per slice to plot
	 * @return the component to display
	 */
	public static Component makePieChartComponent
		(String title, Portions portions)
	{
		DisplayGraphProperties.setPalate ();
		return toComponent (BarAndPieSupport.makePieChart (title, portions));
	}


	/*
	 * plot segment management
	 */


	/**
	 * tabulate multiple series
	 * @param names a name for each series
	 * @param points the plot data to be drawn
	 * @return the GRAL XY plot object
	 */
	public static XYPlot cvtToMultiSeries (String[] names, PlotCollection points)
	{
		SegmentList segments; String[] segmentNames = namesOf (segments = getSegments (names, points));
		return getPlotFor (getSeriesFor (getSegmentData (segments), segmentNames));
	}


	/**
	 * perform plot of series data
	 * @param series the collected data series
	 * @return the GRAL plot object
	 */
	public static XYPlot getPlotFor (DataSeries[] series)
	{
		XYPlot plot = new XYPlot (series); DisplayGraphProperties.setPalate ();
		int c = 0; for (DataSeries data : series) setRendering (plot, data, PlotLegend.COLORS[c++]);
		orientLegend (plot.getLegend (), Orientation.HORIZONTAL);
		//orientLegend (plot.getLegend (), plot);
		plot.setLegendVisible (true);
		return plot;
	}


	/*
	 * chart styles
	 */


	/**
	 * the implemented styles
	 */
	public enum Styles {BAR, PIE}


	/**
	 * @param styleName the format of chart selected
	 * @param portions the portions to use for the display
	 * @return the component formatted for display
	 */
	public Component componentFor
		(
			String styleName,
			Portions portions
		)
	{
		switch (getStyle (styleName))
		{
			case BAR: return makeBarChartComponent (null, portions);
			case PIE: return makePieChartComponent (null, portions);
		}
		return null;
	}


	/**
	 * @param styleName the name of the selected style
	 * @return the style selected from the command of this request
	 * @throws RuntimeException for unrecognized style names
	 */
	public Styles getStyle (String styleName) throws RuntimeException
	{
		try { return Styles.valueOf (styleName.toUpperCase ()); }
		catch (Exception x) { throw new RuntimeException ("Chart style not implemented: " + styleName); }
	}


}

