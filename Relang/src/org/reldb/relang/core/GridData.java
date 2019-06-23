package org.reldb.relang.core;

import org.reldb.relang.core.Grid.RowAction;

/* Abstract definition of a Grid's data */
public interface GridData {
	
	/** Obtain the number of columns. */
	public int getColumnCount();
	
	/** Obtain the number of rows. */
	public int getRowCount();
	
	/** Set the specified column's name. */
	public void setColumnName(int column, String name);
	
	/** Return the specified column's name. */
	public String getColumnName(int column);
	
	/** Return true if there exists a column with the given name. */
	public boolean isColumnNameUsed(String name);
	
	/** Set the type of the specified column. */
	public void setColumnType(int column, Class<?> type);
	
	/** Get the type of the specified column. */
	public Class<?> getColumnType(int column);
	
	/** Set the value of the specified column/row intersection. */
	public void setValue(int column, int row, Object value);
	
	/** Get the value of the specified column/row intersection. */
	public Object getValue(int column, int row);
	
	/** Return true if the given column/row intersection has changed but hasn't been written to backing store yet. */
	public boolean isChanged(int column, int row);
	
	/** Return an error message if unable to write changed rows to backing store. */
	public String getError(int row);
	
	/** Return the intended action for a given row. */
	public RowAction getAction(int row);
}
