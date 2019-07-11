package org.reldb.relang.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog for showing messages to the user.
 * <p>Based on JFace MessageDialog interface, but considerably simplified.
 * </p>
 */
public class MessageDialog {

	private static MessageBox createMessageBox(Shell parent, String title, String message, int style) {
		var msgBox = new MessageBox(parent, style);
		if (title != null)
			msgBox.setText(title);
		if (message != null)
			msgBox.setMessage(message);
		return msgBox;
	}

	/**
	 * Convenience method to open a simple confirm (OK/Cancel) dialog.
	 *
	 * @param parent  the parent shell of the dialog, or <code>null</code> if none
	 * @param title   the dialog's title, or <code>null</code> if none
	 * @param message the message
	 * @return <code>true</code> if the user presses the OK button,
	 *         <code>false</code> otherwise
	 */
	public static boolean openConfirm(Shell parent, String title, String message) {
		return createMessageBox(parent, title, message, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION).open() == SWT.OK;
	}

	/**
	 * Convenience method to open a standard error dialog.
	 *
	 * @param parent  the parent shell of the dialog, or <code>null</code> if none
	 * @param title   the dialog's title, or <code>null</code> if none
	 * @param message the message
	 */
	public static void openError(Shell parent, String title, String message) {
		createMessageBox(parent, title, message, SWT.OK | SWT.ICON_ERROR).open();
	}

	/**
	 * Convenience method to open a standard information dialog.
	 *
	 * @param parent  the parent shell of the dialog, or <code>null</code> if none
	 * @param title   the dialog's title, or <code>null</code> if none
	 * @param message the message
	 */
	public static void openInformation(Shell parent, String title, String message) {
		createMessageBox(parent, title, message, SWT.OK | SWT.ICON_INFORMATION).open();
	}

	/**
	 * Convenience method to open a simple Yes/No question dialog.
	 *
	 * @param parent  the parent shell of the dialog, or <code>null</code> if none
	 * @param title   the dialog's title, or <code>null</code> if none
	 * @param message the message
	 * @return <code>true</code> if the user presses the Yes button,
	 *         <code>false</code> otherwise
	 */
	public static boolean openQuestion(Shell parent, String title, String message) {
		return createMessageBox(parent, title, message, SWT.YES | SWT.NO | SWT.ICON_QUESTION).open() == SWT.YES;
	}

	/**
	 * Convenience method to open a standard warning dialog.
	 *
	 * @param parent  the parent shell of the dialog, or <code>null</code> if none
	 * @param title   the dialog's title, or <code>null</code> if none
	 * @param message the message
	 */
	public static void openWarning(Shell parent, String title, String message) {
		createMessageBox(parent, title, message, SWT.OK | SWT.ICON_WARNING).open();
	}

}