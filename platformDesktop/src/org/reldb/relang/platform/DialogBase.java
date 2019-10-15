package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.EventListener;

public abstract class DialogBase<T> extends Dialog {

	private EventListener<T> eventListener;
	
	protected T result;
	protected Shell shell;

	public DialogBase(Shell parent, EventListener<T> EventListener) {
		super(parent, SWT.NONE);
		this.eventListener = EventListener;
	}
	
	public void launch() {
		Display display = getParent().getDisplay();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		if (eventListener != null)
			eventListener.notify(result);
	}
}
