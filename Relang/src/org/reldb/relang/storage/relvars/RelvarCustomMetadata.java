package org.reldb.relang.storage.relvars;

import org.reldb.relang.storage.RelDatabase;

public abstract class RelvarCustomMetadata extends RelvarMetadata implements RelvarExternalMetadata {
	private static final long serialVersionUID = 0;

	public RelvarCustomMetadata(RelDatabase database, RelvarHeading headingDefinition, String owner) {
		super(database, headingDefinition, owner);
	}
	
	public abstract String tableClassName();
	
	public abstract String getType();

	@Override
	public boolean isExternal() {
		return true;
	}

	@Override
	public abstract RelvarGlobal getRelvar(String name, RelDatabase database);

	@Override
	public abstract void dropRelvar(RelDatabase database);
	
	@Override
	public abstract String getSourceDefinition();
}
