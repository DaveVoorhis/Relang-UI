package org.reldb.relang.platform;

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

	/** This is invoked to configure the shell. */
	protected abstract void create(Shell shell);
	
	/** This is invoked with the dialog closes, and may be overridden to receive result. */
	protected void closed(T result) {}
	
	public void open() {
		Shell parent = getParent();
		shell = new Shell(parent, shellStyle);
		create(shell);
		shell.setText(getText());
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		closed(result);
	}
}
