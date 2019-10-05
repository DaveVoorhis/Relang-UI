import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class Main {

    private static final String webappDirLocation = "WebContent";
    private static final int port = 8080;

	public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        	
        // Force creation of default connector.
        tomcat.getConnector();
		
		// tomcat.setBaseDir(basedir);
        
        InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			tomcat.setHostname(inetAddress.getHostName());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
        
		tomcat.setPort(port);

		var location = new File(webappDirLocation).getAbsolutePath();
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", location);
        System.out.println("Configuring with basedir: " + location);

        // Alternative location for "WEB-INF/classes" during development. 
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("bin");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
        
        File additionWebInfClasses2 = new File("../../Relang/bin");
        WebResourceRoot resources2 = new StandardRoot(ctx);
        resources2.addPreResources(new DirResourceSet(resources2, "/WEB-INF/classes", additionWebInfClasses2.getAbsolutePath(), "/"));
        
        
        ctx.setResources(resources);
        
        try {
			tomcat.start();
			return;
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
        
        tomcat.getServer().await();
	}

}
