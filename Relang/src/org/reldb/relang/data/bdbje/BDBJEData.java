package org.reldb.relang.data.bdbje;

import java.io.Closeable;
import java.util.Vector;

import org.reldb.relang.data.Data;
import org.reldb.relang.data.Heading;
import org.reldb.relang.data.InvalidValueException;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;

public class BDBJEData implements Data, Closeable {
	private BDBJEBase bdbjeBase;
	private Database db;
	private Heading heading;
	private StoredMap<Long, Vector<Object>> data;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BDBJEData(BDBJEBase bdbjeBase, Database db, Heading definition) {
		this.bdbjeBase = bdbjeBase;
		this.db = db;
		this.heading = definition;
		
		var dataKeyBinding = new LongBinding();
		EntryBinding<Vector<Object>> dataValueBinding = new SerialBinding(bdbjeBase.getBDBJE().getClassCatalog(), Vector.class);
		data = new StoredSortedMap<Long, Vector<Object>>(bdbjeBase.getCatalog(), dataKeyBinding, dataValueBinding, true);
	}

	public void close() {
		db.close();
	}

	private void updateCatalog() {
		bdbjeBase.updateCatalog(db.getDatabaseName(), heading);
	}
	
	@Override
	public int getColumnCount() {
		return heading.getColumnCount();
	}

	@Override
	public long getRowCount() {
		return db.count();
	}

	@Override
	public void setColumnName(int column, String name) {
		heading.setColumnName(column, name);
		updateCatalog();
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
			throw new InvalidValueException("ERROR: BDBJEData: The type parameter must not be null.");
		int columnCount = getColumnCount();
		if (column < 0)
			throw new InvalidValueException("ERROR: BDBJEData: Attempt to reference non-existent column " + column + " in a GridDataTemporary with column count " + columnCount);
		else if (column < heading.getColumnCount()) {
			if (!data.values().stream().allMatch(row -> type.isAssignableFrom(row.get(column).getClass())))
				throw new InvalidValueException("ERROR: BDBJEData: Data in column " + column + " cannot be assigned to a " + type.getName());
		}
		heading.setColumnType(column, type, defaultValue);
		updateCatalog();
	}

	@Override
	public String appendDefaultColumn() {
		String newColumnName = heading.appendDefaultColumn();
		Object defaultValueForNewColumn = heading.getDefaultValueAt(getColumnCount() - 1);
		data.values().forEach(row -> {
			row.add(defaultValueForNewColumn);
		});
		updateCatalog();
		return newColumnName;
	}

	@Override
	public Class<?> getColumnTypeAt(int column) {
		return heading.getColumnTypeAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
		heading.deleteColumnAt(column);
		data.values().forEach(row -> {
			row.remove(column);
		});
		updateCatalog();
	}

	@Override
	public void deleteRowAt(int row) {
		data.remove(Integer.valueOf(row));
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
