
package net.myorb.bfo;

// charting
import net.myorb.charting.DisplayGraphLibraryInterface.CategorizedPortions;
import net.myorb.charting.DisplayGraphLibraryInterface.CategorizedPortion;

import net.myorb.charting.DisplayGraphLibraryInterface.Portions;
import net.myorb.charting.DisplayGraphLibraryInterface.Portion;

import net.myorb.charting.DisplayGraphProperties;
import net.myorb.charting.PlotLegend;

// NAV
import net.myorb.gui.graphics.NAV;

// BFO
import org.faceless.graph2.AxesGraph;
import org.faceless.graph2.MultiBarSeries;
import org.faceless.graph2.BarSeries;
import org.faceless.graph2.PieGraph;
import org.faceless.graph2.Graph;

/**
 * bar and pie chart specific methods using BFO
 * @author Michael Druckman
 */
public class BarAndPieSupport extends BarAndPieStyles
{


	/**
	 * the styles of charts
	 */
	public enum Styles
	{
		BAR,
		ZAXIS,
		DIM3D,
		BKT3D,
		PIE
	}


	/**
	 * make a chart based on the style chosen
	 * @param styleName the name of the style of chart to make
	 * @param portions the allocations of the chart to display
	 * @return Image of a BFO graph object
	 */
	public Image makeChart
		(
			String styleName,
			Portions portions
		)
	{
		Graph graph;
		switch (Styles.valueOf (styleName.toUpperCase ()))
		{
			case PIE:	graph = makePieChart (portions); break;
			case BAR:	graph = make2axisBarChart (portions); break;
			case ZAXIS: graph = makeZaxisBarChart (portions, Styles.ZAXIS); break;
			case DIM3D: graph = makeZaxisBarChart (portions, Styles.DIM3D); break;
			case BKT3D: graph = makeZaxisBarChart (portions, Styles.BKT3D); break;
			default: throw new RuntimeException ("Internal error");
		}
		return getImageFor (graph);
	}


	/**
	 * verify the data is appropriate for a z-axis chart
	 * @param portions the allocations of the chart to display
	 * @param style the style of chart to make
	 * @return a BFO graph object
	 */
	public AxesGraph makeZaxisBarChart
		(
			Portions portions, Styles style
		)
	{
		if (portions instanceof CategorizedPortions)
		{
			switch (style)
			{
				case ZAXIS: return makeZaxisBarChart ((CategorizedPortions) portions);
				case BKT3D: return makeBracketedZaxisBarChart ((CategorizedPortions) portions);
				case DIM3D: return makeZaxisLayeredBarChart ((CategorizedPortions) portions);
				default: throw new RuntimeException ("Internal error");
			}
		} else throw new RuntimeException ("Data must be dimensioned by category");
	}


	/**
	 * show bracketed rotation display
	 * @param portions the allocations of the chart to display
	 * @return NULL returned, frame is specially formed here
	 */
	public AxesGraph makeBracketedZaxisBarChart (CategorizedPortions portions)
	{
		AxesGraph graph =
			makeFlatLayeredBarChart (portions);
		Image[] images = new Image[9]; int i = 0;

		for (int x = 2; x >= 0; x--)
			for (int y = 0; y <= 2; y++)
			{
				graph = makeFlatLayeredBarChart (portions); setLightVector (graph);
				graph.setXRotation (ROTATIONS[x]); graph.setYRotation (ROTATIONS[y]);
				images[i++] = getImageFor (graph);
			}
		new NAV ().show ("Rotation Bracket", images);

		return null;
	}
	static int[] ROTATIONS = {30, 50, 70};


	/**
	 * z-axis layered into 3rd dimension
	 * @param portions the allocations of the chart to display
	 * @return a BFO graph object
	 */
	public AxesGraph makeZaxisLayeredBarChart
		(
			CategorizedPortions portions
		)
	{
		AxesGraph graph = makeFlatLayeredBarChart (portions);
		setRotation (graph);
	    return graph;
	}


