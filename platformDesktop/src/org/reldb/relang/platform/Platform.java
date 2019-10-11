package org.reldb.relang.platform;

import org.eclipse.swt.program.Program;

public class Platform {
	public static void exit(int exitCode) {
		System.exit(exitCode);		
	}

	public static void launchBrowserWith(String url) {
		Program.launch(url);
	}
}
