package org.reldb.swt.os_specific;

import org.eclipse.swt.SWT;

public class OSSpecific {

	public static void launch(String app_name) {		
		if (SWT.getPlatform().equals("cocoa")) {
			new CocoaUIEnhancer(app_name).earlyStartup();
		}
	}

}
