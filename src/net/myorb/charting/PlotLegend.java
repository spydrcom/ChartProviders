
package net.myorb.charting;

import net.myorb.data.abstractions.SimplePropertiesManager;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.graphics.ColorTools;

import java.util.Map;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Color;

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
		void setVariable(String variable);

		/**
		 * get name of variable used in expressions
		 * @return name of variable used in expressions
		 */
		String getVariable();

		/**
		 * show frame with legend
		 */
		void showLegend ();
	}


	/*
	 * Plot Color Palate
	 */

	public static final Color
		SADDLE = Color.decode ("0x8B4513"),	DRED = Color.decode ("0x8B0000"),	DGREEN = Color.decode ("0x6400"),
		OLIVE = Color.decode ("0x556B2F"),	TEAL = Color.decode ("0x8080"),		INDIGO = Color.decode ("0x4B0082"),		
		BRICK = Color.decode ("0xB22222"),	SLATE = Color.decode ("0x6A5ACD"),	NAVY = Color.decode ("0x80");
	public static final String LEGEND_IDENTIFIER = "LEGEND";
	private static Color[] colorList = new Color[]
	{
		Color.BLUE,			Color.RED,		Color.GREEN,
		Color.ORANGE,		DGREEN,			Color.CYAN,
		Color.MAGENTA,		TEAL,			INDIGO,
		SADDLE,				OLIVE,			NAVY,
		BRICK,				DRED
	};
	public static Color[] getPalate () { return colorList; }


	/**
	 * @param values the RGBA values for the new palate
	 */
	public static void setPalate (Integer[] values) { colorList = getColors (values); applyAlpha (); }


	/**
	 * apply alpha property if present
	 */
	public static void applyAlpha ()
	{
		SimplePropertiesManager.PropertyValueList propertyValue = DisplayGraphProperties.getChartProperty ("ALPHA");
		if (propertyValue != null) applyAlpha (colorList, propertyValue.get (0).getTokenValueAsCoded ().intValue ());
		
	}


	/**
	 * @return list of current legend colors
	 */
	public static DisplayGraphTypes.Colors getColorList ()
	{
		DisplayGraphTypes.Colors colors = new DisplayGraphTypes.Colors ();
		for (Color c : colorList) colors.add (c);
		return colors;
	}


	/**
	 * build legend display panel
	 * @param map a map object that will collect components
	 * @param size the size to set for display preference
	 * @return a panel with the display
	 */
	public static JPanel makeLegend (Map<String,Object> map, int size)
	{
		JPanel p = new JPanel ();
		int rows = colorList.length;
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
		int n = colorList.length - 1;
		setExpression (map, n, "x");
		setValue (map, n, value);
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


