package org.reldb.relang.datasheet.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

public class PanelRenameData extends Composite {
	public Text textNewName;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PanelRenameData(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblNewName = new Label(this, SWT.NONE);
		FormData fd_lblNewName = new FormData();
		fd_lblNewName.top = new FormAttachment(0, 10);
		fd_lblNewName.left = new FormAttachment(0, 10);
		lblNewName.setLayoutData(fd_lblNewName);
		lblNewName.setText("New name:");
		
		textNewName = new Text(this, SWT.BORDER);
		FormData fd_textNewName = new FormData();
		fd_textNewName.top = new FormAttachment(0, 10);
		fd_textNewName.left = new FormAttachment(lblNewName, 6);
		fd_textNewName.right = new FormAttachment(100, -10);
		textNewName.setLayoutData(fd_textNewName);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
