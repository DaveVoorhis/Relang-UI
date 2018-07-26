package org.reldb.relang.reli;

import java.util.Iterator;
import java.util.LinkedList;

public class Tuples implements Iterable<Tuple>{
	private LinkedList<Tuple> tuples = new LinkedList<>();
	private Heading heading;
	
	public Tuples(Heading heading) {
		this.heading = heading;
	}

	void add(Tuple tuple) {
		if (tuple.getHeading() != heading)
			throw new InvalidValueException("ERROR: Tuples: Attempt to add a Tuple with Heading " + tuple.getHeading() + " which isn't the Tuples Heading " + heading);
		tuples.add(tuple);
	}
	
	public Heading getHeading() {
		return heading;
	}

	public String toString() {
		String lines = "";
		for (Tuple tuple: this)
			lines += ((lines.length() > 0) ? ",\n" : "") + "\t" + tuple.toString();
		return heading + " {\n" + lines + "}"; 
	}

	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}

	// Grow every tuple by one attribute.
	public void extend(String attributeName, Object newValue) {
		if (newValue != null)
			heading.add(attributeName, newValue.getClass());
		else
			heading.add(attributeName,  String.class);
		/*
		for (Tuple tuple: tuples) {
			tuples.
			tuple.extend(newValue);
		}
		*/
	}
	
}
