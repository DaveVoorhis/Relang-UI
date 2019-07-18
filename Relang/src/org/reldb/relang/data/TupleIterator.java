package org.reldb.relang.data;

import java.io.Closeable;
import java.util.Iterator;

public abstract class TupleIterator implements Iterator<Tuple>, Closeable, AutoCloseable {
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public abstract boolean hasNext();
	
	public abstract Tuple next();
	
	public abstract void close();
}
