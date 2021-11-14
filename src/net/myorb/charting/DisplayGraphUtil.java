
package net.myorb.charting;

// charting library
import net.myorb.charting.DisplayGraphSegmentTools;
import net.myorb.charting.DisplayGraphTypes.PointCollection;
import net.myorb.charting.DisplayGraphTypes.RealSeries;
import net.myorb.charting.DisplayGraphTypes.Point;

// IOLIB abstractions
import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.Function;

/**
 * static utility helper methods for building charts and graphs
 * @author Michael Druckman
 */
public class DisplayGraphUtil
{


	/*
	 * series of points
	 */


	/**
	 * @param f function to describe
	 * @param lo the lo of the domain
	 * @param hi the hi of the domain
	 * @param inc the increment of the domain
	 * @return the computed series
	 */
	public static Point.Series getPlotPoints
	(Function<Double> f, double lo, double hi, double inc)
	{ return pointsFor (domain (lo, hi, inc), f); }

	/**
	 * compute domain values
	 * @param lo the lo of the domain
	 * @param hi the hi of the domain
	 * @param inc the increment of the domain
	 * @return the series of domain values
	 */
	public static RealSeries domain
		(double lo, double hi, double inc)
	{
		double threshold = hi + inc / 2;
		RealSeries list = new RealSeries ();
		for (double x = lo; x <= threshold; x += inc)
		{ list.add (x); }
		return list;
	}

	/**
	 * compute function value sequence across domain
	 * @param domain the series values to use as domain
	 * @param f the function to use for calculation of range
	 * @return computed series of points
	 */
	public static Point.Series pointsFor (RealSeries domain, Function<Double> f)
	{ return forRealSequence (DataSequence2D.collectDataFor (f, domain)); }

	/**
	 * combine domain and range series to point series
	 * @param domain the domain series
	 * @param range the range series
	 * @return the point series
	 */
	public static Point.Series pointsFor (RealSeries domain, RealSeries range)
	{ return forRealSequence (new DataSequence2D<Double> (domain, range).corrected ()); }

	/**
	 * convert point sequence to series
	 * @param points the sequence
	 * @return the series
	 */
	public static Point.Series
		forRealSequence (DataSequence2D<Double> points)
	{ return DisplayGraphSegmentTools.checkRange ((Point.Series) points.addTo (new PointCollection ())); }


	/*
	 * profile title
	 */


	/**
	 * format title with domain specified
	 * @param profile the profile of the function
	 * @param parameter the parameter name (with possible notation translation)
	 * @param plot the plot series holding domain specification 
	 * @return the formatted title
	 */
	public static String titleFor (String profile, String parameter, Point.Series plot)
	{
		return profile + ", " + pointsDomainSpan (plot).toStandardNotation (parameter, 3);
	}

	/**
	 * determine span of values of points list
	 * @param points the list of points holding x-axis
	 * @return a range descriptor
	 */
	public static PrimitiveRangeDescription pointsDomainSpan (Point.Series points)
	{
		double lo = points.get (0).x, hi = points.get (points.size()-1).x;
		return new PrimitiveRangeDescription (lo, hi, 0);
	}

	/**
	 * determine span of values of points list
	 * @param points the list of points holding y-axis
	 * @return a range descriptor
	 */
	public static PrimitiveRangeDescription pointsRangeSpan (Point.Series points)
	{
		double lo = points.get (0).y, hi = points.get (points.size()-1).y;
		return new PrimitiveRangeDescription (lo, hi, 0);
	}


	/*
	 * value check
	 */


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

