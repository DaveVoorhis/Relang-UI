package org.reldb.relang.data.bdbje;

import org.reldb.relang.data.CatalogEntry;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.DatabaseException;

import static org.reldb.relang.strings.Strings.*;

public class BDBJEBase {

	public static final String catalogName = "sys.Catalog";
	
	private BDBJEEnvironment environment;
	private BDBJEData catalog;
	
	public BDBJEBase(String dir, boolean create) {
		environment = new BDBJEEnvironment(dir, create);				
		// Catalog
		var catalogDB = environment.open(catalogName, create);
		var catalogEntry = new CatalogEntry(catalogName, CatalogEntry.class, null);
		catalog = new BDBJEData(this, catalogDB, CatalogEntry.class);
		// Does the Catalog contain the Catalog?
		if (catalog.getRowCount() == 0)
			catalog.setValue(0, 0, catalogEntry);
	}
	
	public CatalogEntry getCatalogEntry(String name) {
		// TODO - create index mechanism, so iterative search no longer needed
		for (int i = 0; i < catalog.getRowCount(); i++) {
			var catalogEntry = (CatalogEntry)catalog.getValue(0, i);
			if (catalogEntry.name.equals(name))
				return catalogEntry;
		}
		return null;
	}
	
	void updateCatalog(String name, Heading definition) {
		var newCatalogEntry = new CatalogEntry(name, definition, null);
		int i = 0;
		for (i = 0; i < catalog.getRowCount(); i++) {
			var catalogEntry = (CatalogEntry)catalog.getValue(0, i);
			if (catalogEntry.name.equals(name))
				break;
		}
		catalog.setValue(0, i, newCatalogEntry);
	}

	/**
	 * Return true if a given Data source exists.
	 * 
	 * @param name - name of Data source
	 * @return - boolean - true if Data source exists
	 */
	public boolean exists(String name) {
		return getCatalogEntry(name) != null;
	}

	/**
	 * Get an automatically-generated Data source name that doesn't already exist.
	 * 
	 * @return - name
	 */
	public String getNewName() {
		int uniqueifier = 1;
		String name;
		do {
			name = "Data" + uniqueifier++;
		} while (exists(name));
		return name;
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
		updateCatalog(name, heading);
		return new BDBJEData(this, database, heading);
	}
	
	/**
	 * Open a Data source with a given name. If it doesn't exist, throw ExceptionFatal.
	 * 
	 * @param name - name of Data source.
	 * @return - BDBJEData
	 */
	public BDBJEData open(String name) {
		var definition = getCatalogEntry(name);
		if (definition == null)
			throw new ExceptionFatal(Str.ing(ErrSourceNotExists, name));
		var database = environment.open(name, false);
		return new BDBJEData(this, database, definition.heading);
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
		if (table == null)
			return;
		try {
			table.close();
		} finally {
			table = null;
		}
	}
	
	/**
	 * Close the database.
	 */
	public void close() {
		catalog.close();
		environment.close();
	}

	public ClassCatalog getClassCatalog() {
		return environment.getClassCatalog();
	}

	public void transaction(TransactionWorker worker) throws DatabaseException, Exception {
		environment.transaction(worker);
	}
	
}
