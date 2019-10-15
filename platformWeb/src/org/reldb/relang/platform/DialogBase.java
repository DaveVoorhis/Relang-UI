package org.reldb.relang.platform;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.Action;

public class DialogBase extends Dialog {
	private static final long serialVersionUID = 1L;

	public DialogBase(Shell parent) {
		super(parent);
	}
	
	public DialogBase(Shell parent, int style) {
		super(parent, style);
	}
	
	public void launch(Shell shell, Action closed) {
		open(dlg -> {
			if (closed != null)
				closed.go();
		});
	}

	public void launch(Shell shell) {
		launch(shell, null);
	}
	
}
