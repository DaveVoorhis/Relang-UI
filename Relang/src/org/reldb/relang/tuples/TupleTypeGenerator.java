package org.reldb.relang.tuples;

import org.reldb.relang.strings.Str;
import org.reldb.relang.utilities.Directory;

import static org.reldb.relang.strings.Strings.*;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.reldb.relang.compiler.DirClassLoader;
import org.reldb.relang.compiler.ForeignCompilerJava;
import org.reldb.relang.exceptions.ExceptionFatal;

/**
 * Generates Java code to represent a tuple, which is a class that implements Tuple.
 * 
 * @author dave
 *
 */
public class TupleTypeGenerator {
	
	private String dir;
	private String tupleName;
	private boolean existing;
	private long serialValue = 1;
	private DirClassLoader loader;
	
	private HashMap<String, Class<?>> attributes = new HashMap<>();
	
	public TupleTypeGenerator(String dir, String tupleName) {
		this.dir = dir;
		this.tupleName = tupleName;
		if (!Directory.chkmkdir(dir))
			throw new ExceptionFatal(Str.ing(ErrUnableToCreateOrOpenCodeDirectory, dir));
		loader = new DirClassLoader(dir);
		try {
			var tupleClass = loader.forName(tupleName);
			Arrays.stream(tupleClass.getFields())
				.filter(field -> !Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()))
				.forEach(field -> attributes.put(field.getName(), field.getType()));
			try {
				var serialVersionUIDField = tupleClass.getDeclaredField("serialVersionUID");
				serialValue = serialVersionUIDField.getLong(null) + 1;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				serialValue = 9999999;
			}
			existing = true;
		} catch (ClassNotFoundException e) {
			existing = false;
		}
	}
	
	public boolean isExisting() {
		return existing;
	}
	
	public void addAttribute(String name, Class<?> type) {
		attributes.put(name, type);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public ForeignCompilerJava.CompilationResults compile() {
		var tupleDef = 
			"import org.reldb.relang.tuples.Tuple;\n\n" +
			"/** " + tupleName + " tuple class version " + serialValue + " */\n" +
			"public class " + tupleName + " implements Tuple {\n" +
				"\t/** Version number */\n" +
				"\tpublic static final long serialVersionUID = " + serialValue + ";\n" +
				attributes
					.entrySet()
					.stream()
					.map(entry -> "\t/** Field */\n\tpublic " + entry.getValue().getCanonicalName() + " " + entry.getKey() + ";\n")
					.collect(Collectors.joining()) +
			"}";
		var compiler = new ForeignCompilerJava(dir);
		loader.unload(tupleName);
		return compiler.compileForeignCode(tupleName, tupleDef);
	}
}
