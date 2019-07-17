package org.reldb.relang.storage.catalog;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.values.*;
import org.reldb.relang.storage.RelDatabase;
import org.reldb.relang.storage.relvars.external.Registry;

public class RelvarExternalRelvarTypes extends RelvarSpecial {
	
	RelvarExternalRelvarTypes(RelDatabase database) {
		super(Catalog.relvarExternalRelvarTypes, database);
	}

	public long getCardinality(Generator generator) {
		return Registry.getCardinality();
	}
	
	// Get a TupleIterator
	public TupleIterator iterator(final Generator generator) {
		return Registry.getRegistry(generator);
	}
}
