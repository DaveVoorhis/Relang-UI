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
		// TODO Auto-generated method stub
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isColumnNameUsed(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setColumnType(int column, Class<?> type) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<?> getColumnType(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(int column, int row, Object value) {
		// TODO Auto-generated method stub
		System.out.println("GridDataTemporary: attempt to set " + column + ", " + row + " with " + value.toString());
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
