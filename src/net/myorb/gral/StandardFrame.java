
package net.myorb.gral;

// IOlib GUI
import net.myorb.gui.components.GuiToolkit;
import net.myorb.gui.components.DisplayContainer;
import net.myorb.gui.components.DisplayFrame;

// GRAL
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.ui.InteractivePanel;

// JRE
import java.awt.Component;
import java.awt.BorderLayout;

/**
 * frame support for chart library using GRAL
 * @author Michael Druckman
 */
public class StandardFrame
{

	/**
	 * @param title a title for the frame
	 */
	public StandardFrame (String title)
	{
		display =
			GuiToolkit.newDisplayContainer (title);
		GuiToolkit.setCloseHide (display);
	}
	DisplayContainer display;

	/**
	 * @param drawable the component to display
	 */
	public void setDisplay (Drawable drawable)
	{
		setDisplay (new InteractivePanel (drawable));
 	}

	/**
	 * @param panel the GRAL interactive panel
	 */
	public void setDisplay (InteractivePanel panel)
	{
		setDisplayComponent (panel);
	}

	/**
	 * @param component an AWT component to place in content pane
	 */
	public void setDisplayComponent (Component component)
	{
		display.getContentPane ().add (component, BorderLayout.CENTER);
		display.setMinimumSize (display.getContentPane ().getMinimumSize ());
	}

	/**
	 * resize and show in centered display frame
	 * @param w width for the frame 
	 * @param h height to use
	 */
	public void show (int w, int h)
	{
		display.setSize (w, h);
		GuiToolkit.centerAndShow
		(DisplayFrame.lastFrame = display);
	}

}
