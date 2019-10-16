package org.reldb.relang.feedback;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;

public class SuggestionboxDialog extends FeedbackDialog {
	
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SuggestionboxDialog(Shell parent) {
		super(parent);
		setText("Submit Feedback");
	}

	@Override
	protected String getFeedbackType() {
		return "Feedback";
	}
	
	/** Create contents of the dialog. */
	protected void createContents() {
		shell.setSize(700, 500);
		shell.setLayout(new FormLayout());
		buildContents(
			"light-bulb", 
			"Please complete the following and remove any information that you don't want to send. Then press the Send button to transmit it to the developers.",
			"1. What would you like to tell us or suggest as a feature?"
		);
	}
}
