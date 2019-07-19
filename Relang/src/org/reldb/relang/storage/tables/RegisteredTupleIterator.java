package org.reldb.relang.storage.tables;

import org.reldb.relang.data.TupleIterator;
import org.reldb.relang.storage.LocalDatabase;
import org.reldb.relang.storage.LocalTransaction;
import org.reldb.relang.utilities.ExceptionFatal;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseException;

public abstract class RegisteredTupleIterator extends TupleIterator implements Comparable<RegisteredTupleIterator> {

	private static long iteratorIDGenerator = 0;
	
	private Long iteratorID;
	private LocalDatabase database;
	
	protected Cursor cursor;
	protected LocalTransaction txn = null;
	
	public RegisteredTupleIterator(LocalDatabase database) {
		iteratorID = iteratorIDGenerator++;
		this.database = database;
	    database.registerTupleIterator(this);
	}
	
	public int hashCode() {
		return iteratorID.hashCode();
	}
	
	public int compareTo(RegisteredTupleIterator iterator) {
		return iteratorID.compareTo(iteratorID);
	}
	
	public boolean forceClose() {
		try {
			if (cursor != null) {
				cursor.close();
				database.commitTransaction(txn);
				return true;
			}
			return false;
		} catch (DatabaseException exp) {
    		exp.printStackTrace();
			throw new ExceptionFatal("RS0378: Unable to close cursor: " + exp.getMessage());
		}		
	}
	
	public void close() {
		try {
			forceClose();
		} finally {
			database.unregisterTupleIterator(this);
		}
	}
	
}