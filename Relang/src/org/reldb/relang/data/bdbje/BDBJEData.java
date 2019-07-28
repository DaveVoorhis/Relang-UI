package org.reldb.relang.data.bdbje;

import java.io.Closeable;

import org.reldb.relang.data.Data;

import com.sleepycat.je.Database;

public class BDBJEData implements Data, Closeable {
	private Database db;
	private BDBJEDataDefinition definition;
	
	public BDBJEData(BDBJEBase bdbjeBase, Database db, BDBJEDataDefinition definition) {
		this.db = db;
		this.definition = definition;
	}

	public void close() {
		db.close();
	}

	@Override
	public int getColumnCount() {
		return definition.getColumnCount();
	}

	@Override
	public long getRowCount() {
		return db.count();
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
