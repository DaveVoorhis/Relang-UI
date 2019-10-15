package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.EventListener;

public abstract class DialogBase<T> extends Dialog {
	private static final long serialVersionUID = 1L;

	private EventListener<T> eventListener;

	protected T result;
	
	public DialogBase(Shell parent, EventListener<T> EventListener) {
		super(parent, SWT.NONE);
		this.eventListener = EventListener;
	}
	
	protected void launch() {
		open(dlg -> {
			if (eventListener != null)
				eventListener.notify(result);
		});
	}
}
