package org.reldb.relang.datasheet;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.data.Data;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.reldb.relang.datasheet.sheet.Sheet;

public class Datasheets {

	static long gridNumber = 0;

	public static void openOrCreate(Shell createShell, String string) {
		// TODO Auto-generated method stub
		
	}

	public static void create(Shell newShell, String dbURL) {
		// TODO Auto-generated method stub
		
	}

	public static void open(Shell newShell, String dbURL) {
		// TODO Auto-generated method stub
		
	}

	public static void launchNewGrid() {
		var gridName = "New Grid " + ++gridNumber;
		newShell.setText(gridName);
		newShell.setLayout(new FillLayout());
		var base = new BDBJEBase(System.getProperty("user.home") + File.separator + "relangbase", true);
		Data gridData;
		if (base.exists(gridName)) {
			gridData = base.open(gridName);			
		} else {
			gridData = base.create(gridName);
			gridData.setColumnName(0, "A");
			gridData.setColumnName(1, "B");
			gridData.setColumnName(2, "C");
			gridData.appendRow();
			gridData.appendRow();
			gridData.appendRow();
		}
		new Sheet(newShell, gridData);
		newShell.open();
		newShell.addListener(SWT.Close, evt -> gridData.close());
	}

}
