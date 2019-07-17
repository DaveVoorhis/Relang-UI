package org.reldb.relang.storage.tables;


import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.RelvarHeading;

import com.sleepycat.je.*;

public class TablePrivate extends Table {

	private Storage rawTable;
	
	public TablePrivate(RelDatabase database, Storage rawTable, RelvarHeading headingDefinition) {
		super(database, headingDefinition);
		setTable(rawTable);
	}
	
	public void setTable(Storage rawTable) {
		this.rawTable = rawTable;
	}
	
	@Override
	public Storage getStorage(Transaction txn) {
		return rawTable;
	}

}
