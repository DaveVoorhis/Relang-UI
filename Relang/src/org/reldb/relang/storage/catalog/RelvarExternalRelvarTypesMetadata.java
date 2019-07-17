package org.reldb.relang.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.types.*;
import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.RelvarGlobal;
import org.reldb.relang.storage.relvars.RelvarHeading;
import org.reldb.relang.storage.relvars.RelvarMetadata;
import org.reldb.relang.storage.relvars.external.Registry;

public class RelvarExternalRelvarTypesMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		return Registry.getHeading();
	}
	
	static RelvarHeading getNewKeyDefinition() {
		RelvarHeading keydef = new RelvarHeading(getNewHeading());
		SelectAttributes keyAttributes = new SelectAttributes();
		keyAttributes.add("Identifier");
		keydef.addKey(keyAttributes);
		return keydef;
	}
	
	public RelvarExternalRelvarTypesMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
		return getNewKeyDefinition();
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarExternalRelvarTypes(database);
	}

	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0480: The " + Catalog.relvarExternalRelvarTypes + " relvar may not be dropped.");		
	}
}
