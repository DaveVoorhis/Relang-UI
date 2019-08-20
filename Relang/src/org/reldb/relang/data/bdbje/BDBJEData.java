package org.reldb.relang.data.bdbje;

import java.util.Iterator;
import java.util.Vector;

import org.reldb.relang.data.InvalidValueException;
import org.reldb.relang.data.containers.ContainerWrapper;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;

import static org.reldb.relang.strings.Strings.*;

public class BDBJEData<K, V> implements ContainerWrapper<V> {
	private BDBJEBase bdbjeBase;
	private Class<?> type;
	private Database db;
	private StoredMap<K, V> data;
	
	public BDBJEData(BDBJEBase bdbjeBase, Database db, Class<?> type) {
		this.bdbjeBase = bdbjeBase;
		this.type = type;
		this.db = db;
		EntryBinding<K> dataKeyBinding = (EntryBinding<K>) getKeyBinding();
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
	}

	@Override
	public boolean isReducable() {
		return true;
	}

	@Override
	public void reduce(String name) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}
	
}
