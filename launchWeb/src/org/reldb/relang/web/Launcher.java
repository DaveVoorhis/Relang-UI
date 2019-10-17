package org.reldb.relang.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

public class Launcher {

    private static final String webappDirLocation = "WebContent";
    
    private Tomcat tomcat;
    private String url;
    private String baseDir;

    private void init(int port, Configuration configuration, boolean silent) {
        tomcat = new Tomcat();
        
        tomcat.setSilent(silent);
    	
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
        context.setJarScanner(new NoJarScan());
        
        // Determine URL
	    String scheme = connector.getScheme();
	    String ip = inetAddress.getHostAddress();
	    int listeningPort = connector.getPort();
	    String contextPath = context.getServletContext().getContextPath();
	    url = scheme + "://" + ip + ":" + listeningPort + contextPath;
    	
    }
    
    public Launcher(int port, boolean silent, Configuration configuration) {
    	init(port, configuration, silent);
    }
    
	public Launcher(int port, Handler loggingHandler, Level level, Configuration configuration) {
		Logger logger = Logger.getLogger("STDOUT");
		loggingHandler.setFormatter(new SimpleFormatter());
		loggingHandler.setLevel(level);
		try {
			loggingHandler.setEncoding("UTF-8");
		} catch (SecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.addHandler(loggingHandler);
		init(port, configuration, false);
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
