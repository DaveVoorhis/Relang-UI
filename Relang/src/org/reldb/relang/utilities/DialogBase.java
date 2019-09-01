package org.reldb.relang.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class DialogBase<T> extends Dialog {
	protected T result;
	protected Shell shell;
	
	private int shellStyle;
	
	public DialogBase(Shell parent, int shellStyle) {
		super(parent, SWT.NONE);
		this.shellStyle = shellStyle;
	}
	
	public DialogBase(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	protected abstract void create(Shell shell);
	
	public T open() {
		Shell parent = getParent();
		shell = new Shell(parent, shellStyle);
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