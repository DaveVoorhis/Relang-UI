package org.reldb.relang.grid.nebula;

import java.util.HashMap;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Datagrid extends Grid {
	
	private int focusRow = 0;
	private int focusColumn = 0;
	
	private HashMap<Integer, HashMap<Integer, Control>> controls = new HashMap<Integer, HashMap<Integer, Control>>();
	
	public void checkSubclass() {}

	public Datagrid(Composite parent, int style) {
		super(parent, style);
	}
	
	public void focusOnCell(int rowNumber, int columnNumber) {
		setFocusColumn(getColumn(columnNumber));
		var gridItem = getItem(rowNumber);
		setFocusItem(gridItem);
		setCellSelection(new Point(columnNumber, rowNumber));
		var control = controls.get(rowNumber).get(columnNumber);
		if (!control.setFocus() && !control.forceFocus())
			getParent().forceFocus();
		focusRow = rowNumber;
		focusColumn = columnNumber;
	}

	public void setupControl(Control control, int rowNumber, int columnNumber) {
		var row = controls.get(rowNumber);
		if (row == null) {
			row = new HashMap<Integer, Control>();
			controls.put(rowNumber, row);
		}
		row.put(columnNumber, control);
		control.addListener(SWT.MouseDown, evt -> {
			if (rowNumber == focusRow && columnNumber == focusColumn)
				return;
			focusOnCell(rowNumber, columnNumber);
		});
		if (control instanceof CellComposite) {
			for (Control ctl: ((CellComposite)control).getAllChildren())
				ctl.addListener(SWT.MouseDown, evt -> {
					if (rowNumber == focusRow && columnNumber == focusColumn)
						return;
					focusOnCell(rowNumber, columnNumber);
				});				
		}
		control.addListener(SWT.KeyDown, evt -> {
			if (evt.keyCode == SWT.TAB) {
				if ((evt.stateMask & SWT.SHIFT) == 0)
					traverseNext();
				else
					traversePrevious();
				evt.doit = false;
			} else if (evt.keyCode == SWT.ARROW_DOWN || evt.keyCode == 13) {
				traverseDown();
				evt.doit = false;
			} else if (evt.keyCode == SWT.ARROW_UP) {
				traverseUp();
				evt.doit = false;
			}
		});
		control.addListener(SWT.Traverse, evt -> {
			// disable standard TAB key traversal
			if (evt.detail == SWT.TRAVERSE_TAB_NEXT || evt.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				evt.doit = false;
		});	
	}
	
	public void traverseNext() {
		focusColumn++;
		if (focusColumn >= getColumnCount())
			focusColumn = 0;
		focusOnCell(focusRow, focusColumn);		
	}
	
	public void traversePrevious() {
		focusColumn--;
		if (focusColumn < 0)
			focusColumn = getColumnCount() - 1;		
		focusOnCell(focusRow, focusColumn);		
	}

	public void traverseDown() {
		focusRow++;
		if (focusRow >= getItemCount())
			focusRow = getItemCount() - 1;
		focusOnCell(focusRow, focusColumn);
	}

	public void traverseUp() {
		focusRow--;
		if (focusRow < 0)
			focusRow = 0;
		focusOnCell(focusRow, focusColumn);
	}
}
