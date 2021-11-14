
package net.myorb.bfo;

// CalcLib charting
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphProperties;

// BFO
import org.faceless.graph2.Key;
import org.faceless.graph2.Axis;
import org.faceless.graph2.Graph;
import org.faceless.graph2.ZAxis;
import org.faceless.graph2.TextStyle;
import org.faceless.graph2.ImageOutput;
import org.faceless.graph2.ScatterSeries;
import org.faceless.graph2.LineSeries;
import org.faceless.graph2.AxesGraph;
import org.faceless.graph2.Marker;
import org.faceless.graph2.Series;
import org.faceless.graph2.Style;
import org.faceless.graph2.Align;

// IOlib
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.data.abstractions.SimplePropertiesManager.PropertyValueList;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.graphics.ColorTools;

// JRE AWT
import java.awt.Color;

//JRE IO
import java.io.FileOutputStream;
import java.io.File;

/**
 * support for an implementation of the chart library using BFO
 * @author Michael Druckman
 */
public class BfoPrimitives extends SimpleScreenIO
{


	/**
	 * the kinds of styles
	 */
	public enum KindOfStyle {TEXT, LINE, BAR, MARKER, KEY, IMAGE}


	/**
	 * the shapes of BFO markers
	 */
	public enum MarkerStyle {square, diamond, circle, star, cross, plus}


	/**
	 * enumerate BFO Align values
	 */
	public enum Alignment
	{
		BOTTOM (Align.BOTTOM), BOX (Align.BOX), CENTER (Align.CENTER),
		LEFT (Align.LEFT), MIDDLE (Align.MIDDLE), RIGHT (Align.RIGHT), TOP (Align.TOP);
		Alignment (int bfoAlign) { this.alignmentValue = bfoAlign; }
		public int getValue () { return alignmentValue; }
		private int alignmentValue;
	}


	/*
	 * populate BFO series objects from CalcLib Display Graph types
	 */


	/**
	 * @param series BFO line series
	 * @param from CalcLib point series
	 */
	public static void populate (LineSeries series, Point.Series from)
	{
		for (Point p : from)
		{
			if ( ! p.outOfRange ) series.set (p.x, p.y);
		}
	}


	/**
	 * @param series BFO scatter series
	 * @param from CalcLib point series
	 */
	public static void populate (ScatterSeries series, Point.Series from)
	{
		for (Point p : from)
		{
			if ( ! p.outOfRange ) series.set (p.x, p.y);
		}
	}


	/*
	 * component styling
	 */


	/**
	 * @param graph the graph to title
	 * @param to the text of the title
	 */
	public static void setTitle (Graph graph, String to)
	{
		int pointSize = 24 - to.length () / 5; if (pointSize < 12) pointSize = 12;
		StyleWrapper wrapper = getStyle ("TITLE_STYLE", KindOfStyle.TEXT); wrapper.setPointSize (pointSize);
		graph.addText (to, wrapper.getTextStyle ());
	}


	/**
	 * apply style to line series
	 * @param series a series to be styled
	 * @param using the color for the series
	 */
	public static void setStyleFor (LineSeries series, Color using)
	{
		StyleWrapper wrapper = getStyle ("LINE_STYLE", KindOfStyle.LINE);
		wrapper.setColor (using); series.setStyle (wrapper.getStyle ());
	}


	/**
	 * apply style to scatter series
	 * @param name a name for the series
	 * @return a scatter series configured with properties
	 */
	public static ScatterSeries getScatterSeries (String name)
	{
		StyleWrapper keyMarkerProperties =
			getStyle ("KEY_MARKER_STYLE", KindOfStyle.MARKER);
		ScatterSeries dataSeries = new ScatterSeries
			(
				name,
				keyMarkerProperties.getShape (),
				keyMarkerProperties.getPointSize ().doubleValue ()
			);
		return dataSeries;
	}


	/**
	 * set axis properties
	 * @param graph the graph to set axis
	 * @param withZaxis include a Z axis on the graph
	 * @param withRotation perform rotations
	 */
	public static void setAxis
		(
			AxesGraph graph, boolean withZaxis, boolean withRotation
		)
	{
		BfoBackgrounds.setAxisBG (graph); setLightVector (graph);
		if (withZaxis) graph.setAxis (Axis.ZAXIS, new ZAxis ());
	    if (withRotation) setRotation (graph);
	}


	/*
	 * style for key objects
	 */


