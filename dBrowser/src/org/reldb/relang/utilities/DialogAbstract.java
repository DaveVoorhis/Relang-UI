package org.reldb.relang.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.Action;
import org.reldb.relang.platform.DialogBase;

public abstract class DialogAbstract extends DialogBase {
	
	public DialogAbstract(Shell parent, int shellStyle) {
		super(parent, shellStyle);
	}
	
	public DialogAbstract(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}
	
	public void close() {
		shell.close();
		shell.dispose();
		shell = null;
	}

	/** Override to create the contents of the dialog in shell. */
	protected abstract void createContents();
	
	public void open(Action onClose) {
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents();
		launch(onClose);
	}
	
	public void open() {
		open((Action)null);
	}
}
