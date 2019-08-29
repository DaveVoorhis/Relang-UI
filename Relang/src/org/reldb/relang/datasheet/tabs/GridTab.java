package org.reldb.relang.datasheet.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.relang.datasheet.Datasheet;
import org.reldb.relang.datasheet.Tab;

public class GridTab extends Tab {

	private GridPanel grid;
	
	public GridTab(Datasheet sheet, String name, boolean create, int style) {
		super(sheet, style);
		var base = sheet.getBase();
		var data = base.open(name, create);
		setText(name);
		grid = new GridPanel(getParent(), data);
		setControl(grid);
	}
	
	public void populateToolbar(ToolBar toolBar) {
		var refresh = new ToolItem(toolBar, SWT.NONE);
		refresh.setText("Refresh");
		refresh.addListener(SWT.Selection, evt -> grid.refresh());
	}
	
}
