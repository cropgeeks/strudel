package sbrn.mapviewer.gui.components;

import java.text.*;
import javax.swing.*;
import javax.swing.JFormattedTextField.*;
import scri.commons.gui.*;

public class FormattedTextFieldVerifier extends InputVerifier
{
	public boolean verify(JComponent input)
	{
		if (input instanceof JFormattedTextField)
		{
			JFormattedTextField ftf = (JFormattedTextField) input;
			AbstractFormatter formatter = ftf.getFormatter();
			if (formatter != null)
			{
				String text = ftf.getText();

					if(Float.parseFloat(text) >= 0)
						return true;
					else
					{
						TaskDialog.error("This number must be positive.", "Close");
						return false;
					}
			}
		}
		return true;
	}
	
	public boolean shouldYieldFocus(JComponent input)
	{
		return verify(input);
	}
}
