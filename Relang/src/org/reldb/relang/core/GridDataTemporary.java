package org.reldb.relang.core;

import java.util.Vector;

import org.reldb.relang.core.Grid.RowAction;

public class GridDataTemporary implements GridData {

	private Heading heading = new Heading();
	private Vector<Vector<Object>> data = new Vector<>();
	
	@Override
	public int getColumnCount() {
		return heading.getColumnCount();
	}

	@Override
	public int getRowCount() {
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
		// TODO - need to make sure new column type is compatible (whatever that shall mean) with data type
		heading.setColumnType(column, type, defaultValue);
		// TODO - need to append to data column, possibly
	}

	@Override
	public Class<?> getColumnTypeAt(int column) {
		return heading.getColumnTypeAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
		heading.deleteColumnAt(column);
		// TODO - delete data columns here
	}
	
	/** Delete a row. */
	public void deleteRowAt(int row) {
		
	}
	
	/** Append a blank row. */
	public void appendRow() {
		
	}
	
	@Override
	public void setValue(int column, int row, Object value) {
		int columnCount = getColumnCount();
		if (column >= columnCount || column < 0)
			throw new InvalidValueException("ERROR: GridDataTemporary: Attempt to reference non-existent column " + column + " in a GridDataTemporary with column count " + columnCount);
		if (row < 0)
			throw new InvalidValueException("ERROR: GridDataTemporary: Attempt to reference non-existent row " + row);
		while (row >= getRowCount())
			appendRow();
		
	}

	@Override
	public Object getValue(int column, int row) {
		if (column >= getColumnCount() || column < 0)
			throw new InvalidValueException("ERROR: GridDataTemporary: Attempt to reference non-existent column " + column);
		if (row > getRowCount() || row < 0)
			throw new InvalidValueException("ERROR: GridDataTemporary: Attempt to reference non-existent row " + row);
		return data.get(column).get(row);
	}

	@Override
	public boolean isChanged(int column, int row) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getError(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowAction getAction(int row) {
		// TODO Auto-generated method stub
		return null;
	}

}