	/**
	 * @param category name of the category
	 * @return the styled series object
	 */
	public BarSeries barSeriesFor (String category)
	{
		BarSeries series = new BarSeries (category);
		StyleWrapper barProperties = getStyle ("BAR_STYLE", KindOfStyle.BAR);
		if (barProperties.hasBarPoly ()) series.setRoundBars (barProperties.getBarPoly ().doubleValue ());
		series.setBarWidth (barProperties.getBarWidth ().doubleValue ());
		return series;
	}


	/**
	 * @param portions the allocations of the chart to display
	 * @return flat chart, no rotation
	 */
	public AxesGraph makeFlatLayeredBarChart
		(
			CategorizedPortions portions
		)
	{
		DisplayGraphProperties.setPalate ();
		String[] categories = portions.getCategories ();
		AxesGraph graph = new AxesGraph (); setTitle (graph);
		graph.setDefaultColors (PlotLegend.getPalate ());
	
		for (int i = 0; i < categories.length; i++)
		{
			BarSeries series = barSeriesFor (categories[i]);
	
			for (Portion portion : portions)
	        {
				CategorizedPortion p = (CategorizedPortion) portion;
		        series.set (p.getName (), p.getPortionsByCategory () [i]);
	        }
	
			graph.addSeries (series);
		}
	
		setAxis (graph, true, false);;
	    return graph;
	}


	/**
	 * portions are now validated as having z-axis data
	 * @param portions the allocations of the chart to display
	 * @return a BFO graph object
	 */
	public AxesGraph makeZaxisBarChart
		(
			CategorizedPortions portions
		)
	{
		String[] catagories = portions.getCategories ();
		MultiBarSeries group = new MultiBarSeries (portions.getCategoryTitle ());

		for (int i = 0; i < catagories.length; i++)
		{
			BarSeries series = barSeriesFor (catagories[i]);

			for (Portion portion : portions)
	        {
				CategorizedPortion p = (CategorizedPortion) portion;
		        series.set (p.getName (), p.getPortionsByCategory () [i]);
	        }

			group.add (series);
		}

        return makeZaxisBarChart (group, catagories);
	}


	/**
	 * construct the top layer of the z-axis chart
	 * @param group the BFO series for multi-bar description
	 * @param catagories the names of the categories in the data
	 * @return a BFO graph object for the display
	 */
	public AxesGraph makeZaxisBarChart
	(MultiBarSeries group, String[] catagories)
	{
		DisplayGraphProperties.setPalate ();
		AxesGraph graph = new AxesGraph ();
		graph.setDefaultColors (PlotLegend.getPalate ());
        graph.addSeries (group); setKey (graph, catagories);
        setAxis (graph, true, true);
        setTitle (graph);
        return graph;
	}


	/**
	 * @param portions the allocations of the chart to display
	 * @return a BFO graph object
	 */
	public AxesGraph make2axisBarChart
		(
			Portions portions
		)
	{
		AxesGraph graph = new AxesGraph ();
		graph.addSeries (makeBarSeries (portions));
		setAxis (graph, true, true);
		setTitle (graph);
	    return graph;
	}


	/**
	 * @param portions the allocations of the chart to display
	 * @return a BFO series object
	 */
	public BarSeries makeBarSeries (Portions portions)
	{
		BarSeries series = barSeriesFor ("");
		for (Portion p : portions) series.set (p.getName (), p.getPortion ());
		return series;
	}


	/**
	 * @param portions the allocations of the chart to display
	 * @return a BFO graph object
	 */
	public PieGraph makePieChart
		(
			Portions portions
		)
	{
		DisplayGraphProperties.setPalate ();
		PieGraph graph = new PieGraph ();
		graph.setDefaultColors (PlotLegend.getPalate ());
		for (Portion p : portions) graph.set (p.getName (), p.getPortion ());
        graph.setLightVector (1, 0, -1); BfoPrimitives.setRotation (graph);
        graph.setDepth (30); setKey (graph, portions);
        setTitle (graph);
		return graph;
	}


}

