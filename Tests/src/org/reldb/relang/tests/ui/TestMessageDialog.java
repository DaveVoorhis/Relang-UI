package org.reldb.relang.tests.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import org.reldb.relang.utilities.MessageDialog;

public class TestMessageDialog {

	private Shell shell;
	
	public static void main(String[] args) {
		(new TestMessageDialog()).run();
	}
	
	public void addTest(String prompt, Listener listener) {
		var button = new Button(shell, SWT.NONE);
		button.setText(prompt);
		button.addListener(SWT.Selection, listener);
	}
	
	public void run() {
		var display = new Display();
		shell = new Shell(display);
		shell.setLayout(new GridLayout());
		
		addTest("Confirm (Ok)", evt -> testConfirmOk());
		addTest("Confirm (Cancel)", evt -> testConfirmCancel());
		addTest("Question (Yes)", evt -> testQuestionYes());
		addTest("Question (No)", evt -> testQuestionNo());
		addTest("Information", evt -> testInformation());
		addTest("Warning", evt -> testWarning());
		addTest("Error", evt -> testError());
		
		shell.setSize(800, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();		
	}
	
	public void testConfirmOk() {
		boolean result = MessageDialog.openConfirm(shell, "ConfirmTest", "This is a confirmation message. Press Ok to verify it works.");
		if (result)
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed Ok.");
		else
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed something other than Ok.");
	}
	
	public void testConfirmCancel() {
		boolean result = MessageDialog.openConfirm(shell, "ConfirmTest", "This is a confirmation message. Press Cancel to verify it works.");
		if (result)
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed Ok.");
		else
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed something other than Ok.");
	}

	public void testError() {
		MessageDialog.openError(shell, "Error", "Error message.");
	}
	
	public void testInformation() {
		MessageDialog.openInformation(shell, "Information", "Information message.");
	}
	
	public void testWarning() {
		MessageDialog.openWarning(shell, "Warning", "Warning message.");
	}
	
	public void testQuestionYes() {
		boolean result = MessageDialog.openQuestion(shell, "QuestionTest", "This is a question message. Press Yes to verify it works.");
		if (result)
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed Yes.");
		else
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed something other than Yes.");
	}
	
	public void testQuestionNo() {
		boolean result = MessageDialog.openQuestion(shell, "QuestionTest", "This is a question message. Press No to verify it works.");
		if (result)
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed Yes.");
		else
			MessageDialog.openInformation(shell, "ConfirmTest", "You pressed something other than Yes.");
	}
}
