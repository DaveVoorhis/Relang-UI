package org.reldb.relang.data.containers;

import java.util.Iterator;

/** Machinery to make containers visible to Datasheets. */
public interface ContainerWrapper<T> {
	public boolean isExtendable();
	public void extend(String name, Class<?> type);
	public boolean isReducable();
	public void reduce(String name);
	public boolean isStrictlyReadonly();
	public int size();
	public Iterator<T> iterator();
}
