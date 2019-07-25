package org.reldb.relang.data.bdbje;

import org.reldb.relang.data.Data;

import com.sleepycat.je.Database;

public class BDBJEData implements Data {

	private BDBJE db;
	private Database table;
	
	/** 
	 * A Data source based on a specified Berkeley DB Java Edition environment.
	 * 
	 * @param db - Berekely DB Java Edition environment.
	 * @param name - unique name within the specified environment for this Data source.
	 * @param create - true to create this Data source if it doesn't already exist.
	 */
	public BDBJEData(BDBJE db, String name, boolean create) {
		this.db = db;
		table = db.open(name, create);
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColumnName(int column, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getColumnNameAt(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasColumnNamed(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setColumnType(int column, Class<?> type, Object defaultValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String appendDefaultColumn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getColumnTypeAt(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteColumnAt(int column) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRowAt(int row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appendRow() {
		// TODO Auto-generated method stub
		
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
	
}
