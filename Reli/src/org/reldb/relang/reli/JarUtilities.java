package org.reldb.relang.reli;

import java.io.File;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarUtilities {
	public static String getJarName() {
		return new File(JarUtilities.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
	}

	public static boolean isRunningFromJAR() {
		try {
			String jarFilePath = new File(getJarName()).toString();
			jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
			try (ZipFile zipFile = new ZipFile(jarFilePath)) {
				ZipEntry zipEntry = zipFile.getEntry("META-INF/MANIFEST.MF");
				return zipEntry != null;
			}
		} catch (Exception exception) {
			return false;
		}
	}
}