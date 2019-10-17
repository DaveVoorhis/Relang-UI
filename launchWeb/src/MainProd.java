import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import org.apache.catalina.webresources.DirResourceSet;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.reldb.relang.version.Version;
import org.reldb.relang.web.Launcher;

public class MainProd {
    private static final int portDefault = 8080;
    
	public static void main(String[] args) throws SecurityException, IOException {
        var options = new Options();
        
        var portOption = new Option("p", "port", true, "network port");
        portOption.setOptionalArg(false);
        options.addOption(portOption);

        var parser = new DefaultParser();
        var formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        	formatter.printHelp(Version.getAppName(), options);
            System.exit(1);
        }

        var portOptionValue = cmd.getOptionValue("port");
        int port = portDefault;
        try {
        	port = (portOptionValue == null) ? portDefault : Integer.valueOf(portOptionValue);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	formatter.printHelp(Version.getAppName(), options);
        	System.exit(2);
        }
        
		(new Launcher(port, new FileHandler("tomcat.log", true), Level.WARNING, resources -> {
	        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("bin").getAbsolutePath(), "/"));
		})).start();
	}

}
