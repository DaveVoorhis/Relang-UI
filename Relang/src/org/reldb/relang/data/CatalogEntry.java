package org.reldb.relang.data;

import java.io.Serializable;

public class CatalogEntry extends Heading implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final Heading heading;
	public final CatalogMetadata metadata;
	
	public CatalogEntry(String name, Heading heading, CatalogMetadata metadata) {
		this.name = name;
		this.heading = heading;
		this.metadata = metadata;
	}

	public String toString() {
		return String.format("CatalogEntry(\"%s\", %s, %s)", name, heading.toString(), (metadata != null) ? metadata.toString() : "<null>"); 
	}
	
}
