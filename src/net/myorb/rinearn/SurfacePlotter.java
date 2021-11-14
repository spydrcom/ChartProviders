
package net.myorb.rinearn;

import com.rinearn.graph3d.RinearnGraph3D;
import com.rinearn.graph3d.RinearnGraph3DOptionItem;

import net.myorb.charting.DisplayGraphTypes.SurfaceDescription3D;
import net.myorb.charting.DisplayGraphTypes.ViewSpace;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.utilities.NumericRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * produce 3D surface plot using Rinearn
 * @author Michael Druckman
 */
public class SurfacePlotter
{


	/**
	 * @param title name of the function plotted
	 */
	public SurfacePlotter (String title)
	{
		this.title = title;
	}
	String title;


	/**
	 * @param filepath path to data source
	 */
	public void plot (String filepath)
	{
		// use Rinearn package for 3D display
		RinearnGraph3D graph = new RinearnGraph3D ();
		graph.setWindowTitle (title);

		try
		{
			// Plot data read from a file
			graph.openDataFile (new File (filepath));
		}
		catch (FileNotFoundException fnfe)
		{
			throw new RuntimeException ("The file is not found", fnfe);
		}
		catch (IOException ioe)
		{
			throw new RuntimeException ("The file can not be opened", ioe);
		}
	}


	/**
	 * @param title name of the function plotted
	 * @param lowCorner the low left corner of the axis
	 * @param edgeSizeX the size of x axis
	 * @param edgeSizeY the size of y axis
	 */
	public SurfacePlotter (String title, Point lowCorner, double edgeSizeX, double edgeSizeY)
	{
		this (title);
		this.edgeSizeX = edgeSizeX; this.edgeSizeY = edgeSizeY;
		this.xlo = lowCorner.x; this.xhi = this.xlo + this.edgeSizeX;
		this.ylo = lowCorner.y; this.yhi = this.ylo + this.edgeSizeY;
		this.lowCorner = lowCorner;
	}

	/**
	 * @param title name of the function plotted
	 * @param view description of plot from request
	 */
	public SurfacePlotter (String title, ViewSpace view)
	{
		this (title, view.getLowCorner (), view.getEdgeSize (), view.getAltEdgeSize ());
	}
	double xlo, xhi, ylo, yhi;
	double edgeSizeX, edgeSizeY;
	Point lowCorner;


	/**
	 * title from constructor was sufficient
	 * @param x x-axis values
	 * @param y y-axis values
	 * @param z z-axis values
	 */
	public void plot (double[][] x, double[][] y, double[][] z)
	{
		plot (null, x, y, z);
	}

	/**
	 * @param points surface description object
	 */
	public void plot (SurfaceDescription3D points)
	{
		plot (points.getX (), points.getY (), points.getZ ());
	}

	/**
	 * multiple plots will be done on the supplied surface.
	 *  the subtitle will provide the distinction on the various plots.
	 *  mostly useful for complex plots with real, imaginary, and magnitude
	 *  being shown as separate displays needing identification.
	 *  null subtitle is ignored, title from constructor used.
	 * @param subtitle additional title text
	 * @param x x-axis values
	 * @param y y-axis values
	 * @param z z-axis values
	 */
	public void plot (String subtitle, double[][] x, double[][] y, double[][] z)
	{
		String fullTitle = title;
		if (subtitle != null) fullTitle = title + " - " + subtitle;
		RinearnGraph3D graph = new RinearnGraph3D ();
		graph.setWindowTitle (fullTitle);
		graph.setZLabel (title);

		// Settings of plotting options
		// (disables point-plotting and enables mesh-plotting)
		graph.setOptionSelected (RinearnGraph3DOptionItem.POINT, false);
		graph.setOptionSelected (RinearnGraph3DOptionItem.MEMBRANE, true);
		graph.setOptionSelected (RinearnGraph3DOptionItem.MESH, false);

		// use computed range
		graph.setXAutoRangingEnabled (false);
		graph.setYAutoRangingEnabled (false);
		graph.setZAutoRangingEnabled (false);

		// Range settings
		graph.setXRange (xlo, xhi);
		graph.setYRange (ylo, yhi);

		NumericRange range = new NumericRange ();
		for (int n=0; n<z.length; n++) range.evaluate (z[n]);
		graph.setZRange (range.getLo ().doubleValue (), range.getHi ().doubleValue ());

		// set data on plot
		graph.setData (x, y, z);
	}


}

