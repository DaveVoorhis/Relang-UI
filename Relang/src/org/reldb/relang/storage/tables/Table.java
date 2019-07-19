package org.reldb.relang.storage.tables;

import com.sleepycat.je.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.types.AttributeMap;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;
import org.reldb.relang.storage.LocalDatabase;
import org.reldb.relang.storage.TransactionRunner;
import org.reldb.relang.storage.relvars.RelvarHeading;
import org.reldb.relang.storage.temporary.TempTable;
import org.reldb.relang.storage.temporary.TempTableImplementation;

/**
 * An updatable collection of ValueTupleS; a wrapper around Berkeley DB's
 * "Database".
 */
public abstract class Table {

	private LocalDatabase database;
	private RelvarHeading headingDefinition;
	private AttributeMap[] keyMaps;

	public Table(LocalDatabase database, RelvarHeading headingDefinition) {
		this.database = database;
		this.headingDefinition = headingDefinition;
		keyMaps = new AttributeMap[headingDefinition.getKeyCount()];
		for (int keyNumber = 0; keyNumber < headingDefinition.getKeyCount(); keyNumber++) {
			SelectAttributes keyAttributes = headingDefinition.getKey(keyNumber);
			Heading sourceHeading = headingDefinition.getHeading();
			Heading targetHeading = sourceHeading.project(keyAttributes);
			keyMaps[keyNumber] = new AttributeMap(targetHeading, sourceHeading);
		}
	}

	public RelvarHeading getHeadingDefinition() {
		return headingDefinition;
	}

	protected abstract Storage getStorage(Transaction txn) throws DatabaseException;

	public LocalDatabase getDatabase() {
		return database;
	}

	private DatabaseEntry getKeyValueFromTuple(Generator generator, Tuple tuple, int keyNumber) {
		DatabaseEntry theKey = new DatabaseEntry();
		if (headingDefinition.getKeyCount() == 0)
			database.getTupleBinding().objectToEntry(tuple, theKey);
		else {
			Tuple keyTuple = keyMaps[keyNumber].project(generator, tuple);
			database.getTupleBinding().objectToEntry(keyTuple, theKey);
		}
		return theKey;
	}

	public boolean insertTupleNoDuplicates(Generator generator, Storage table, Transaction txn, Tuple tuple,
			String description) throws DatabaseException {
		DatabaseEntry theData = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(tuple, theData);
		// Put it in the database.
		for (int i = 0; i < table.size(); i++) {
			Database tab = table.getDatabase(i);
			DatabaseEntry entry = (i == 0) ? theData : database.getKeyTableEntry();
			if (tab.putNoOverwrite(txn, getKeyValueFromTuple(generator, tuple, i), entry) == OperationStatus.KEYEXIST)
				throw new ExceptionSemantic("RS0232: " + description
						+ " tuple would violate uniqueness constraint of KEY {" + headingDefinition.getKey(i) + "}");
		}
		return true;
	}

	private boolean insertTuple(Generator generator, Storage table, Transaction txn, Tuple tuple, String description)
			throws DatabaseException {
		DatabaseEntry theData = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(tuple, theData);
		// Put it in the database. Skip it silently if duplicate.
		for (int i = 0; i < table.size(); i++) {
			Database tab = table.getDatabase(i);
			DatabaseEntry entry = (i == 0) ? theData : database.getKeyTableEntry();
			if (tab.putNoOverwrite(txn, getKeyValueFromTuple(generator, tuple, i), entry) == OperationStatus.KEYEXIST)
				return false;
		}
		return true;
	}

