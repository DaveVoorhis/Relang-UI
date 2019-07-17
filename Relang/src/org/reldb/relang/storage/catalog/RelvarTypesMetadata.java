package org.reldb.relang.storage.catalog;

import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.RelvarGlobal;
import org.reldb.relang.storage.relvars.RelvarHeading;

public class RelvarTypesMetadata extends RelvarSystemMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getTypeReferenceHeading() {
		Heading subtypeHeading = new Heading();
		subtypeHeading.add("Name", TypeCharacter.getInstance());
		return subtypeHeading;
	}
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		heading.add("Owner", TypeCharacter.getInstance());
		heading.add("CreationSequence", TypeInteger.getInstance());
		heading.add("Language", TypeCharacter.getInstance());
		heading.add("Subtypes", new TypeRelation(getTypeReferenceHeading()));
		heading.add("Supertypes", new TypeRelation(getTypeReferenceHeading()));
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarTypesMetadata(RelDatabase database, String name) {
		super(database, name, getNewKeyDefinition());
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarTypes(name, database, this);
	}
	
}
