
package net.myorb.charting;

import net.myorb.gui.graphics.DisplayImaging;

import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.data.abstractions.Function;

import java.util.List;
import java.awt.Color;

/**
 * lists of data that describe plots
 * @author Michael Druckman
 */
public class DisplayGraphTypes extends DisplayImaging
{


	/**
	 * lists of colors for plot legends
	 */
	public static class Colors
		extends CommonDataStructures.ItemList <Color>
	{ private static final long serialVersionUID = -3409317615227644258L; }


	/**
	 * map function values to colors for contour plots
	 */
	public static class ColorMap
		extends CommonDataStructures.OrderedMap <Integer,Color>
	{ private static final long serialVersionUID = 1673222062687056423L; }


	/**
	 * point identified with x,y coordinates
	 */
	public static class Point
	{
		public double x, y;
		public boolean outOfRange = false;
		public static class Series extends CommonDataStructures.ItemList <Point>
		{

			/**
			 * translate a point series to coordinate lists
			 * @param xData a list collecting the x coordinates from the series
			 * @param yData a list collecting the y coordinates from the series
			 */
			public void toCoordinateLists
			(List <Number> xData, List <Number> yData)
			{
				for (Point p : this)
				{
					Double py = p.y;
					if ( p.outOfRange || py.isNaN() ) continue;
					xData.add (p.x); yData.add (p.y);
				}
			}

			private static final long serialVersionUID = 1766515409673116432L;
		}
		public String toString () { return "(" + x + "," + y + ")"; }
		public Point (double x, double y) { this.x = x; this.y = y; }
		public Point () {}
	}


	/**
	 * describe a field at a point
	 *  using magnitude and angle for given point
	 */
	public static class VectorField extends Point
	{
		/**
		 * describe chosen plot points
		 *  where directional indicators
		 *  are to be drawn
		 */
		@SuppressWarnings("serial")
		public static class Locations
			extends CommonDataStructures.ItemList <VectorField>
		{}

		/**
		 * connect domain with range in function plot
		 * @param P the Point X and Y from the function domain
		 * @param magnitude the magnitude of the function result vector
		 * @param angle the angle of the function result vector
		 */
		public VectorField
			(Point P, double magnitude, double angle)
		{
			super (P.x, P.y);
			this.outOfRange = P.outOfRange;
			this.magnitude = magnitude;
			this.angle = angle;
		}

		/* (non-Javadoc)
		 * @see net.myorb.charting.DisplayGraphTypes.Point#toString()
		 */
		public String toString ()
		{
			return super.toString () + " : " + vectorDescription ();
		}

		public String vectorDescription ()
		{
			return magnitude + " @ " + angle;
		}

		public double getMagnitude () { return magnitude; }
		public void setMagnitude (double magnitude) { this.magnitude = magnitude; }
		public void setAngle(double angle) { this.angle = angle; }
		public double getAngle () { return angle; }
		protected double magnitude, angle;
	}


	/**
	 * use DataSequence2D to build Point.Series
	 */
	public static class PointCollection extends Point.Series
		implements DataSequence2D.PointCollector <Double>
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.DataSequence2D.PointCollector#addPoint(java.lang.Object, java.lang.Object)
		 */
		public void addPoint (Double x, Double y) { add (new Point (x, y)); }

