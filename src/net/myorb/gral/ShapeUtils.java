
package net.myorb.gral;

import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.util.GraphicsUtils;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Shape;

/**
 * copied from GRAL BarPlot class.
 *  shape drawing is driven by renderer.
 *  stroke and paint are taken from renderer.
 *  refactored to use MYORB code style standards.
 */
public class ShapeUtils
{

	/**
	 * use renderer to paint shape
	 * @param renderer the renderer to use
	 * @param shape description of shape to draw
	 * @param graphics the 2D graphics object to paint to
	 * @param paintBoundaries the rectangle boundaries or NULL for none
	 */
	public static void drawPaintedShape
	(BarPlot.BarRenderer renderer, Shape shape, Graphics2D graphics, Rectangle2D paintBoundaries)
	{ drawPaintedShape (shape, renderer.getBorderStroke (), renderer.getBorderColor (), paintBoundaries, graphics); }

	/**
	 * draw shape using paint and stroke
	 * @param shape description of shape to draw
	 * @param stroke the stroke to use drawing the shape
	 * @param strokePaint the paint to use drawing the shape
	 * @param paintBoundaries the boundaries to use drawing the shape
	 * @param graphics the 2D graphics object to paint to
	 */
	public static void drawPaintedShape
		(
			Shape shape, Stroke stroke, Paint strokePaint, Rectangle2D paintBoundaries, Graphics2D graphics
		)
	{
		if (stroke == null || strokePaint == null) return;

		GraphicsUtils.drawPaintedShape
		(
			graphics, shape, strokePaint, paintBoundaries, stroke
		);
	}

}
