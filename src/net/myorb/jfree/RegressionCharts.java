
package net.myorb.jfree;

import net.myorb.charting.PlotLegend;
import net.myorb.charting.DisplayGraphTypes;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * regression chart specific methods using JFreeChart
 * @author Michael Druckman
 */
public class RegressionCharts extends ChartLibSupport
{


	/**
	 * construct regression chart
	 * @param chartTitle the chart title
	 * @param dataCollection the sparse data points
	 * @param interpolationCollection the interpolation plot
	 * @param domainTitle a label for the domain
	 * @param rangeTitle a label for the range
	 * @return a component for display
	 */
	public static ChartPanel makeRegressionChart
		(
			String chartTitle,
			XYSeriesCollection dataCollection,
			XYSeriesCollection interpolationCollection,
			String domainTitle, String rangeTitle
		)
	{
		XYPlot plot = new XYPlot ();

		DisplayGraphTypes.Colors colors = PlotLegend.getColorList ();
		mapDatasetToAxis (0, plot, interpolationCollection, true, false, colors);
		mapDatasetToAxis (1, plot, dataCollection, false, true, colors);
		labelAxis (plot, domainTitle, rangeTitle);

		JFreeChart chart = new JFreeChart
			(
				chartTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, true
			);
		return new ChartPanel (chart);
	}


}

