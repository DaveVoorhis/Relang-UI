package org.reldb.relang.data.bdbje;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	private BDBJEBase base;
	private Class<?> tupleType;
	private Database db;
	private StoredMap<K, V> data;
	
	public BDBJEData(BDBJEBase bdbjeBase, Database db, Class<?> tupleType, EntryBinding<K> keyBinding) {
		this.base = bdbjeBase;
		this.tupleType = tupleType;
		this.db = db;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
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
		base.updateCatalog(db.getDatabaseName(), tupleType);
	}

	/** Obtain the container.
	 * 	
	 * @return - StoredMap<K, V>
	 */
	public StoredMap<K, V> getStoredMap() {
		return data;
	}

	@SuppressWarnings("unchecked")
	private void copyOldToNew(Class<?> oldTupleClass, String newName) {
		Class<?> newTupleClass;
		try {
			newTupleClass = base.loadClass(newName);
		} catch (ClassNotFoundException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToLoadTupleTypeClass, newName));			
		}
		Method copyFrom;
		try {
			copyFrom = newTupleClass.getMethod("copyFrom", oldTupleClass);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToLocateCopyFromMethod, newName));
		}
		try {
			var newInstance = newTupleClass.getConstructor().newInstance();
			base.transaction(() -> {
				data.forEach((key, value) -> {
					try {
						copyFrom.invoke(newInstance, value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new ExceptionFatal(Str.ing(ErrSchemaUpdateCopyFromFailure, e.getMessage()));						
					}
					data.put(key, (V)newInstance);
				});
			});
		} catch (Exception e) {
			throw new ExceptionFatal(Str.ing(ErrSchemaUpdateFailure, e.getMessage()));
		}
		tupleType = newTupleClass;
	}
	
	@FunctionalInterface
	private static interface Action {
		public abstract void change(TupleTypeGenerator tupleTypeGenerator);
	}
	
	@FunctionalInterface
	private static interface Renamer {
		public abstract String newName(TupleTypeGenerator tupleTypeGenerator);
	}
	
	private void changeSchema(Action tupleTypeAction, Renamer tupleTypeRenamer) {
		String dbName = db.getDatabaseName();
		String oldTupleClassName;
		Class<?> oldTupleClass;
		try {
			oldTupleClassName = base.getTupleTypeNameOf(dbName);
			oldTupleClass = base.loadClass(oldTupleClassName);
		} catch (ClassNotFoundException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToLoadTupleType, dbName));
		}
		var codeDir = base.getCodeDir();
		var oldTupleTypeGenerator = new TupleTypeGenerator(codeDir, oldTupleClassName);
		var newName = tupleTypeRenamer.newName(oldTupleTypeGenerator);
		var tupleTypeGenerator = oldTupleTypeGenerator.copyTo(newName);
		if (tupleTypeAction != null)
			tupleTypeAction.change(tupleTypeGenerator);
		var compileResult = tupleTypeGenerator.compile();
		if (!compileResult.compiled)
			throw new ExceptionFatal(Str.ing(ErrUnableToExtendTupleType, newName, compileResult));
		copyOldToNew(oldTupleClass, newName);
		updateCatalog();
		oldTupleTypeGenerator.destroy();		
	}

	private void changeSchema(Renamer renamer) {
		changeSchema(null, renamer);
	}
	
	private void changeSchema(Action tupleTypeAction) {
		changeSchema(tupleTypeAction, tupleTypeGenerator -> db.getDatabaseName() + (tupleTypeGenerator.getSerial() + 1));
	}

	/** Rename this data store; do not rename associated tuple type. */
	public void renameDataTo(String newName) {
		base.rename(db.getDatabaseName(), newName);
	}

	/** Rename this data store and associated tuple type. */
	public void renameAllTo(String newName) {
		changeSchema((Renamer)tupleTypeGenerator -> newName);
		renameDataTo(newName);
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
		changeSchema((Action)tupleTypeGenerator -> tupleTypeGenerator.addAttribute(name, type));
	}

	@Override
	public boolean isRemovable() {
		return true;
	}

	@Override
	public void remove(String name) {
		changeSchema((Action)tupleTypeGenerator -> tupleTypeGenerator.removeAttribute(name));
	}

	@Override
	public boolean isRenameable() {
		return true;
	}

	@Override
	public void rename(String oldName, String newName) {
		changeSchema((Action)tupleTypeGenerator -> tupleTypeGenerator.renameAttribute(oldName, newName));
	}

	@Override
	public boolean isTypeChangeable() {
		return true;
	}

	@Override
	public void changeType(String name, Class<?> type) {
		changeSchema((Action)tupleTypeGenerator -> tupleTypeGenerator.changeAttributeType(name, type));
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
