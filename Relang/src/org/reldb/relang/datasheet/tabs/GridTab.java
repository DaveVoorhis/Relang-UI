package org.reldb.relang.datasheet.tabs;

import org.eclipse.swt.widgets.ToolBar;
import org.reldb.relang.datasheet.Datasheet;
import org.reldb.relang.datasheet.Tab;

public class GridTab extends Tab {

	public GridTab(Datasheet sheet, int style) {
		super(sheet, style);
	}

	public void populateToolbar(ToolBar toolBar) {}
	
	/*
	Data gridData;
	if (base.exists(gridName)) {
		gridData = base.open(gridName);
	} else {
		gridData = base.create(gridName);
		gridData.setColumnName(0, "A");
		gridData.setColumnName(1, "B");
		gridData.setColumnName(2, "C");
		gridData.appendRow();
		gridData.appendRow();
		gridData.appendRow();
	}
	new SheetPanel(newShell, gridData);
	*/
	
}
