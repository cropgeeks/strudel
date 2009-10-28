// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.io;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.components.*;

public class FileDropAdapter extends DropTargetAdapter
{
	private WinMain winMain;

	public FileDropAdapter(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void drop(DropTargetDropEvent dtde)
	{
		Transferable t = dtde.getTransferable();

		try
		{
			DataFlavor[] dataFlavors = t.getTransferDataFlavors();

			dtde.acceptDrop(DnDConstants.ACTION_COPY);

			for (int i = 0; i < dataFlavors.length; i++)
			{
				if (dataFlavors[i].isFlavorJavaFileListType())
				{
					List<?> list = (List<?>) t.getTransferData(dataFlavors[i]);

					//open the file
					MapViewer.winMain.fatController.dragAndDropDataLoad = true;
					DataLoadUtils.loadDataInThread(list.get(0).toString());
					
					dtde.dropComplete(true);
					return;
				}
			}

			dtde.dropComplete(true);
		}
		catch (Exception e) {}
	}
}