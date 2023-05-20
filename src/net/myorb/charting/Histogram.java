
package net.myorb.charting;

import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.data.abstractions.SimpleUtilities;

import java.util.HashMap;
import java.util.List;

/**
 * keep count of times number is noted
 * @author Michael Druckman
 */
public class Histogram
{

	/**
	 * @return highest entry in series
	 */
	public long size () { return highest + 1; }
	
	/**
	 * @return lowest value seen
	 */
	public long getLowest () { return lowest; }
	public long getHighest () { return highest; }
	private long highest = 0, lowest = Long.MAX_VALUE - 1;

	/**
	 * initialize range of histogram
	 * @param highest high value of range
	 * @param lowest low value of range
	 */
	public void setRange (long highest, long lowest)
	{ this.highest = highest; this.lowest = lowest; }

	/**
	 * @return highest increase in series
	 */
	public long max () { return largestCount; }
	private long largestCount = 0;

	/**
	 * @return count of all function points
	 */
	public int countOfAll () { return countAllPoints; }
	private int countAllPoints = 0;

	/**
	 * @return sum of all function values
	 */
	public float sumOfAll () { return sumAllPoints; }
	private float sumAllPoints = 0;

	/**
	 * @param forValue the value from the map
	 * @return the count of increases
	 */
	public long get (long forValue)
	{
		long mapTo = 0;
//		if (forValue < 0)
//			forValue = - forValue;
		if (mapping.containsKey (forValue))
		{ mapTo = mapping.get (forValue); }
		return mapTo;
	}

	/**
	 * increase count for specified value
	 * @param forValue value to be increased
	 */
	public void increase (long forValue)
	{
		countAllPoints++;
		long mapTo = get (forValue);
//		if (forValue < 0) forValue = - forValue;
		if (forValue > highest) highest = forValue + 1;
		if (forValue < lowest) lowest = forValue;
		mapping.put (forValue, ++mapTo);
		if (mapTo  >  largestCount)
		{ largestCount = mapTo; }
		sumAllPoints += forValue;
	}

	/**
	 * @param value the value to be rated
	 * @return the ratio of the value magnitude to full range
	 */
	public float relativeToRange (long value)
	{
		float
			aboveLowest = value - lowest,
			fullRange = highest - lowest;
		return (float) aboveLowest / fullRange;
	}

	/**
	 * @param limitedTo maximum points allowed
	 * @return a point series for bar chart display
	 */
	public Point.Series forDisplay (int limitedTo)
	{
		int n = 0;
		boolean limited = limitedTo > 0;
		Point.Series series = new Point.Series ();
		for (Long i : SimpleUtilities.orderedKeys (mapping))
		{
			series.add (new Point (i, mapping.get (i)));
			if (limited && ++n > limitedTo) break;
		}
		if (dumping) dump (series);
		return series;
	}
	private void dump (Point.Series series)
	{
		System.out.print ("Unique keys: " + mapping.keySet ().size ());
		System.out.print ("  Size: " + highest); System.out.print ("  Max: " + largestCount);
		System.out.print ("  Count: " + countAllPoints); System.out.println ("  Sum: " + sumAllPoints);
		for (Point p : series) System.out.println (p);
	}
	private boolean dumping = false;

	/**
	 * @return the ordered set of mapped values
	 */
	public List<Long> mappedEntries () { return SimpleUtilities.orderedKeys (mapping); }

	private HashMap<Long,Long> mapping = new HashMap<Long,Long>();

}

