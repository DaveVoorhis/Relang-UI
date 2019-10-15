package org.reldb.relang.about;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.platform.IconLoader;
import org.reldb.relang.utilities.DialogAbstract;
import org.reldb.relang.version.Version;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class AboutDialog extends DialogAbstract {

	private Shell shell;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		setText("About");
	}

	/**
	 * Create contents of the dialog.
	 */
	protected void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Button btnClose = new Button(shell, SWT.NONE);
		FormData fd_btnClose = new FormData();
		fd_btnClose.left = new FormAttachment(100, -104);
		fd_btnClose.right = new FormAttachment(100, -10);
		fd_btnClose.bottom = new FormAttachment(100, -10);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText("Close");
		btnClose.addListener(SWT.Selection, evt -> shell.dispose());
		
		Label lblNewLabel = new Label(shell, SWT.BORDER);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setImage(IconLoader.loadIcon("Candle32"));
		
		Label lblNewTitle = new Label(shell, SWT.NONE);
		FormData fd_lblNewTitle = new FormData();
		fd_lblNewTitle.bottom = new FormAttachment(0, 42);
		fd_lblNewTitle.right = new FormAttachment(0, 440);
		fd_lblNewTitle.top = new FormAttachment(0, 10);
		fd_lblNewTitle.left = new FormAttachment(0, 48);
		lblNewTitle.setLayoutData(fd_lblNewTitle);
		lblNewTitle.setFont(SWTResourceManager.getFont(".AppleSystemUIFont", 16, SWT.NORMAL));
		lblNewTitle.setText(Version.getAppName() + " " + Version.getAppSubtitle());
		
		shell.layout();
	}
	
	public void launch() {
		createContents();
		shell.open();
		launch(shell);
	}
	
	public void open() {
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
