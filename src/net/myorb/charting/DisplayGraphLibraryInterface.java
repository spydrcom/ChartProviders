
package net.myorb.charting;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.RealFunction;
import net.myorb.charting.DisplayGraphTypes.Colors;

import java.util.Set;

import java.awt.Color;

/**
 * graphics library interface
 * @author Michael Druckman
 */
public interface DisplayGraphLibraryInterface
{


	/**
	 * name/value pair for pie/bar charts
	 */
	public interface Portion
	{
		/**
		 * @return name of the portion
		 */
		String getName ();

		/**
		 * @return size of the portion
		 */
		double getPortion ();
	}
	public interface Portions extends Set<Portion>
	{
		/**
		 * @return the names of the portions
		 */
		String[] getNames ();
	}


	/**
	 * portion description by category for z-axis bar charts
	 */
	public interface CategorizedPortion extends Portion
	{
		/**
		 * @return portions positional by category
		 */
		double[] getPortionsByCategory ();
	}
	public interface CategorizedPortions extends Portions
	{
		/**
		 * @return a label for describing the category
		 */
		String getCategoryTitle ();

		/**
		 * @return a label per category
		 */
		String[] getCategories ();
	}


	/**
	 * produce a portioned chart
	 * @param styleName the style of the chart
	 * @param portions the portions to be displayed
	 */
	public void traditionalChart
		(
			String styleName,
			Portions portions
		);


	/**
	 * produce an interpolation function graph against data points
	 * @param dataPoints the set of raw plot points from original data
	 * @param funcPlot the set of plot points for the interpolation function
	 * @param title a text title for use by the display frame
	 */
	public void regressionPlot
		(
			Point.Series dataPoints,
			Point.Series funcPlot,
			String title
		);


	/**
	 * simple bar chart
	 * @param funcPlot the set of plot points
	 * @param title a text title for use by the display frame
	 */
	public void barChart
		(
			Point.Series funcPlot,
			String title
		);


	/**
	 * simple single function plot
	 * @param color the color to use for the plot
	 * @param funcPlot the set of plot points for the function
	 * @param functionName the name of the user defined function
	 * @param parameter name of the parameter from the declaration
	 * @param f access to the function
	 */
	public void singlePlotWithAxis
		(
			Color color, PlotCollection funcPlot,
			String functionName, String parameter, RealFunction f
		);


	/**
	 * display multiple plots on same set of axis
	 * @param colors the color to use for each plot
	 * @param funcPlot the set of plot points for each function
	 * @param title a text title for the display frame
	 * @param expression description of plot request
	 * @param f access to the function
	 */
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlot, String title, String expression, RealFunction f
		);


}

