package org.reldb.relang;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.preferences.PreferencePageGeneral;
import org.reldb.relang.preferences.Preferences;
import org.reldb.relang.version.Version;

public class IconLoader {
	
	public static Image loadIcon(String name) {
		boolean largeIcons = Preferences.getPreferenceBoolean(PreferencePageGeneral.LARGE_ICONS);
		return (largeIcons) ? loadIconLarge(name) : loadIconNormal(name);
	}
	
	static int scaleFactor;
	
	private static ImageData grabzoom(int zoom, Image image) {
		scaleFactor = zoom;
		return image.getImageData();
	}
	
	public static int getDPIScaling() {
		Image imgRaw = SWTResourceManager.getMissingImage();
		new Image(Display.getCurrent(), (ImageDataProvider)zoom -> grabzoom(zoom, imgRaw));
		return scaleFactor;
	}
	
	public static Image loadIconNormal(String name) {
		return new Image(Display.getCurrent(), (ImageDataProvider)zoom -> {
			switch (zoom) {
			case 200:
				Image imgRaw = SWTResourceManager.getImageOrNull(Version.getResourceDirectory() + name + "@2x.png");
				if (imgRaw == null) {
					imgRaw = SWTResourceManager.getImageOrNull(Version.getResourceDirectory() + name + ".png");
					if (imgRaw == null)
						imgRaw = SWTResourceManager.getImage(Version.getResourceDirectory() + "noimage@2x.png");
				}
				return imgRaw.getImageData();
			default:
				imgRaw = SWTResourceManager.getImageOrNull(Version.getResourceDirectory() + name + ".png");
				if (imgRaw == null)
					imgRaw = SWTResourceManager.getImage(Version.getResourceDirectory() + "noimage.png");
				return imgRaw.getImageData();
			}
		});
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = SWTResourceManager.getImageOrNull(Version.getResourceDirectory() + name + "@2x.png");
		if (imgBig == null)
			return SWTResourceManager.getImage(Version.getResourceDirectory() + name + ".png");
		return imgBig;
	}
}
