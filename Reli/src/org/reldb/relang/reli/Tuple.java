package org.reldb.relang.reli;

import java.util.Vector;

public class Tuple {

	private Vector<String> attributeNames = new Vector<String>();
	private Vector<Object> attributeValues = new Vector<Object>();
	
	Tuple() {}
	
	void addAttributeName(String name) {
		attributeNames.add(name);
	}
	
	void addValue(Object value, boolean b) {
		attributeValues.add(value);
	}

	/** Get quantity of attributes in this Tuple. */
	public int getAttributeCount() {
		return attributeValues.size();
	}
	
	/** Get ith attribute name. */
	public String getAttributeName(int i) {
		return attributeNames.get(i);
	}
	
	/** Get ith attribute value. */
	public Object getAttributeValue(int i) {
		return attributeValues.get(i);
	}
	
	/** Get index of a given attribute name. -1 if not found. */
	public int getIndexOf(String name) {
		return attributeNames.indexOf(name);
	}
	
	/** Get attribute Value for given attribute Name.  Return null if not found. */
	public Object getAttributeValue(String name) {
		int index = getIndexOf(name);
		if (index < 0)
			return null;
		return getAttributeValue(index);
	}
	
	/** Shortcut for getAttributeValue */
	public Object get(int i) {
		return getAttributeValue(i);
	}
	
	/** Shortcut for getAttributeValue */
	public Object get(String name) {
		return getAttributeValue(name);
	}
	
	/** True if this is the end-of-list Tuple in a set of TupleS. */
	public boolean isNull() {
		return false;
	}

	public int toInt() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to int.");
	}

	public long toLong() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to long.");
	}

	public double toDouble() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to double.");
	}

	public float toFloat() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to float.");
	}

	public boolean toBoolean() throws InvalidValueException {
		throw new InvalidValueException("Tuple can't be cast to boolean.");
	}
	
	public String toString(int depth) {
		String tuples = "";
		for (int i=0; i<getAttributeCount(); i++) {
			if (tuples.length() > 0)
				tuples += ", ";
			tuples += getAttributeName(i) + " " + getAttributeValue(i).toString();
		}
		return "TUPLE {" + tuples + "}";
	}
	
	public String toString() {
		return toString(0);
	}
	
	public String toCSV() {
		String line = "";
		for (int i=0; i<getAttributeCount(); i++) {
			if (line.length() > 0)
				line += ",";
			line += "\"" + getAttributeValue(i).toString().replaceAll("\"", "\"\"") + "\"";
		}
		return line;
	}
	
}
