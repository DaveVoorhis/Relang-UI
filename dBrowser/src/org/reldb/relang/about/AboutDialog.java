package org.reldb.relang.about;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.platform.IconLoader;
import org.reldb.relang.utilities.DialogAbstract;
import org.reldb.relang.version.Version;

public class AboutDialog extends DialogAbstract<String> {

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, null);
	}

	/**
	 * Create contents of the dialog.
	 */
	protected void create(Shell shell) {
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
		
		shell.layout();
	}
}
