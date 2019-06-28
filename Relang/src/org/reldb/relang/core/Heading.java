package org.reldb.relang.core;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class Heading {
	
	private static class ColumnType {
		public ColumnType(Class<?> type, Object defaultValue) {this.type = type; this.defaultValue = defaultValue;}
		public Class<?> type;
		public Object defaultValue;
	}

	private static class ColumnAttribute {
		public ColumnAttribute(String name, ColumnType typeAndDefault) {this.name = name; this.typeAndDefault = typeAndDefault;}
		public String name;
		public ColumnType typeAndDefault;
	}
	
	private HashMap<String, ColumnType> columnLookup = new HashMap<>();
	private Vector<ColumnAttribute> columnAttributes = new Vector<>();
	private boolean frozen = false;
	
	private void checkFrozen() {
		if (frozen)
			throw new InvalidValueException("ERROR: Heading: " + this + " is in use and can't be changed.");		
	}
	
	public Heading() {}
	
	public void appendDefaultColumn() {
		checkFrozen();
		int columnNumber = columnAttributes.size();
		String name;
		do {
			name = "Col" + Integer.toString(columnNumber++);
		} while (hasColumnNamed(name));
		ColumnType columnType = new ColumnType(Object.class, new Object());
		columnLookup.put(name, columnType);
		int index = columnNumber - 1;
		columnAttributes.add(index, new ColumnAttribute(name, columnType));
	}

	public void widenToIncludeColumnNumber(int columnNumber) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		while (columnAttributes.size() <= columnNumber)
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
		columnLookup.put(attributeName, columnType);
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void appendColumn(String attributeName, Class<?> attributeType, Object defaultValue) {
		int columnNumber = columnAttributes.size();
		defineColumn(columnNumber, attributeName, attributeType, defaultValue);
	}

	public void setColumnName(int columnNumber, String attributeName) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		ColumnType columnType = getColumnType(columnNumber).typeAndDefault;
		columnLookup.put(attributeName, columnType);
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
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
		columnLookup.put(attributeName, columnType);
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void deleteColumnAt(int columnNumber) {
		checkFrozen();
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Attempt to delete column " + columnNumber + " in a heading with column count " + columnCount);
		String name = getColumnNameAt(columnNumber);
		columnLookup.remove(name);
		columnAttributes.remove(columnNumber);
	}
	
	public int getColumnCount() {
		return columnLookup.size();
	}
	
	public String toString() {
		String attributeString = columnAttributes.stream()
				.map(item -> item.typeAndDefault.type.getName() + " " + item.name)
				.collect(Collectors.joining(", "));
		return "{" + attributeString + "}";
	}

	public boolean hasColumnNamed(String name) {
		return columnLookup.containsKey(name);
	}

	public Class<?> typeOf(String name) {
		return columnLookup.get(name).type;
	}

	public Object defaultValueOf(String name) {
		return columnLookup.get(name).defaultValue;
	}

	private ColumnAttribute getColumnType(int columnNumber) {
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: invalid column number " + columnNumber + " in a heading with column count " + columnCount);		
		return columnAttributes.get(columnNumber);
	}
	
	public String getColumnNameAt(int columnNumber) {
		return getColumnType(columnNumber).name;
	}
	
	public Class<?> getColumnTypeAt(int columnNumber) {
		return getColumnType(columnNumber).typeAndDefault.type;
	}

	public Object getDefaultValueAt(int columnNumber) {
		return getColumnType(columnNumber).typeAndDefault.defaultValue;
	}

	public List<String> getAttributeNames() {
		return columnAttributes.stream().map(item -> item.name).collect(Collectors.toList());
	}

	public void freeze() {
		frozen = true;
	}
}
