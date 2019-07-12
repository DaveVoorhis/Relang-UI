package org.reldb.relang.grid;

import org.eclipse.swt.widgets.Control;

public interface GridWidgetInterface {
	public Control getControl();
	public boolean focus();
	public int getRow();
	public int getColumn();
}
