package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import javax.media.j3d.*;

public class Utils
{
	public static void setChildRelatedCapabilities(TransformGroup tg)
	{
		tg.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		
	}
	 
	public static void setDetachRelatedCapabilities(BranchGroup bg)
	{
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	}
}
