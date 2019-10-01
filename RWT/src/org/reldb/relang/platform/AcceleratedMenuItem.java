package org.reldb.relang.platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class AcceleratedMenuItem extends IconMenuItem {
	private static final long serialVersionUID = 1L;

	/** MenuItem with text, accelerator, image, style and Listener. */
	public AcceleratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName, int style, Listener listener) {
		super(parentMenu, text, imageName, style, listener);
		if (accelerator != 0)
			setAccelerator(accelerator);
	}

	/** MenuItem with text, accelerator, image and Listener. */
	public AcceleratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName, Listener listener) {
		this(parentMenu, text, accelerator, imageName, SWT.PUSH, listener);
	}
	
	/** MenuItem with text, accelerator, image, and explicit style */
	public AcceleratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName, int style) {
		this(parentMenu, text, accelerator, imageName, style, null);
	}
	
	/** MenuItem with text, accelerator, and Listener. */
	public AcceleratedMenuItem(Menu parentMenu, String text, int accelerator, Listener listener) {
		this(parentMenu, text, accelerator, null, listener);
	}
	
	/** MenuItem with text, accelerator, and image */
	public AcceleratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName) {
		this(parentMenu, text, accelerator, imageName, null);
	}

	public boolean canExecute() {
		return true;
	}

	public void setToolTipText(String toolTipText) {
		// No-op in RWT
	}
	
	public void checkSubclass() {}
}
