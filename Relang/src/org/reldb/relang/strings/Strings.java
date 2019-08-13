package org.reldb.relang.strings;

import org.reldb.relang.data.Heading;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.reldb.relang.data.bdbje.BDBJEData;
import org.reldb.relang.data.bdbje.BDBJEEnvironment;
import org.reldb.relang.data.temporary.DataTemporary;
import org.reldb.relang.external.DirClassLoader;
import org.reldb.relang.external.ForeignCompilerJava;
import org.reldb.relang.tuples.TupleGenerator;

/**
 * Internationalisable messages.
 * 
 * @author dave
 *
 */
public class Strings {
	
	public static final int NoteOpening = Str.N("Opening BDBJE at %s.", BDBJEEnvironment.class.toString());
	public static final int NoteOpened = Str.N("Opened BDBJE at %s.", BDBJEEnvironment.class.toString());
	public static final int ErrNotExists = Str.E("BDBJE directory %s does not exist.", BDBJEEnvironment.class.toString());
	private static final String UnableToCreateMsg = "Unable to create directory %s";
	public static final int ErrUnableToCreate1 = Str.E(UnableToCreateMsg, BDBJEEnvironment.class.toString());
	public static final int ErrUnableToCreate2 = Str.E(UnableToCreateMsg, BDBJEEnvironment.class.toString());
	public static final int ErrUnableToCreate3 = Str.E(UnableToCreateMsg, BDBJEEnvironment.class.toString());	
	public static final int NoteClosing = Str.N("Closing BDBJE at %s.", BDBJEEnvironment.class.toString());
	public static final int NoteClosed = Str.N("Closed BDBJE at %s.", BDBJEEnvironment.class.toString());
	public static final int WarnClosingClassRepo = Str.W("Error closing class repository at %s due to %s.", BDBJEEnvironment.class.toString());
	public static final int WarnClosingClassRepoEnv = Str.W("Error closing class repository environment at %s due to %s.", BDBJEEnvironment.class.toString());
	public static final int WarnClosingDataEnv = Str.W("Error closing data storage environment at %s due to %s.", BDBJEEnvironment.class.toString());

	public static final int ErrSourceExists = Str.E("Data source %s already exists.", BDBJEBase.class.toString());
	public static final int ErrSourceNotExists = Str.E("Data source %s does not exist.", BDBJEBase.class.toString());
	
	public static final int ErrTypeParmNotNull = Str.E("The type parameter must not be null.", BDBJEData.class.toString());
	public static final int ErrNonexistentColumn = Str.E("Attempt to reference non-existent column %d in a BDBJEData with column count %d", BDBJEData.class.toString());
	public static final int ErrTypeMismatch = Str.E("Data in column %d cannot be assigned to a %s.", BDBJEData.class.toString());
	public static final int ErrTEAppendDefaultColumn = Str.E("Transaction exception in appendDefaultColumn().", BDBJEData.class.toString());
	public static final int ErrTEDeleteColumnAt = Str.E("Transaction exception in deleteColumnAt().", BDBJEData.class.toString());
	public static final int ErrNonexistentColumn2 = Str.E("Attempt to reference non-existent column %d in a BDBJEData with column count %d.", BDBJEData.class.toString());
	public static final int ErrNonexistentRow = Str.E("Attempt to reference non-existent row %d.", BDBJEData.class.toString());
	public static final int ErrTypeMismatch2 = Str.E("Attempt to assign value of type %s to cell with type %s.", BDBJEData.class.toString());
	public static final int ErrTESetValue = Str.E("Transaction exception in setValue().", BDBJEData.class.toString());
	public static final int ErrNonexistentColumn3 = Str.E("Attempt to reference non-existent column %d in a BDBJEData with column count %d.", BDBJEData.class.toString());
	public static final int ErrNonexistentRow2 = Str.E("Attempt to reference non-existent row %d.", BDBJEData.class.toString());

