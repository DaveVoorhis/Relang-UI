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
	public void setColumnType(int column, Class<?> type) {
		heading.setColumnType(column, type);
	}

	@Override
	public Class<?> getColumnTypeAt(int column) {
		return heading.getColumnTypeAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
		heading.deleteColumnAt(column);
	}
	
	@Override
	public void setValue(int column, int row, Object value) {
		int columnCount = getColumnCount();
		if (column > columnCount - 1)
			throw new InvalidValueException("ERROR: GridDataTemporary: Attempt to reference non-existent column " + column + " in a GridDataTemporary with column count " + columnCount);
		// TODO - complete this!
	}

	@Override
	public Object getValue(int column, int row) {
		// TODO Auto-generated method stub
		return null;
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
