
package net.myorb.gral;

import net.myorb.charting.DisplayGraphLibraryInterface.Portion;
import net.myorb.charting.DisplayGraphLibraryInterface.Portions;

import de.erichseifert.gral.data.DataTable;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Orientation;

import de.erichseifert.gral.plots.legends.ValueLegend;

import de.erichseifert.gral.plots.PiePlot;
import de.erichseifert.gral.plots.Plot;

import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;

import java.util.ArrayList;

/**
 * bar and pie chart specific methods using GRAL
 * @author Michael Druckman
 */
public class BarAndPieSupport extends GralPrimitives
{


	/**
	 * bar chart formatter
	 * @param title the title
	 * @param portions the portions
	 * @return the chart
	 */
	public static Drawable makeBarChart
		(
			String title,
			Portions portions
		)
	{
		PortionedTable
			data = new PortionedTable
				(getPortionsFrom (portions));
		return data.getPlot ();
	}


	/**
	 * pie chart formatter
	 * @param title the title
	 * @param portions the portions
	 * @return the chart
	 */
	public static Drawable makePieChart
		(
			String title,
			Portions portions
		)
	{
		Plot plot;
		Portion[] portion = getPortionsFrom (portions);
		DataTable data = new DataTable (Double.class, Integer.class);
		LabelFormatter formatter = new LabelFormatter ();

		for (int n = 0; n < portions.size (); n++)
		{
			data.add (portion[n].getPortion (), n);
			formatter.add (portion[n].getName ());
		}
	
        plot = startPlot (new PiePlot (data));
		prepareLegend ((ValueLegend) plot.getLegend (), 1, formatter, Orientation.VERTICAL);
		plot.setLegendVisible (true);
	
		return plot;
	}


	/**
	 * formatter for labels of pie chart legends
	 */
	@SuppressWarnings ("serial")
	public static class LabelFormatter extends Format
	{

		/* (non-Javadoc)
		 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
		 */
		@Override
		public StringBuffer format (Object key, StringBuffer buf, FieldPosition pos)
		{
			int index = ((Number) key).intValue ();
			StringBuffer buffer = new StringBuffer ().append (labels.get (index));
			return buffer;
		}
		public void add (String label) { labels.add (label); }
		ArrayList <String> labels = new ArrayList <> ();

		/* (non-Javadoc)
		 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
		 */
		@Override
		public Object parseObject (String text, ParsePosition pos) { return null; }

	}


}

