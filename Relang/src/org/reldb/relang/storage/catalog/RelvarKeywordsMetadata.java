package org.reldb.relang.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.languages.tutoriald.Keywords;
import org.reldb.rel.v0.types.*;
import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.RelvarGlobal;
import org.reldb.relang.storage.relvars.RelvarHeading;
import org.reldb.relang.storage.relvars.RelvarMetadata;

public class RelvarKeywordsMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		return Keywords.getHeading();
	}
	
	static RelvarHeading getNewKeyDefinition() {
		RelvarHeading keydef = new RelvarHeading(getNewHeading());
		SelectAttributes keyAttributes = new SelectAttributes();
		keyAttributes.add("Keyword");
		keydef.addKey(keyAttributes);
		return keydef;
	}
	
	public RelvarKeywordsMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
		return getNewKeyDefinition();
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarKeywords(database);
	}

	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0481: The " + Catalog.relvarKeywords + " relvar may not be dropped.");		
	}
}
