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
	
	public BDBJEData create(String name) {
		if (catalog.get(name) != null)
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
	
	public BDBJEData open(String name) {
		var definition = catalog.get(name);
		if (definition == null)
			throw new ExceptionFatal(Str.ing(ErrSourceNotExists, name));
		var database = environment.open(name, false);
		return new BDBJEData(this, database, definition);
	}
	
	public void close(BDBJEData table) {
		table.close();
	}
	
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
