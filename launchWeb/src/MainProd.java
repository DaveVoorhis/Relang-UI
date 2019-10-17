import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import org.apache.catalina.webresources.DirResourceSet;
import org.reldb.relang.web.Launcher;

public class MainProd {
    private static final int port = 8080;

	public static void main(String[] args) throws SecurityException, IOException {
		var launcher = new Launcher(port, new FileHandler("tomcat.log", true), Level.WARNING, resources -> {
	        // DirResourceSet(WebResourceRoot root, java.lang.String webAppMount, java.lang.String base, java.lang.String internalPath)
	        //  root - The WebResourceRoot this new WebResourceSet will be added to.
	        //  webAppMount - The path within the web application at which this WebResourceSet will be mounted. 
	        //      For example, to add a directory of JARs to a web application, the directory would be mounted at "/WEB-INF/lib/"
	        //  base - The absolute path to the directory on the file system from which the resources will be served.
	        //  internalPath - The path within this new WebResourceSet where resources will be served from.
	        var additionWebInfClasses = new File("bin");
	        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
		});
		System.out.println("Base directory: " + launcher.getBaseDir());
		System.out.println("Please wait for start...");
		if (!launcher.go())
			return;
		System.out.println("...started!");
		System.out.println("Listening at " + launcher.getURL());
	}
}
