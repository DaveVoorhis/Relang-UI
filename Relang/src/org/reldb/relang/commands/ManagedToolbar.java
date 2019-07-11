package org.reldb.relang.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ManagedToolbar extends ToolBar {    
    
	public ManagedToolbar(Composite parent) {
		super(parent, SWT.NONE);
	}
	
	public void addSeparator() {
		new ToolItem(this, SWT.SEPARATOR);
	}
	
	public void addSeparatorFill() {
		new ToolItem(this, SWT.SEPARATOR);
	}

	public void checkSubclass() {}
}
