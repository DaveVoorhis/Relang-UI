package org.reldb.relang.datasheet.tabs;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.relang.datasheet.Datasheet;
import org.reldb.relang.datasheet.SheetPanel;
import org.reldb.relang.datasheet.Tab;

public class GridTab extends Tab {

	public GridTab(Datasheet sheet, TreeItem item, int style) {
		super(sheet, style);
		var dataName = item.getText();
		var base = sheet.getBase();
		var gridData = base.open(dataName);
		setText(dataName);
		setControl(new SheetPanel(getParent(), gridData));
	}

	public void populateToolbar(ToolBar toolBar) {}
	
}
