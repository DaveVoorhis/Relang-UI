package org.reldb.relang.about;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.core.version.Version;
import org.reldb.relang.platform.IconLoader;

public class AboutDialog extends Dialog {
	private static final long serialVersionUID = 1L;
	
	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent) {
		super(parent, SWT.NONE);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.APPLICATION_MODAL);
		shell.setSize(450, 300);
		shell.setText(getText());
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.setBounds(346, 263, 94, 27);
		btnClose.setText("Close");
		btnClose.addListener(SWT.Selection, evt -> shell.dispose());
		
		Label lblNewLabel = new Label(shell, SWT.BORDER);
		lblNewLabel.setImage(IconLoader.loadIcon("Candle32"));
		lblNewLabel.setBounds(10, 10, 32, 32);
		
		Label lblNewTitle = new Label(shell, SWT.NONE);
		lblNewTitle.setFont(SWTResourceManager.getFont(".AppleSystemUIFont", 16, SWT.NORMAL));
		lblNewTitle.setBounds(48, 10, 392, 32);
		lblNewTitle.setText(Version.getAppName() + " " + Version.getAppSubtitle());

	}
}
