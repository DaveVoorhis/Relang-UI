package org.reldb.relang.reli.core;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Tuple {

	private HashMap<String, Object> attributes = new HashMap<>();
	private Heading heading;
	
	public Tuple(Heading heading) {
		this.heading = heading;
	}

	public Heading getHeading() {
		return heading;
	}

	public Object getAttributeValue(String name) {
		return attributes.get(name);
	}

	public void setAttributeValue(String name, Object value) {
		System.out.println("Tuple: setAttributeValue for " + name + " to " + value);
		if (!heading.hasAttribute(name))
			throw new InvalidValueException("ERROR: Tuple: tuple with heading " + heading + " doesn't have an attribute named " + name);
		if (heading.typeOf(name) != value.getClass())
			throw new InvalidValueException("ERROR: Tuple: tuple has heading " + heading + " where type of attribute " + name + " is " + heading.typeOf(name) + " but attempting to set the attribute value to " + value.getClass());
		attributes.put(name, value);
	}

	public String toString() {
		String tuples = attributes.entrySet().stream()
				.map(entry -> entry.getKey() + " " + entry.getValue().toString())
				.collect(Collectors.joining(", "));
		return "TUPLE {" + tuples + "}";
	}
	
}
