package org.reldb.relang.datasheet.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.utilities.DialogOkCancel;

public class DialogRenameData extends DialogOkCancel {

	private PanelRenameData renameData;
	private String name;
	private boolean cancelled = false;
	
	public DialogRenameData(Shell shell, String name) {
		super(shell);
		this.name = name;
	}
	
	@Override
	protected void cancel() {
		cancelled = true;
		super.cancel();
	}
	
	@Override
	protected void createContent(Composite content) {
		setText("Rename");
		renameData = new PanelRenameData(content, SWT.NONE);
		renameData.textNewName.setText(name);
		shell.setSize(400, 150);
	}

	public String getNewName() {
		if (cancelled)
			return null;
		return renameData.textNewName.getText().trim();
	}
	
}
