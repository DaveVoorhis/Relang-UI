package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

public abstract class DialogBase<T> extends Dialog {
	private static final long serialVersionUID = 1L;

	protected T result;
	
	public DialogBase(Shell parent) {
		super(parent, SWT.NONE);
	}

	protected abstract void closed(T result);
	
	protected void launch() {
		open(dlg -> closed(result));
	}
}
