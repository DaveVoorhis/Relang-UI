package org.reldb.relang.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class DialogBase extends Dialog {
	private Object result;
	private Shell shell;
	
	public DialogBase(Shell parent, int style) {
		super(parent, style);
	}

	public abstract void create(Shell shell);
	
	public Shell getContent() {
		return shell;
	}
	
	public Object open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());
		create(shell);
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}
}