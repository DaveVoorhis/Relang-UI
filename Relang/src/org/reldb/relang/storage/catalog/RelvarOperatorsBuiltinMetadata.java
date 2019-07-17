package org.reldb.relang.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.RelvarGlobal;
import org.reldb.relang.storage.relvars.RelvarHeading;
import org.reldb.relang.storage.relvars.RelvarMetadata;

public class RelvarOperatorsBuiltinMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Signature", TypeCharacter.getInstance());
		heading.add("ReturnsType", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Signature");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarOperatorsBuiltinMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
		return getNewKeyDefinition();
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarOperatorsBuiltin(database);
	}

	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0202: The " + Catalog.relvarOperatorsBuiltin + " relvar may not be dropped.");		
	}	
}
