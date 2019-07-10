package org.reldb.relang.grid.nebula;

import org.eclipse.swt.widgets.Composite;

public abstract class CellComposite extends Composite implements GridWidgetInterface {

	public CellComposite(Composite parent, int style) {
		super(parent, style);
	}
		
	public void checkSubclass() {}

}
