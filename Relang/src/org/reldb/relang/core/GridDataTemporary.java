package org.reldb.relang.core;

import java.util.Vector;

import org.reldb.relang.core.Grid.RowAction;

public class GridDataTemporary implements GridData {

	private Heading heading = new Heading();
	private Vector<Vector<Object>> data = new Vector<>();
	
	@Override
	public int getCardinality() {
		return heading.getCardinality();
	}

	@Override
	public int getDegree() {
		return data.size();
	}

	@Override
	public void setName(int column, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setType(int column, Class<?> type) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<?> getType(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(int column, int row, Object value) {
		// TODO Auto-generated method stub

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
