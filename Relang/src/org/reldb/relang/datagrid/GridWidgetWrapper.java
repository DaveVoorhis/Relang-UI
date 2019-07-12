package org.reldb.relang.datagrid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

public class GridWidgetWrapper extends GridCell {

	private Control control;
	
	public GridWidgetWrapper(Datagrid grid, Control control, int rowNumber, int columnNumber) {
		super(rowNumber, columnNumber);
		this.control = control;
		
		control.addListener(SWT.MouseDown, evt -> {
			if (rowNumber == grid.getFocusRow() && columnNumber == grid.getFocusColumn())
				return;
			grid.focusOnCell(rowNumber, columnNumber);
		});
		control.addListener(SWT.MouseDown, evt -> {
			if (rowNumber == grid.getFocusRow() && columnNumber == grid.getFocusColumn())
				return;
			grid.focusOnCell(rowNumber, columnNumber);
		});
		control.addListener(SWT.KeyDown, evt -> {
			if (evt.keyCode == SWT.TAB) {
				if ((evt.stateMask & SWT.SHIFT) == 0)
					grid.traverseNext();
				else
					grid.traversePrevious();
				evt.doit = false;
			} else if (evt.stateMask == 0 && (evt.keyCode == SWT.ARROW_DOWN || evt.keyCode == 13)) {
					grid.traverseDown();
					evt.doit = false;
			} else if (evt.stateMask == 0 && (evt.keyCode == SWT.ARROW_UP)) {
					grid.traverseUp();
					evt.doit = false;
			}
		});
		control.addListener(SWT.Traverse, evt -> {
			// disable standard TAB key traversal
			if (evt.detail == SWT.TRAVERSE_TAB_NEXT || evt.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				evt.doit = false;
		});
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public boolean focus() {
		if (!control.setFocus())
			return control.forceFocus();
		return true;
	}

}
