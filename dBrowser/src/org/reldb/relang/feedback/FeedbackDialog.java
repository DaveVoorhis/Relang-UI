package org.reldb.relang.feedback;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.relang.platform.Feedback;
import org.reldb.relang.utilities.DialogAbstract;
import org.reldb.relang.utilities.MessageDialog;

public abstract class FeedbackDialog extends DialogAbstract<String> {
	protected Label lblProgress;
	protected ProgressBar progressBar;
	
	protected Button btnSend;
	protected Button btnCancel;
	
	protected Tree treeDetails;
	
	protected TreeItem report;
	
	protected Feedback phoneHome;
		
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FeedbackDialog(Shell parent) {
		super(parent);
	}
	
	protected static String getCurrentTimeStamp() {
	    return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")).format(new Date());
	}	

	private void setupTreeItem(TreeItem item, String text) {
		item.setText(text);
		item.setChecked(true);
		item.setExpanded(true);
	}
	
	private TreeItem newTreeItem(TreeItem parent, String text) {
		TreeItem item = new TreeItem(parent, SWT.None);
		setupTreeItem(item, text);
		return item;
	}
	
	protected TreeItem newTreeItem(Tree parent, String text) {
		TreeItem item = new TreeItem(parent, SWT.None);
		setupTreeItem(item, text);
		return item;
	}
	
	private void putStacktraceInTree(TreeItem root, StackTraceElement[] trace) {
		TreeItem stackTraceTree = newTreeItem(root, "StackTrace");
		for (StackTraceElement element: trace)
			newTreeItem(stackTraceTree, element.toString());
	}
	
	protected void checkPath(TreeItem item, boolean checked, boolean grayed) {
	    if (item == null) 
	    	return;
	    if (grayed) {
	        checked = true;
	    } else {
	        int index = 0;
	        TreeItem[] items = item.getItems();
	        while (index < items.length) {
	            TreeItem child = items[index];
	            if (child.getGrayed() || checked != child.getChecked()) {
	                checked = grayed = true;
	                break;
	            }
	            index++;
	        }
	    }
	    item.setChecked(checked);
	    item.setGrayed(grayed);
	    checkPath(item.getParentItem(), checked, grayed);
	}

	protected void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    item.setChecked(checked);
	    TreeItem[] items = item.getItems();
	    for (int i = 0; i < items.length; i++) {
	        checkItems(items[i], checked);
	    }
	}
	
	private void putExceptionInTree(TreeItem root, Throwable t) {
		if (t != null) {
			newTreeItem(root, "ErrorClass: " + t.getClass().toString());
			putStacktraceInTree(root, t.getStackTrace());
			newTreeItem(root, "Message: " + t.toString());
			if (t.getCause() != null) {
				TreeItem cause = newTreeItem(root, "Cause");
				putExceptionInTree(cause, t.getCause());
			}
		} else {
			newTreeItem(root, "Error details unavailable.");
		}		
	}
	
	protected void putExceptionInTree(Throwable t) {
		TreeItem root = newTreeItem(report, "JavaException");
		putExceptionInTree(root, t);
	}
	
	protected void putClientInfoInTree(String clientVersion) {
		newTreeItem(report, "Timestamp: " + getCurrentTimeStamp().toString());
		newTreeItem(report, "Version: " + clientVersion);
		newTreeItem(report, "Java version: " + System.getProperty("java.version"));
		newTreeItem(report, "Java vendor: " + System.getProperty("java.vendor"));
		newTreeItem(report, "Java URL: " + System.getProperty("java.vendor.url"));
		newTreeItem(report, "Java home: " + System.getProperty("java.home"));
		newTreeItem(report, "OS Name: " + System.getProperty("os.name"));
		newTreeItem(report, "OS Version: " + System.getProperty("os.version"));
		newTreeItem(report, "OS Architecture: " + System.getProperty("os.arch"));
	}
	
	protected void completed(Feedback.SendStatus sendStatus) {
		String failHeading = "Feedback Failed";
		try {
			if (sendStatus.getResponse() != null && sendStatus.getResponse().startsWith("Success")) {
				Shell parent = getParent();
				quit();
	    		MessageDialog.openInformation(parent, "Feedback Sent", sendStatus.getResponse());
	    		return;
	        } else
	        	if (sendStatus.getException() != null) {
        			sendStatus.getException().printStackTrace();
	        		MessageDialog.openError(getParent(), failHeading, "Unable to send feedback: " + sendStatus.getException().toString());
	        	} else
	        		MessageDialog.openError(getParent(), failHeading, "Unable to send feedback: " + sendStatus.getResponse());
		} catch (Exception e1) {
    		String exceptionName = e1.getClass().getName().toString(); 
    		if (exceptionName.equals("java.lang.InterruptedException"))
    			MessageDialog.openError(getParent(), failHeading, "Send Cancelled");
    		else {
    			e1.printStackTrace();
    			MessageDialog.openError(getParent(), failHeading, "Unable to send feedback: " + e1.toString());
    		}
		}
	}

	protected abstract FeedbackInfo getFeedbackInfo();
	
	protected void doSend() {
		phoneHome.doSend(getFeedbackInfo().toString());
	}
	
	protected void doCancel() {
		phoneHome.doCancel();
	}
	
	protected void quit() {
		shell.dispose();
	}
}
