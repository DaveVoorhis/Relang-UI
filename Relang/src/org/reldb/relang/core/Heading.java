package org.reldb.relang.core;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

public class Heading {
	
	private static class ColumnType {
		public ColumnType(Class<?> type, Object defaultValue) {this.type = type; this.defaultValue = defaultValue;}
		public Class<?> type;
		public Object defaultValue;
	}

	private HashMap<String, ColumnType> columns = new HashMap<>();
	private Vector<String> columnNames = new Vector<>();
	private Vector<ColumnType> columnTypes = new Vector<>();
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
		ColumnType columnType = new ColumnType(Object.class, new Object());
		columns.put(name, columnType);
		int index = columnNumber - 1;
		columnNames.set(index, name);
		columnTypes.set(index, columnType);
	}

	public void widenToIncludeColumnNumber(int columnNumber) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		while (columnNames.size() < columnNumber)
			appendDefaultColumn();
	}
	
	public void defineColumn(int columnNumber, String attributeName, Class<?> attributeType, Object defaultValue) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		if (!(attributeType.isAssignableFrom(defaultValue.getClass())))
				throw new InvalidValueException("ERROR: Heading: defaultValue of type " + defaultValue.getClass() + " cannot be assigned to an " + attributeType);
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		ColumnType columnType = new ColumnType(attributeType, defaultValue);
		columns.put(attributeName, columnType);
		columnNames.set(columnNumber, attributeName);
		columnTypes.set(columnNumber, columnType);
	}
	
	public void appendColumn(String attributeName, Class<?> attributeType, Object defaultValue) {
		int columnNumber = columnNames.size();
		defineColumn(columnNumber, attributeName, attributeType, defaultValue);
	}

	public void setColumnName(int columnNumber, String attributeName) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		ColumnType columnType = getColumnType(columnNumber);
		columns.put(attributeName, columnType);
		columnNames.set(columnNumber, attributeName);
	}
	
	public void setColumnType(int columnNumber, Class<?> attributeType, Object defaultValue) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		checkFrozen();
		widenToIncludeColumnNumber(columnNumber);
		String attributeName = getColumnNameAt(columnNumber);
		if (!(attributeType.isAssignableFrom(defaultValue.getClass())))
			throw new InvalidValueException("ERROR: Heading: defaultValue of type " + defaultValue.getClass() + " cannot be assigned to an " + attributeType);		
		ColumnType columnType = new ColumnType(attributeType, defaultValue);
		columns.put(attributeName, columnType);
		columnTypes.set(columnNumber, columnType);
	}
	
	public void deleteColumnAt(int columnNumber) {
		checkFrozen();
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
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
		return columns.get(name).type;
	}

	public Object defaultValueOf(String name) {
		return columns.get(name).defaultValue;
	}

	
	public String getColumnNameAt(int columnNumber) {
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: invalid column number " + columnNumber + " in a heading with column count " + columnCount);		
		return columnNames.get(columnNumber);
	}

	private ColumnType getColumnType(int columnNumber) {
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: invalid column number " + columnNumber + " in a heading with column count " + columnCount);		
		return columnTypes.get(columnNumber);
	}
	
	public Class<?> getColumnTypeAt(int columnNumber) {
		return getColumnType(columnNumber).type;
	}

	public Object getDefaultValueAt(int columnNumber) {
		return getColumnType(columnNumber).defaultValue;
	}
	
	public Vector<String> getAttributeNames() {
		return columnNames;
	}

	public void freeze() {
		frozen = true;
	}
}
