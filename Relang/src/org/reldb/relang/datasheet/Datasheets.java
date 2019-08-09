package org.reldb.relang.datasheet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.reldb.relang.data.bdbje.BDBJEEnvironment;
import org.reldb.relang.utilities.MessageDialog;

public class Datasheets {

	private DirectoryDialog createDialog = null;
	private DirectoryDialog openDialog = null;
	
	public String openOrCreate(Shell newShell, String dbURL, boolean create) {
		var base = new BDBJEBase(dbURL, create);
		newShell.setText(newShell.getText() + " - Datasheet " + dbURL);
		newShell.setLayout(new FillLayout());
		new Datasheet(newShell, base);
		newShell.open();
		newShell.addListener(SWT.Close, evt -> base.close());
		return dbURL;
	}

	/** Ask the user for an empty directory in which to create a new Datasheet. Create it, display it in the given shell and return the dbURL if successful, null if not. */
	public String create(Shell newShell) {
		if (createDialog == null) {
			createDialog = new DirectoryDialog(newShell);
			if (openDialog != null)
				createDialog.setFilterPath(openDialog.getFilterPath());
			else
				createDialog.setFilterPath(System.getProperty("user.home"));
			createDialog.setText("Create Datasheet");
			createDialog.setMessage("Select or create a directory to hold your new datasheet.");
		}
		String dbURL = createDialog.open();
		if (dbURL == null) {
			newShell.dispose();
			return null;
		}
		if (BDBJEEnvironment.exists(dbURL)) {
			MessageDialog.openInformation(newShell, "Datasheet Exists", "A datasheet already exists in " + dbURL);
			newShell.dispose();
			return null;
		}
		return openOrCreate(newShell, dbURL, true);
	}

	/** Open a specified existing directory which hopefully contains a Datasheet. Open it in the given shell and return the dbURL if successful, null if not. */
	public String open(Shell newShell, String dbURL) {
		if (!BDBJEEnvironment.exists(dbURL)) {
			MessageDialog.openInformation(newShell, "Datasheet Does Not Exist", "A datasheet can't be found in " + dbURL);
			newShell.dispose();
			return null;
		}
		return openOrCreate(newShell, dbURL, false);
	}

	/** Ask the user for an existing directory which contains a Datasheet. Open it in the given shell and return the dbURL if successful, null if not. */
	public String open(Shell newShell) {
		if (openDialog == null) {
			openDialog = new DirectoryDialog(newShell);
			if (createDialog != null)
				openDialog.setFilterPath(createDialog.getFilterPath());
			else
				openDialog.setFilterPath(System.getProperty("user.home"));
			openDialog.setText("Open Datasheet");
			openDialog.setMessage("Select a datasheet directory to open.");
		}
		String dbURL = openDialog.open();
		if (dbURL == null) {
			newShell.dispose();
			return null;
		}
		return open(newShell, dbURL);
	}

}
