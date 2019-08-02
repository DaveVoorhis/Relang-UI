package org.reldb.relang.data.temporary;

import java.util.Vector;

import org.reldb.relang.data.Data;
import org.reldb.relang.data.Heading;
import org.reldb.relang.data.InvalidValueException;
import org.reldb.relang.strings.Str;

import static org.reldb.relang.strings.Strings.*;

public class DataTemporary implements Data {

	private Heading heading = new Heading();
	private Vector<Vector<Object>> data = new Vector<>();
	
	// For debugging.
	public void dump() {
		data.forEach(row -> {
			row.forEach(columnValue -> System.out.print(columnValue.toString() + " "));
			System.out.println();
		});
	}
	
	@Override
	public int getColumnCount() {
		return heading.getColumnCount();
	}

	@Override
	public long getRowCount() {
		return data.size();
	}

	@Override
	public void setColumnName(int column, String name) {
		heading.setColumnName(column, name);
	}

	@Override
	public String getColumnNameAt(int column) {
		return heading.getColumnNameAt(column);
	}

	@Override
	public boolean hasColumnNamed(String name) {
		return heading.hasColumnNamed(name);
	}

	@Override
	public void setColumnType(int column, Class<?> type, Object defaultValue) {
		if (type == null)
			throw new InvalidValueException(Str.ing(ErrTypeNotNull));
		int columnCount = getColumnCount();
		if (column < 0)
			throw new InvalidValueException(Str.ing(ErrColumnNotFound, column, columnCount));
		else if (column < heading.getColumnCount()) {
			if (!data.stream().allMatch(tuple -> type.isAssignableFrom(tuple.get(column).getClass())))
				throw new InvalidValueException(Str.ing(ErrCannotBeAssigned, column, type.getName()));
		}
		heading.setColumnType(column, type, defaultValue);
	}

	@Override
	public String appendDefaultColumn() {
		String newColumnName = heading.appendDefaultColumn();
		Object defaultValueForNewColumn = heading.getDefaultValueAt(getColumnCount() - 1);
		data.forEach(row -> row.add(defaultValueForNewColumn));
		return newColumnName;
	}

	@Override
	public Class<?> getColumnTypeAt(int column) {
		return heading.getColumnTypeAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
		heading.deleteColumnAt(column);
		data.forEach(row -> row.remove(column));
	}
	
	/** Delete a row. */
	public void deleteRowAt(long row) {
		if (row < 0 || row >= data.size())
			throw new InvalidValueException(Str.ing(ErrNonexistentRow3, row));
		data.remove((int)row);
	}
	
	/** Append a blank row. */
	public void appendRow() {
		var row = new Vector<Object>();
		for (int column = 0; column < heading.getColumnCount(); column++)
			row.add(heading.getDefaultValueAt(column));
		data.add(row);
	}
	
	@Override
	public void setValue(int column, long row, Object value) {
		int columnCount = getColumnCount();
		if (column >= columnCount || column < 0)
			throw new InvalidValueException(Str.ing(ErrNonexistentColumn4, column, columnCount));
		if (row < 0)
			throw new InvalidValueException(Str.ing(ErrNonexistentRow4, row));
		Class<?> headingColumnType = heading.getColumnTypeAt(column);
		Class<?> valueType = value.getClass();
		if (!headingColumnType.isAssignableFrom(valueType))
			throw new InvalidValueException(Str.ing(ErrTypeMismatch3, valueType.getName(), headingColumnType.getName()));
		while (row >= getRowCount())
			appendRow();
		data.get((int)row).set(column, value);
	}

	@Override
	public Object getValue(int column, long row) {
		if (column >= getColumnCount() || column < 0)
			throw new InvalidValueException(Str.ing(ErrNonexistentColumn5, column));
		if (row > getRowCount() || row < 0)
			throw new InvalidValueException(Str.ing(ErrNonexistentRow5, row));
		return data.get((int)row).get(column);
	}

	@Override
	public boolean isChanged(int column, long row) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getError(long row) {
		// TODO Auto-generated method stub
		return null;
	}

}
