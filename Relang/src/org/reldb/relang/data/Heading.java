package org.reldb.relang.data;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.reldb.relang.strings.Str;

import static org.reldb.relang.strings.Strings.*;

public class Heading implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Class<?> defaultNewColumnClass = String.class;
	private static final Object defaultNewColumnDefaultValue = new String();
	
	private static class ColumnType implements Serializable {
		private static final long serialVersionUID = 1L;
		public ColumnType(Class<?> type, Object defaultValue) {
			this.type = type; 
			this.defaultValue = defaultValue;
		}
		public Class<?> type;
		public Object defaultValue;
		public String toString() {
			return type.getName().toString();
		}
	}

	private static class ColumnAttribute implements Serializable {
		private static final long serialVersionUID = 1L;
		public ColumnAttribute(String name, ColumnType typeAndDefault) {
			this.name = name; 
			this.typeAndDefault = typeAndDefault;
		}
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
			throw new InvalidValueException(Str.ing(ErrInUse,  this.toString()));	
	}
	
	public Heading() {}
	
	public String appendDefaultColumn() {
		checkFrozen();
		String name;
		int columnNumber = columnAttributes.size();
		do {
			name = "Col" + Integer.toString(columnNumber++);
		} while (hasColumnNamed(name));
		ColumnType columnType = new ColumnType(defaultNewColumnClass, defaultNewColumnDefaultValue);
		columnAttributes.add(new ColumnAttribute(name, columnType));
		return name;
	}

	public void widenToIncludeColumnNumber(int columnNumber) {
		if (columnNumber < 0)
			throw new InvalidValueException(Str.ing(ErrInvalidColumn1, columnNumber));
		while (columnAttributes.size() <= columnNumber)
			appendDefaultColumn();
	}
	
	public void defineColumn(int columnNumber, String attributeName, Class<?> attributeType, Object defaultValue) {
		if (columnNumber < 0)
			throw new InvalidValueException(Str.ing(ErrInvalidColumn2, columnNumber));
		if (defaultValue == null)
			throw new InvalidValueException(Str.ing(ErrAttemptToSetNullDefault));
		if (attributeType == null)
			throw new InvalidValueException(Str.ing(ErrAttemptToSetNullAttribute1));
		if (attributeName == null)
			throw new InvalidValueException(Str.ing(ErrAttemptToSetNullAttribute2));
		if (!(attributeType.isAssignableFrom(defaultValue.getClass())))
				throw new InvalidValueException(Str.ing(ErrDefaultTypeMismatch1, defaultValue.getClass().toString(), attributeType.toString()));
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException(Str.ing(ErrAttributeDuplicate1, this.toString(), attributeName));
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
			throw new InvalidValueException(Str.ing(ErrInvalidColumn3, columnNumber));
		if (attributeName == null)
			throw new InvalidValueException(Str.ing(ErrAttemptToSetNullAttribute3));
		checkFrozen();
		if (hasColumnNamed(attributeName))
			throw new InvalidValueException(Str.ing(ErrAttributeDuplicate2, this.toString(), attributeName));
		widenToIncludeColumnNumber(columnNumber);
		ColumnType columnType = getColumnType(columnNumber).typeAndDefault;
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void setColumnType(int columnNumber, Class<?> attributeType, Object defaultValue) {
		if (columnNumber < 0)
			throw new InvalidValueException(Str.ing(ErrInvalidColumn4, columnNumber));
		if (defaultValue == null)
			throw new InvalidValueException(Str.ing(ErrAttemptToSetNullDefault2));
		if (attributeType == null)
			throw new InvalidValueException(Str.ing(ErrAttemptToSetNullAttributeType));
		checkFrozen();
		widenToIncludeColumnNumber(columnNumber);
		String attributeName = getColumnNameAt(columnNumber);
		if (!(attributeType.isAssignableFrom(defaultValue.getClass())))
			throw new InvalidValueException(Str.ing(ErrDefaultTypeMismatch2, defaultValue.getClass().toString(), attributeType.toString()));
		ColumnType columnType = new ColumnType(attributeType, defaultValue);
		columnAttributes.set(columnNumber, new ColumnAttribute(attributeName, columnType));
	}
	
	public void deleteColumnAt(int columnNumber) {
		checkFrozen();
		int columnCount = getColumnCount();
		if (columnNumber >= columnCount || columnNumber < 0)
			throw new InvalidValueException(Str.ing(ErrInvalidColumn5, columnNumber, columnCount));
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
			throw new InvalidValueException(Str.ing(ErrInvalidColumn6, columnNumber, columnCount));
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
