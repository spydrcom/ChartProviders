
package net.myorb.jfree;

import net.myorb.gui.components.DisplayContainer;
import net.myorb.gui.components.GuiToolkit;

import org.jfree.chart.*;

import javax.swing.WindowConstants;
import javax.swing.JScrollPane;

/**
 * A frame for displaying a chart.
 * { moved from org.jfree.chart }
 */
public class ChartFrame
{


	/**
     * Constructs a frame for a chart.
     *
     * @param title  the frame title.
     * @param chart  the chart.
     */
    public ChartFrame (String title, JFreeChart chart)
    {
        this (title, chart, false);
    }


    /**
     * Constructs a frame for a chart.
     *
     * @param title  the frame title.
     * @param chart  the chart.
     * @param scrollPane  if <code>true</code>, put the Chart(Panel) into a
     *                    JScrollPane.
     */
    public ChartFrame (String title, JFreeChart chart, boolean scrollPane)
    {
        //super (title);
    	display = GuiToolkit.newDisplayContainer (title);

        display.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.chartPanel = new ChartPanel (chart);

        if (scrollPane)
        {
        	display.setContentPane (new JScrollPane (this.chartPanel));
        }
        else
        {
        	display.setContentPane (this.chartPanel);
        }
    }
    DisplayContainer display;


    /**
     * Returns the chart panel for the frame.
     *
     * @return The chart panel.
     */
    public ChartPanel getChartPanel ()
    {
        return this.chartPanel;
    }
    private ChartPanel chartPanel;	/** The chart panel. */


//    private static final long serialVersionUID = -3996832757395355935L;

}
