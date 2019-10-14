package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.reldb.relang.dengine.utilities.Action;

public abstract class MessageBoxLauncher {
	public static void open(MessageBox dialog, Action act) {
		int result = dialog.open();
		if (act != null && (result == SWT.OK || result == SWT.YES))
			act.go();
	}
}
