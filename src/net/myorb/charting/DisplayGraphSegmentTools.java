
package net.myorb.charting;

import net.myorb.charting.DisplayGraphSegmenting;

import net.myorb.charting.DisplayGraphTypes.NamedSegment;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * tools for using degenerate case of segment collector
 * @author Michael Druckman
 */
public class DisplayGraphSegmentTools
{


	public static Point.Series checkRange (Point.Series points)
	{ return segmenting == null? points: segmenting.checkRange (points); }
	public static DisplayGraphSegmenting getSegmentControl () { return segmenting; }
	public static void resetSegmentControl () { segmenting = new DisplayGraphSegmenting (); }
	private static DisplayGraphSegmenting segmenting = new DisplayGraphSegmenting ();


	/**
	 * a list of segments
	 */
	@SuppressWarnings ("serial") public static class SegmentList extends ArrayList <NamedSegment> {}


	/*
	 * segment naming conventions
	 */


	/**
	 * construct names for the segments with a primary name
	 * @param segmentCount the number of segments to be plotted
	 * @param primaryName the primary name for the group
	 * @return the list of names to use
	 */
	public static String[] segmentNames (int segmentCount, String primaryName)
	{
		String[] names =
				DisplayGraphSegmentTools.initialSegmentNames
					(segmentCount);
		names[0] = primaryName;
		return names;
	}


	/**
	 * construct names for the segments
	 * @param segmentCount the number of segments to be plotted
	 * @return the list of names to use
	 */
	public static String[] initialSegmentNames (int segmentCount)
	{
		String[] segmentsNameList = new String [segmentCount];

		for (int i = 0; i < segmentCount; i++)
		{
			addNameFor (i, segmentsNameList);
		}

		return segmentsNameList;
	}


	/**
	 * assign a name to the segment identified by index
	 * @param item the index into the plot collection
	 * @param segmentsNameList the list being compiled
	 */
	public static void addNameFor (int item, String[] segmentsNameList)
	{ segmentsNameList [item] = "#" + Integer.toString (item + 1); }


	/*
	 * segment recognition conventions
	 */


	/**
	 * remove out-of-range points breaking plots into segments
	 * @param sourcePlots the plots with segment denoted by out-of-range marks
	 * @param separated each segment becomes a separate plot
	 */
	public static void separateSegments (PlotCollection sourcePlots, PlotCollection separated)
	{
		Point.Series series = null;
		for (Point.Series source : sourcePlots)
		{
			for (Point p : source)
			{
				if ( ! p.outOfRange )
				{
					if (series == null)
					{ separated.add (series = new Point.Series ()); }
					series.add (p);
				}
				else { series = null; }
			}
		}
	}


	/**
	 * collector for named segments as plots are analyzed
	 */
	public static class SegmentCollector
		implements DisplayGraphTypes.SegmentCollector
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.charting.DisplayGraphAtomic.SegmentCollector#add(net.myorb.math.expressions.charting.DisplayGraphAtomic.NamedSegment)
		 */
		public void add (NamedSegment segment) { segments.add (segment); }
		public SegmentCollector (List <NamedSegment> segments) { this.segments = segments; }
		List <NamedSegment> segments;
	}


	/**
	 * translate plots into segments
	 * @param names the names of the plots
	 * @param points the collection of plot points
	 * @return a list of collected segments
	 */
	public static SegmentList getSegments (String[] names, PlotCollection points)
	{
		SegmentList segments = new SegmentList ();
		SegmentCollector collection = new SegmentCollector (segments);
		DisplayGraphSegmenting segmenting = getSegmentControl ();

		for (int i = 0; i < names.length; i++)
		{
			segmenting.constructSegmentCollection (points.get (i), names[i], collection);
		}

		return segments;
	}


	/**
	 * compile list of segment names
	 * @param segments the list of segment descriptors
	 * @return an array of the segment names
	 */
	public static String[] namesOf (SegmentList segments)
	{
		String[] names = new String [segments.size ()];
		for (int i = 0; i < names.length; i++) names [i] = segments.get (i).getName ();
		return names;
	}


	/**
	 * apply OUT-OF-BOUNDS logic to points
	 * @param p the data point to check
	 * @return the value of the point
	 */
	public static Double valueFor (Point p)
	{
		return p.outOfRange ? null : p.y;
	}


}

