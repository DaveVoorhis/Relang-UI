package org.reldb.relang.grid;

public abstract class GridCell implements GridWidgetInterface {

	private int rowNumber;
	private int columnNumber;

	public GridCell(int rowNumber, int columnNumber) {
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
	}
	
	@Override
	public int getRow() {
		return rowNumber;
	}

	@Override
	public int getColumn() {
		return columnNumber;
	}

}
