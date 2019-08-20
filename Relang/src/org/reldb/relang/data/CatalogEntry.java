package org.reldb.relang.data;

import java.io.Serializable;

public class CatalogEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final Class<?> type;
	public final CatalogMetadata metadata;
	
	public CatalogEntry(String name, Class<?> type, CatalogMetadata metadata) {
		this.name = name;
		this.type = type;
		this.metadata = metadata;
	}

	public String toString() {
		return String.format("CatalogEntry(\"%s\", %s, %s)", name, type.toString(), (metadata != null) ? metadata.toString() : "<null>"); 
	}
	
}
