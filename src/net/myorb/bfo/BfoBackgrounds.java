
package net.myorb.bfo;

// CalcLib charting
import net.myorb.charting.DisplayGraphProperties;

// BFO
import org.faceless.graph2.Axis;
import org.faceless.graph2.AxesGraph;

// IOlib
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.data.abstractions.SimpleStreamIO.TextSource;
import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonReader;
import net.myorb.data.notations.json.JsonTools;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.graphics.ColorTools;

// JRE
import java.awt.Color;
import java.awt.Paint;

public class BfoBackgrounds extends SimpleScreenIO
{


	/**
	 * @param graph the graph to modify
	 * @param paints the paints to use in the background
	 * @param lineColor the color of the separation line
	 * @param dash the numeric pattern of dash to use
	 */
	public static void setGraphBG (AxesGraph graph, Paint[] paints, Color lineColor, double[] dash)
	{
        graph.getAxis (Axis.LEFT).setWallPaint (paints, lineColor, dash);
	    graph.getAxis (Axis.BOTTOM).setWallPaint (paints, lineColor, dash);
	    graph.setBackWallPaint (paints, lineColor, Axis.LEFT, Axis.BOTTOM, dash);
	}


	/**
	 * set the default axis wall paper
	 * @param graph the graph to add BG
	 */
	public static void setDefaultAxisBG
	(AxesGraph graph) { setGraphBG (graph, G238_WHITE, G204, null); }
	static final Color G238 = greyScale (238), G204 = greyScale (204);
	static final Paint[] G238_WHITE = new Paint[]{G238, Color.WHITE};


	/**
	 * set the axis wall paper
	 * @param graph the graph to add BG
	 */
	public static void setAxisBG (AxesGraph graph)
	{
		SimplePropertiesManager.PropertyValueList propertyValues =
			SimplePropertiesManager.pget (DisplayGraphProperties.CHART_ENTRY, "AXIS_BG");
		if (propertyValues == null) setDefaultAxisBG (graph);
		else { readBG (propertyValues, graph); }
	}


	/**
	 * @param from properties identifying the file holding the description
	 * @param to the graph to be modified
	 */
	public static void readBG (SimplePropertiesManager.PropertyValueList from, AxesGraph to)
	{
		try
		{
			String fileName = SimplePropertiesManager.pgetText (from, 0);
			TextSource source = SimpleStreamIO.getFileSource ("properties/" + fileName + ".json");
			readBG (JsonReader.readFrom (source), to);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Error in BG properties: " + from.get (0).getTokenImage ());
		}
	}


	/**
	 * @param json the JSON from the properties file
	 * @param to the graph to be modified
	 */
	public static void readBG (JsonSemantics.JsonValue json, AxesGraph to)
	{
		JsonSemantics.JsonObject object = (JsonSemantics.JsonObject) json;
		Paint[] colors = getPaletteFrom (object.getMember ("paints"));
		double[] dash = getArrayFrom (object.getMember ("dash"));
		Color line = getColorFrom (object.getMember ("line"));
		setGraphBG (to, colors, line, dash);
	}


	/**
	 * @param value a JSON description
	 * @return an array of color objects
	 */
	public static Paint[] getPaletteFrom (JsonSemantics.JsonValue value)
	{
		Paint[] colors;
		String[] paintIdentifiers = JsonTools.toStringArray ((JsonSemantics.JsonArray) value);
		ColorTools.paletteFor (paintIdentifiers, colors = new Paint[paintIdentifiers.length]);
		return colors;
	}


	/**
	 * @param value a JSON description
	 * @return a Color object set by value
	 */
	public static Color getColorFrom (JsonSemantics.JsonValue value)
	{
		String lineColorDescription = JsonSemantics.getStringOrNull (value);
		if (lineColorDescription != null) return ColorTools.colorFor (lineColorDescription);
		return null;
	}


	/**
	 * @param value a JSON description
	 * @return an array of double float
	 */
	public static double[] getArrayFrom (JsonSemantics.JsonValue value)
	{
		double[] array = null;
		if (JsonSemantics.isNull (value)) return array;
		Number[] numbers = JsonTools.toNumberArray (value);
		SimpleUtilities.toFloats (numbers, array = new double[numbers.length]);
		return array;
	}


}

