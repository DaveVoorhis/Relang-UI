package org.reldb.relang.data.bdbje;

import org.reldb.relang.data.CatalogEntry;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;
import org.reldb.relang.tuples.TupleTypeGenerator;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.DatabaseException;

import static org.reldb.relang.strings.Strings.*;

import org.reldb.relang.compiler.DirClassLoader;

public class BDBJEBase {

	public static final String catalogName = "sys.Catalog";
	
	private BDBJEEnvironment environment;
	private BDBJEData<String, CatalogEntry> catalogData;
	private StoredMap<String, CatalogEntry> catalog;
	
	public BDBJEBase(String dir, boolean create) {
		environment = new BDBJEEnvironment(dir, create);				
		// Catalog
		var catalogDB = environment.open(catalogName, create);
		catalogData = new BDBJEData<String, CatalogEntry>(this, catalogDB, CatalogEntry.class) {
			@Override
			protected EntryBinding<?> getKeyBinding() {
				return new StringBinding();
			}
		};
		catalog = catalogData.getStoredMap();
		// Does the Catalog contain the Catalog?
		if (!catalog.containsKey(catalogName))
			catalog.put(catalogName, new CatalogEntry(catalogName, CatalogEntry.class, null));
	}
	
	public String getCodeDir() {
		return environment.getCodeDir();
	}
	
	public CatalogEntry getCatalogEntry(String name) {
		return catalog.get(name);
	}
	
	void updateCatalog(String name, Class<?> definition) {
		catalog.put(name, new CatalogEntry(name, definition, null));
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
	public BDBJEData<?, ?> create(String name) {
		if (exists(name))
			throw new ExceptionFatal(Str.ing(ErrSourceExists, name));
		try {
			// if Berkeley Database (somehow) already exists, delete it.
			(environment.open(name, false)).close();
			environment.remove(name);
		} catch (DatabaseException de) {
		}
		var database = environment.open(name, true);
		var codeDir = environment.getCodeDir();
		var tupleTypeGenerator = new TupleTypeGenerator(codeDir, name);
		tupleTypeGenerator.compile();
		var loader = new DirClassLoader(codeDir);
		Class<?> testclass;
		try {
			testclass = loader.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToGenerateTupleType, name));
		}
		updateCatalog(name, testclass);
		return new BDBJEData<>(this, database, testclass);
	}
	
	/**
	 * Open a Data source with a given name. If it doesn't exist, throw ExceptionFatal.
	 * 
	 * @param name - name of Data source.
	 * @return - BDBJEData
	 */
	public BDBJEData<?, ?> open(String name) {
		var definition = getCatalogEntry(name);
		if (definition == null)
			throw new ExceptionFatal(Str.ing(ErrSourceNotExists, name));
		var database = environment.open(name, false);
		return new BDBJEData<>(this, database, definition.type);
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
	public BDBJEData<?, ?> open(String name, boolean create) {
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
	public void close(BDBJEData<?, ?> table) {
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
		catalogData.close();
		environment.close();
	}

	public ClassCatalog getClassCatalog() {
		return environment.getClassCatalog();
	}

	public void transaction(TransactionWorker worker) throws DatabaseException, Exception {
		environment.transaction(worker);
	}
	
}
