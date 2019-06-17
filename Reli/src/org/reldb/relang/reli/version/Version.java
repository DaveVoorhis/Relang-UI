package org.reldb.relang.reli.version;

public class Version {

	public static String getAppName() {
		return "RelI";	
	}
	
	public static float getVersionNumber() {
		return 1.0F;
	}

	public static String getAppID() {
		return getAppName() + " " + getVersionNumber();
	}

	public static String getResourcePath() {
		return "org/reldb/relang/reli/resources/";	
	}
	
	public static String getResourceDirectory() {
		return "/" + getResourcePath();
	}
	
	public static String[] getIconsPaths() {
		return new String[] {
			getResourcePath() + "Candle16.png",
			getResourcePath() + "Candle32.png",
			getResourcePath() + "Candle64.png",
			getResourcePath() + "Candle128.png",
			getResourcePath() + "Candle256.png",
			getResourcePath() + "Candle512.png",
			getResourcePath() + "Candle1024.png",
		};
	}

	public static String getPreferencesRepositoryName() {
		return ".reli";
	}

	public static String getVersion() {
		return String.format("Version %.3f", getVersionNumber());
	}

	public static String getCopyright() {
		return "Copyright 2019 Dave Voorhis and RelDB.org";
	}
	
	public static String getURL() {
		return "https://reldb.org";
	}
	
	public static String getReportLogURL() {
	    return "http://reldb.org/feedback/";		
	}
	
	public static String getUpdateURL() {
		return "http://reldb.org/updates/";
	}

	public static String getSplashName() {
		return "splash";
	}
	
}
