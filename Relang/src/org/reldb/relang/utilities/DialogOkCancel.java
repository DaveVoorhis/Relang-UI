package org.reldb.relang.utilities;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public abstract class DialogOkCancel<T> extends DialogBase<T> {

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogOkCancel(Shell parent) {
		super(parent);
	}
	
	private void close(T openReturnValue) {
		result = openReturnValue;
		shell.dispose();
	}
	
	/** Create contents of the content panel.
	 * 
	 * @param content - Composite - parent panel.
	 */
	protected abstract void createContent(Composite content);
	
	/** 
	 * Invoked when the OK button is pressed. Return the value that should be returned to the caller of open().
	 * 
	 * @return - value to be returned to the caller of open(), of type T.
	 */
	protected abstract T ok();
	
	/**
	 * Invoked when the CANCEL button is pressed or shell is closed via close button. 
	 * 
	 * By default, returns Null to the caller of open().
	 */
	protected void cancel() {
		close(null);
	}
	
	/**
	 * Create contents of the dialog.
	 */
	protected void create(Shell shell) {
		shell.setLayout(new FormLayout());
		shell.addListener(SWT.CLOSE, evt -> cancel());
		
		var btnCancel = new Button(shell, SWT.NONE);
		var fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, evt -> cancel());
		
		var btnOk = new Button(shell, SWT.NONE);
		var fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnOk.right = new FormAttachment(btnCancel, -6);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		btnOk.addListener(SWT.Selection, evt -> close(ok()));
		
		var contentPanel = new Composite(shell, SWT.NONE);
		var fd_contentPanel = new FormData();
		fd_contentPanel.top = new FormAttachment(0, 10);
		fd_contentPanel.left = new FormAttachment(0, 10);
		fd_contentPanel.right = new FormAttachment(100, -10);
		fd_contentPanel.bottom = new FormAttachment(btnCancel, -10, SWT.TOP);
		contentPanel.setLayoutData(fd_contentPanel);
		createContent(contentPanel);
	}
	
}
