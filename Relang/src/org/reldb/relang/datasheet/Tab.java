package org.reldb.relang.datasheet;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.ToolBar;

public class Tab extends CTabItem {

	public Tab(Datasheet sheet, int style) {
		super(sheet.getTabFolder(), style);
	}

	public void populateToolbar(ToolBar toolBar) {}
}
