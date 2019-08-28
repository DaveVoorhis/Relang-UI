package org.reldb.relang.data.bdbje;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.reldb.relang.data.Data;
import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.strings.Str;
import org.reldb.relang.tuples.Tuple;
import org.reldb.relang.tuples.TupleTypeGenerator;

import static org.reldb.relang.strings.Strings.*;

public class BDBJEData<K extends Serializable, V extends Serializable> implements Data<K, V> {
	private BDBJEBase base;
	private String name;
	
	BDBJEData(BDBJEBase bdbjeBase, String name) {
		this.base = bdbjeBase;
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<V> getType() {
		try {
			return (Class<V>)base.getTupleTypeOf(name);
		} catch (ClassNotFoundException e) {
			throw new ExceptionFatal(Str.ing(ErrUnableToLoadTupleTypeClass2, name));
		}
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends Tuple> copyOldToNew(Class<? extends Tuple> oldTupleClass, String newName) {
		Class<? extends Tuple> newTupleClass;
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
			base.transaction(() -> access(data -> data.forEach((key, value) -> {
				try {
					copyFrom.invoke(newInstance, value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new ExceptionFatal(Str.ing(ErrSchemaUpdateCopyFromFailure, e.getMessage()));						
				}
				data.put(key, (V)newInstance);
			})));
		} catch (Exception e) {
			throw new ExceptionFatal(Str.ing(ErrSchemaUpdateFailure, e.getMessage()));
		}
		return newTupleClass;
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
		String dbName = name;
		String oldTupleClassName;
		Class<? extends Tuple> oldTupleClass;
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
		var newTupleType = copyOldToNew(oldTupleClass, newName);
		base.updateCatalog(name, newTupleType);
		oldTupleTypeGenerator.destroy();		
	}

	private void changeSchema(Renamer renamer) {
		changeSchema(null, renamer);
	}
	
	private void changeSchema(Action tupleTypeAction) {
		changeSchema(tupleTypeAction, tupleTypeGenerator -> name + (tupleTypeGenerator.getSerial() + 1));
	}

	/** Rename this data store; do not rename associated tuple type. */
	public void renameDataTo(String newName) {
		base.rename(name, newName);
		name = newName;
	}

	/** Rename this data store and associated tuple type. */
	public void renameAllTo(String newName) {
		changeSchema((Renamer)tupleTypeGenerator -> newName);
		renameDataTo(newName);
	}
	
	@Override
	public boolean isExtendable() {
		return true;
	}
	
	@Override
	public void extend(String name, Class<? extends Serializable> type) {
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
	public void changeType(String name, Class<? extends Serializable> type) {
		changeSchema((Action)tupleTypeGenerator -> tupleTypeGenerator.changeAttributeType(name, type));
	}
	
	@Override
	public boolean isStrictlyReadonly() {
		return false;
	}

	@Override
	public void access(Transaction<K, V> xaction) {
		base.openAndRun(this, xaction);
	}
	
}
