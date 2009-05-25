package sbrn.mapviewer.gui.components;

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
				String text = ftf.getText();
				
				if(!isUpperThreshold && (Float.parseFloat(text) >= threshold))
					return true;
				else if(isUpperThreshold && (Float.parseFloat(text) <= threshold))
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