		private static final long serialVersionUID = 824420407949674125L;
	}


	/**
	 * collections of plots making up a chart
	 */
	public static class PlotCollection
		extends CommonDataStructures.ItemList <Point.Series>
	{ private static final long serialVersionUID = 5330371539112482742L; }


	/**
	 * collect point to plot segments
	 */
	public static class NamedSegment extends PointCollection
	{
		String name; int segmentNumber;
		private static final long serialVersionUID = -458950621629048328L;

		/**
		 * @return simple name for single segment, otherwise name + segment number
		 */
		public String getName () { return segmentNumber==1? name: name + "#" + segmentNumber; }

		/**
		 * @return the number of points in the segment
		 */
		public int getItemCount () { return this.size (); }

		/**
		 * @param name the plot identification
		 * @param segmentNumber the segment identifier
		 */
		public NamedSegment (String name, int segmentNumber)
		{
			this.name = name; this.segmentNumber = segmentNumber;
		}
	}


	/**
	 * collections of segments
	 */
	public interface SegmentCollector
	{
		/**
		 * @param segment to be added to collection
		 */
		void add (NamedSegment segment);
	}


	/**
	 * describe series of number making up a plot
	 */
	public static class RealSeries extends DataSequence <Double>
	{
		public RealSeries () {}
		public RealSeries (List<Double> items) { this.addAll (items); }
		private static final long serialVersionUID = -8390580795236803241L;
	}


	/**
	 * function of real numbers used to draw charts
	 */
	public interface RealFunction extends Function <Double> {}


	/*
	 * 3D plot types
	 */


	/**
	 * a wrapper for the function that provides the transformation
	 */
	public interface Transform3D
	{
		/**
		 * a point of the x/y plane is evaluated 
		 * @param x the x coordinate of the point on the domain plane
		 * @param y the y coordinate of the point on the domain plane
		 * @return the resulting value
		 */
		int evaluate (double x, double y);
		void setPlotNumber (int plotNumber);
		void setMultiplier (double multiplier);
		double evaluateReal (double x, double y);
		double evaluateAngle (double x, double y);
		Object evaluateGeneric (double x, double y);
		String identifyTransform ();
		double getMultiplier ();
		int getPlotNumber ();
		int getPlotParent ();
	}


	/**
	 * mapping from section of histogram to color
	 */
	public interface ContourColorScheme
	{
		/**
		 * @return the color selection object implementing the color scheme
		 */
		ColorSelection getColorSelector ();
	}


	/**
	 * specification of location and size of display area
	 */
	public interface ViewSpace
	{
		/**
		 * coordinates of the low corner of the full view
		 * @return point description of low corner
		 */
		Point getLowCorner ();

		/**
		 * the size of the full view on ALT axis
		 * @return number of units square of the view area
		 */
		float getAltEdgeSize ();

		/**
		 * the size of the full view
		 * @return number of units square of the view area
		 */
		float getEdgeSize ();
	}


	/**
	 * describe results of 3D plot computations
	 */
	public interface SurfaceDescription3D
	{
		/**
		 * @return x-axis coordinate
		 */
		double[][] getX ();

		/**
		 * @return Y-axis coordinate
		 */
		double[][] getY ();

		/**
		 * @return result vector magnitude
		 */
		double[][] getZ ();

		/**
		 * @param n component number of result
		 * @return the value for that result component
		 */
		double[][] getZ (int n);
	}


	/**
	 * perform calculations to support plot
	 */
	public interface PlotComputer
	{
		/**
		 * @param descriptor the parameters to the plot
		 * @param pointsPerAxis the number of points per axis
		 * @param points the (x,y) coordinates of the input points
		 * @param range the range of computed values for each of the points
		 * @param histogram a Histogram object to accumulate display statistics
		 */
		void computeRange
		(
			ContourPlotDescriptor descriptor, int pointsPerAxis,
			Point[] points, Object[] range,
			Histogram histogram
		);
	}


	/**
	 * identify algorithm for computation of transform
	 */
	public interface TransformRealization
	{
		/**
		 * @param computer source of algorithm
		 */
		void setPlotComputer (PlotComputer computer);

		/**
		 * @return access to identified algorithm
		 */
		PlotComputer getPlotComputer ();
	}


	/**
	 * description of the transform
	 */
	public interface ContourPlotDescriptor
	extends ViewSpace, Transform3D, ContourColorScheme, TransformRealization
	{ void buildLegendWidgetsFor (Histogram histogram); }


	/**
	 * @param tokens the source token list
	 * @param starting the starting index into token list
	 * @param point an output object for values of x,y
	 * @return the token index after the point
	 */
	public int parsePoint
		(
			List<CommonCommandParser.TokenDescriptor> tokens,
			int starting, DisplayGraphTypes.Point point
		)
	{
		return new PointParser (tokens, starting).parse (point);
	}


	// Legend processing


	/**
	 * describe the range established in a plot
	 */
	public interface Legend
	{
		/**
		 * in the range of values give the relative ratio for the point
		 * @return value in 0..1 relative to lowest..highest of plotted values
		 */
		double getRangeRelative ();

		/**
		 * get the associated value
		 * @return the value for the point
		 */
		double getValue ();
	}
	public static class LegendEntries
		extends CommonDataStructures.ItemList <Legend>
	{ private static final long serialVersionUID = -6044887574380955577L; }


	/**
	 * build meta-data table for use building legend display
	 * @param entryCount the number of table entries for the legend
	 * @param histogram the histogram used to capture plot meta-data
	 * @return the list of legend entries
	 */
	public static LegendEntries legendEntriesFor (int entryCount, Histogram histogram)
	{
		long
			lowest = histogram.getLowest (), highest = histogram.getHighest (),
			fullRange = highest - lowest, perEntry = fullRange / entryCount;
		LegendEntries entries = new LegendEntries ();

		for (long current = lowest; current < highest; current += perEntry)
		{ entries.add (mappingFor (current, histogram.relativeToRange (current))); }
		return entries;
	}
	public static Legend mappingFor (final long value, final float relative)
	{
		return new Legend ()
			{
				public double getRangeRelative () { return relative; }
				public double getValue () { return value; }
			};
	}


	/**
	 * color selection implementation description
	 */
	public interface ScaledColorSelector
	{
		/**
		 * select color based on point in range
		 * @param selectionRangePoint value in 0..1 relative to lowest..highest of plotted values
		 * @return the color mapped from point
		 */
		Color mappedFrom (double selectionRangePoint);
	}


	/**
	 * a list of screen components holding Legend meta-data
	 */
	public static class LegendWidgets
		extends CommonDataStructures.ItemList < SimpleScreenIO.Widget >
	{ private static final long serialVersionUID = -1098927143727951391L; }


	/**
	 * build a series of display widget for a legend
	 * @param entries the meta-data that described the legend elements
	 * @param multiplier the multiplier factored into the plot construction
	 * @param selector a selection object synchronized with the selection criteria
	 * @return the list of widgets that will make up the legend
	 */
	public static LegendWidgets legendWidgetsFor
		(LegendEntries entries, double multiplier, ScaledColorSelector selector)
	{
		LegendWidgets widgets = new LegendWidgets ();

		for (Legend L : entries)
		{
			widgets.add
			(
				widgetFor
				(
					L.getValue () / multiplier,
					L.getRangeRelative (), selector
				)
			);
		}

		return widgets;
	}


	/**
	 * allocate a display widget for a legend entry
	 * @param value the value of the function being described
	 * @param colorSelection the color assigned to the area of the value
	 * @param selector a selection object synchronized with the selection criteria
	 * @return a screen widget for the entry
	 */
	public static SimpleScreenIO.Widget widgetFor
		(double value, double colorSelection, ScaledColorSelector selector)
	{
		SimpleScreenIO.Label entry =
			new SimpleScreenIO.Label (selector.mappedFrom (colorSelection));
		entry.setText (" [ " + Double.toString (value) + " ] ");
		return entry;
	}


}


/**
 * parse syntax of (x,y)
 */
class PointParser
{

	PointParser (List<CommonCommandParser.TokenDescriptor> tokens, int starting)
	{
		this.tokens = tokens; this.pos = starting + 1;						// assume tokens(starting) = "("
	}
	List<CommonCommandParser.TokenDescriptor> tokens;
	int pos;
	
	/**
	 * @param p parsed point value x/y set
	 * @return the token index after the point
	 */
	int parse (DisplayGraphTypes.Point p)
	{
		p.x = Double.parseDouble (readUpTo (","));							// x found between "(" and ","
		p.y = Double.parseDouble (readUpTo (")"));							// y found between "," and ")"
		return pos;
	}

	/**
	 * @param delimiter token sought
	 * @return tokens up to delimiter
	 */
	String readUpTo (String delimiter)
	{
		String t;
		StringBuffer buffer = new StringBuffer ();

		while
			(
				pos < tokens.size() &&										// end of token list
				!(t = tokens.get (pos).getTokenImage ())					// token image retained
					.startsWith (delimiter)									// token is delimiter
			)
		{
			buffer.append (t); pos++;
		}

		pos++;																// move past delimiter
		//System.out.println(buffer.toString ());
		return buffer.toString ();
	}

}

