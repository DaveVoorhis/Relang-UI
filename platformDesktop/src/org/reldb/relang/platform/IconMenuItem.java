package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

// A MenuItem with an optional icon that can change size in response to change in Preferences.
public class IconMenuItem extends MenuItem {
	private String imageName;
	
	public IconMenuItem(Menu parentMenu, String text, String imageName, int style, Listener listener) {
		super(parentMenu, style);
		if (text != null)
			setText(text);
		this.imageName = imageName;
		if (listener != null)
			addListener(SWT.Selection, listener);
		reloadImage();
	}
	
	public IconMenuItem(Menu parentMenu, String text, int style) {
		this(parentMenu, text, null, style, null);
	}

	public void reloadImage() {
		if (imageName != null)
			setImage(IconLoader.loadIcon(imageName));
	}
	
	public void checkSubclass() {}
}
