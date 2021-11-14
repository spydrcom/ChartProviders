
package net.myorb.gral;

import net.myorb.charting.DisplayGraphLibraryInterface.Portion;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.Plot;

import java.util.Arrays;

/**
 * translate CalcLib Portions to GRAL DataTable
 * @author Michael Druckman
 */
@SuppressWarnings ("serial")
public class PortionedTable  extends DataTable
{


	/**
	 * @param bars the number of bars to plot
	 */
	protected PortionedTable (int bars)
	{
		super
		(
			// table entry for each bar plus x-axis label column (0)
			GralPrimitives.doubleDataType (bars + 1)
		);
		this.barCount = bars;
	}
	// bar count and column count differ by 1
	int barCount;


	/**
	 * @param portions the Portions object to be represented
	 */
	public PortionedTable (Portion[] portions)
	{
		this (portions.length);
        this.portions = portions;
        populateTable ();
	}
	Portion[] portions;


	/**
	 * DataTable becomes populated from Portions object
	 */
	public void populateTable ()
	{
		Double bar [] = new Double [getColumnCount ()], yMax = 0.0, nxt;
        for (int i = 1; i <= barCount; i++)
        {
        	Arrays.fill (bar, 0.0); bar [0] = (double) i;
        	nxt = bar [i] = portions [i - 1].getPortion ();
        	if (nxt > yMax) yMax = nxt;
        	add (bar);
        }
        this.yMax = Y_SCALE * yMax;
	}
	static final double Y_SCALE = 1.1; // 10% head-room on largest bar


	/**
	 * @return GRAL DataSeries built from Portions
	 */
	public DataSeries[] getDataSeries ()
	{
        DataSeries [] dataSeries =
        	new DataSeries [barCount];
        for (int i = 0; i < barCount; i++)
        { dataSeries[i] = getDataSeriesFor (i); }
        return dataSeries;
	}


	/**
	 * @param barIndex the index of the bar described
	 * @return the data series for the bar at given index
	 */
	private DataSeries getDataSeriesFor (int barIndex)
	{
		return new DataSeries
		(
			portions [barIndex].getName (), this, 0, barIndex + 1
		);
	}


	/**
	 * @return a Bar Chart GRAL XYPlot for the Portions set
	 */
	public Plot getPlot ()
	{
		return GralPrimitives.configured
		(
			GralPrimitives.startPlot
			(
				getDataSeries (),
				getColumnCount (),
				yMax.intValue ()
			)
		);
	}
	Number yMax;


}

