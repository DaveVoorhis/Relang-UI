package org.reldb.relang.platform;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.version.Version;

public class IconLoader {	
	public static Image loadIcon(String name) {
		return SWTResourceManager.getImage(Version.getResourceDirectory() + name + ".png");		
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = SWTResourceManager.getImageOrNull(Version.getResourceDirectory() + name + "@2x.png");
		if (imgBig == null)
			return SWTResourceManager.getImage(Version.getResourceDirectory() + name + ".png");
		return imgBig;
	}
}