	/**
	 * add key to graph
	 * @param to the graph being built
	 * @param series the series being added to key
	 */
	public static void addKey (AxesGraph to, Series... series)
	{
		for (Series s : series) to.addSeries (s);
		StyleWrapper keyProperties = getStyle ("KEY_STYLE", KindOfStyle.KEY);
		StyleWrapper keyTextProperties = getStyle ("KEY_TEXT_STYLE", KindOfStyle.TEXT);
		to.addKey (getKeyFor (keyProperties, keyTextProperties, series), keyProperties.getAlign ().getValue ());
	}


	/**
	 * add series to key
	 * @param keyProperties properties for key style
	 * @param keyTextProperties properties for key text style
	 * @param series the series object to be included
	 * @return the key object
	 */
	public static Key getKeyFor
		(
			StyleWrapper keyProperties,
			StyleWrapper keyTextProperties,
			Series... series
		)
	{
		Key key = new Key (keyProperties.getStyle ());
		for (Series s : series) key.addSeries (s, keyTextProperties.getTextStyle ());
		return key;
	}


	/**
	 * add item to key with custom style
	 * @param text extra text to be added
	 * @param keyTextProperties text style properties
	 * @param markerProperties properties for marker
	 * @param key the key to customize
	 */
	public static void customizeKey
		(
			String text,
			StyleWrapper keyTextProperties,
			StyleWrapper markerProperties,
			Key key
		)
	{
		key.addCustom
		(
			markerProperties.getMarker (),
			text, keyTextProperties.getTextStyle ()
		);
	}


	/*
	 * BFO frame mechanism
	 */


	/**
	 * @param title the title for the frame
	 * @param graph the graph to show in the frame
	 */
	public static void showFrame (String title, Graph graph)
	{
		if (graph == null) return;
		showImageFrame (title, getImageFor (graph));
	}
	public static void showImageFrame (String title, Image display)
	{
		new WidgetFrame (display, title).showOrHide ();
	}


	/**
	 * @param graph the graph to convert
	 * @return an image widget for display
	 */
	public static Image getImageFor (Graph graph)
	{
		return new Image (getPngFileFor (graph));
	}


	/**
	 * @param graph the graph being processed
	 * @return the absolute path to the PNG file
	 */
	public static String getPngFileFor (Graph graph)
	{
        try
        {
        	File out = SimpleStreamIO.createTempFile ("IMG", "png");
        	StyleWrapper imageProperties = getStyle ("IMAGE", KindOfStyle.IMAGE);
        	ImageOutput image = imageProperties.getImageOutput (); graph.draw (image);
			FileOutputStream stream = new FileOutputStream (out);
			image.writePNG (stream, 0); stream.close ();
			return out.getAbsolutePath ();
		}
        catch (Exception e)
        {
        	throw new RuntimeException ("Unable to process image", e);
		}
	}


	/*
	 * processing of chart properties
	 */


	/**
	 * properties cache for Style objects
	 */
	public static class StyleCache
		extends SimplePropertiesManager.Cache <StyleWrapper, KindOfStyle>
		implements SimplePropertiesManager.Cache.ObjectFactory <StyleWrapper, KindOfStyle>
	{
		public StyleCache ()
		{
			super
			(
				DisplayGraphProperties.CHART_ENTRY,
				DisplayGraphProperties.getPropertyTokens ()
			);
			setFactory (this); init ();
		}

		void init ()
		{
			addDefaultFor ("AXIS_TITLE_STYLE", "Default 18 BLACK");
			addDefaultFor ("TITLE_STYLE", "Default 24 BLACK CENTER");
			addDefaultFor ("KEY_TEXT_STYLE", "Times 12 BLACK");
			addDefaultFor ("KEY_MARKER_STYLE", "diamond 12");
			addDefaultFor ("KEY_STYLE", "WHITE BOTTOM");
			addDefaultFor ("LINE_STYLE", "BLACK 3");
			addDefaultFor ("BAR_STYLE", "0.8");
			addDefaultFor ("LIGHT", "X + Y -");
			addDefaultFor ("IMAGE", "800 600");
			addDefaultFor ("ALPHA", "0xFF");
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimplePropertiesManager.Cache.ObjectFactory#newInstance(net.myorb.data.abstractions.SimplePropertiesManager.PropertyValueList, java.lang.Object)
		 */
		public StyleWrapper newInstance (PropertyValueList tokens, KindOfStyle kind)
		{
			return getStyle (tokens, kind);
		}
	}
	public static StyleCache styleCache = new StyleCache ();
	public static StyleWrapper getStyle (String named, KindOfStyle kind)
	{ return styleCache.lookup (named, kind); }


	/*
	 * implementation of style object factories
	 */


	/**
	 * instance the chosen kind of style object
	 * @param propertyValues the tokens of the property
	 * @param kind the kind of the object to be created using the property
	 * @return the object requested
	 */
	public static StyleWrapper getStyle
	(SimplePropertiesManager.PropertyValueList propertyValues, KindOfStyle kind)
	{
		switch (kind)
		{
			case KEY:		return getKeyStyle (propertyValues);
			case TEXT:		return getTextStyle (propertyValues);
			case MARKER:	return getMarkerStyle (propertyValues);
			case IMAGE:		return getImage (propertyValues);
			case LINE:		return getStyle (propertyValues);
			case BAR:		return getBar (propertyValues);
			default:		return null;
		}
	}


	/**
	 * collect image properties
	 * @param propertyValues the tokens of the property
	 * @return image properties
	 */
	public static StyleWrapper getImage
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		int
			w = SimplePropertiesManager.pgetNumber (propertyValues, 0).intValue (),
			h = SimplePropertiesManager.pgetNumber (propertyValues, 1).intValue ();
		return new StyleWrapper (w, h);
	}


