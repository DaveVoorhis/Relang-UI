package org.reldb.relang.data.bdbje;

import org.reldb.relang.data.Heading;
import org.reldb.relang.utilities.ExceptionFatal;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;

public class BDBJEBase {

	private final String catalogName = "sys.Catalog";
	
	private BDBJE db;
	private Database catalogDb;
	private StoredMap<String, Heading> catalog;
	
	public BDBJEBase(String dir, boolean create) {
		db = new BDBJE(dir, create);				
		// Catalog
		catalogDb = db.open(catalogName, true);
		var catalogKeyBinding = new StringBinding();
		var catalogValueBinding = new SerialBinding<Heading>(db.getClassCatalog(), Heading.class);
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
	
	public BDBJEData create(String name, Heading definition) {
		if (catalog.get(name) != null)
			throw new ExceptionFatal("RS0399: BDBJE table " + name + " already exists.");
		var database = db.open(name, true);
		database.getEnvironment().truncateDatabase(null, name, false);
		catalog.put(name, definition);
		return new BDBJEData(this, database, definition);
	}
	
	public BDBJEData open(String name) {
		var definition = catalog.get(name);
		if (definition == null)
			throw new ExceptionFatal("RS0400: BDBJE table " + name + " does not exist.");
		var database = db.open(name, false);
		return new BDBJEData(this, database, definition);
	}
	
	public void close(BDBJEData table) {
		table.close();
	}
	
	public void close() {
		catalogDb.close();
		db.close();
	}

	public BDBJE getBDBJE() {
		return db;
	}
	
	Database getCatalog() {
		return catalogDb;
	}
	
}
