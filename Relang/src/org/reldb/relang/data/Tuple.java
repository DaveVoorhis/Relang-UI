package org.reldb.relang.data;

public class Tuple {

	private Object[] values;
	
	/** Create a new tuple with default attribute values. */
	public Tuple(Heading heading) {
		int columnCount = heading.getColumnCount();
		this.values = new Object[columnCount];
		for (int column = 0; column < columnCount; column++)
			this.values[column] = heading.getDefaultValueAt(column);
	}
	
	/** Return the array of instances in this tuple. */
	public Object[] getValues() {
		return values;
	}
	
	public String toString() {
		String out = null;
		for (Object value: values)
			out = ((out == null) ? "" : out + ", ") + ((value == null) ? "null" : value.toString());
		return "TUPLE {" + ((out == null) ? "" : out) + "}";
	}
}
