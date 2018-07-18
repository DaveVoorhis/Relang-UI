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
			throw new InvalidValueException("ERROR: Tuples: Attempt to add a tuple with heading " + tuple.getHeading() + " which doesn't match Tuples heading " + heading);
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
	
}
