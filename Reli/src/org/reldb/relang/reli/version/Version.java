package org.reldb.relang.reli.version;

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

	public static String[] getIconsPaths() {
		return new String[] {
			"org/reldb/relang/reli/resources/Candle16.png",
			"org/reldb/relang/reli/resources/Candle32.png",
			"org/reldb/relang/reli/resources/Candle64.png",
			"org/reldb/relang/reli/resources/Candle128.png",
			"org/reldb/relang/reli/resources/Candle256.png",
			"org/reldb/relang/reli/resources/Candle512.png",
			"org/reldb/relang/reli/resources/Candle1024.png",
		};
	}
	
}
