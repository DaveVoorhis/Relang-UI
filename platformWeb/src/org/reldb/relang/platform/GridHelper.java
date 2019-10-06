package org.reldb.relang.platform;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;

public class GridHelper {
	
	public static void setFocusItem(Grid grid, int rowNumber, int columnNumber) {
		// grid.setFocusColumn(grid.getColumn(columnNumber));
		var gridItem = grid.getItem(rowNumber);
		grid.setFocusItem(gridItem);
		// grid.setCellSelection(new Point(columnNumber, rowNumber));		
	}
	
	public static void setupGrid(Grid grid) {
		grid.setLinesVisible(true);
		/*
		grid.setCellSelectionEnabled(true);
		grid.setRowsResizeable(true);
		grid.setRowHeaderVisible(true);
		grid.setColumnScrolling(true);
		grid.setRowHeaderVisible(true);
		*/
	}
	
	public static int getRowIndex(GridItem row) {
		// return row.getRowIndex();
		return 0;
	}
	
}
