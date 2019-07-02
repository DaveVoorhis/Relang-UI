package org.reldb.relang.data;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class Heading {
	
	private static class ColumnType {
		public ColumnType(Class<?> type, Object defaultValue) {this.type = type; this.defaultValue = defaultValue;}
		public Class<?> type;
		public Object defaultValue;
		public String toString() {
			return type.getName().toString();
		}
	}

	private static class ColumnAttribute {
		public ColumnAttribute(String name, ColumnType typeAndDefault) {this.name = name; this.typeAndDefault = typeAndDefault;}
		public String name;
		public ColumnType typeAndDefault;
		public String toString() {
			return typeAndDefault + " " + name;
		}
	}
	
	private Vector<ColumnAttribute> columnAttributes = new Vector<>();
	private boolean frozen = false;
	
	private void checkFrozen() {
		if (frozen)
			throw new InvalidValueException("ERROR: Heading: " + this + " is in use and can't be changed.");		
	}
	
	public Heading() {}
	
	public void appendDefaultColumn() {
		checkFrozen();
		String name;
		int columnNumber = columnAttributes.size();
		do {
			name = "Col" + Integer.toString(columnNumber++);
		} while (hasColumnNamed(name));
		ColumnType columnType = new ColumnType(Object.class, new Object());
		columnAttributes.add(new ColumnAttribute(name, columnType));
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
		if (defaultValue == null)
			throw new InvalidValueException("ERROR: Heading: Attempt to set null default value.");
		if (attributeType == null)
			throw new InvalidValueException("ERROR: Heading: Attempt to set null attribute type.");
		if (attributeName == null)
			throw new InvalidValueException("ERROR: Heading: attempt to set null attribute name.");
		if (!(attributeType.isAssignableFrom(defaultValue.getClass())))
				throw new InvalidValueException("ERROR: Heading: defaultValue of type " + defaultValue.getClass() + " cannot be assigned to an " + attributeType);
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		ColumnType columnType = new ColumnType(attributeType, defaultValue);
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void appendColumn(String attributeName, Class<?> attributeType, Object defaultValue) {
		int columnNumber = columnAttributes.size();
		defineColumn(columnNumber, attributeName, attributeType, defaultValue);
	}

	public void setColumnName(int columnNumber, String attributeName) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		if (attributeName == null)
			throw new InvalidValueException("ERROR: Heading: attempt to set null attribute name.");
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException("ERROR: Heading: heading " + this + " already contains an attribute named " + attributeName);
		widenToIncludeColumnNumber(columnNumber);
		ColumnType columnType = getColumnType(columnNumber).typeAndDefault;
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void setColumnType(int columnNumber, Class<?> attributeType, Object defaultValue) {
		if (columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Invalid column number " + columnNumber);
		if (defaultValue == null)
			throw new InvalidValueException("ERROR: Heading: Attempt to set null default value.");
		if (attributeType == null)
			throw new InvalidValueException("ERROR: Heading: Attempt to set null attribute type.");
		checkFrozen();
		widenToIncludeColumnNumber(columnNumber);
		String attributeName = getColumnNameAt(columnNumber);
		if (!(attributeType.isAssignableFrom(defaultValue.getClass())))
			throw new InvalidValueException("ERROR: Heading: defaultValue of type " + defaultValue.getClass() + " cannot be assigned to an " + attributeType);		
		ColumnType columnType = new ColumnType(attributeType, defaultValue);
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void deleteColumnAt(int columnNumber) {
		checkFrozen();
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
			throw new InvalidValueException("ERROR: Heading: Attempt to delete column " + columnNumber + " in a heading with column count " + columnCount);
		columnAttributes.remove(columnNumber);
	}
	
	public int getColumnCount() {
		return columnAttributes.size();
	}
	
	public String toString() {
		String attributeString = columnAttributes.stream()
				.map(item -> item.typeAndDefault.type.getName() + " " + item.name)
				.collect(Collectors.joining(", "));
		return "{" + attributeString + "}";
	}

	private ColumnAttribute findColumnAttribute(String name) {
		for (var columnAttribute: columnAttributes)
			if (columnAttribute.name.contentEquals(name))
				return columnAttribute;
		return null;
	}
	
	public boolean hasColumnNamed(String name) {
		return findColumnAttribute(name) != null;
	}

	public Class<?> typeOf(String name) {
		var columnAttribute = findColumnAttribute(name);
		return (columnAttribute == null) ? null : columnAttribute.typeAndDefault.type;
	}

	public Object defaultValueOf(String name) {
		var columnAttribute = findColumnAttribute(name);
		return (columnAttribute == null) ? null : columnAttribute.typeAndDefault.defaultValue;
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
