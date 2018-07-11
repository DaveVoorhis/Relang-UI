package org.reldb.relang.reli;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

public class DecoratedMenuItem extends MenuItem {
	
	/** MenuItem with text, accelerator, image, and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Image image, Listener listener) {
		super(parentMenu, SWT.PUSH);
		if (text != null)
			setText(text);
		if (accelerator != 0)
			setAccelerator(accelerator);
		if (image != null)
			setImage(image);
		if (listener != null)
			addListener(SWT.Selection, listener);
	}

	/** MenuItem with text, accelerator, and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Listener listener) {
		this(parentMenu, text, accelerator, null, listener);
	}

	/** MenuItem with text, accelerator, and image */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Image image) {
		this(parentMenu, text, accelerator, image, null);
	}
	
	public void checkSubclass() {}
}
