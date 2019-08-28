package org.reldb.relang.data;

import java.io.Serializable;
import java.util.Map;

/** A wrapper around a container to make it visible to, and possibly modifiable by, a Datasheet's GridPanel. */
public interface Data<K extends Serializable, V extends Serializable> {
	
	/**
	 * Get the type contained by the container.
	 * 
	 * @return - Class<T>
	 */
	public Class<V> getType();
	
	/** True if attributes can be added. */
	public boolean isExtendable();
	
	/** Add an attribute with a given name and type.
	 * 
	 * @param name - String - attribute name
	 * @param type - Class<? extends Serializable> - attribute type
	 */
	public void extend(String name, Class<? extends Serializable> type);
	
	/** True if attributes can be removed. */
	public boolean isRemovable();
	
	/** Remove an attribute with a given name. 
	 * 
	 * @param name - String - attribute name.
	 */
	public void remove(String name);
	
	/** True if attributes can be renamed. */
	public boolean isRenameable();
	
	/** Rename an attribute with a given name. 
	 * 
	 * @param oldName - String - current (or old) name.
	 * @param newName - String - new name.
	 */
	public void rename(String oldName, String newName);
	
	/** True if attributes can be assigned to a new type. */
	public boolean isTypeChangeable();
	
	/** Change the type of a given attribute.
	 * 
	 * To change type, given an attribute of current type T there must exist a constructor of the form T'(T) where T' is the new type. 
	 * 
	 * @param name - String - attribute name.
	 * @param type - Class<? extends Serializable> - new attribute type.
	 */
	public void changeType(String name, Class<? extends Serializable> type);
	
	/** True if this Data is read-only and will not accept data updates. */
	public boolean isStrictlyReadonly();
	
	@FunctionalInterface
	public static interface Transaction<KK, VV> {
		public abstract void go(Map<KK, VV> map);
	}
	
	/**
	 * Access the underlying data container to retrieve data or perform an update.
	 * 
	 * @param xaction - Transaction - typically a lambda expression representing data retrieval or update.
	 */
	public void access(Transaction<K, V> xaction);
}
