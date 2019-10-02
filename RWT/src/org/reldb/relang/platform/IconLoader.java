package org.reldb.relang.platform;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

public class IconLoader {	
	public static Image loadIcon(String name) {
		return SWTResourceManager.getImage(ResourceLocator.getResourceDirectory() + name + ".png");		
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = SWTResourceManager.getImageOrNull(ResourceLocator.getResourceDirectory() + name + "@2x.png");
		if (imgBig == null)
			return SWTResourceManager.getImage(ResourceLocator.getResourceDirectory() + name + ".png");
		return imgBig;
	}
}
