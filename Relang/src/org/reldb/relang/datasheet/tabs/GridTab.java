package org.reldb.relang.datasheet.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.relang.datasheet.Datasheet;
import org.reldb.relang.datasheet.SheetPanel;
import org.reldb.relang.datasheet.Tab;

public class GridTab extends Tab {

	public GridTab(Datasheet sheet, String name, boolean create, int style) {
		super(sheet, style);
		var base = sheet.getBase();
		var data = base.open(name, create);
		setText(name);
		var grid = new SheetPanel(getParent(), data);
		setControl(grid);
		addListener(SWT.Dispose, evt -> data.close());
	}
	
	public void populateToolbar(ToolBar toolBar) {}
	
}
