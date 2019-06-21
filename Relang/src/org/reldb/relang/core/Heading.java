package org.reldb.relang.core;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

public class Heading {

	private HashMap<String, Class<?>> attributes = new HashMap<>();
	private Vector<String> attributeNames = new Vector<>();
	private Vector<Class<?>> attributeTypes = new Vector<>();
	
	public Heading() {}
	
	public void add(String attributeName, Class<?> attributeType) {
		if (attributes.containsKey(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		attributes.put(attributeName, attributeType);
		attributeNames.add(attributeName);
		attributeTypes.add(attributeType);
	}
	
	public int getCardinality() {
		return attributes.size();
	}
	
	public String toString() {
		String attributeString = attributes.entrySet().stream()
				.map(entry -> entry.getKey() + " " + entry.getValue().toString())
				.collect(Collectors.joining(", "));
		return "{" + attributeString + "}";
	}

	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public Class<?> typeOf(String name) {
		return attributes.get(name);
	}

	public String getAttributeNameAt(int columnIndex) {
		return attributeNames.get(columnIndex);
	}

	public Class<?> getAttributeTypeAt(int columnIndex) {
		return attributeTypes.get(columnIndex);
	}

	public Vector<String> getAttributeNames() {
		return attributeNames;
	}
}
