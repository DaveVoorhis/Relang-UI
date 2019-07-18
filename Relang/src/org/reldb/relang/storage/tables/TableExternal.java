package org.reldb.relang.storage.tables;

import org.reldb.relang.data.TupleIterator;

public interface TableExternal {
	
	public enum DuplicateHandling {
		DUP_COUNT,
		AUTOKEY,
		DUP_REMOVE
	}
	
	public TupleIterator iterator();
	
	public long getCardinality();

	public long insert(Generator generator, ValueRelation relation);
	
	public void purge();

	public void delete(Generator generator, ValueTuple tuple);

	public long delete(Generator generator, RelTupleFilter relTupleFilter);

	public long delete(Generator generator, TupleFilter filter);

	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded);

	public long update(Generator generator, RelTupleMap relTupleMap);

	public long update(Generator generator, RelTupleFilter relTupleFilter, RelTupleMap relTupleMap);
}
