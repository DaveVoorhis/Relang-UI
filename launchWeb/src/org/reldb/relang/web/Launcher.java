package org.reldb.relang.web;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

public class Launcher {

    private static final String webappDirLocation = "WebContent";
    
    private Tomcat tomcat;

	public Launcher(int port, Configuration configuration) {
        tomcat = new Tomcat();
    	
        // Force creation of default connector.
        tomcat.getConnector();
		
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

		var location = new File(webappDirLocation).getAbsolutePath();
        System.out.println("Configuring Tomcat with basedir: " + location);

        var context = tomcat.addWebapp("", location); 
        var resources = new StandardRoot(context);
        configuration.configure(resources);
        context.setResources(resources);
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
