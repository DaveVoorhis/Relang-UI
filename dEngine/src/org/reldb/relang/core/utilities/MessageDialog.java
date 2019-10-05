package org.reldb.relang.core.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog for showing messages to the user.
 * <p>Based on JFace MessageDialog interface, but considerably simplified.
 * </p>
 */
public class MessageDialog {

	/**
	 * Create a MessageBox.
	 * 
	 * @param parent  the parent shell of the dialog or <code>null</code> if none
	 * @param title  the dialog's title, or <code>null</code> if none
	 * @param message  the message
	 * @param style  Style codes, e.g., SWT.ICON_QUESTION, SWT.ICON_WARNING, SWT.ICON_ERROR | SWT.YES, SWT.NO, SWT.OK, SWT.CANCEL, etc.
	 * @return New MessageBox
	 */
	public static MessageBox createMessageBox(Shell parent, String title, String message, int style) {
		var msgBox = new MessageBox(parent, style);
		if (title != null)
			msgBox.setText(title);
		if (message != null)
			msgBox.setMessage(message);
		return msgBox;
	}

	/**
	 * Create and open a MessageBox.
	 * 
	 * @param parent  the parent shell of the dialog or <code>null</code> if none
	 * @param title  the dialog's title, or <code>null</code> if none
	 * @param message  the message
	 * @param style  Style codes, e.g., SWT.ICON_QUESTION, SWT.ICON_WARNING, SWT.ICON_ERROR | SWT.YES, SWT.NO, SWT.OK, SWT.CANCEL, etc.
	 * @return integer return code indicating button press, e.g., SWT.OK, SWT.CANCEL, etc.
	 */
	public static int openMessageBox(Shell parent, String title, String message, int style) {
		return createMessageBox(parent, title, message, style).open();
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
		return openMessageBox(parent, title, message, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION) == SWT.OK;
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
		return openMessageBox(parent, title, message, SWT.YES | SWT.NO | SWT.ICON_QUESTION) == SWT.YES;
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