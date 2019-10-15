package org.reldb.relang.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.EventListener;
import org.reldb.relang.platform.DialogBase;

public abstract class DialogAbstract<T> extends DialogBase<T> {
	private int shellStyle;
	
	public DialogAbstract(Shell parent, int shellStyle, EventListener<T> actionResult) {
		super(parent, actionResult);
		this.shellStyle = shellStyle;
	}
	
	public DialogAbstract(Shell parent, EventListener<T> actionResult) {
		this(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL, actionResult);
	}

	/** Create the contents of the dialog in shell. */
	protected abstract void create(Shell shell);
	
	/** Create, open, and and assign a new dialog Shell to shell. */
	private void newShell() {
		shell = new Shell(getParent(), shellStyle);
		create(shell);
		shell.setText(getText());
		shell.open();
	}

	public void open() {
		newShell();
		launch();
	}

}
