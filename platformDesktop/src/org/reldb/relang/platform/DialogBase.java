package org.reldb.relang.platform;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.Action;

public class DialogBase extends Dialog {
	
	protected Shell shell;
	
	public DialogBase(Shell parent) {
		super(parent);
	}
	
	public DialogBase(Shell parent, int style) {
		super(parent, style);
	}

	protected void launch(Action closed) {
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		if (closed != null)
			closed.go();
	}

	protected void launch() {
		launch(null);
	}
	
}
