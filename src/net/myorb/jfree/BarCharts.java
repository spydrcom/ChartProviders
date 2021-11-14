
package net.myorb.jfree;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphLibraryInterface.CategorizedPortion;
import net.myorb.charting.DisplayGraphLibraryInterface.CategorizedPortions;
import net.myorb.charting.DisplayGraphLibraryInterface.Portions;
import net.myorb.charting.DisplayGraphLibraryInterface.Portion;
import net.myorb.charting.DisplayGraphProperties;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DatasetUtilities;

import javax.swing.JComponent;
import java.awt.Dimension;

/**
 * bar chart specific methods using JFreeChart
 * @author Michael Druckman
 */
public class BarCharts extends ChartLibSupport
{


	/**
	 * prepare display panel
	 * @param chart the chart to display
	 * @return the chart panel
	 */
	public static ChartPanel makeChartPanel
			(JFreeChart chart)
	{
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setFillZoomRectangle (true); chartPanel.setMouseWheelEnabled (true);
        chartPanel.setPreferredSize (new Dimension (600, 400));
        return chartPanel;
	}


	/**
	 * @param dataset the data for the plot
	 * @param withLegend TRUE = legend included
	 * @return the chart panel
	 */
	public static ChartPanel makeChartPanel
	(CategoryDataset dataset, boolean withLegend)
	{
        JFreeChart chart = 
            	ChartFactory.createBarChart
            	(
            		DisplayGraphProperties.getTextProperty (TITLE),
            		DisplayGraphProperties.getTextProperty (DOMAIN_LABEL),
            		DisplayGraphProperties.getTextProperty (RANGE_LABEL),
    	            dataset, getPlotOrientation (),
    	            withLegend, true, true											// include legend, tool-tip, URLs
    	        );
		return makeChartPanel (chart);
	}
	public static final String TITLE = "TITLE",
	DOMAIN_LABEL = "XAXIS_LABEL", RANGE_LABEL = "YAXIS_LABEL";


	/**
	 * @return the orientation as configured or defaulted
	 */
	public static PlotOrientation getPlotOrientation ()
	{
		String orient =
			DisplayGraphProperties.getTextProperty (ORIENTATION);
		if (orient.toUpperCase ().equals ("HORIZONTAL"))
		{ return PlotOrientation.HORIZONTAL; }
		return PlotOrientation.VERTICAL;
	}
	public static final String ORIENTATION = "ORIENTATION";


	/**
	 * construct bar chart
	 * @param title a title for the chart
	 * @param levels the relative lengths of bars
	 * @return the display component
	 */
	public static ChartPanel makeBarChart
		(
			String title, Point.Series levels
		)
	{
        CategoryDataset dataset = createDataset (levels);
        return makeChartPanel (createChart (dataset, title));
	}
	public static JComponent makeBarChartComponent
		(
			String title, Point.Series portions
		)
	{
		return makeBarChart (title, portions);
	}


