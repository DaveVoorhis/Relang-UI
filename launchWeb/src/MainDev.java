import java.io.File;
import java.util.stream.Stream;

import org.apache.catalina.webresources.DirResourceSet;
import org.reldb.relang.web.Launcher;

public class MainDev {
    private static final int port = 8080;

	public static void main(String[] args) {
		Stream.generate("=== DEVELOPMENT LAUNCH. NOT FOR PRODUCTION!!! ==="::toString).limit(3).forEach(s -> System.out.println(s));
		var launcher = new Launcher(port, true, resources -> {
	        // --- Alternative location for "WEB-INF/classes" during development. --- 
	        
	        // DirResourceSet(WebResourceRoot root, java.lang.String webAppMount, java.lang.String base, java.lang.String internalPath)
	        //  root - The WebResourceRoot this new WebResourceSet will be added to.
	        //  webAppMount - The path within the web application at which this WebResourceSet will be mounted. 
	        //      For example, to add a directory of JARs to a web application, the directory would be mounted at "/WEB-INF/lib/"
	        //  base - The absolute path to the directory on the file system from which the resources will be served.
	        //  internalPath - The path within this new WebResourceSet where resources will be served from.
	        var additionWebInfClasses = new File("bin");
	        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
	        
	        additionWebInfClasses = new File("../dBrowser/bin");
	        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
	        
	        additionWebInfClasses = new File("../dEngine/bin");
	        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
	        
	        additionWebInfClasses = new File("../platformWeb/bin");
	        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
		});
		System.out.println("Development launch base directory: " + launcher.getBaseDir());
		launcher.go();
		System.out.println("Development launch listening at " + launcher.getURL());
	}
}
