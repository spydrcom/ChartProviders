
package net.myorb.charting;

import net.myorb.charting.PlotLegend;

import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.CommonCommandParser.SpecialTokenSegments;
import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.gui.graphics.ColorTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * methods for getting property values from scripts
 * @author Michael Druckman
 */
public class DisplayGraphProperties
{


	/**
	 * get simple value for named property
	 * @param propertyName the name of the property
	 * @return the text value of the property
	 */
	public static String getTextProperty (String propertyName)
	{
		SimplePropertiesManager.PropertyValueList
					propertyValue = getChartProperty (propertyName);
		if (propertyValue != null) return SimplePropertiesManager.pgetText (propertyValue, 0);
		return "";
	}


	/**
	 * get text values for named property
	 * @param propertyName the name of the property
	 * @return the list of text values of the property
	 */
	public static List <String> getTextListProperty (String propertyName)
	{
		ArrayList <String> list = new ArrayList<>();
		for (CommonCommandParser.TokenDescriptor d : getChartProperty (propertyName))
		{
			if (d.getTokenType () == CommonCommandParser.TokenType.QOT)
			{ list.add (CommonCommandParser.stripQuotes (d.getTokenImage ())); }
		}
		return list;
	}


	/**
	 * get simple value for named property
	 * @param propertyName the name of the property
	 * @return the numeric value of the property
	 */
	public static Number getNumericProperty (String propertyName)
	{
		SimplePropertiesManager.PropertyValueList
					propertyValue = getChartProperty (propertyName);
		if (propertyValue != null) return SimplePropertiesManager.pgetNumber (propertyValue, 0);
		return Double.MIN_VALUE;
	}


	/**
	 * set palate to property values
	 */
	public static void setPalate ()
	{
		SimplePropertiesManager.PropertyValueList
					propertyValue = getChartProperty ("PALETTE");
		if (propertyValue != null)
		{
			ArrayList <Integer> list = new ArrayList <> ();
			ColorTools.parseColorList (propertyValue, list);
			PlotLegend.setPalate (list.toArray (new Integer[]{}));
		}
	}


	/**
	 * get property value as token list
	 * @param propertyName the name of the property
	 * @return the full value of the property
	 */
	public static SimplePropertiesManager.PropertyValueList
			getChartProperty (String propertyName)
	{
		SimplePropertiesManager.PropertyValueList propertyValue =
				SimplePropertiesManager.pget (CHART_ENTRY, propertyName);
		return propertyValue;
	}
	public static final String CHART_ENTRY = "CHART";


	/**
	 * get the token segments parser for properties
	 * @return token segments descriptor for properties
	 */
	public static SpecialTokenSegments getPropertyTokens ()
	{
		return new PropertyTokensParser ();
	}


}


/**
 * token segments descriptor for properties
 */
class PropertyTokensParser extends CommonCommandParser
		implements CommonCommandParser.SpecialTokenSegments
{
	public final String WHITE_SPACE = " \t\r\n";
	public final String OPERATOR = "+-*/^~!@#%&$|<>,.:;[]{}()=\\?'";
	public final String MULTI_CHARACTER_OPERATOR = "", OPERATOR_EXTENDED = "";
	public final String IDN_LEAD = LETTER, IDN_BODY = LETTER + "_";

	public String getIdnLead () { return IDN_LEAD; }
	public String getWhiteSpace () { return WHITE_SPACE; }
	public String getSequenceCaptureMarkers () { return null; }
	public Collection <String> getCommentIndicators () { return null; }
	public String getMultiCharacterOperator () { return MULTI_CHARACTER_OPERATOR; }
	public String getExtendedOperator () { return OPERATOR_EXTENDED; }
	public String getOperator () { return OPERATOR; }
	public String getIdnBody () { return IDN_BODY; }
}

