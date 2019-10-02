package org.reldb.relang.platform;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

public class IconLoader {
	
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
	
	public static Image loadIcon(String name) {
		return new Image(Display.getCurrent(), (ImageDataProvider)zoom -> {
			switch (zoom) {
			case 200:
				Image imgRaw = SWTResourceManager.getImageOrNull(ResourceLocator.getResourceDirectory() + name + "@2x.png");
				if (imgRaw == null) {
					imgRaw = SWTResourceManager.getImageOrNull(ResourceLocator.getResourceDirectory() + name + ".png");
					if (imgRaw == null)
						imgRaw = SWTResourceManager.getImage(ResourceLocator.getResourceDirectory() + "noimage@2x.png");
				}
				return imgRaw.getImageData();
			default:
				imgRaw = SWTResourceManager.getImageOrNull(ResourceLocator.getResourceDirectory() + name + ".png");
				if (imgRaw == null)
					imgRaw = SWTResourceManager.getImage(ResourceLocator.getResourceDirectory() + "noimage.png");
				return imgRaw.getImageData();
			}
		});
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = SWTResourceManager.getImageOrNull(ResourceLocator.getResourceDirectory() + name + "@2x.png");
		if (imgBig == null)
			return SWTResourceManager.getImage(ResourceLocator.getResourceDirectory() + name + ".png");
		return imgBig;
	}
}
