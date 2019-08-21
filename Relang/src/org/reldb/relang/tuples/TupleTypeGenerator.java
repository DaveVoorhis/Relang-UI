package org.reldb.relang.tuples;

import org.reldb.relang.strings.Str;
import org.reldb.relang.utilities.Directory;

import static org.reldb.relang.strings.Strings.*;

import java.io.File;
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
	private String oldTupleName;	
	private HashMap<String, Class<?>> attributes = new HashMap<>();
	private TupleTypeGenerator copyFrom = null;
	
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
	
	/** Return true if this tuple definition already exists.
	 * 
	 * @return - true if this tuple definition already exists, false if it is new.
	 */
	public boolean isExisting() {
		return existing;
	}
	
	/** Add the specified attribute of specified type. NOTE: Will not take effect until compile() has been invoked.
	 * 
	 * @param name - name of new attribute
	 * @param type - type (class) of new attribute
	 */
	public void addAttribute(String name, Class<?> type) {
		attributes.put(name, type);
	}
	
	/** Remove the specified attribute. NOTE: Will not take effect until compile() has been invoked.
	 * 
	 * @param name - attribute to remove
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	/** Rename this tuple type (class) definition and associated files to that specified by newName. NOTE: Will not take effect until compile() has been invoked.
	 * 
	 * @param newName - the new name, which must follow Java class name identifier rules.
	 */
	public void rename(String newName) {
		if (existing && oldTupleName == null)
			oldTupleName = tupleName;
		tupleName = newName;
	}
	
	/** Create a new tuple type (class) definition that is a copy of this one. NOTE: Will not take effect until compile() has been invoked.
	 * 
	 * @param newName - the new name, which must follow Java class name identifier rules.
	 * @return - a new TupleTypeGenerator which is a copy of this one.
	 */
	@SuppressWarnings("unchecked")
	public TupleTypeGenerator copyTo(String newName) {
		var target = new TupleTypeGenerator(dir, newName);
		target.attributes = (HashMap<String, Class<?>>)attributes.clone();
		target.copyFrom = this;
		return target;
	}
	
	private String getCopyFromCode() {
		if (copyFrom == null)
			return "";
		return
			"\t/** Method to copy from specified tuple to this tuple.\n\t@param source - tuple to copy from */\n" +
			"\tpublic void copyFrom(" + copyFrom.tupleName + " source) {\n" +
				attributes.entrySet().stream().filter(copyFrom.attributes.entrySet()::contains)
					.map(entry -> "\t\tthis." + entry.getKey() + " = source." + entry.getKey() + ";\n").collect(Collectors.joining()) +
			"\t}\n";
	}
	
	/** Compile this tuple type as a class.
	 * 
	 * @return - an instance of ForeignCompilerJava.CompilationResults, which indicates compilation results.
	 */
	public ForeignCompilerJava.CompilationResults compile() {
		if (oldTupleName != null) {
			var pathName = dir + File.separator + oldTupleName;
			System.out.println("Remove file starting with " + pathName);
			(new File(pathName + ".java")).delete();
			(new File(pathName + ".class")).delete();
		}
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
				getCopyFromCode() +
			"}";
		var compiler = new ForeignCompilerJava(dir);
		loader.unload(tupleName);
		return compiler.compileForeignCode(tupleName, tupleDef);
	}
}
