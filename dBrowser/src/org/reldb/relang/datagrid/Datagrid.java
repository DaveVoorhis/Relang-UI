package org.reldb.relang.datagrid;

import java.util.HashMap;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.widgets.Composite;
import org.reldb.relang.platform.GridHelper;

public class Datagrid {
	
	private int focusRow = 0;
	private int focusColumn = 0;
	
	private HashMap<Integer, HashMap<Integer, GridWidgetInterface>> controls = new HashMap<Integer, HashMap<Integer, GridWidgetInterface>>();

	private Grid grid;
	private Composite parent;
	
	public Datagrid(Composite parent, int style) {
		this.parent = parent;
		grid = new Grid(parent, style);
	}
	
	public void focusOnCell(int rowNumber, int columnNumber) {
		GridHelper.setFocusItem(grid, rowNumber, columnNumber);
		var row = controls.get(rowNumber);
		if (row != null) {
			var control = row.get(columnNumber);
			if (control == null || !control.focus())
				parent.forceFocus();
		} else
			parent.forceFocus();
		focusRow = rowNumber;
		focusColumn = columnNumber;
		grid.showSelection();
	}

	public void setupControl(GridWidgetInterface control) {
		var row = controls.get(control.getRow());
		if (row == null) {
			row = new HashMap<Integer, GridWidgetInterface>();
			controls.put(control.getRow(), row);
		}
		row.put(control.getColumn(), control);
	}
	
	public void traverseNext() {
		focusColumn++;
		if (focusColumn >= grid.getColumnCount())
			focusColumn = 0;
		focusOnCell(focusRow, focusColumn);		
	}
	
	public void traversePrevious() {
		focusColumn--;
		if (focusColumn < 0)
			focusColumn = grid.getColumnCount() - 1;		
		focusOnCell(focusRow, focusColumn);		
	}

	public void traverseDown() {
		focusRow++;
		if (focusRow >= grid.getItemCount())
			focusRow = grid.getItemCount() - 1;
		focusOnCell(focusRow, focusColumn);
	}

	public void traverseUp() {
		focusRow--;
		if (focusRow < 0)
			focusRow = 0;
		focusOnCell(focusRow, focusColumn);
	}
	
	public int getFocusRow() {
		return focusRow;
	}
	
	public int getFocusColumn() {
		return focusColumn;
	}
	
	public Grid getGrid() {
		return grid;
	}

	public void dispose() {
		grid.dispose();
	}
}
