
package net.myorb.charting;

/**
 * processing for range limiting and asymptotic segmenting
 * @author Michael Druckman
 */
public class DisplayGraphSegmenting extends DisplayGraphTypes
{


	/**
	 * check for range limiting option.
	 *  mark points out-of-range where appropriate
	 * @param points the points of the current series
	 * @return series with optionally flagged points
	 */
	public Point.Series checkRange (Point.Series points)
	{
		if (limitingRange)
		{
			for (Point p : points)
			{
				if (Math.abs (p.y) > rangeLimit) p.outOfRange = true;
			}
		}
		return points;
	}


	/**
	 * @param rangeLimitToUse the limit specified
	 */
	public void setRangeLimit (double rangeLimitToUse)
	{ rangeLimit = rangeLimitToUse; limitingRange = true; }
	private double rangeLimit = 0;


	/**
	 * stop applying range limit
	 */
	public void setRangeUnlimited () { limitingRange = false; }
	private boolean limitingRange = false;


	/**
	 * convert series to segments
	 * @param points the series being converted
	 * @param named a name for the segment (used in legend)
	 * @param segmentCollector a collector for accumulation of segments
	 */
	public void constructSegmentCollection
	(Point.Series points, String named, SegmentCollector segmentCollector)
	{
		int segment = 1;
		boolean inSeries = false;
		NamedSegment series = null;

		for (Point p : points)
		{
			if (!inSeries && !p.outOfRange)
			{
				series = new NamedSegment (named, segment);
				inSeries = true;
			}
			if (inSeries)
			{
				if (!p.outOfRange)
				{
					series.addPoint (p.x, p.y);
				}
				else if (segmenting)
				{
					segment +=
						addToCollection (series, segmentCollector);
					inSeries = false; series = null;
				}
			}
		}

		addToCollection (series, segmentCollector);
	}


	/**
	 * @param enabled TRUE = apply segmenting
	 */
	public void setSegmenting
		(boolean enabled) { segmenting = enabled; }
	private boolean segmenting = false;


	/**
	 * verify series significance
	 *  and add to collection if found to be
	 * @param series the series in question
	 * @param to the collection of plots
	 * @return segment increase
	 */
	public int addToCollection (NamedSegment series, SegmentCollector to)
	{
		if (series == null) return 0;
		if (series.getItemCount () < seriesMinimum) return 0;
		to.add (series);
		return 1;
	}


	/**
	 * identify minimum number of points allowed to accept segment
	 * @param minumumCount the required number of points
	 */
	public void setSeriesMinimum
	(int minumumCount) { seriesMinimum = minumumCount; }
	public int getSeriesMinimum () { return seriesMinimum; }
	private int seriesMinimum = 3;


}

