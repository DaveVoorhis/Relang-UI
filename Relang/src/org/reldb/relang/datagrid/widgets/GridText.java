package org.reldb.relang.datagrid.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetInterface;
import org.reldb.relang.datagrid.GridWidgetWrapper;
import org.reldb.relang.datagrid.GridWidgetInterface.SpecialInstructions;

public class GridText extends GridWidgetWrapper {
	private boolean changed = false;
	private Text control;
	
	public void notifyChange(GridWidgetInterface.SpecialInstructions specialInstruction) {
		changed = false;
		getNotifier().changed(this, control.getText(), specialInstruction);
	}
	
	public GridText(Datagrid grid, Text control, int rowNumber, int columnNumber) {
		super(grid, control, rowNumber, columnNumber);
		this.control = control;
		
		control.addListener(SWT.Modify, evt -> {
			changed = true;
		});
		control.addListener(SWT.FocusOut, evt -> {
			if (changed)
				notifyChange(GridWidgetInterface.SpecialInstructions.NONE);
		});
		control.addListener(SWT.FocusIn, evt -> {
			changed = false;
		});
		control.addListener(SWT.KeyDown, evt -> {
			if ((evt.keyCode == 13 || evt.keyCode == SWT.ARROW_DOWN) && changed)
				notifyChange(GridWidgetInterface.SpecialInstructions.MOVE_DOWN);
		});
	}
}
