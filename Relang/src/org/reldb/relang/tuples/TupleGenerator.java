package org.reldb.relang.tuples;

import org.reldb.relang.strings.Str;
import org.reldb.relang.utilities.Directory;

import static org.reldb.relang.strings.Strings.*;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.reldb.relang.exceptions.ExceptionFatal;

/**
 * Generates Java code to represent a tuple, which is a class that implements Tuple.
 * 
 * @author dave
 *
 */
public class TupleGenerator {
	
	private String dir;
	private String tupleName;
	
	private HashMap<String, Class<?>> attributes = new HashMap<>();
	
	public TupleGenerator(String dir, String tupleName) {
		this.dir = dir;
		this.tupleName = tupleName;
		if (!Directory.chkmkdir(dir))
			throw new ExceptionFatal(Str.ing(ErrUnableToCreateOrOpenCodeDirectory, dir));
	}
	
	public void addAttribute(String name, Class<?> type) {
		attributes.put(name, type);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public boolean compile() {
		var tupleDef = 
			"import org.reldb.relang.tuples.Tuple;\n\n" +
			"public class " + tupleName + " implements Tuple {\n\t" +
				attributes
					.entrySet()
					.stream()
					.map(entry -> "public " + entry.getValue().getCanonicalName() + " " + entry.getKey())
					.collect(Collectors.joining(";\n\t")) +
			";\n}";
		System.out.println(tupleDef);
		
		return true;
	}
}
