package org.reldb.relang.reli;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.ResourceManager;

public class IconLoader {
		
	private final static String IconPath = "org/reldb/relang/reli/resources/";
	
	public static Image loadIcon(String name) {
		boolean largeIcons = Preferences.userRoot().getBoolean(PreferencePageGeneral.LARGE_ICONS, false);
		return (largeIcons) ? loadIconLarge(name) : loadIconNormal(name);
	}
	
	public static Image loadIconNormal(String name) {
		Image imgBigRaw = ResourceManager.getImage(IconPath + name + "@2x.png");
		Image imgSmallRaw = ResourceManager.getImage(IconPath + name + ".png");
		if (imgBigRaw == null && imgSmallRaw == null) {
			imgBigRaw = ResourceManager.getImage(IconPath + "noimage@2x.png");
			imgSmallRaw = ResourceManager.getImage(IconPath + "noimage.png");
		}
		Image imgLarge = (imgBigRaw == null) ? imgSmallRaw : imgBigRaw;
		Image imgSmall = imgSmallRaw;
		final ImageDataProvider imageDataProvider = zoom -> {
			switch (zoom) {
			case 200:
				return imgLarge.getImageData();
			default:
				return imgSmall.getImageData();
			}
		};
		// TODO - should cache image in ResourceManager here!
		return new Image(Display.getCurrent(), imageDataProvider);
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = ResourceManager.getImage(IconPath + name + "@2x.png");
		if (imgBig == null)
			return ResourceManager.getImage(IconPath + name + ".png");
		return imgBig;
	}
}
