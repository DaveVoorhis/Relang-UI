package org.reldb.relang.datasheet.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.utilities.DialogOkCancel;

public class RenameDataDialog extends DialogOkCancel {

	private RenameDataPanel renameData;
	private String name;
	
	public RenameDataDialog(Shell shell, String name) {
		super(shell);
		this.name = name;
		setText("Rename");
	}
	
	@Override
	protected void createContent(Composite content) {
		renameData = new RenameDataPanel(content, SWT.NONE);
		renameData.textNewName.setText(name);
		shell.setSize(400, 150);
	}

	public String getNewName() {
		return renameData.textNewName.getText().trim();
	}
	
}
