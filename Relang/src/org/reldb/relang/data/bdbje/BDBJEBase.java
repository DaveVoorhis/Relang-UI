package org.reldb.relang.data.bdbje;

import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;

public class BDBJEBase {

	private BDBJE db;
	private Database catalogDb;
	private StoredMap<String, TableDefinition> catalog;
	
	public BDBJEBase(String dir, boolean create) {
		db = new BDBJE(dir, create);				
		// Catalog
		catalogDb = db.open("sys.Catalog", true);
		var catalogKeyBinding = new StringBinding();
		var catalogValueBinding = new SerialBinding<TableDefinition>(db.getClassCatalog(), TableDefinition.class);
		catalog = new StoredSortedMap<String, TableDefinition>(catalogDb, catalogKeyBinding, catalogValueBinding, true);
	}

	public void close() {
		catalogDb.close();
		db.close();
	}

	public BDBJE getBDBJE() {
		return db;
	}
	
}
