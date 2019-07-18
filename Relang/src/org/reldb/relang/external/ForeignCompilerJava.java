package org.reldb.relang.external;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.utilities.ExceptionFatal;
import org.reldb.relang.utilities.ExceptionSemantic;

/**
 * @author Dave
 *
 */
public class ForeignCompilerJava {
	
	private boolean verbose;
	private RelDatabase database;
	
	public ForeignCompilerJava(RelDatabase database, boolean verbose) {
		this.database = database;
		this.verbose = verbose;
	}
    
    /** Return a classpath cleaned of non-existent files and Web Start's deploy.jar.  
     * Classpath elements with spaces are converted to quote-delimited strings. */
    private final static String cleanClassPath(String s) {
    	if (java.io.File.separatorChar == '/')
    		s = s.replace('\\', '/');
    	else
    		s = s.replace('/', '\\');
        String outstr = "";
        java.util.StringTokenizer st = new java.util.StringTokenizer(s, java.io.File.pathSeparator);
        while (st.hasMoreElements()) {
            String element = (String)st.nextElement();
            java.io.File f = new java.io.File(element);
            if (f.exists() && !element.contains("deploy.jar")) {
            	String fname = f.toString();
            	if (fname.indexOf(' ')>=0)
            		fname = '"' + fname + '"';
                outstr += ((outstr.length()>0) ? java.io.File.pathSeparator : "") + fname;
            }
        }
        return outstr;
    }
    
	/** Return classpath to the Rel core. */
    private String getLocalClasspath() {
        String classPath = System.getProperty("user.dir") + 
        	   java.io.File.pathSeparatorChar + database.getJavaUserSourcePath() +
        	   java.io.File.pathSeparatorChar + database.getHomeDir();
        if (database.getAdditionalJarsForJavaCompilerClasspath() != null)
        	for (String path: database.getAdditionalJarsForJavaCompilerClasspath()) {
       			notify("ForeignCompilerJava: extra jar for classpath: " + path);
	    		classPath += java.io.File.pathSeparator + path;
        	}
        return classPath;
    }
   
    /** Compile foreign code using Eclipse JDT compiler. */
    public void compileForeignCode(PrintStream stream, String className, String src) {
    	ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
    	ByteArrayOutputStream warningStream = new ByteArrayOutputStream();
    	String warningSetting = new String("allDeprecation,"
    			+ "allJavadoc," + "assertIdentifier," + "charConcat,"
    			+ "conditionAssign," + "constructorName," + "deprecation,"
    			+ "emptyBlock," + "fieldHiding," + "finalBound,"
    			+ "finally," + "indirectStatic," + "intfNonInherited,"
    			+ "javadoc," + "localHiding," + "maskedCatchBlocks,"
    			+ "noEffectAssign," + "pkgDefaultMethod," + "serial,"
    			+ "semicolon," + "specialParamHiding," + "staticReceiver,"
    			+ "syntheticAccess," + "unqualifiedField,"
    			+ "unnecessaryElse," + "uselessTypeCheck," + "unsafe,"
    			+ "unusedArgument," + "unusedImport," + "unusedLocal,"
    			+ "unusedPrivate," + "unusedThrown");

    	String classpath = 
    			cleanClassPath(System.getProperty("java.class.path")) + 
    			java.io.File.pathSeparatorChar + 
    			cleanClassPath(getLocalClasspath());

        // If resource directory doesn't exist, create it.
        File resourceDir = new File(database.getJavaUserSourcePath()); 
        if (!(resourceDir.exists()))
            resourceDir.mkdirs();
    	File sourcef;
    	try {
    		// Write source to a Java source file
    		sourcef = new File(database.getJavaUserSourcePath() + java.io.File.separator + getStrippedClassname(className) + ".java");
    		PrintStream sourcePS = new PrintStream(new FileOutputStream(sourcef));
    		sourcePS.print(src);
    		sourcePS.close();
    	} catch (IOException ioe) {
    		throw new ExceptionFatal("RS0293: Unable to save Java source: " + ioe.toString());
    	}
    	
    	// Start compilation using JDT
   		String commandLine = "-1.9 -source 1.9 -warn:" + 
    			warningSetting + " " + 
    			"-cp " + classpath + " \"" + sourcef + "\"";
    	boolean compiled = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(
    			commandLine,
    			new PrintWriter(messageStream), 
    			new PrintWriter(warningStream), 
    			null);
 
    	String compilerMessages = "";
    	// Parse the messages and the warnings.
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(messageStream.toByteArray())));
    	while (true) {
    		String str = null;
    		try {
    			str = br.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		if (str == null) {
    			break;
    		}
    		compilerMessages += str + '\n';
    	}
    	BufferedReader brWarnings = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(warningStream.toByteArray())));
    	while (true) {
    		String str = null;
    		try {
    			str = brWarnings.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		if (str == null) {
    			break;
    		}
    		compilerMessages += str + '\n';
    	}

    	if (!compiled)
        	throw new ExceptionSemantic("RS0005: Compilation failed due to errors: \n" + compilerMessages + "\n");    		    
    }
    
    /** Get a stripped name.  Only return text after the final '.' */
    private static String getStrippedName(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0)
            return name.substring(lastDot + 1);
        else
            return name;
    }
    
    /** Get stripped Java Class name. */
    private static String getStrippedClassname(String name) {
    	return getStrippedName(name);
    }
    
    private void notify(String s) {
    	if (verbose)
    		System.out.println(s);
    }
    
}
