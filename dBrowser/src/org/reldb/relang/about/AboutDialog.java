package org.reldb.relang.about;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.reldb.relang.utilities.DialogOk;

public class AboutDialog extends DialogOk {
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent) {
		super(parent);
		setText("About");
	}

	@Override
	protected void createContent(Composite content) {
		new AboutDialogPanel(content, SWT.NONE);
		shell.setSize(450, 300);
	}
	
}