	public void insert(final Generator generator, final Tuple tuple) {
		try {
			(new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					insertTupleNoDuplicates(generator, getStorage(txn), txn, (Tuple) tuple.getSerializableClone(),
							"Inserting");
					return null;
				}
			}).execute(database);
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			throw new ExceptionSemantic("RS0233: insert tuple failed: " + t.getMessage());
		}
	}

	private static abstract class Inserter {
		abstract boolean insert(Generator generator, Storage table, Transaction txn, Tuple tuple, String comment);
	}

	private long insert(final Generator generator, final ValueRelation relation, final Inserter inserter) {
		try {
			return ((Long) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					// use of temporary storage prevents problems with deadlock or infinite
					// iteration if we insert a relvar into itself
					// TODO - improve this so we only use temporary storage if there's the potential
					// of inserting relvar 'x' (or a query involving relvar 'x') into itself.
					TempTable tmp = new TempTableImplementation(database);
					long insertCount = 0;
					try {
						Storage table = getStorage(txn);
						TupleIterator iterator = relation.iterator();
						try {
							while (iterator.hasNext()) {
								Tuple tuple = (Tuple) iterator.next().getSerializableClone();
								tmp.put(tuple);
							}
						} finally {
							iterator.close();
						}
						iterator = tmp.values();
						try {
							while (iterator.hasNext()) {
								Tuple tuple = iterator.next();
								if (inserter.insert(generator, table, txn, tuple, "Inserting"))
									insertCount++;
								else
									rollback();
							}
						} finally {
							iterator.close();
						}
					} finally {
						tmp.close();
					}
					return Long.valueOf(insertCount);
				}
			}).execute(database)).longValue();
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionSemantic("RS0234: insert relation failed: " + t.getMessage());
		}
	}

	public long insert(final Generator generator, final ValueRelation relation) {
		return insert(generator, relation, new Inserter() {
			boolean insert(Generator generator, Storage table, Transaction txn, Tuple tuple, String comment) {
				return insertTuple(generator, table, txn, tuple, comment);
			}
		});
	}

	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		return insert(generator, relation, new Inserter() {
			boolean insert(Generator generator, Storage table, Transaction txn, Tuple tuple, String comment) {
				return insertTupleNoDuplicates(generator, table, txn, tuple, comment);
			}
		});
	}

	public long getCardinality() {
		try {
			return ((Long) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					return Long.valueOf(getStorage(txn).getDatabase(0).count());
				}
			}).execute(database)).longValue();
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable de) {
			de.printStackTrace();
			throw new ExceptionFatal("RS0370: getCardinality failed: " + de.getMessage());
		}
	}

	/** Obtain tuple value given a key. Return null if not found. */
	public Tuple getTupleForKey(final Generator generator, final Tuple tuple) {
		try {
			return ((Tuple) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					DatabaseEntry foundData = new DatabaseEntry();
					Storage storage = getStorage(txn);
					if (storage == null)
						return null;
					if (storage.getDatabase(0).get(txn, getKeyValueFromTuple(generator, tuple, 0), foundData,
							LockMode.READ_COMMITTED) == OperationStatus.SUCCESS)
						return (Tuple) database.getTupleBinding().entryToObject(foundData);
					return null;
				}
			}).execute(database));
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0371: getTupleForKey failed: " + t.getMessage());
		}
	}

	public boolean contains(final Generator generator, final Tuple tuple) {
		try {
			return ((Boolean) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					DatabaseEntry foundData = new DatabaseEntry();
					DatabaseEntry keyData = getKeyValueFromTuple(generator, tuple, 0);
					return Boolean.valueOf(getStorage(txn).getDatabase(0).get(txn, keyData, foundData,
							LockMode.READ_COMMITTED) == OperationStatus.SUCCESS);
				}
			}).execute(database)).booleanValue();
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0372: contains failed: " + t.getMessage());
		}
	}

	// Delete all tuples
	private void purge(Transaction txn) throws DatabaseException {
		Storage tables = getStorage(txn);
		for (int i = 0; i < tables.size(); i++) {
			Cursor cursor = tables.getDatabase(i).openCursor(txn, null);
			try {
				DatabaseEntry foundKey = new DatabaseEntry();
				DatabaseEntry foundData = new DatabaseEntry();
				while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS)
					cursor.delete();
			} finally {
				cursor.close();
			}
		}
	}

	// Delete all tuples
	public void purge() {
		try {
			(new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					purge(txn);
					return null;
				}
			}).execute(database);
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0373: purge failed: " + t.getMessage());
		}
	}

	// Delete given tuple.
	public void delete(final Generator generator, final Tuple tuple) {
		try {
			(new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					Storage tables = getStorage(txn);
					for (int i = 0; i < tables.size(); i++)
						tables.getDatabase(i).delete(txn, getKeyValueFromTuple(generator, tuple, i));
					return null;
				}
			}).execute(database);
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0374: delete tuple failed: " + t.getMessage());
		}
	}

	// Delete selected tuples
	public long delete(final Generator generator, final TupleFilter filter) {
		try {
			return ((Long) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					Storage tables = getStorage(txn);
					Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
					long deleteCount = 0;
					try {
						DatabaseEntry foundKey = new DatabaseEntry();
						DatabaseEntry foundData = new DatabaseEntry();
						while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
							Tuple tuple = (Tuple) database.getTupleBinding().entryToObject(foundData);
							tuple.loaded(generator);
							if (filter.filter(tuple)) {
								cursor.delete();
								for (int i = 1; i < tables.size(); i++)
									tables.getDatabase(i).delete(txn, getKeyValueFromTuple(generator, tuple, i));
								deleteCount++;
							}
						}
					} finally {
						cursor.close();
					}
					return Long.valueOf(deleteCount);
				}
			}).execute(database)).longValue();
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0375: delete tuples failed: " + t.getMessage());
		}
	}

	// Delete specified tuples. If there are tuplesToDelete not found in this
	// Relvar, and errorIfNotIncluded is true, throw an error.
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		final HashMap<Tuple, Boolean> toDelete = new HashMap<Tuple, Boolean>();
		Generator generator = context.getGenerator();
		// index the tuplesToDelete into the toDelete index, which uses each
		// tupleToDelete as a key and a Boolean as the value.
		TupleIterator iterator = tuplesToDelete.iterator();
		try {
			while (iterator.hasNext())
				toDelete.put(iterator.next(), Boolean.FALSE);
		} finally {
			iterator.close();
		}
		if (errorIfNotIncluded) {
			// make sure every tuple in toDelete is found in this relvar (table) at least
			// once.
			TupleIterator relvarTupleIterator = iterator(generator);
			try {
				while (relvarTupleIterator.hasNext()) {
					Tuple keyTuple = relvarTupleIterator.next();
					if (toDelete.containsKey(keyTuple))
						toDelete.put(keyTuple, Boolean.TRUE);
				}
			} finally {
				relvarTupleIterator.close();
			}
			// make sure every entry in index is TRUE, i.e., has been found at least once
			Collection<Boolean> values = toDelete.values();
			Iterator<Boolean> valueIterator = values.iterator();
			while (valueIterator.hasNext())
				if (!valueIterator.next().booleanValue())
					throw new ExceptionSemantic(
							"RS0235: In I_DELETE, one or more specified tuples are not included in the relvar.");
		}
		return delete(generator, new TupleFilter() {
			public boolean filter(Tuple tuple) {
				return toDelete.containsKey(tuple);
			}
		});
	}

	// Update selected tuples using a given TupleMap
	public long update(final Generator generator, final TupleFilter whereFilter, final TupleMap updateMap) {
		try {
			return ((Long) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					TempTable insertionTemporaryTable = new TempTableImplementation(database);
					long updateCount = 0;
					try {
						Storage tables = getStorage(txn);
						DatabaseEntry foundKey = new DatabaseEntry();
						DatabaseEntry foundData = new DatabaseEntry();
						Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
						try {
							while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
								Tuple tuple = (Tuple) database.getTupleBinding().entryToObject(foundData);
								tuple.loaded(generator);
								if (whereFilter.filter(tuple)) {
									Tuple newTuple = updateMap.map(tuple);
									cursor.delete();
									for (int i = 1; i < tables.size(); i++)
										tables.getDatabase(i).delete(txn, getKeyValueFromTuple(generator, tuple, i));
									Tuple data = (Tuple) newTuple.getSerializableClone();
									insertionTemporaryTable.put(data);
									updateCount++;
								}
							}
						} finally {
							cursor.close();
						}
						TupleIterator iterator = insertionTemporaryTable.values();
						try {
							while (iterator.hasNext())
								insertTupleNoDuplicates(generator, tables, txn, iterator.next(), "Updating");
						} finally {
							iterator.close();
						}
					} finally {
						insertionTemporaryTable.close();
					}
					return Long.valueOf(updateCount);
				}
			}).execute(database)).longValue();
		} catch (ExceptionSemantic se) {
			throw se;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0376: update failed: " + t.getMessage());
		}
	}

	// Update all tuples using a given TupleMap
	public long update(final Generator generator, final TupleMap map) {
		return update(generator, new TupleFilter() {
			public boolean filter(Tuple tuple) {
				return true;
			}
		}, map);
	}

	// Get a TupleIterator
	public TupleIterator iterator(final Generator generator) {
		return new RegisteredTupleIterator(database) {
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();
			Tuple current = null;
			boolean atEnd = false;

			public boolean hasNext() {
				if (current != null)
					return true;
				if (atEnd)
					return false;
				try {
					if (cursor == null) {
						txn = database.beginTransaction();
						cursor = getStorage(txn.getTransaction()).getDatabase(0).openCursor(txn.getTransaction(), null);
					}
					if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						current = (Tuple) database.getTupleBinding().entryToObject(foundData);
						current.loaded(generator);
						return true;
					} else
						atEnd = true;
				} catch (DatabaseException exp) {
					exp.printStackTrace();
					throw new ExceptionFatal("RS0377: Unable to get next tuple: " + exp.getMessage());
				}
				return false;
			}

			public Tuple next() {
				if (hasNext())
					try {
						return current;
					} finally {
						current = null;
					}
				throw new NoSuchElementException();
			}
		};
	}

	// Alter every tuple to include the rightTuple
	public void expandTuples(Transaction txn, Tuple rightTuple) {
		Storage tables = getStorage(txn);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
		try {
			while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
				Tuple oldTuple = (Tuple) database.getTupleBinding().entryToObject(foundData);
				Tuple newTuple = oldTuple.joinDisjoint(rightTuple);
				DatabaseEntry theData = new DatabaseEntry();
				database.getTupleBinding().objectToEntry(newTuple, theData);
				cursor.putCurrent(theData);
			}
		} finally {
			cursor.close();
		}
	}

	// Alter every tuple to drop the attribute with the specified index
	public void shrinkTuples(Transaction txn, int attributeIndex) {
		Storage tables = getStorage(txn);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
		try {
			while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
				Tuple oldTuple = (Tuple) database.getTupleBinding().entryToObject(foundData);
				Tuple newTuple = oldTuple.shrink(attributeIndex);
				DatabaseEntry theData = new DatabaseEntry();
				database.getTupleBinding().objectToEntry(newTuple, theData);
				cursor.putCurrent(theData);
			}
		} finally {
			cursor.close();
		}
	}

	// Alter every tuple to set attribute at newAttributeIndex to the result of
	// invoking selector newAttributeSelector
	// with an argument of the attribute at oldAttributeIndex.
	public void convertTuples(Generator generator, Transaction txn, Type oldType, int oldAttributeIndex,
			int newAttributeIndex, Instruction selector) {
		Storage tables = getStorage(txn);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
		VirtualMachine vm = new VirtualMachine(generator, database, System.out);
		Context context = new Context(generator, vm);
		try {
			while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
				Tuple oldTuple = (Tuple) database.getTupleBinding().entryToObject(foundData);

				Value oldValue = ValueCharacter.select(generator,
						oldTuple.getValues()[oldAttributeIndex].toParsableString(oldType));
				context.push(oldValue);
				selector.execute(context);
				Value newValue = context.pop();
				oldTuple.getValues()[newAttributeIndex] = newValue;

				DatabaseEntry theData = new DatabaseEntry();
				database.getTupleBinding().objectToEntry(oldTuple, theData);
				cursor.putCurrent(theData);
			}
		} finally {
			cursor.close();
		}
	}

}