    /**
     * create data set for bar chart
     * @param levels the relative lengths of bars
     * @return the data set
     */
    private static CategoryDataset createDataset (Point.Series levels)
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset ();
        //int c = 1; for (Point p : levels) dataset.addValue (p.y, "R", Integer.toString (c++));
        //for (int i = 0; i < levels.size (); i++) dataset.addValue (barSum (levels, i), "Value", Integer.toString (i));
        for (int i = 0; i < levels.size (); i++) dataset.addValue (barSum (levels, i), "Value", Double.toString (levels.get (i).x));
        return dataset;
    }
    static double barSum (Point.Series levels, int from)
    {
//    	double sum = 0;
//    	int first = from, last = from + BAR_WIDTH;
//    	if (last >= levels.size()) last = levels.size();
//    	for (int i=first; i < last; i++) sum += levels.get (i).y;
//    	return sum;
    	return levels.get (from).y;
    }
    static int BAR_WIDTH = 25;


	/**
	 * make a z-axis bar chart
	 * @param title a title for the frame of the display
	 * @param portions the set of portions of the data
	 * @return a chart panel for display
	 */
	public static ChartPanel makeZaxisBarChart
		(
			String title, CategorizedPortions portions
		)
	{
		String[] catagories = portions.getCategories ();
		String[] itemLabels = new String[portions.size ()];

		return makeChartPanel
		(
			DatasetUtilities.createCategoryDataset
			(
				catagories, itemLabels,
				populate (portions, itemLabels, catagories.length)
			),
			true
		);
	}

	/**
	 * @param portions the description of all portions
	 * @param itemLabels the list capturing item labels
	 * @param catagories the count of catagories
	 * @return the populated data matrix
	 */
	static double[][] populate
	(CategorizedPortions portions, String[] itemLabels, int catagories)
	{
		int p = 0, items = itemLabels.length;
		double[][] data = dataFor (catagories, items);
		for (Portion portion : portions)
		{
			populate
			(
				data,
				(CategorizedPortion) portion,
				itemLabels, catagories, p++
			);
		}
		return data;
	}

	/**
	 * @param data the data matrix
	 * @param portion the portion list by category
	 * @param itemLabels the list capturing item labels
	 * @param catagories the count of catagories
	 * @param portionNumber the portion index
	 */
	static void populate
		(
			double[][] data, CategorizedPortion portion,
			String[] itemLabels, int catagories, int portionNumber
		)
	{
		populate
		(
			data,
			portion.getName (), portion.getPortionsByCategory (),
			itemLabels, catagories, portionNumber
		);
	}

	/**
	 * @param data the data matrix
	 * @param name the name of the item being processed
	 * @param portionByCategory the portions by category
	 * @param itemLabels the list capturing item labels
	 * @param catagories the count of catagories
	 * @param portion the portion index
	 */
	static void populate
		(
			double[][] data,
			String name, double[] portionByCategory,
			String[] itemLabels, int catagories, int portion
		)
	{
		for (int i = 0; i < catagories; i++)
		{ data[i][portion] = portionByCategory[i]; }
		itemLabels[portion] = name;
	}

	/**
	 * @param catagories the list of categories
	 * @param items the list of data items
	 * @return allocated data matrix
	 */
	static double[][] dataFor (int catagories, int items)
	{
		double[][] data = new double[catagories][];
		for (int i = 0; i < data.length; i++) data[i] = new double[items];
		return data;
	}


	/**
	 * make a bar chart
	 * @param title a title for the frame of the display
	 * @param portions the set of portions of the data
	 * @return a chart panel for display
	 */
	public static ChartPanel makeBarChart
		(
			String title, Portions portions
		)
	{
		String name; double value;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset ();

        for (Portion p : portions)
        {
        	name = p.getName (); value = p.getPortion ();
        	dataset.addValue (value, Double.toString (value), name);
        }

	    return makeChartPanel (dataset, true);
	}


	/**
	 * make a pie chart
	 * @param title a title for the frame of the display
	 * @param portions the set of portions of the data
	 * @return a chart panel for display
	 */
	public static ChartPanel makePieChart
		(
			String title, Portions portions
		)
	{
	    DefaultPieDataset data = new DefaultPieDataset ();
	    for (Portion p : portions) { data.setValue (p.getName (), p.getPortion ()); }
	    JFreeChart chart = ChartFactory.createPieChart (DisplayGraphProperties.getTextProperty (TITLE), data);
	    chart.removeLegend (); return makeChartPanel (chart);
	}


	/**
	 * make chart of specified kind
	 * @param kind the kind chosen (BAR, PIE)
	 * @param title a title for the frame of the display
	 * @param portions the set of portions of the data
	 * @return a chart panel for display
	 */
	public static ChartPanel makeChart
		(
			String kind, String title, Portions portions
		)
	{
		//DisplayGraph.setPalette ();
		if ("BAR".equals (kind.toUpperCase ()))
		{ return makeBarChart (title, portions); }
		else if ("ZAXIS".equals (kind.toUpperCase ()))
		{
			if (portions instanceof CategorizedPortions)
			{ return makeZaxisBarChart (null, (CategorizedPortions) portions); }
			else throw new RuntimeException ("Data must be dimensioned by category");
		}
		else if ("PIE".equals (kind.toUpperCase ()))
		{
			return makePieChart (title, portions);
		}
		else throw new RuntimeException ("No implementation for chart kind specified: " + kind);
	}
	public static JComponent makeChartComponent
		(
			String kind, String title, Portions portions
		)
	{
		return makeChart (kind, title, portions);
	}


}