	/**
	 * instance of marker object
	 * @param propertyValues the tokens of the property
	 * @return a marker with shape and point size set
	 */
	public static StyleWrapper getMarkerStyle
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper (SimplePropertiesManager.pgetText (propertyValues, 0));
		style.setPointSize (SimplePropertiesManager.pgetNumber (propertyValues, 1));
		return style;
	}


	/**
	 * instance of key object
	 * @param propertyValues the tokens of the property
	 * @return a new key object
	 */
	public static StyleWrapper getKeyStyle
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper (ColorTools.getColorFor (propertyValues.get (0)));
		if (propertyValues.size () > 1) style.setAlign (getAlignment (propertyValues, 1));
		return style;
	}


	/**
	 * instance of style object
	 * @param propertyValues the tokens of the property
	 * @return a new style object
	 */
	public static StyleWrapper getStyle
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		Color color = ColorTools.getColorFor (propertyValues.get (0));
		StyleWrapper style = new StyleWrapper (color);

		if (propertyValues.size () > 1)
		{
			style.setThickness (SimplePropertiesManager.pgetNumber (propertyValues, 1));
		}

		if (propertyValues.size () > 2)
		{
			style.setBorder (ColorTools.getColorFor (propertyValues.get (2)));
		}

		return style;
	}


	/**
	 * bar style
	 * @param propertyValues the tokens of the property
	 * @return a new bar style object
	 */
	public static StyleWrapper getBar
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper ();
		style.setBarWidth (SimplePropertiesManager.pgetNumber (propertyValues, 0));

		if (propertyValues.size () > 1)
		{
			style.setBarPoly (SimplePropertiesManager.pgetNumber (propertyValues, 1));
		}

		return style;
	}

	/**
	 * instance text style
	 * @param propertyValues the tokens of the property
	 * @return a new text style object
	 */
	public static StyleWrapper getTextStyle
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		String fontName = SimplePropertiesManager.pgetText (propertyValues, 0);
		Number pointSize = SimplePropertiesManager.pgetNumber (propertyValues, 1);
		Color color = ColorTools.getColorFor (propertyValues.get (2));

		StyleWrapper style =
				new StyleWrapper
				(
					fontName, pointSize, color
				);
		if (propertyValues.size () > 3)
				style.setAlign (getAlignment (propertyValues, 3));
		return style;
	}


	/*
	 * alignment object generation
	 */


	/**
	 * @param propertyValues the token list for the property
	 * @param atIndex the index of the token to interpret
	 * @return the alignment value
	 */
	public static Alignment getAlignment
	(SimplePropertiesManager.PropertyValueList propertyValues, int atIndex)
	{
		return Alignment.valueOf (propertyValues.get (atIndex).getTokenImage ());
	}


	/*
	 * light vector property implementation
	 */


	public static void setLightVector (AxesGraph graph)
	{
		SimplePropertiesManager.PropertyValueList
			propertyValues = DisplayGraphProperties.getChartProperty ("LIGHT");
		if (propertyValues != null) setLightVector (graph, propertyValues);
	}
	public static void setLightVector
		(
			AxesGraph graph, SimplePropertiesManager.PropertyValueList propertyValues
		)
	{
		int x = 0, y = 0, z = 0, l = 70;
		for (int i = 0; i < propertyValues.size (); )
		{
			String
				axis = propertyValues.get (i++).getTokenImage ().toUpperCase (),
				value = propertyValues.get (i++).getTokenImage ();
			int direction = value.charAt (0) == '+' ? 1 : -1;

			switch (axis.charAt (0))
			{
				case 'X': x = direction; break;
				case 'Y': y = direction; break;
				case 'Z': z = direction; break;
				case 'L': l = Integer.parseInt (value); break;
				default: throw new RuntimeException ("No such axis: " + axis);
			}
		}
		graph.setLightVector (x, y, z); graph.setLightLevel (l);
	}


	/*
	 * rotation property implementation
	 */


	/**
     * check for rotation properties to apply to graph
     * @param graph the graph to apply rotations to
     */
    public static void setRotation (Graph graph)
    {
		SimplePropertiesManager.PropertyValueList
			values = DisplayGraphProperties.getChartProperty ("ROTATION");
		if (values != null) setRotation (graph, values, values.size () - 2);
    }
    public static void setRotation
    	(
    		Graph graph,
    		SimplePropertiesManager.PropertyValueList propertyValues,
    		int last
    	)
    {
    	double angle;
		for (int i = 0; i <= last; )
		{
			char axis = propertyValues.get (i++).getTokenImage ().toUpperCase ().charAt (0);
			if ((angle = propertyValues.get (i++).getTokenValue ().doubleValue ()) > 180) angle -= 360;

			switch (axis)
			{
				case 'X': graph.setXRotation (angle); break;
				case 'Y': graph.setYRotation (angle); break;
				case 'Z': graph.setZRotation (angle); break;
				default: throw new RuntimeException ("No such axis: " + axis);
			}
		}
    }


}


