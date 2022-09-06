
package net.myorb.charting;

import net.myorb.gui.palate.PalateTool;
import net.myorb.data.abstractions.SimplePropertiesManager;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.graphics.ColorNames;
import net.myorb.gui.graphics.ColorTools;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * produce a legend for a multi-plot display
 * @author Michael Druckman
 */
public class PlotLegend extends ColorTools
{


	/**
	 * the interface used by the display layer
	 */
	public interface SampleDisplay
	{
		/**
		 * the value of X
		 *  and each of the Y function values
		 * @param x the text to display for X
		 * @param samples a value for each function
		 */
		void display (String x, String[] samples);

		/**
		 * get expressions for plots
		 * @return an array of expressions
		 */
		String[] getPlotExpressions ();

		/**
		 * identify variable
		 * @param variable name of variable used in expressions
		 */
		void setVariable (String variable);

		/**
		 * get name of variable used in expressions
		 * @return name of variable used in expressions
		 */
		String getVariable ();

		/**
		 * show frame with legend
		 */
		void showLegend ();
	}


	/*
	 * Palate tool created and used to read current color list
	 */


	/**
	 * identify source of legend color list
	 * @param filePath the path to the source file
	 */
	public static void setLegendPalate (String filePath)
	{
		palate = new PalateTool (filePath);
	}

	/**
	 * @return the current list of colors in the palate
	 */
	public static Color[] getPalate ()
	{ return palate.getPalateColors (); }
	public static PalateTool getPalateTool () { return palate; }
	public static final String LEGEND_IDENTIFIER = "LEGEND";
	static PalateTool palate;

	public static ColorNames.ColorList getPalateColorNames ()
	{ return palate.getColorNames (); }


	/**
	 * @param values the RGBA values for the new palate
	 */
	public static void setPalate (Integer[] values)
	{ palate.setPalateColors (getColors (values)); applyAlpha (); }


	/**
	 * apply alpha property if present
	 */
	public static void applyAlpha ()
	{
		SimplePropertiesManager.PropertyValueList propertyValue =
			DisplayGraphProperties.getChartProperty ("ALPHA");
		if (propertyValue != null)
		{
			applyAlpha
			(
				palate.getPalateColors (),

				propertyValue.get (0)
					.getTokenValueAsCoded ()
					.intValue ()
			);
		}
	}


	/**
	 * @return list of current legend colors
	 */
	public static DisplayGraphTypes.Colors getColorList ()
	{
		Color[] colorList = palate.getPalateColors ();
		DisplayGraphTypes.Colors colors = new DisplayGraphTypes.Colors ();
		for (Color c : colorList) colors.add (c);
		return colors;
	}


	/*
	 * Legend Construction
	 */


	/**
	 * build legend display panel
	 * @param map a map object that will collect components
	 * @param size the size to set for display preference
	 * @return a panel with the display
	 */
	public static JPanel makeLegend (Map<String,Object> map, int size)
	{
		Color[] colorList =
			palate.getPalateColors ();
		int rows = colorList.length;

		JPanel p = new JPanel ();
		p.setLayout (new GridLayout (rows, 2));
		p.setBackground (Color.GRAY);
		
		for (int r = 0; r < rows; r++)
		{
			Color color = colorList[r];

			JLabel l = new JLabel ("", JLabel.RIGHT);
			l.setBackground (Color.GRAY);
			l.setForeground (color);
			map.put ("X" + r, l);
			p.add (l);

			l = new JLabel ("", JLabel.LEFT);
			l.setBackground (Color.GRAY);
			l.setForeground (color);
			map.put ("V" + r, l);
			p.add (l);
		}

		p.setPreferredSize (new Dimension (size/2, size));
		return p;
	}


	/**
	 * set an item in the expression column
	 * @param map the map of display components
	 * @param row the row number of the element
	 * @param expression the text of the expression
	 */
	public static void setExpression (Map<String,Object> map, int row, String expression)
	{
		((JLabel) map.get ("X" + row)).setText (expression);
	}


	/**
	 * set an item in the value column
	 * @param map the map of display components
	 * @param row the row number of the element
	 * @param value the text of the value
	 */
	public static void setValue (Map<String,Object> map, int row, String value)
	{
		((JLabel) map.get ("V" + row)).setText (" = " + value);
	}


	/**
	 * specifically set the contents of the X row
	 * @param map the map of display components
	 * @param value the text of the value
	 */
	public static void setXValue (Map<String,Object> map, String value)
	{
		int n = palate.getPalateColors ().length - 1;
		setExpression (map, n, "x"); setValue (map, n, value);
	}


	/**
	 * fill the expression column with sections of overall expression
	 * @param expression text of the overall expression
	 * @return a sample display object
	 */
	public static PlotLegend.SampleDisplay constructLegend (String expression)
	{
		Map<String,Object> map =
			new HashMap<String,Object> ();
		LegendUpdate legend = new LegendUpdate (map);
		legend.setLegendDisplay (makeLegend (map, 200));
		legend.setPlotExpressions (expression);
		return legend;
	}


}


/**
 * a connect object between the mouse handler and the display
 */
class LegendUpdate implements PlotLegend.SampleDisplay
{

	public LegendUpdate (Map<String, Object> map)
	{
		this.map = map;
	}
	Map<String,Object> map;
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MouseSampleTrigger.SampleDisplay#getPlotExpressions()
	 */
	public String[] getPlotExpressions () { return plotExpressions; }

	/**
	 * @param expression comma separated list of expressions 
	 */
	void setPlotExpressions (String expression)
	{
		String expressionList = expression.substring (1, expression.length()-2);
		plotExpressions = expressionList.split (",");

		int row = 0;
		for (String expr : plotExpressions)
		{
			PlotLegend.setExpression (map, row++, expr);
		}
	}
	String[] plotExpressions;

	/**
	 * @param p the panel for display
	 */
	void setLegendDisplay (JPanel p)
	{
		this.p = p;
	}
	JPanel p;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MouseSampleTrigger.SampleDisplay#display(java.lang.String, java.lang.String[])
	 */
	public void display (String x, String[] samples)
	{
		PlotLegend.setXValue (map, x);
		
		for (int i = 0; i < samples.length; i++)
		{
			PlotLegend.setValue (map, i, samples[i]);
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MouseSampleTrigger.SampleDisplay#showLegend()
	 */
	public void showLegend () { new DisplayFrame (p, "Plot Legend").show (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MouseSampleTrigger.SampleDisplay#setVariable(java.lang.String)
	 */
	public void setVariable(String variable) { this.variable = variable; }
	public String getVariable() { return variable; }
	String variable;

}


