package org.reldb.relang.data.bdbje;

import org.reldb.relang.data.Access;
import org.reldb.relang.data.CatalogEntry;
import org.reldb.relang.data.Data;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;
import org.reldb.relang.tuples.Tuple;
import org.reldb.relang.tuples.TupleTypeGenerator;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.DatabaseException;

import static org.reldb.relang.strings.Strings.*;

import java.io.Serializable;

import org.reldb.relang.compiler.DirClassLoader;

public class BDBJEBase {

	public static final String catalogName = "sys_Catalog";
	
	private BDBJEEnvironment environment;
	private BDBJEData<String, CatalogEntry> catalogData;
	private StoredMap<String, CatalogEntry> catalog;
	
	public BDBJEBase(String dir, boolean create) {
		environment = new BDBJEEnvironment(dir, create);				
		// Catalog
		var catalogDB = environment.open(catalogName, create);
		catalogData = new BDBJEData<String, CatalogEntry>(this, catalogDB, CatalogEntry.class, new StringBinding());
		catalog = catalogData.getStoredMap();
		// Does the Catalog contain the Catalog?
		if (!catalog.containsKey(catalogName))
			catalog.put(catalogName, new CatalogEntry(catalogName, CatalogEntry.class.getName(), null));
	}
	
	public String getCodeDir() {
		return environment.getCodeDir();
	}
	
	public CatalogEntry getCatalogEntry(String name) {
		return catalog.get(name);
	}
	
	void updateCatalog(String name, Class<?> tupleType) {
		catalog.put(name, new CatalogEntry(name, tupleType.getName(), null));
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
	 * Load a class using the classloader.
	 * 
	 * @param name - String - name of class to be loaded.
	 * @return - Class<?> - loaded Class.
	 * @throws ClassNotFoundException if class is not found.
	 */
	public Class<? extends Tuple> loadClass(String name) throws ClassNotFoundException {
		return (Class<? extends Tuple>) environment.getClassLoader().forName(name);
	}
	
	/**
	 * Get 
	 * @param name
	 * @return
	 */
	public String getTupleTypeNameOf(String name) {
		var definition = getCatalogEntry(name);
		if (definition == null)
			throw new ExceptionFatal(Str.ing(ErrSourceNotExists, name));
		return definition.typeName;
	}
	
	public Class<?> getTupleTypeOf(String name) throws ClassNotFoundException {
		return loadClass(getTupleTypeNameOf(name));
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
		var compileResult = tupleTypeGenerator.compile();
		if (!compileResult.compiled)
			throw new ExceptionFatal(Str.ing(ErrUnableToGenerateTupleType2, name, compileResult));
		Class<?> tupleType;
		try {
			tupleType = environment.getClassLoader().forName(name);
		} catch (ClassNotFoundException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToGenerateTupleType, name));
		}
		updateCatalog(name, tupleType);
		return new BDBJEData<>(this, database, tupleType, new LongBinding());
	}
	
	/**
	 * Open a Data source with a given name. If it doesn't exist, throw ExceptionFatal.
	 * 
	 * @param name - name of Data source.
	 * @return - BDBJEData
	 */
	public BDBJEData<?, ?> open(String name) {
		Class<?> tupleType;
		try {
			tupleType = getTupleTypeOf(name);
		} catch (ClassNotFoundException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToLoadTupleClass, name));
		}
		// TODO - eliminate the following hack by generalising how BDB keys are specified
		var binding = name.equals(catalogName) ? new StringBinding() : new LongBinding();
		var database = environment.open(name, false);
		return new BDBJEData<>(this, database, tupleType, binding);
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
		return (create && !exists(name)) ? create(name) : open(name);
	}

	void openAndRun(BDBJEData bdbjeData, Data.Access xaction) {
		
//		EntryBinding<V> valueBinding = new SerialBinding(bdbjeBase.getClassCatalog(), Tuple.class);
//		data = new StoredSortedMap<K, V>(db, keyBinding, valueBinding, true);
		
		// TODO Auto-generated method stub
		
	}

	/**
	 * Rename a Data source.
	 * 
	 * @param oldName - old name
	 * @param newName - new name
	 */
	public void rename(String oldName, String newName) {
		var catalogEntry = catalog.get(oldName);
		if (catalogEntry == null)
			return;
		catalog.remove(oldName);
		environment.rename(oldName, newName);
		catalog.put(newName, catalogEntry);
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

	public DirClassLoader getClassLoader() {
		return environment.getClassLoader();
	}

	public void transaction(TransactionWorker worker) throws DatabaseException, Exception {
		environment.transaction(worker);
	}
	
}
