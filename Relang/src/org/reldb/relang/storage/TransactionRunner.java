package org.reldb.relang.storage;

import org.reldb.relang.utilities.ExceptionFatal;

import com.sleepycat.je.*;

public abstract class TransactionRunner {
	private final static int retryLimit = 10;
	private boolean mustRollback = false;
	
	/** Request rollback of transaction when it completes. Otherwise, if it runs it will commit. */
	public void rollback() {
		mustRollback = true;
	}
	
	public abstract Object run(Transaction txn) throws Throwable;
	
	public Object execute(LocalDatabase environment) throws Throwable {
		boolean ran = false;
		Object returnvalue = null;
		LocalTransaction txn = null;
		for (int attempt=0; attempt<retryLimit && !mustRollback; attempt++) {
			txn = environment.beginTransaction();
			try {
				returnvalue = run(txn.getTransaction());
				ran = true;
				break;
			} catch (LockTimeoutException lte) {
				environment.rollbackTransaction(txn);
				Thread.sleep((long)(Math.random() * 5000.0));
			} catch (Throwable t) {
				environment.rollbackTransaction(txn);
				throw t;
			}
		}
		if (ran)
			if (mustRollback)
				environment.rollbackTransaction(txn);
			else
				environment.commitTransaction(txn);
		else
			throw new ExceptionFatal("RS0379: Transaction " + txn + " attempted " + retryLimit + " times.  Aborted.");
		return returnvalue;
	}
}
