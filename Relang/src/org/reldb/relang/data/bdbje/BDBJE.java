package org.reldb.relang.data.bdbje;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;

import org.reldb.relang.utilities.Directory;
import org.reldb.relang.utilities.ExceptionFatal;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.JEVersion;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class BDBJE implements Closeable {

	private String homeDir;
	private Environment dataEnv;
	private Environment classesEnv;
	private ClassCatalog classes;
	private Database catalogDb;

	public String getBerkeleyJavaDBVersion() {
		return JEVersion.CURRENT_VERSION.getVersionString();
	}

	private String getClickerFileName() {
		return homeDir + File.separator + "ClickToOpen.rl";
	}

	private void writeClicker() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(getClickerFileName(), false);
			if (writer != null)
				writer.close();
		} catch (Exception e) {
			System.out.println("WARNING: Unable to create " + getClickerFileName());
		}
	}

	/**
	 * Create a connection to the Berkeley DB Java Edition.
	 * 
	 * @param dir - directory to hold data
	 * @param create - true if directory can be created if it doesn't exist
	 */
	public BDBJE(String dir, boolean create) {
		homeDir = dir;
		
		if (!Directory.chkmkdir(homeDir)) 
			throw new ExceptionFatal("RS0324: Unable to create directory: " + homeDir);	
		
		var dataDir = homeDir + File.separator + "data";
		if (!Directory.chkmkdir(dataDir))
			throw new ExceptionFatal("RS0325: Unable to create directory: " + dataDir);
		
		var classDir = homeDir + File.separator + "classes";
		if (!Directory.chkmkdir(classDir))
			throw new ExceptionFatal("RS0326: Unable to create directory: " + classDir);

		if (create)
			writeClicker();

		var envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(create);
		
		dataEnv = new Environment(new File(dataDir), envConfig);
		classesEnv = new Environment(new File(classDir), envConfig);

		var dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(create);

		// Needed for serial bindings (i.e., Java serialization)
		var classesDb = classesEnv.openDatabase(null, "classes", dbConfig);
		classes = new StoredClassCatalog(classesDb);
	}

	public Transaction beginTransaction() {
		var config = new TransactionConfig();
	    config.setSerializableIsolation(true);		
		return dataEnv.beginTransaction(null, config);
	}

	public void transaction(TransactionWorker worker) throws DatabaseException, Exception {
		var runner = new TransactionRunner(dataEnv);
		runner.run(worker);
	}
	
	/** 
	 * Open a Berkeley DB "Database", i.e., a persistent key/value store.
	 * 
	 * @param name - name of store
	 * @param create - true to create this Database if it doesn't exist
	 * @return - a new Database
	 */
	Database open(String name, boolean create) {
		var dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(create);
		return dataEnv.openDatabase(null, name, dbConfig);
	}

	/**
	 * Close an open Berkeley DB "Database" returned by <link>open</link>.
	 * 
	 * @param database - <link>Database</link> to close.
	 */
	void close(Database database) {
		database.close();
	}

	/**
	 * Obtain the Java class catalog.
	 * 
	 * @return - <link>ClassCatalog</link>
	 */
	ClassCatalog getClassCatalog() {
		return classes;
	}
	
	/** Closes the database. */
	public void close() {
		if (catalogDb != null) {
			try {
				catalogDb.close();
			} catch (Throwable t) {
				System.out.println("WARNING: BerkeleyDBJE: Error closing catalog: " + t);				
			}
		}
		if (classes != null) {
			try {
				classes.close();
			} catch (Throwable t) {
				System.out.println("WARNING: BerkeleyDBJE: Error closing classes storage: " + t);
			}
			classes = null;
		}
		if (classesEnv != null) {
			try {
				classesEnv.close();
			} catch (Throwable t) {
				System.out.println("WARNING: BerkeleyDBJE: Error closing classes storage environment: " + t);
			}
			classesEnv = null;
		}
		if (dataEnv != null) {
			try {
				dataEnv.close();
			} catch (Throwable t) {
				System.out.println("WARNING: BerkeleyDBJE: Error closing data storage environment: " + t);
			}
			dataEnv = null;
		}
	}

}
