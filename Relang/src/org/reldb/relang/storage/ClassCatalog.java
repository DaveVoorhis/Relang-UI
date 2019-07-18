package org.reldb.relang.storage;

import java.io.File;

import org.reldb.relang.data.Tuple;
import org.reldb.relang.external.DirClassLoader;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class ClassCatalog {
    private StoredClassCatalog classCatalog;
    private Database classCatalogDb;
    private Environment environment;
    private EntryBinding<String> relvarMetadataBinding;
    private SerialBinding<Tuple> tupleBinding;
    
    // class loader for external Java-based operators and types
    private DirClassLoader dirClassLoader;

    ClassCatalog(String directory, EnvironmentConfig environmentConfig, DatabaseConfig dbConfig) {
    	// This should be main database directory
    	dirClassLoader = new DirClassLoader(directory);
    	
        // Open the environment in subdirectory of the above
    	String classesDir = directory + java.io.File.separator + "classes";
    	LocalDatabase.mkdir(classesDir);
        environment = new Environment(new File(classesDir), environmentConfig);
        
        // Open the class catalog db. This is used to optimize class serialization.
        classCatalogDb = environment.openDatabase(null, "_ClassCatalog", dbConfig);

        // Create our class catalog
        classCatalog = new StoredClassCatalog(classCatalogDb);
        
        // Need a serial binding for metadata
        relvarMetadataBinding = new SerialBinding<String>(classCatalog, String.class);
        
        // Need serial binding for data
        tupleBinding = new SerialBinding<Tuple>(classCatalog, Tuple.class) {
        	public ClassLoader getClassLoader() {
        		return dirClassLoader;
        	}
        };   	
    }

	public void close() throws DatabaseException {
    	classCatalogDb.close();
    	environment.close();
	}
    
    EntryBinding<String> getRelvarMetadataBinding() {
    	return relvarMetadataBinding;
    }
    
    SerialBinding<Tuple> getTupleBinding() {
    	return tupleBinding;
    }

	public StoredClassCatalog getStoredClassCatalog() {
		return classCatalog;
	}
    
}
