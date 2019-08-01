package org.reldb.relang.errors;

import java.util.Vector;

/**
 * Message registry. Later, this will be used to facilitate localisation/internationalisation of error messages.
 * 
 * @author dave
 *
 */
public class Err {

	private static Vector<String> strings = new Vector<>();
	
	private static int errorText = register("ERROR");
	private static int warningText = register("WARNING");
	private static int noteText = register("NOTE");
	private static int msgPreambleText = register("MSG");
	private static int fromText = register("from");
	
	public static int register(String string) {
		strings.add(string);
		return strings.size() - 1;		
	}
	
	public static int register(String format, String module, String msgType) {
		return register(msgType + " " + strings.get(fromText) + " " + module + ": " + format);
	}
	
	public static int E(String format, String module) {
		return register(format, module, strings.get(errorText));
	}
	
	public static int W(String format, String module) {
		return register(format, module, strings.get(warningText));
	}
	
	public static int N(String format, String module) {
		return register(format, module, strings.get(noteText));
	}

	public static String getString(int msgIdx) {
		if (msgIdx < 0 || msgIdx >= strings.size())
			return null;
		return strings.get(msgIdx);
	}
	
	public static String or(int msgIdx, Object ...objects) {
		String formatString = getString(msgIdx);
		if (formatString == null)
			return strings.get(errorText) + ": " + msgIdx;
		return String.format(strings.get(msgPreambleText) + String.format("%05d: ", msgIdx) + formatString, objects);
	}
}
