package org.reldb.relang.core.datasheet;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.ToolBar;

public class Tab extends CTabItem {

	public Tab(CTabFolder folder, int style) {
		super(folder, style);
	}

	public void populateToolbar(ToolBar toolBar) {}
}
