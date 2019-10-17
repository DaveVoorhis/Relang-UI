package org.reldb.relang.web;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

public class Launcher {

    private static final String webappDirLocation = "WebContent";
    
    private Tomcat tomcat;
    private String url;
    private String baseDir;

	public Launcher(int port, Configuration configuration) {
        tomcat = new Tomcat();
    	
        // Force creation of default connector.
        var connector = tomcat.getConnector();
		
		// tomcat.setBaseDir(basedir);
        
        InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			tomcat.setHostname(inetAddress.getHostName());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
        
		tomcat.setPort(port);

		baseDir = new File(webappDirLocation).getAbsolutePath();

        var context = tomcat.addWebapp("", baseDir); 
        var resources = new StandardRoot(context);
        configuration.configure(resources);
        context.setResources(resources);
        
        // Determine URL
	    String scheme = connector.getScheme();
	    String ip = inetAddress.getHostAddress();
	    int listeningPort = connector.getPort();
	    String contextPath = context.getServletContext().getContextPath();
	    url = scheme + "://" + ip + ":" + listeningPort + contextPath;
	}

	public String getURL() {
		return url;
	}

	public String getBaseDir() {
		return baseDir;
	}
	
	public void go() {  
		try {
			tomcat.start();
		} catch (Throwable e) {
			System.out.println("Launch failure due to: " + e);
			e.printStackTrace();
		}
	}

}
