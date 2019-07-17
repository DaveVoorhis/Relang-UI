package org.reldb.relang.storage.catalog;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.values.*;
import org.reldb.relang.storage.RelDatabase;

public class RelvarCatalog extends RelvarSpecial {
	
	RelvarCatalog(RelDatabase database) {
		super(Catalog.relvarCatalog, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getCatalogTupleIterator(generator);
	}
	
}
