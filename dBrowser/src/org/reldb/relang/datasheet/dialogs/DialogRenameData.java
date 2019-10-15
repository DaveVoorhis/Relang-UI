package org.reldb.relang.datasheet.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.Action;
import org.reldb.relang.utilities.DialogOkCancel;

public class DialogRenameData extends DialogOkCancel {

	private PanelRenameData renameData;
	private String name;
	
	public DialogRenameData(Shell shell, String name, Action onOk) {
		super(shell, onOk);
		this.name = name;
	}
	
	@Override
	protected void createContent(Composite content) {
		setText("Rename");
		renameData = new PanelRenameData(content, SWT.NONE);
		renameData.textNewName.setText(name);
		shell.setSize(400, 150);
	}

	public String getNewName() {
		return renameData.textNewName.getText().trim();
	}
	
	public void launch() {
		createContents();
		shell.open();
		launch(shell);
	}
	
	public void open() {
		shell = new Shell(getParent(), getStyle());
		createContents();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
}
