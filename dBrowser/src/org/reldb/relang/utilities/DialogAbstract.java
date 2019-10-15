package org.reldb.relang.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.platform.DialogBase;

public abstract class DialogAbstract extends DialogBase {
	private Point size;
	
	public DialogAbstract(Shell parent, int shellStyle) {
		super(parent);
	}
	
	public DialogAbstract(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	/** Create the contents of the dialog in shell. */
	protected abstract void createContents();

	protected void setSize(Point size) {
		this.size = size;
	}
	
	protected Point getSize() {
		return size;
	}

}
