package org.reldb.relang.grid.nebula;

import java.util.HashMap;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

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
		grid.setFocusColumn(grid.getColumn(columnNumber));
		var gridItem = grid.getItem(rowNumber);
		grid.setFocusItem(gridItem);
		grid.setCellSelection(new Point(columnNumber, rowNumber));
		var control = controls.get(rowNumber).get(columnNumber);
		if (!control.focus())
			parent.forceFocus();
		focusRow = rowNumber;
		focusColumn = columnNumber;
	}

	public void setupControl(GridWidgetInterface control, int rowNumber, int columnNumber) {
		var row = controls.get(rowNumber);
		if (row == null) {
			row = new HashMap<Integer, GridWidgetInterface>();
			controls.put(rowNumber, row);
		}
		row.put(columnNumber, control);
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
}
