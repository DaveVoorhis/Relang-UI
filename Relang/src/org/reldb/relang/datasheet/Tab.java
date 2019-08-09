package org.reldb.relang.datasheet;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.ToolBar;

public class Tab extends CTabItem {

	public Tab(CTabFolder parent, int style) {
		super(parent, style);
	}

	public void populateToolbar(ToolBar toolBar) {}
}
