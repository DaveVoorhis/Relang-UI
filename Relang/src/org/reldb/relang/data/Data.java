package org.reldb.relang.data;

/* Abstract definition of a Grid's data */
public interface Data {
	
	/** Obtain the number of columns. */
	public int getColumnCount();
	
	/** Obtain the number of rows. */
	public long getRowCount();
	
	/** Set the specified column's name. */
	public void setColumnName(int column, String name);
	
	/** Return the specified column's name. */
	public String getColumnNameAt(int column);
	
	/** Return true if there exists a column with the given name. */
	public boolean hasColumnNamed(String name);
	
	/** Set the type and default value of the specified column. */
	public void setColumnType(int column, Class<?> type, Object defaultValue);
	
	/** Append a new column of default type. Return the column name. */
	public String appendDefaultColumn();
	
	/** Get the type of the specified column. */
	public Class<?> getColumnTypeAt(int column);
	
	/** Delete a column. */
	public void deleteColumnAt(int column);
	
	/** Delete a row. */
	public void deleteRowAt(int row);
	
	/** Append a blank row. */
	public void appendRow();
	
	/** Set the value of the specified column/row intersection. */
	public void setValue(int column, int row, Object value);
	
	/** Get the value of the specified column/row intersection. */
	public Object getValue(int column, int row);
	
	/** Return true if the given column/row intersection has changed but hasn't been written to backing store yet. */
	public boolean isChanged(int column, int row);
	
	/** Return an error message if unable to write changed rows to backing store. */
	public String getError(int row);
}