/**
 * a wrapper object for style object factories
 */
class StyleWrapper
{

	StyleWrapper
	(int w, int h) { setW (w); setH (h); }
	StyleWrapper (Color color) { setColor (color); }
	StyleWrapper (String fontName, Number pointSize, Color color)
	{ setFontName (fontName); setPointSize (pointSize); setColor (color); }
	StyleWrapper (String shape) { setShape (shape); }
	StyleWrapper () {}

	/*
	 * POJO getters/setters
	 */

	public String getShape () { return associatedName; }
	public void setShape (String shape) { this.associatedName = shape; }
	public void setFontName (String fontName) { this.associatedName = fontName; }
	public String getFontName () { return associatedName; }
	private String associatedName = null;

	public Number getPointSize () { return pointSize; }
	public void setPointSize (Number pointSize) { this.pointSize = pointSize; }
	private Number pointSize = null;

	public Color getColor () { return color; }
	public void setColor (Color color) { this.color = color; }
	private Color color = null;

	public BfoPrimitives.Alignment getAlign () { return align; }
	public void setAlign (BfoPrimitives.Alignment align) { this.align = align; }
	private BfoPrimitives.Alignment align = null;

	public Number getThickness () { return thickness; }
	public void setThickness (Number thickness) { this.thickness = thickness; }
	private Number thickness = null;

	public Number getBarWidth () { return barWidth; }
	public void setBarWidth (Number barWidth) { this.barWidth = barWidth; }
	private Number barWidth = null;

	public Number getBarPoly() { return barPoly; }
	public boolean hasBarPoly() { return barPoly != null; }
	public void setBarPoly(Number barPoly) { this.barPoly = barPoly;}
	private Number barPoly = null;

	public Color getBorder () { return border; }
	public void setBorder (Color border) { this.border = border; }
	private Color border = null;

	public int getW () { return w; }
	public void setW (int w) { this.w = w; }
	public void setH (int h) { this.h = h; }
	public int getH () { return h; }
	private int w, h;

	/**
	 * @return a new ImageOutput instance with properties set
	 */
	ImageOutput getImageOutput ()
	{
		return new ImageOutput (w, h);
	}

	/**
	 * @return a new BFO TextStyle object configured with properties
	 */
	TextStyle getTextStyle ()
	{
		TextStyle style = new TextStyle (associatedName, pointSize.doubleValue (), color);
		if (align != null) style.setAlign (align.getValue ());
		return style;
	}

	/**
	 * @return a new BFO Style object configured with properties
	 */
	Style getStyle ()
	{
		Style style = new Style (color);
		if (thickness != null) style.setLineThickness (thickness.doubleValue ());
		if (border != null) style.setBorderColor (border);
		return style;
	}

	/**
	 * @return a new BFO Marker object configured with properties
	 */
	Marker getMarker ()
	{
		Marker marker = new Marker (associatedName, pointSize.doubleValue ());
		if (color != null) marker.setStyle (getStyle ());
		return marker;
	}

}

