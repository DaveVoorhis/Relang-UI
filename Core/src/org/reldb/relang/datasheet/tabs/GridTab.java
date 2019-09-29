package org.reldb.relang.datasheet.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.relang.data.Data;
import org.reldb.relang.datasheet.Tab;

public class GridTab extends Tab {

	private GridPanel grid;
	
	public GridTab(CTabFolder folder, Data<?, ?> data, int style) {
		super(folder, style);
		grid = new GridPanel(getParent(), data);
		setText(data.getName());
		setControl(grid);
	}
	
	public void populateToolbar(ToolBar toolBar) {
		var refresh = new ToolItem(toolBar, SWT.NONE);
		refresh.setText("Refresh");
		refresh.addListener(SWT.Selection, evt -> grid.refresh());
	}
	
}