	public static final int ErrTypeNotNull = Str.E("The type parameter must not be null.", DataTemporary.class.toString());
	public static final int ErrColumnNotFound = Str.E("Attempt to reference non-existent column %d in a DataTemporary with column count %d.", DataTemporary.class.toString());
	public static final int ErrCannotBeAssigned = Str.E("Data in column %d cannot be assigned to a %s.", DataTemporary.class.toString());
	public static final int ErrNonexistentRow3 = Str.E("Attempt to reference non-existent row %d.", DataTemporary.class.toString());	
	public static final int ErrNonexistentColumn4 = Str.E("Attempt to reference non-existent column %d in a DataTemporary with column count %d.", DataTemporary.class.toString());
	public static final int ErrNonexistentRow4 = Str.E("Attempt to reference non-existent row %d.", DataTemporary.class.toString());
	public static final int ErrTypeMismatch3 = Str.E("Attempt to assign value of type %s to cell with type %s.", DataTemporary.class.toString());
	public static final int ErrNonexistentColumn5 = Str.E("Attempt to reference non-existent column %d in a DataTemporary with column count %d.", DataTemporary.class.toString());
	public static final int ErrNonexistentRow5 = Str.E("Attempt to reference non-existent row %d.", DataTemporary.class.toString());
	
	public static final int ErrInUse = Str.E("Heading %s is in use and can't be changed.", Heading.class.toString());   
	public static final int ErrInvalidColumn1 = Str.E("Invalid column number %d.", Heading.class.toString()); 
	public static final int ErrInvalidColumn2 = Str.E("Invalid column number %d.", Heading.class.toString()); 
	public static final int ErrAttemptToSetNullDefault = Str.E("Attempt to set null default value.", Heading.class.toString()); 
	public static final int ErrAttemptToSetNullAttribute1 = Str.E("Attempt to set null attribute type.", Heading.class.toString()); 
	public static final int ErrAttemptToSetNullAttribute2 = Str.E("Attempt to set null attribute name.", Heading.class.toString()); 
	public static final int ErrDefaultTypeMismatch1 = Str.E("A defaultValue of type %s cannot be assigned to an %s.", Heading.class.toString()); 
	public static final int ErrAttributeDuplicate1 = Str.E("Heading %s already contains an attribute named %s.", Heading.class.toString()); 
	public static final int ErrInvalidColumn3 = Str.E("Invalid column number %d.", Heading.class.toString()); 
	public static final int ErrAttemptToSetNullAttribute3 = Str.E("Attempt to set null attribute name.", Heading.class.toString()); 
	public static final int ErrAttributeDuplicate2 = Str.E("Heading %s already contains an attribute named %s.", Heading.class.toString()); 
	public static final int ErrInvalidColumn4 = Str.E("Invalid column number %d.", Heading.class.toString()); 
	public static final int ErrAttemptToSetNullDefault2 = Str.E("Attempt to set null default value.", Heading.class.toString()); 
	public static final int ErrAttemptToSetNullAttributeType = Str.E("Attempt to set null attribute type.", Heading.class.toString()); 
	public static final int ErrDefaultTypeMismatch2 = Str.E("A defaultValue of type %s cannot be assigned to a %s.", Heading.class.toString());   
	public static final int ErrInvalidColumn5 = Str.E("Attempt to delete column %d in a heading with column count %d.", Heading.class.toString()); 
	public static final int ErrInvalidColumn6 = Str.E("Invalid column number %d in a heading with column count %d.", Heading.class.toString()); 
	
	public static final int ErrFileNotFound1 = Str.E("File %s not found for %s.", DirClassLoader.class.toString()); 
	public static final int ErrReading = Str.E("Error reading %s: %s.", DirClassLoader.class.toString()); 
	
	public static final int ErrSavingJavaSource = Str.E("Unable to save Java source: %s.", ForeignCompilerJava.class.toString());
	public static final int ErrJavaCompilationFailed = Str.E("Compilation failed due to errors: %n%s%n", ForeignCompilerJava.class.toString());
	
	public static final int ErrUnableToCreateOrOpenCodeDirectory = Str.E("Unable to create or open code directory %s.", TupleGenerator.class.toString());
}
