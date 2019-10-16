package org.reldb.relang.platform;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DialogBase extends Dialog {
	private static final long serialVersionUID = 1L;

	public DialogBase(Shell parent) {
		super(parent);
	}
	
	public DialogBase(Shell parent, int style) {
		super(parent, style);
	}
	
	protected void launch() {
		open(null);
		Rectangle displaySize =  Display.getCurrent().getBounds();
		Point position = new Point((displaySize.width - shell.getSize().x) / 2 + displaySize.x, (displaySize.height - shell.getSize().y) / 2 + displaySize.y);
		shell.setLocation(position);
	}
	
}
