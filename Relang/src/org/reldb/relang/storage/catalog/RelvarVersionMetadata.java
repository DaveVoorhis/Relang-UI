package org.reldb.relang.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.RelvarGlobal;
import org.reldb.relang.storage.relvars.RelvarHeading;
import org.reldb.relang.storage.relvars.RelvarMetadata;

public class RelvarVersionMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("minor", TypeInteger.getInstance());
		heading.add("major", TypeInteger.getInstance());
		heading.add("revision", TypeInteger.getInstance());
		heading.add("copyright", TypeCharacter.getInstance());
		heading.add("license", TypeCharacter.getInstance());
		heading.add("versionnum", TypeCharacter.getInstance());
		heading.add("redistribution", TypeCharacter.getInstance());
		heading.add("release", TypeCharacter.getInstance());
		heading.add("versionstr", TypeCharacter.getInstance());
		heading.add("warranty", TypeCharacter.getInstance());
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		RelvarHeading keydef = new RelvarHeading(getNewHeading());
		keydef.addKey(new SelectAttributes());
		return keydef;
	}
	
	public RelvarVersionMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarVersion(database);
	}

	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0206: The " + Catalog.relvarVersion + " relvar may not be dropped.");		
	}
}
