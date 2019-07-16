package org.reldb.relang.datagrid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

public class GridText extends GridWidgetWrapper {
	public GridText(Datagrid grid, Text control, int rowNumber, int columnNumber) {
		super(grid, control, rowNumber, columnNumber);
		control.addListener(SWT.Modify, evt -> getNotifier().changed(this, control.getText()));
	}
}
