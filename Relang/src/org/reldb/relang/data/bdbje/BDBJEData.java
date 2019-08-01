package org.reldb.relang.data.bdbje;

import java.io.Closeable;
import java.util.Vector;

import org.reldb.relang.data.Data;
import org.reldb.relang.data.Heading;
import org.reldb.relang.data.InvalidValueException;
import org.reldb.relang.errors.Err;
import org.reldb.relang.errors.ExceptionFatal;

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
		EntryBinding<Vector<Object>> dataValueBinding = new SerialBinding(bdbjeBase.getClassCatalog(), Vector.class);
		data = new StoredSortedMap<Long, Vector<Object>>(db, dataKeyBinding, dataValueBinding, true);
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
	
	private static final int ErrTypeParmNotNull = Err.E("The type parameter must not be null.", BDBJEData.class.toString());
	private static final int ErrNonexistentColumn = Err.E("Attempt to reference non-existent column %d in a BDBJEData with column count %d", BDBJEData.class.toString());
	private static final int ErrTypeMismatch = Err.E("Data in column %d cannot be assigned to a %s.", BDBJEData.class.toString());

	@Override
	public void setColumnType(int column, Class<?> type, Object defaultValue) {
		if (type == null)
			throw new InvalidValueException(Err.or(ErrTypeParmNotNull));
		int columnCount = getColumnCount();
		if (column < 0)
			throw new InvalidValueException(Err.or(ErrNonexistentColumn, column, columnCount));
		else if (column < heading.getColumnCount()) {
			if (!data.values().stream().allMatch(tuple -> type.isAssignableFrom(tuple.get(column).getClass())))
				throw new InvalidValueException(Err.or(ErrTypeMismatch, column, type.getName()));
		}
		heading.setColumnType(column, type, defaultValue);
		updateCatalog();
	}

	private static final int ErrTEAppendDefaultColumn = Err.E("Transaction exception in appendDefaultColumn().", BDBJEData.class.toString());
	
	@Override
	public String appendDefaultColumn() {
		String newColumnName = heading.appendDefaultColumn();
		Object defaultValueForNewColumn = heading.getDefaultValueAt(getColumnCount() - 1);
		try {
			data.entrySet().forEach(entry -> {
				var key = entry.getKey();
				var value = entry.getValue();
				value.add(defaultValueForNewColumn);
				data.put(key, value);				
			});
			updateCatalog();			
		} catch (Exception e) {
			throw new ExceptionFatal(Err.or(ErrTEAppendDefaultColumn), e);			
		}
		return newColumnName;
	}

	@Override
	public Class<?> getColumnTypeAt(int column) {
		return heading.getColumnTypeAt(column);
	}
	
	private static final int ErrTEDeleteColumnAt = Err.E("Transaction exception in deleteColumnAt().", BDBJEData.class.toString());

	@Override
	public void deleteColumnAt(int column) {		
		try {
			bdbjeBase.transaction(() -> {
				data.entrySet().forEach(entry -> {
					var key = entry.getKey();
					var value = entry.getValue();
					value.remove(column);
					data.put(key, value);
				});
				heading.deleteColumnAt(column);
				updateCatalog();			
			});
		} catch (Exception e) {
			throw new ExceptionFatal(Err.or(ErrTEDeleteColumnAt), e);
		}
	}

	@Override
	public void deleteRowAt(long row) {
		data.remove(row);
	}

	@Override
	public void appendRow() {
		var tuple = new Vector<Object>();
		for (int column = 0; column < heading.getColumnCount(); column++)
			tuple.add(heading.getDefaultValueAt(column));
		data.put((long)data.size(), tuple);
	}
	
	private static final int ErrNonexistentColumn2 = Err.E("Attempt to reference non-existent column %d in a BDBJEData with column count %d.", BDBJEData.class.toString());
	private static final int ErrNonexistentRow = Err.E("Attempt to reference non-existent row %d.", BDBJEData.class.toString());
	private static final int ErrTypeMismatch2 = Err.E("Attempt to assign value of type %s to cell with type %s.", BDBJEData.class.toString());
	private static final int ErrTESetValue = Err.E("Transaction exception in setValue().", BDBJEData.class.toString());

	@Override
	public void setValue(int column, long row, Object value) {
		int columnCount = getColumnCount();
		if (column >= columnCount || column < 0)
			throw new InvalidValueException(Err.or(ErrNonexistentColumn2, column, columnCount));
		if (row < 0)
			throw new InvalidValueException(Err.or(ErrNonexistentRow, row));
		Class<?> headingColumnType = heading.getColumnTypeAt(column);
		Class<?> valueType = value.getClass();
		if (!headingColumnType.isAssignableFrom(valueType))
			throw new InvalidValueException(Err.or(ErrTypeMismatch2, valueType.getName(), headingColumnType.getName()));
		try {
			bdbjeBase.transaction(() -> {				
				while (row >= getRowCount())
					appendRow();
				var tuple = data.get(row);
				tuple.set(column, value);
				data.put(row, tuple);
			});
		} catch (Exception e) {
			throw new ExceptionFatal(Err.or(ErrTESetValue), e);
		}
	}
	
	private static final int ErrNonexistentColumn3 = Err.E("Attempt to reference non-existent column %d in a BDBJEData with column count %d.", BDBJEData.class.toString());
	private static final int ErrNonexistentRow2 = Err.E("Attempt to reference non-existent row %d.", BDBJEData.class.toString());

	@Override
	public Object getValue(int column, long row) {
		int columnCount = getColumnCount();
		if (column >= columnCount || column < 0)
			throw new InvalidValueException(Err.or(ErrNonexistentColumn3, column, columnCount));
		if (row > getRowCount() || row < 0)
			throw new InvalidValueException(Err.or(ErrNonexistentRow2, row));
		return data.get(row).get(column);
	}

	@Override
	public boolean isChanged(int column, long row) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getError(long row) {
		// TODO Auto-generated method stub
		return null;
	}
}
