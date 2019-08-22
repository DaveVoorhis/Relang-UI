package org.reldb.relang.data.bdbje;

import java.io.Closeable;
import java.util.Iterator;

import org.reldb.relang.data.Data;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;
import org.reldb.relang.tuples.Tuple;
import org.reldb.relang.tuples.TupleTypeGenerator;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;

import static org.reldb.relang.strings.Strings.*;

public class BDBJEData<K, V> implements Data<V>, Closeable {
	private BDBJEBase bdbjeBase;
	private Class<?> tupleType;
	private Database db;
	private StoredMap<K, V> data;
	
	public BDBJEData(BDBJEBase bdbjeBase, Database db, Class<?> tupleType, EntryBinding<K> keyBinding) {
		this.bdbjeBase = bdbjeBase;
		this.tupleType = tupleType;
		this.db = db;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
	//	EntryBinding<V> valueBinding = new SerialBinding(bdbjeBase.getClassCatalog(), tupleType);
		EntryBinding<V> valueBinding = new SerialBinding(bdbjeBase.getClassCatalog(), Tuple.class);
		data = new StoredSortedMap<K, V>(db, keyBinding, valueBinding, true);
	}

	public void close() {
		if (db == null)
			return;
		db.close();
		db = null;
	}
	
	private void updateCatalog() {
		bdbjeBase.updateCatalog(db.getDatabaseName(), tupleType);
	}

	public StoredMap<K, V> getStoredMap() {
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<V> getType() {
		return (Class<V>)tupleType;
	}
	
	@Override
	public boolean isExtendable() {
		return true;
	}

	@Override
	public void extend(String name, Class<?> type) {
		var codeDir = bdbjeBase.getCodeDir();
		var tupleTypeGenerator = new TupleTypeGenerator(codeDir, db.getDatabaseName());
		tupleTypeGenerator.addAttribute(name, type);
		var compileResult = tupleTypeGenerator.compile();
		if (!compileResult.compiled)
			throw new ExceptionFatal(Str.ing(ErrUnableToExtendTupleType, name, compileResult));
		// TODO - copy old data to new data here
		updateCatalog();
	}

	@Override
	public boolean isRemovable() {
		return true;
	}

	@Override
	public void remove(String name) {
		var codeDir = bdbjeBase.getCodeDir();
		var tupleTypeGenerator = new TupleTypeGenerator(codeDir, db.getDatabaseName());
		tupleTypeGenerator.removeAttribute(name);
		var compileResult = tupleTypeGenerator.compile();
		if (!compileResult.compiled)
			throw new ExceptionFatal(Str.ing(ErrUnableToRemoveInTupleType, name, compileResult));
		// TODO - copy old data to new data here
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
