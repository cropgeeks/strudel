package sbrn.mapviewer.gui.components;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JFormattedTextField.*;
import sbrn.mapviewer.*;
import scri.commons.gui.*;

/**
 * Checks that the user has entered a value into a textfield that exceeds the specified threshold
 */
public class FormattedTextFieldVerifier extends InputVerifier
{

	private final String errorMessage;
	private final float threshold;
	private final boolean isUpperThreshold;

	public FormattedTextFieldVerifier(String errorMessage, float threshold, boolean isUpperThreshold)
	{
		this.errorMessage = errorMessage;
		this.threshold = threshold;
		this.isUpperThreshold = isUpperThreshold;
	}

	@Override
	public boolean verify(JComponent input)
	{
		if (input instanceof JFormattedTextField)
		{
			JFormattedTextField ftf = (JFormattedTextField) input;
			AbstractFormatter formatter = ftf.getFormatter();
			if (formatter != null)
			{
				NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
				float value = 0;
				try
				{
					value = nf.parse(ftf.getText()).floatValue();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}

				if(!isUpperThreshold && (value >= threshold))
					return true;
				else if(isUpperThreshold && (value <= threshold))
					return true;

				TaskDialog.error(errorMessage, "Close");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean shouldYieldFocus(JComponent input)
	{
		return verify(input);
	}
}
