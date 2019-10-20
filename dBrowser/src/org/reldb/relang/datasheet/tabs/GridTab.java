package org.reldb.relang.datasheet.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.relang.commands.CommandActivator;
import org.reldb.relang.commands.Commands.Do;
import org.reldb.relang.datasheet.Tab;
import org.reldb.relang.dengine.data.Data;

public class GridTab extends Tab {

	private GridPanel grid;
	private Data<?, ?> data;
	
	public GridTab(CTabFolder folder, Data<?, ?> data, int style) {
		super(folder, style);
		this.data = data;
		grid = new GridPanel(getParent(), data);
		setText(data.getName());
		setControl(grid);
	}
	
	public void populateToolbar(ToolBar toolBar) {
		new CommandActivator(Do.Refresh, toolBar, "reload", SWT.NONE, "Refresh", evt -> grid.refresh());
		new CommandActivator(Do.AddColumn, toolBar, "addcolumn", SWT.NONE, "Add column", evt -> grid.addColumn()).setEnabled(data.isExtendable());
	}
	
}
