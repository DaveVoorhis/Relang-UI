package org.reldb.relang.data;

import java.util.Iterator;

/** A wrapper around a container to make it visible to, and possibly modifiable by, a Datasheet's GridPanel. */
public interface Data<T> {
	
	public Class<T> getType();
	
	public boolean isExtendable();
	public void extend(String name, Class<?> type);
	
	public boolean isReducable();
	public void reduce(String name);
	
	public boolean isRenameable();
	public void rename(String oldName, String newName);
	
	public boolean isTypeChangeable();
	public void changeType(String name, Class<?> type);
	
	public boolean isStrictlyReadonly();
	
	public int size();
	public Iterator<T> iterator();
}
