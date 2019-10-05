package org.reldb.relang.datagrid;

public abstract class GridCell implements GridWidgetInterface {

	private int rowNumber;
	private int columnNumber;
	private Notifier notifier;

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
	
	@Override
	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}
	
	@Override
	public Notifier getNotifier() {
		return notifier;
	}

}
