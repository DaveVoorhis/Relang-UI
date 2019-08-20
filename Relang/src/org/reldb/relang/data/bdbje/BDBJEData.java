package org.reldb.relang.data.bdbje;

import java.io.Closeable;
import java.util.Iterator;

import org.reldb.relang.data.Data;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;

public class BDBJEData<K, V> implements Data<V>, Closeable {
	private BDBJEBase bdbjeBase;
	private Class<?> type;
	private Database db;
	private StoredMap<K, V> data;
	
	public BDBJEData(BDBJEBase bdbjeBase, Database db, Class<?> type) {
		this.bdbjeBase = bdbjeBase;
		this.type = type;
		this.db = db;
		
		@SuppressWarnings("unchecked")
		EntryBinding<K> dataKeyBinding = (EntryBinding<K>)getKeyBinding();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		EntryBinding<V> dataValueBinding = new SerialBinding(bdbjeBase.getClassCatalog(), type);
		data = new StoredSortedMap<K, V>(db, dataKeyBinding, dataValueBinding, true);
	}

	public void close() {
		if (db == null)
			return;
		db.close();
		db = null;
	}

	protected EntryBinding<?> getKeyBinding() {
		return new LongBinding();
	}
	
	private void updateCatalog() {
		bdbjeBase.updateCatalog(db.getDatabaseName(), type);
	}

	public StoredMap<K, V> getStoredMap() {
		return data;
	}
	
	@Override
	public boolean isExtendable() {
		return true;
	}

	@Override
	public void extend(String name, Class<?> type) {
		// TODO - implement extend
		updateCatalog();
	}

	@Override
	public boolean isReducable() {
		return true;
	}

	@Override
	public void reduce(String name) {
		// TODO - implement reduce
		updateCatalog();
	}

	@Override
	public boolean isRenameable() {
		return true;
	}

	@Override
	public void rename(String oldName, String newName) {
		// TODO - implement rename
		updateCatalog();
	}

	@Override
	public boolean isTypeChangeable() {
		return true;
	}

	@Override
	public void changeType(String name, Class<?> type) {
		// TODO - implement change type
		updateCatalog();
	}
	
	@Override
	public boolean isStrictlyReadonly() {
		return false;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Iterator<V> iterator() {
		return data.values().iterator();
	}
	
}
