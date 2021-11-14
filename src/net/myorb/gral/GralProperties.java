
package net.myorb.gral;

import de.erichseifert.gral.plots.PlotArea;
import de.erichseifert.gral.graphics.Insets2D;

import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.data.abstractions.SimplePropertiesManager.PropertyValueList;

import net.myorb.charting.DisplayGraphProperties;
import net.myorb.gui.graphics.ColorTools;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * property support for
 *  an implementation of the chart library using GRAL
 * @author Michael Druckman
 */
public class GralProperties
{

	/**
	 * the kinds of styles
	 */
	public enum KindOfStyle {BARS, COLORS, BORDER, IMAGE, INSETS, LEGEND}

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
			addDefaultFor ("ALPHA", "0xFF");
			addDefaultFor ("IMAGE", "800  600");
			addDefaultFor ("GRAL_INSETS", "20  60  60  20");
			addDefaultFor ("GRAL_BORDER", "DARK_GRAY  2");
			addDefaultFor ("GRAL_LEGEND", "1  1");
			addDefaultFor ("GRAL_BARS", "0.8");
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
	 * force property cache invalidation
	 * so new property set commands can
	 * override previous settings
	 */

	static
	{
		SimplePropertiesManager.setmaintenance
		(
			new SimplePropertiesManager.Maintenance ()
			{
				@Override
				public void invalidate (String entry, String property)
				{
					styleCache.invalidate (property);
				}
			}
		);
	}

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
			case LEGEND:	return getLegend (propertyValues);
			case INSETS:	return getInsets (propertyValues);
			case BORDER:	return getBorder (propertyValues);
			case IMAGE:		return getImage (propertyValues);
			case BARS:		return getBar (propertyValues);
			default:		return null;
		}
	}

	/**
	 * Legend
	 * @param propertyValues the tokens of the property
	 * @return an object holding the properties
	 */
	public static StyleWrapper getLegend
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper ();

		style.setXalign (SimplePropertiesManager.pgetNumber (propertyValues, 0).doubleValue ());
		style.setYalign (SimplePropertiesManager.pgetNumber (propertyValues, 1).doubleValue ());

		if (propertyValues.size () > 2)
		{
			style.setOrientation (SimplePropertiesManager.pgetText (propertyValues, 2));
		}

		return style;
	}

	/**
	 * Insets
	 * @param propertyValues the tokens of the property
	 * @return an object holding the properties
	 */
	public static StyleWrapper getInsets
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper ();
		style.top = SimplePropertiesManager.pgetNumber (propertyValues, 0).doubleValue ();
		style.left = SimplePropertiesManager.pgetNumber (propertyValues, 1).doubleValue ();
		style.bottom = SimplePropertiesManager.pgetNumber (propertyValues, 2).doubleValue ();
		style.right = SimplePropertiesManager.pgetNumber (propertyValues, 3).doubleValue ();
		return style;
	}

	/**
	 * borders
	 * @param propertyValues the tokens of the property
	 * @return an object holding the properties
	 */
	public static StyleWrapper getBorder
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper ();
		style.setColor (ColorTools.getColorFor (propertyValues.get (0)));

		if (propertyValues.size () > 1)
		{
			style.setStroke (SimplePropertiesManager.pgetNumber (propertyValues, 1));
		}

		return style;
	}

	/**
	 * bars of bar charts
	 * @param propertyValues the tokens of the property
	 * @return an object holding the properties
	 */
	public static StyleWrapper getBar
	(SimplePropertiesManager.PropertyValueList propertyValues)
	{
		StyleWrapper style = new StyleWrapper ();
		style.setBarWidth (SimplePropertiesManager.pgetNumber (propertyValues, 0));

		if (propertyValues.size () > 1)
		{
			style.setMinBarHeight (SimplePropertiesManager.pgetNumber (propertyValues, 1));
		}

		return style;
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

}


/**
 * a wrapper object for style object factories
 */
class StyleWrapper implements ExtendedBarRenderer.Properties
{

	StyleWrapper () {}
	StyleWrapper (int w, int h) { setW (w); setH (h); }

	// image properties
	public int getW () { return w; }
	public void setW (int w) { this.w = w; }
	public void setH (int h) { this.h = h; }
	public int getH () { return h; }
	private int w, h;

	// bar properties
	public double getBarWidth () { return barWidth; }
	public void setBarWidth (Number barWidth) { this.barWidth = barWidth.doubleValue (); }
	public void setMinBarHeight (Number minBarHeight) { this.minBarHeight = minBarHeight.doubleValue (); }
	public double getMinimumBarHeight () { return minBarHeight; }
	double barWidth = 0.5, minBarHeight = 0.0;

	// border properties
	public Color getColor () { return color; }
	public void setColor (Color color) { this.color = color; }
	public void setStroke (Number stroke) { this.stroke = stroke.floatValue (); }
	public float getStroke () { return stroke; }
	Color color; float stroke = 2;

	public void setBorder (PlotArea plotArea)
	{
		plotArea.setBorderStroke (new BasicStroke (getStroke ()));
		plotArea.setBorderColor (getColor ());
	}

	// Insets
	public Insets2D.Double getInsets ()
	{ return new Insets2D.Double (top, left, bottom, right); }
	double top, left, bottom, right;

	// Legend
	public double getXalign () { return xAlign; }
	public double getYalign () { return yAlign; }
	public void setXalign (double xAlign) { this.xAlign = xAlign; }
	public void setYalign (double yAlign) { this.yAlign = yAlign; }
	public void setOrientation (String orientation) { this.orientation = orientation; }
	public String getOrientation () { return orientation; }
	double xAlign = 1, yAlign = 1;
	String orientation = null;

}

