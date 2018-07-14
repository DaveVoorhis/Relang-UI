package org.reldb.relang.reli;

public class Attribute {
	private String name;
	private Class<?> type;

	public Attribute(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public String toString() {
		return name + " " + type;
	}
}
