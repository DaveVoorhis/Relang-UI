package org.reldb.relang.grid;

import org.eclipse.swt.widgets.Composite;

public abstract class CellComposite extends Composite implements GridWidgetInterface {

	private int rowNumber;
	private int columnNumber;
	
	public CellComposite(Composite parent, int style, int rowNumber, int columnNumber) {
		super(parent, style);
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
		
	public void checkSubclass() {}
}
