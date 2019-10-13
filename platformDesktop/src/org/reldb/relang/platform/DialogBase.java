package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class DialogBase<T> extends Dialog {

	protected T result;	
	protected Shell shell;

	public DialogBase(Shell parent) {
		super(parent, SWT.NONE);
	}

	protected abstract void closed(T result);
	
	public void launch() {
		Display display = getParent().getDisplay();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		closed(result);
	}
}
