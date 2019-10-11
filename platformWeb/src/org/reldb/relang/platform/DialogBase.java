package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

public abstract class DialogBase<T> extends Dialog {
	private static final long serialVersionUID = 1L;

	protected T result;
	private int shellStyle;
	
	public DialogBase(Shell parent, int shellStyle) {
		super(parent, SWT.NONE);
		this.shellStyle = shellStyle;
	}
	
	public DialogBase(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	protected abstract void create(Shell shell);
	
	/** This is invoked with the dialog closes, and may be overridden to receive result. */
	protected void closed(T result) {
	}
	
	public void open() {
		Shell parent = getParent();
		shell = new Shell(parent, shellStyle);
		create(shell);
		shell.setText(getText());
		shell.open();
		open(dlg -> closed(result));
	}
}
