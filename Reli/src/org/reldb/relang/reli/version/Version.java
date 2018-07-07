package org.reldb.relang.reli.version;

import org.eclipse.swt.graphics.Image;

public class Version {

	public static String getAppName() {
		return "RelI";	
	}
	
	public static int getVersionNumber() {
		return 1;
	}

	public static String getAppID() {
		return getAppName() + " " + getVersionNumber();
	}

	public static Image[] getIcons() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
