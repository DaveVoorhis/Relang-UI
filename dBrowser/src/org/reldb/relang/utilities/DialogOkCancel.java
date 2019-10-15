package org.reldb.relang.utilities;

import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.dengine.utilities.Action;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;

public abstract class DialogOkCancel extends DialogAbstract {
	
	private Action onOk;
	protected Shell shell;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogOkCancel(Shell parent, Action onOk) {
		super(parent);
		this.onOk = onOk;
	}
	
	private void close() {
		shell.close();
		shell.dispose();
	}
	
	/** Create contents of the content panel.
	 * 
	 * @param content - Composite - parent panel.
	 */
	protected abstract void createContent(Composite content);
	
	/**
	 * Create contents of the dialog.
	 */
	protected void createContents() {
		shell.setLayout(new FormLayout());
		shell.addListener(SWT.CLOSE, evt -> close());
		
		System.out.println("Shell = " + shell);
		var btnCancel = new Button(shell, SWT.BORDER);
		var fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, evt -> close());
		
		var btnOk = new Button(shell, SWT.NONE);
		var fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnOk.right = new FormAttachment(btnCancel, -6);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		btnOk.addListener(SWT.Selection, evt -> {
			onOk.go();
			close();
		});
		
		var contentPanel = new Composite(shell, SWT.NONE);
		var fd_contentPanel = new FormData();
		fd_contentPanel.top = new FormAttachment(0, 10);
		fd_contentPanel.left = new FormAttachment(0, 10);
		fd_contentPanel.right = new FormAttachment(100, -10);
		fd_contentPanel.bottom = new FormAttachment(btnCancel, -10, SWT.TOP);
		contentPanel.setLayoutData(fd_contentPanel);
		contentPanel.setLayout(new FillLayout());
		createContent(contentPanel);
		
		shell.setDefaultButton(btnOk);
	}
	
	public Shell obtainShell() {
		return new Shell(getParent(), getStyle());
	}

	public void launch() {
		createContents();
		shell.open();
		launch(shell);
	}
	
	public void open() {
		shell = obtainShell();
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
