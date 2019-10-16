package org.reldb.relang.feedback;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;

public class CrashDialog extends FeedbackDialog {
	private Throwable exception;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CrashDialog(Shell parent, Throwable exception) {
		super(parent);
		this.exception = exception;
		setText("Crash Report");
	}

	/** Launch the dialog. */
	public static void launch(Shell shell, Throwable t) {
		try {
			shell.getDisplay().syncExec(() -> (new CrashDialog(shell, t)).open());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getFeedbackType() {
		return "CrashReport";
	}
	
	@Override
	protected void populateTree() {
		super.populateTree();
		putExceptionInTree(exception);		
	}

	/** Create contents of the dialog. */
	protected void createContents() {
		shell.setSize(700, 500);
		shell.setLayout(new FormLayout());
		buildContents(
			"explosion",
			"Unfortunately, something went wrong. We'd like to send the developers a message about it, so they can fix it in a future update.\n\nIf you'd rather not send anything, that's ok. Press the Cancel button and nothing will be sent.\n\nOtherwise, please answer the following questions as best you can and remove any information that you don't want to send. Then press the Send button to transmit it to the developers.",
			"1. What were you doing when this happened?"
		);
	}
}
