package org.reldb.relang.core;

import org.reldb.relang.core.Grid.RowAction;

/* Abstract definition of a Grid's data */
public interface GridData {
	public int getCardinality();
	public int getDegree();
	public void setName(int column, String name);
	public String getName(int column);
	public boolean hasName(String name);
	public void setType(int column, Class<?> type);
	public Class<?> getType(int column);
	public void setValue(int column, int row, Object value);
	public Object getValue(int column, int row);
	public boolean isChanged(int column, int row);
	public String getError(int row);
	public RowAction getAction(int row);
}
