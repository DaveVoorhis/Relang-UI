package org.reldb.relang.core.datagrid;

import org.eclipse.swt.widgets.Composite;

public abstract class CellComposite extends Composite implements GridWidgetInterface {
	private static final long serialVersionUID = 1L;
	
	private int rowNumber;
	private int columnNumber;
	private Notifier notifier;
	
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
		
	@Override
	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}
	
	@Override
	public Notifier getNotifier() {
		return notifier;
	}
	
	public void checkSubclass() {}
}
