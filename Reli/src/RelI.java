import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.reldb.relang.reli.JarUtilities;

// TODO - need to change JAR loading to suit directory hierarchy in lib.
public class RelI {
	public static void main(String args[]) {
		if (JarUtilities.isRunningFromJAR())
			System.out.println("Running from JAR: " + JarUtilities.getJarName());
		if (GraphicsEnvironment.isHeadless())
			addJarToClasspath(new File("lib/org.eclipse.rap.rwt_3.4.0.20171130-0837.jar"));
		else {
			File swtJar = new File(getArchFilename("lib/swt"));
			System.out.println("Loading SWT jar " + swtJar);
			addJarToClasspath(swtJar);
		}
		addJarToClasspath(new File("lib/ecj-4.7.3a.jar"));
		org.reldb.relang.reli.RelI.main(args);
	}

	public static void addJarToClasspath(File jarFile) {
		try {
			URL url = jarFile.toURI().toURL();
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<?> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class<?>[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { url });
		} catch (Throwable t) {
			System.out.println("ERROR: " + t + " in RelI::addJarToClasspath.");
			t.printStackTrace();
		}
	}

	private static String getArchFilename(String prefix) {
		return prefix + "_" + getOSName() + "_" + getArchName() + ".jar";
	}

	private static String getOSName() {
		String osNameProperty = System.getProperty("os.name");

		if (osNameProperty == null) {
			throw new RuntimeException("os.name property is not set");
		} else {
			osNameProperty = osNameProperty.toLowerCase();
		}

		if (osNameProperty.contains("win")) {
			return "win";
		} else if (osNameProperty.contains("mac")) {
			return "macos";
		} else if (osNameProperty.contains("linux") || osNameProperty.contains("nix")) {
			return "linux";
		} else {
			throw new RuntimeException("Unknown OS name: " + osNameProperty);
		}
	}

	private static String getArchName() {
		String osArch = System.getProperty("os.arch");

		if (osArch != null && osArch.contains("64")) {
			return "64";
		} else {
			return "32";
		}
	}

}
