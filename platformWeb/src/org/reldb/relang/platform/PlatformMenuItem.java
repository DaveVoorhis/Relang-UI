package org.reldb.relang.platform;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

// A MenuItem with an optional icon.
public class PlatformMenuItem extends MenuItem {
	private static final long serialVersionUID = 1L;

	public PlatformMenuItem(Menu parent, int style) {
		super(parent, style);
	}

	/** No-op under RWT/RAP */
	public void setToolTip(String text) {}
	
	public void checkSubclass() {}
}
