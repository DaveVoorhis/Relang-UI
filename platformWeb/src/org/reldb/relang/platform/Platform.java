package org.reldb.relang.platform;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;

public class Platform {
	public static void exit(int exitCode) {
	}
	
	public static void launchBrowserWith(String url) {
		UrlLauncher launcher = RWT.getClient().getService(UrlLauncher.class);
		launcher.openURL(url);		
	}
}
