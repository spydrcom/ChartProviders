
package net.myorb.gral;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

import java.awt.geom.Rectangle2D;

import java.awt.Graphics2D;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;

/**
 * copied from GRAL BarPlot class.
 *  Properties interface replaces references to Plot.
 *  Bars can be displayed with style set be properties.
 * refactor of original GRAL source done by Michael Druckman
 */
@SuppressWarnings ("serial")
public class ExtendedBarRenderer extends BarPlot.BarRenderer
{

	/**
	 * added to replace Plot class as source of style properties
	 * @author Michael
	 */
	public interface Properties
	{
		double getBarWidth ();
		double getMinimumBarHeight ();
		Color getColor ();
	}

	/**
	 * @param properties taken from interface rather than plot
	 */
	ExtendedBarRenderer (Properties properties)
	{ super (null); this.properties = properties; }
	Properties properties;

	/**
	 * connect bar renderer to series
	 * @param plot the plot being updated
	 * @param data the series containing one bar of data
	 * @param properties the properties to use for style
	 */
	public static void setPointRendering
	(Plot plot, DataSource data, Properties properties)
	{
		ExtendedBarRenderer renderer =
			new ExtendedBarRenderer (properties);
        renderer.setColor (properties.getColor ());
        ((XYPlot) plot).setPointRenderers (data, renderer);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.BarPlot.BarRenderer#getPointShape(de.erichseifert.gral.plots.points.PointData)
	 */
	@Override
	public Shape getPointShape (PointData data)
	{
		int colX = 0, colY = 1; Row row = data.row;
		if ( ! row.isColumnNumeric (colX) || ! row.isColumnNumeric (colY)) return null;

		AxisRenderer
			axisXRenderer = data.axisRenderers.get (0),
			axisYRenderer = data.axisRenderers.get (1);
		Axis axisX = data.axes.get (0), axisY = data.axes.get (1);

		double  valueY = ((Number) row.get (colY)).doubleValue ();
		double barYOrg = GralPrimitives.computePoint (axisYRenderer, axisY, 0.0, PointND.Y),
			   barYVal = GralPrimitives.computePoint (axisYRenderer, axisY, valueY, PointND.Y);
		double barYMin = Math.min (barYVal, barYOrg), barYMax = Math.max (barYVal, barYOrg);

		double  valueX = ((Number) row.get (colX)).doubleValue ();
		// straddle the x-axis point, half the bar width on each side
		double xOffset = Math.max (properties.getBarWidth (), 0.1) / 2; // don't allow too narrow
		double barXMin = GralPrimitives.computePoint (axisXRenderer, axisX, valueX - xOffset, PointND.X),
			   barXMax = GralPrimitives.computePoint (axisXRenderer, axisX, valueX + xOffset, PointND.X);
		double barWdth = Math.abs (barXMax - barXMin), barHeight = Math.abs (barYMax - barYMin);

		// position of the bar's left edge in screen coordinates
		double barX = GralPrimitives.computePoint (axisXRenderer, axisX, valueX, PointND.X);
		// position of the bar's upper edge in screen coordinates (the origin of the screen y axis is at the top)
		boolean barAboveAxis = barYMax == barYOrg; double barY = barAboveAxis ? 0.0 : -barHeight;

		double barHeightMin = properties.getMinimumBarHeight ();
		if (MathUtils.isCalculatable (barHeightMin) && barHeightMin > 0.0 && barHeight < barHeightMin)
		{ if (barAboveAxis) { barY += -barHeightMin + barHeight; } barHeight = barHeightMin; }
		return getBarShape (barXMin - barX, barY, barWdth, barHeight);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.BarPlot.BarRenderer#getPoint(de.erichseifert.gral.plots.points.PointData, java.awt.Shape)
	 */
	@Override
	public Drawable getPoint (final PointData data, final Shape shape)
	{
		return new Bar (this, data, shape, paintBoundaries);
	}
	Rectangle2D paintBoundaries = null;


}

/**
 * point drawing for a bar
 */
@SuppressWarnings ("serial")
class Bar extends AbstractDrawable
{

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.graphics.Drawable#draw(de.erichseifert.gral.graphics.DrawingContext)
	 */
	public void draw (DrawingContext context)
	{
		Row row = data.row;
		Rectangle2D paintBoundaries = null;
		Graphics2D graphics = context.getGraphics ();
		Paint paint = renderer.getColor ().get (row.getIndex ());
		GraphicsUtils.fillPaintedShape (graphics, shape, paint, paintBoundaries);
		ShapeUtils.drawPaintedShape (renderer, shape, graphics, paintBoundaries);
	}

	/**
	 * describe draw to perform
	 * @param renderer the renderer to use
	 * @param data the data that are to be described
	 * @param shape the shape to use to draw the representation
	 * @param paintBoundaries the rectangle boundaries or NULL for none
	 */
	Bar (ExtendedBarRenderer renderer, PointData data, Shape shape, Rectangle2D paintBoundaries)
	{ this.renderer = renderer; this.data = data; this.shape = shape; this.paintBoundaries = paintBoundaries; }
	ExtendedBarRenderer renderer;
	Rectangle2D paintBoundaries;
	PointData data;
	Shape shape;

}
