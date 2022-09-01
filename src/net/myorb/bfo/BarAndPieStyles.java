
package net.myorb.bfo;

// CalcLib charting
import net.myorb.charting.DisplayGraphLibraryInterface.Portions;
import net.myorb.charting.DisplayGraphProperties;
import net.myorb.charting.PlotLegend;

import java.awt.Color;

// BFO
import org.faceless.graph2.Graph;
import org.faceless.graph2.Key;

/**
 * bar and pie chart specific style methods for bar/pie charts using BFO
 * @author Michael Druckman
 */
public class BarAndPieStyles extends BfoPrimitives
{


	/**
	 * set the title of a graph
	 * @param graph the graph to be titled
	 */
	public static void setTitle (Graph graph)
	{
		setTitle (graph, DisplayGraphProperties.getTextProperty (TITLE));
	}
	public static final String TITLE = "TITLE";


	/**
	 * set a key based on the portion data
	 * @param graph the graph to have the key added
	 * @param portions the portions to represent in the key
	 */
	public static void setKey (Graph graph, Portions portions)
	{
		setKey (graph, portions.getNames ());
	}


	/**
	 * set a key based on the portion items
	 * @param graph the graph to have the key added
	 * @param names the items to place in the key
	 */
	public static void setKey (Graph graph, String[] names)
	{
		Key key;
		StyleWrapper
			keyProperties = getStyle ("KEY_STYLE", KindOfStyle.KEY),
			textProperties = getStyle ("KEY_TEXT_STYLE", KindOfStyle.TEXT),
			markerProperties = getStyle ("KEY_MARKER_STYLE", KindOfStyle.MARKER);
        addToKey (key = new Key (keyProperties.getStyle ()), names, textProperties, markerProperties);
        graph.addKey (key, keyProperties.getAlign ().getValue ());
	}


	/**
	 * add items to key
	 * @param key the key being built
	 * @param names the names of items in the key
	 * @param keyText the properties for text style 
	 * @param markers the style for the markers
	 */
	public static void addToKey
	(Key key, String[] names, StyleWrapper keyText, StyleWrapper markers)
	{
        int c = 0;
		Color[] colorList = PlotLegend.getPalate ();

        for (String name : names)
        {
        	markers.setColor (colorList[c++]);
        	customizeKey (name, keyText, markers, key);
        }
	}


}

