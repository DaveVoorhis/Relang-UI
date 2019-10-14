package org.reldb.relang.platform;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

// A MenuItem with an optional icon.
public class PlatformMenuItem extends MenuItem {

	public PlatformMenuItem(Menu parent, int style) {
		super(parent, style);
	}
	
	public void checkSubclass() {}
}
