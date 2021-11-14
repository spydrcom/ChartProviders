
package net.myorb.simplecharts;

import net.myorb.bfo.BfoBackgrounds;
import net.myorb.bfo.BfoPrimitives;

import org.faceless.graph2.Axis;
import org.faceless.graph2.Graph;
import org.faceless.graph2.Align;
import org.faceless.graph2.TextStyle;
import org.faceless.graph2.AxesGraph;
import org.faceless.graph2.ScatterSeries;
import org.faceless.graph2.LineSeries;
import org.faceless.graph2.BarSeries;
import org.faceless.graph2.Series;
import org.faceless.graph2.Style;
import org.faceless.graph2.Key;

import net.myorb.gui.components.SimpleScreenIO.WidgetFrame;
import net.myorb.gui.components.SimpleScreenIO.Image;

import java.awt.Color;

import java.util.List;

/**
 * provide for encapsulation of simple bar chart generation
 * @author Michael Druckman
 */
public class SimpleBarChart
{


	public SimpleBarChart () {}

	public SimpleBarChart (int[] units)
	{ this.units = units; }
	int units[] = null;


	/**
	 * @param title text of frame title
	 * @param x the x-axis tags to be displayed
	 * @param values the values per tag for the bar size
	 */
	public void showChart
	(String title, String[] x, List<Integer> values)
	{ showFrame (title, graphFor (barsFor (x, values), null, values)); }

	public void showChartWithScatterOverlay
	(String title, String[] x, List<Integer> values, List<Integer> overlay)
	{ showFrame (title, graphFor (barsFor (x, values), scatterFor (overlay), values)); }
	public void showChartWithLineOverlay (String title, String[] x, List<Integer> values, List<Integer> overlay)
	{ showFrame (title, graphFor (barsFor (x, values), lineFor (overlay), values)); }
	

	/**
	 * @param series the bar chart series
	 * @param overlay the overlay pattern to show, NULL if none
	 * @param values the list of values for the bars
	 * @return the graph for display
	 */
	public AxesGraph graphFor (Series series, Series overlay, List<Integer> values)
	{
        AxesGraph graph = new AxesGraph ();
        BfoBackgrounds.setAxisBG (graph); graph.addSeries (series);
        if (overlay != null) addKeyForOverlay (graph, series, overlay);
        applyUnitRange (graph, values);
        return graph;
	}
	public Series barsFor (String[] x, List<Integer> values)
	{
        BarSeries data = new BarSeries ("Observed");
        data.setRoundBars (true); data.setBarWidth (0.8);
        for (int i = 0; i < x.length; i++) data.set (x[i], values.get (i));
        return data;
	}


	/**
	 * @param graph the graph for display
	 * @param bars the series for the bar chart
	 * @param overlay the series for the overlay
	 */
	public void addKeyForOverlay (AxesGraph graph, Series bars, Series overlay)
	{
    	graph.addSeries (overlay);
    	Key key = new Key (new Style (Color.WHITE));
    	key.addSeries (overlay, new TextStyle ("Arial", 12, Color.BLUE));
    	key.addSeries (bars, new TextStyle ("Arial", 12, Color.BLUE));
    	graph.addKey (key, Align.BOTTOM);
	}


	/**
	 * build overlay series
	 * @param values the values for the overlay
	 * @return the overlay series
	 */
	public LineSeries lineFor (List<Integer> values)
	{
		LineSeries lineSeries = new LineSeries ("Expected");
		for (int i = 0; i < values.size (); i++) lineSeries.set(i, values.get (i));
		return lineSeries;
	}
	public ScatterSeries scatterFor (List<Integer> values)
	{
		ScatterSeries scatterSeries = new ScatterSeries ("Expected", "diamond", 12);
		for (int i = 0; i < values.size (); i++) scatterSeries.set(i, values.get (i));
		return scatterSeries;
	}


	/**
	 * set current scale based on values displayed
	 * @param graph the graph being scaled to the data
	 * @param values the values of the series
	 */
	public void applyUnitRange (AxesGraph graph, List<Integer> values)
	{
		if (units != null) setRange (graph.getAxis (Axis.LEFT), findExtreme (abs (values)));
	}
	public void setRange (Axis axis, int extreme)
	{
        axis.setMinValue (-extreme);
        axis.setMaxValue (extreme);
	}
	public int findExtreme (int[] values)
	{
		int unit = 0;
		int last = units.length - 1;
		for (int current : values)
		{
			while (current > units[unit])
			{ if (++unit == last) return units[last]; }
		}
		return units[unit];
	}
	public int[] abs (List<Integer> values)
	{
		int[] absValues =
				new int[values.size ()];
		for (int i = 0; i < values.size (); i++)
		{ absValues[i] = Math.abs (values.get(i)); }
		return absValues;
	}


	/**
	 * @param title the title for the frame
	 * @param graph the graph to show in the frame
	 */
	public void showFrame (String title, Graph graph)
	{
		if (graph == null) return; else if (image == null)
		{ new WidgetFrame (image = BfoPrimitives.getImageFor (graph), title).showOrHide (); }
		else { image.changeTo (Image.iconFor (BfoPrimitives.getPngFileFor (graph))); }
	}
	Image image = null;


	/**
	 * @param content text to be displayed on mouse-over
	 */
	public void setToolTipText (String content)
	{
		image.setToolTipText (content);
	}


	/**
	 * @return a chart that adjusts scale dynamically
	 */
	public static SimpleBarChart newDynamicallyScaledChart ()
	{
		return new SimpleBarChart (VALUES);
	}
	static final int[] VALUES = new int[]{5, 10, 20, 40, 80};


}

