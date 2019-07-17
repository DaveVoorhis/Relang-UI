package org.reldb.relang.storage.relvars;

import org.reldb.relang.storage.RelDatabase;

public interface RelvarExternalMetadata {
	
	public RelvarGlobal getRelvar(String name, RelDatabase database);
	
	public void dropRelvar(RelDatabase database);
	
	public String getSourceDefinition();
}
