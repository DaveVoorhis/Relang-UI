package org.reldb.relang.transpiler;

import java.lang.reflect.Method;

import org.reldb.relang.exceptions.ExceptionFatal;
import org.reldb.relang.exceptions.ExceptionSemantic;
import org.reldb.relang.java.DirClassLoader;
import org.reldb.relang.java.ForeignCompilerJava;
import org.reldb.relang.parser.ast.*;

public class Transpiler {
	
	private static void usage() {
		System.out.println("Usage: silc [-d0 | -d1] < <source>");
		System.out.println("          -d0 -- run-time debugging");
		System.out.println("          -d1 -- output AST");
	}
	
	public static void main(String args[]) {
		boolean debugOnRun = false;
		boolean debugAST = false;
		if (args.length == 1) {
			if (args[0].equals("-d0"))
				debugOnRun = true;
			else if (args[0].equals("-d1"))
				debugAST = true;
			else {
				usage();
				return;
			}
		}
		Relang language = new Relang(System.in);
		language.setTabSize(4);
		try {
			ASTCode parser = language.code();
			RelangVisitor nodeVisitor;
			if (debugAST)
				nodeVisitor = new ParserDebugger();
			else {
				if (debugOnRun)
					System.out.println("Compiling...");
				nodeVisitor = new Parser();
			}
			// Run the input stream through through the translator to get translated code.
			String code = (String)parser.jjtAccept(nodeVisitor, null);
			// Dump if debugging
			if (debugOnRun) {
				System.out.println("Compiled:");
				code.toString();
				System.out.println("Executing...");
			}
			// compile translated code
			ForeignCompilerJava compiler = new ForeignCompilerJava(debugOnRun);
			compiler.compileForeignCode(System.out, Parser.generatedCodeClassName, code);
			// load and run translated code
			DirClassLoader classLoader = new DirClassLoader(ForeignCompilerJava.dataDir);
			Class<?> generatedClass = classLoader.forName(Parser.generatedCodeClassName);
			Method mainMethod = generatedClass.getMethod(Parser.generatedCodeMainMethodName, (Class<?>[])null);
			mainMethod.invoke(null);
		} catch (ExceptionSemantic es) {
			System.out.println(es.getMessage());
		} catch (ExceptionFatal ef) {
			System.out.println(ef.getMessage());
			ef.printStackTrace();			
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
