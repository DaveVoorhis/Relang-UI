package org.reldb.relang.data.bdbje;

import org.reldb.relang.data.Heading;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;

import static org.reldb.relang.strings.Strings.*;

public class BDBJEBase {

	private static final String catalogName = "sys.Catalog";
	
	private BDBJEEnvironment environment;
	private Database catalogDb;
	private StoredMap<String, Heading> catalog;
	
	public BDBJEBase(String dir, boolean create) {
		environment = new BDBJEEnvironment(dir, create);				
		// Catalog
		catalogDb = environment.open(catalogName, true);
		var catalogKeyBinding = new StringBinding();
		var catalogValueBinding = new SerialBinding<Heading>(environment.getClassCatalog(), Heading.class);
		catalog = new StoredSortedMap<String, Heading>(catalogDb, catalogKeyBinding, catalogValueBinding, true);
		// Does the Catalog contain the Catalog?
		if (create && catalog.get(catalogName) == null) {
			var catalogDefinition = new Heading();
			// TODO - define catalog here
			updateCatalog(catalogName, catalogDefinition);
		}
	}
	
	void updateCatalog(String name, Heading definition) {
		catalog.put(name, definition);
	}

	/**
	 * Return true if a given Data source exists.
	 * 
	 * @param name - name of Data source
	 * @return - boolean - true if Data source exists
	 */
	public boolean exists(String name) {
		return catalog.get(name) != null;
	}
	
	/**
	 * Create a Data source with a given name. If it exists already, throw ExceptionFatal.
	 * 
	 * @param name - name of Data source.
	 * @return - BDBJEData
	 */
	public BDBJEData create(String name) {
		if (exists(name))
			throw new ExceptionFatal(Str.ing(ErrSourceExists, name));
		try {
			// if Database (somehow) exists, delete it.
			(environment.open(name, false)).close();
			environment.remove(name);
		} catch (DatabaseException de) {
		}
		var database = environment.open(name, true);
		var heading = new Heading();
		catalog.put(name, heading);
		return new BDBJEData(this, database, heading);
	}
	
	/**
	 * Open a Data source with a given name. If it doesn't exist, throw ExceptionFatal.
	 * 
	 * @param name - name of Data source.
	 * @return - BDBJEData
	 */
	public BDBJEData open(String name) {
		var definition = catalog.get(name);
		if (definition == null)
			throw new ExceptionFatal(Str.ing(ErrSourceNotExists, name));
		var database = environment.open(name, false);
		return new BDBJEData(this, database, definition);
	}
	
	/**
	 * Open a Data source with a given name. 
	 * 
	 * If Data source doesn't exist and <code>create</code> is false, throw an ExceptionFatal. 
	 * If Data source doesn't exist and <code>create</code> is true, create it.
	 * If Data source exists, open it regardless of value of <code>create</code>. 
	 * 
	 * @param name - name of Data source.
	 * @param create - boolean - if true and Data source doesn't exist, create it.
	 * @return
	 */
	public BDBJEData open(String name, boolean create) {
		if (create && !exists(name))
			return create(name);
		else
			return open(name);
	}
	
	/**
	 * Close an open Data source.
	 * 
	 * @param table - BDBJEData
	 */
	public void close(BDBJEData table) {
		table.close();
	}
	
	/**
	 * Close the database.
	 */
	public void close() {
		catalogDb.close();
		environment.close();
	}

	public ClassCatalog getClassCatalog() {
		return environment.getClassCatalog();
	}

	public void transaction(TransactionWorker worker) throws DatabaseException, Exception {
		environment.transaction(worker);
	}
	
}
