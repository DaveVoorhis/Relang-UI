package org.reldb.relang.utilities;

import java.io.File;

public class Directory {

	/**
	 * Return true if specified directory exists. Otherwise, attempt to create it and return true if successful. Return false if unable to create the directory.
	 * 
	 * @param dir - specified directory
	 */
	public static boolean chkmkdir(String dir) {
		File dirf = new File(dir);
		if (!dirf.exists())
			return dirf.mkdirs();
		return true;
	}

}
