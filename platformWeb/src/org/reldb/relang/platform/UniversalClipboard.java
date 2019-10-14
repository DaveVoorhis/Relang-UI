package org.reldb.relang.platform;

import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

public class UniversalClipboard {

	public UniversalClipboard(Display current) {
	}

	public Object getContents(TextTransfer textTransfer) {
		return null;
	}

	public Object getContents(HTMLTransfer htmlTransfer) {
		return null;
	}

	public void dispose() {
	}

	public static boolean isClipboardAvailable() {
		return false;
	}
	
}
