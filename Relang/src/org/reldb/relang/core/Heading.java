package org.reldb.relang.core;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

public class Heading {

	private HashMap<String, Class<?>> columns = new HashMap<>();
	private Vector<String> columnNames = new Vector<>();
	private Vector<Class<?>> columnTypes = new Vector<>();
	private boolean frozen = false;
	
	private void checkFrozen() {
		if (frozen)
			throw new InvalidValueException("ERROR: Heading: " + this + " is in use and can't be changed.");		
	}
	
	public Heading() {}
	
	public void appendDefaultColumn() {
		checkFrozen();
		int columnNumber = columnNames.size();
		String name;
		do {
			name = Integer.toString(columnNumber++);
		} while (hasColumnNamed(name));
		columns.put(name, Object.class);
		int index = columnNumber - 1;
		columnNames.set(index, name);
		columnTypes.set(index, Object.class);
	}

	public void widenToIncludeColumnNumber(int columnNumber) {
		while (columnNames.size() < columnNumber)
			appendDefaultColumn();		
	}
	
	public void defineColumn(int columnNumber, String attributeName, Class<?> attributeType) {
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		columns.put(attributeName, attributeType);
		columnNames.set(columnNumber, attributeName);
		columnTypes.set(columnNumber, attributeType);
	}
	
	public void appendColumn(String attributeName, Class<?> attributeType) {
		int columnNumber = columnNames.size();
		defineColumn(columnNumber, attributeName, attributeType);
	}

	public void setColumnName(int columnNumber, String attributeName) {
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		Class<?> type = getColumnTypeAt(columnNumber);
		columns.put(attributeName, type);
		columnNames.set(columnNumber, attributeName);
	}
	
	public void setColumnType(int columnNumber, Class<?> attributeType) {
		checkFrozen();
		widenToIncludeColumnNumber(columnNumber);
		String attributeName = getColumnNameAt(columnNumber);
		columns.put(attributeName, attributeType);
		columnTypes.set(columnNumber, attributeType);
	}
	
	public void deleteColumnAt(int columnNumber) {
		checkFrozen();
		int columnCount = getColumnCount();
		if (columnNumber > columnCount - 1)
			throw new InvalidValueException("ERROR: Heading: Attempt to delete column " + columnNumber + " in a heading with column count " + columnCount);
		String name = getColumnNameAt(columnNumber);
		columns.remove(name);
		columnNames.remove(columnNumber);
		columnTypes.remove(columnNumber);
	}
	
	public int getColumnCount() {
		return columns.size();
	}
	
	public String toString() {
		String attributeString = columns.entrySet().stream()
				.map(entry -> entry.getKey() + " " + entry.getValue().toString())
				.collect(Collectors.joining(", "));
		return "{" + attributeString + "}";
	}

	public boolean hasColumnNamed(String name) {
		return columns.containsKey(name);
	}

	public Class<?> typeOf(String name) {
		return columns.get(name);
	}

	public String getColumnNameAt(int columnIndex) {
		return columnNames.get(columnIndex);
	}

	public Class<?> getColumnTypeAt(int columnIndex) {
		return columnTypes.get(columnIndex);
	}

	public Vector<String> getAttributeNames() {
		return columnNames;
	}

	public void freeze() {
		frozen = true;
	}
}
