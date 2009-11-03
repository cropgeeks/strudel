package sbrn.mapviewer.gui.components;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JFormattedTextField.*;
import sbrn.mapviewer.*;
import scri.commons.gui.*;

public class FormattedTextFieldVerifier extends InputVerifier
{

	private String negNumberMessage;
	private float threshold;
	private boolean isUpperThreshold;
	
	public FormattedTextFieldVerifier(String negNumberMessage, float threshold, boolean isUpperThreshold)
	{
		this.negNumberMessage = negNumberMessage;
		this.threshold = threshold;
		this.isUpperThreshold = isUpperThreshold;
	}
	
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
				
				TaskDialog.error(negNumberMessage, "Close");
				return false;
			}
		}
		return true;
	}
	
	public boolean shouldYieldFocus(JComponent input)
	{
		return verify(input);
	}
}
