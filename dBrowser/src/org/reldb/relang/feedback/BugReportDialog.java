package org.reldb.relang.feedback;

import org.eclipse.swt.widgets.Shell;

public class BugReportDialog extends FeedbackDialog {
	
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public BugReportDialog(Shell parent) {
		super(parent);
		setText("Bug Report");
	}

	@Override
	protected String getFeedbackType() {
		return "BugReport";
	}
	
	/** Create contents of the dialog. */
	protected void createContents() {
		buildContents(
			"bug",
			"Please complete the following and remove any information that you don't want to send. Then press the Send button to transmit it to the developers.",
			"1. Please describe the problem."
		);
	}
}
