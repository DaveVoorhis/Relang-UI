package org.reldb.relang.storage;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.reldb.relang.data.Tuple;
import org.reldb.relang.external.DirClassLoader;
import org.reldb.relang.storage.catalog.*;
import org.reldb.relang.storage.tables.RegisteredTupleIterator;
import org.reldb.relang.storage.tables.Storage;
import org.reldb.relang.storage.tables.StorageNames;
import org.reldb.relang.storage.tables.Table;
import org.reldb.relang.utilities.ExceptionFatal;
import org.reldb.relang.utilities.ExceptionSemantic;
import org.reldb.relang.version.Version;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.*;

public class LocalDatabase {

	public final static String systemOwner = "Rel";

	private Environment environment;
	private DatabaseConfig dbConfigurationNormal;
	private DatabaseConfig dbConfigurationAllowCreate;
	private DatabaseConfig dbConfigurationTemporary;
	private DatabaseConfig dbConfigurationTemporaryWithDuplicatesNoComparator;
	private DatabaseConfig dbConfigurationMetadataAllowCreateNoComparator;

	// Metadata
	private ClassCatalog classCatalog;
	private StringBinding stringDataBinding;
	private DatabaseEntry keyTableEntry = new DatabaseEntry();
	private Database relvarDb;

	// open real relvar tables
	private Hashtable<String, Database> openStorage = new Hashtable<String, Database>();

	// active transaction per thread
	private Hashtable<Long, LocalTransaction> transactions = new Hashtable<Long, LocalTransaction>();

	// Active registered tuple iterators
	private HashSet<RegisteredTupleIterator> registeredTupleIterators = new HashSet<RegisteredTupleIterator>();

	// Relative database directory name
	private static final String databaseHomeRelative = "RelangDB";

	// Relative user code directory name
	private static final String userCodeHomeRelative = "UserCode";

	// Rel user Java code package
	private static final String relUserCodePackage = databaseHomeRelative + "." + userCodeHomeRelative;

	// User code home dir
	private String userCodeHome;

	// Database home dir
	private String databaseHome = databaseHomeRelative;

	// Rel home dir
	private String homeDir;

	// Quiet mode. Don't emit startup/shutdown messages to the console.
	private boolean quiet = false;

	private String getBerkeleyJavaDBVersion() {
		return JEVersion.CURRENT_VERSION.getVersionString();
	}

	public static void mkdir(String dir) {
		File dirf = new File(dir);
		if (!dirf.exists()) {
			if (!dirf.mkdirs()) {
				String msg = "Unable to create directory: " + dirf;
				throw new ExceptionFatal("RS0324: " + msg);
			}
		}
	}

	private String getClickerFileName() {
		return homeDir + File.separator + "ClickToOpen.dsh";
	}

	private void writeClicker() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(getClickerFileName(), false);
			if (writer != null)
				writer.close();
		} catch (Exception e) {
			System.out.println("WARNING: Unable to create " + getClickerFileName());
		}
	}

	private String getExtensionDirectoryName() {
		return homeDir + File.separator + "Extensions";
	}

	private void ensureExtensionDirectoryExists() {
		mkdir(getExtensionDirectoryName());
	}

	public File getExtensionDirectory() {
		return new File(getExtensionDirectoryName());
	}

	private String getVersionFileName() {
		return databaseHome + File.separator + "version";
	}

	private void writeVersion() throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(getVersionFileName(), false);
			writer.write(Integer.toString(Version.getDatabaseVersion()));
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private int readVersion() {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(new File(getVersionFileName()).toPath());
		} catch (IOException e1) {
			return -1;
		}
		if (lines.isEmpty())
			return -1;
		try {
			return Integer.parseInt(lines.get(0));
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public void setQuietMode(boolean quiet) {
		this.quiet = quiet;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public void open(File envHome, boolean canCreateDb, PrintStream outputStream) throws DatabaseFormatVersionException {
		if (!quiet)
			System.out.println("Opening database in " + envHome + "\nIf it doesn't exist, we'll "
				+ ((canCreateDb) ? "try to create it" : "cause an error") + ".");

		String usingBerkeleyJavaDBVersion = getBerkeleyJavaDBVersion();
		if (!usingBerkeleyJavaDBVersion.equals(Version.expectedBerkeleyDBVersion))
			throw new ExceptionFatal("RS0323: Expected to find Berkeley Java DB version "
					+ Version.expectedBerkeleyDBVersion + " but found version " + usingBerkeleyJavaDBVersion
					+ ".\nAn attempted update or re-installation has probably failed.\nPlease make sure "
					+ Version.getBerkeleyDbJarFilename()
					+ " is not read-only, then try the update or re-installation again.");
		homeDir = envHome.getAbsolutePath();
		if (homeDir.endsWith("."))
			homeDir = homeDir.substring(0, homeDir.length() - 1);
		if (!homeDir.endsWith(java.io.File.separator))
			homeDir += java.io.File.separator;

		databaseHome = homeDir + databaseHomeRelative;

		if (!(new File(databaseHome)).exists())
			if (!canCreateDb)
				throw new ExceptionSemantic(
						"RS0406: Database " + homeDir + " either doesn't exist or isn't a Rel database.");
			else {
				mkdir(databaseHome);
				try {
					writeVersion();
				} catch (IOException ioe) {
					throw new ExceptionSemantic("RS0408: Can't write version file in database in " + homeDir + ".");
				}
			}
		else {
			int detectedVersion = readVersion();
			if (detectedVersion < 0) {
				throw new ExceptionSemantic("RS0407: Database in " + homeDir
						+ " has no version information, or it's invalid.  The database must be upgraded manually.\nBack it up with the version of Rel used to create it and load the backup into a new database.");
			} else if (detectedVersion < Version.getDatabaseVersion()) {
				String msg = "RS0410: Database requires conversion from format v" + detectedVersion + " to format v"
						+ Version.getDatabaseVersion();
				throw new DatabaseFormatVersionException(msg, detectedVersion);
			} else if (detectedVersion > Version.getDatabaseVersion()) {
				throw new ExceptionSemantic("RS0409: Database in " + homeDir
						+ " appears to have been created by a newer version of Rel than this one.\nOpen it with the latest version of Rel.");
			}
		}

		writeClicker();
		ensureExtensionDirectoryExists();

		userCodeHome = databaseHome + java.io.File.separator + userCodeHomeRelative;

		try {
			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setReadOnly(false);
			environmentConfig.setAllowCreate(true);
			environmentConfig.setTransactional(true);
			environmentConfig.setTxnSerializableIsolation(true);

			dbConfigurationNormal = new DatabaseConfig();
			dbConfigurationNormal.setReadOnly(false);
			dbConfigurationNormal.setAllowCreate(false);
			dbConfigurationNormal.setTransactional(true);
			dbConfigurationNormal.setSortedDuplicates(false);

			dbConfigurationAllowCreate = new DatabaseConfig();
			dbConfigurationAllowCreate.setReadOnly(false);
			dbConfigurationAllowCreate.setAllowCreate(true);
			dbConfigurationAllowCreate.setTransactional(true);
			dbConfigurationAllowCreate.setSortedDuplicates(false);

			dbConfigurationTemporary = new DatabaseConfig();
			dbConfigurationTemporary.setReadOnly(false);
			dbConfigurationTemporary.setAllowCreate(true);
			dbConfigurationTemporary.setTransactional(false);
			dbConfigurationTemporary.setSortedDuplicates(false);
			dbConfigurationTemporary.setTemporary(true);

			dbConfigurationTemporaryWithDuplicatesNoComparator = new DatabaseConfig();
			dbConfigurationTemporaryWithDuplicatesNoComparator.setReadOnly(false);
			dbConfigurationTemporaryWithDuplicatesNoComparator.setAllowCreate(true);
			dbConfigurationTemporaryWithDuplicatesNoComparator.setTransactional(false);
			dbConfigurationTemporaryWithDuplicatesNoComparator.setSortedDuplicates(true);
			dbConfigurationTemporaryWithDuplicatesNoComparator.setTemporary(true);

			dbConfigurationMetadataAllowCreateNoComparator = new DatabaseConfig();
			dbConfigurationMetadataAllowCreateNoComparator.setReadOnly(false);
			dbConfigurationMetadataAllowCreateNoComparator.setAllowCreate(true);
			dbConfigurationMetadataAllowCreateNoComparator.setTransactional(true);
			dbConfigurationMetadataAllowCreateNoComparator.setSortedDuplicates(false);

			// Open the class catalog
			classCatalog = new ClassCatalog(databaseHome, environmentConfig,
					dbConfigurationMetadataAllowCreateNoComparator);

			// Open the main database environment
			environment = new Environment(new File(databaseHome), environmentConfig);

			// Data for key table entries
			stringDataBinding = new StringBinding();
			stringDataBinding.objectToEntry("", keyTableEntry);

			// Open the metadata db.
			relvarDb = environment.openDatabase(null, "_Relvars", dbConfigurationMetadataAllowCreateNoComparator);

			// Declare the Catalog
			Catalog catalog = new Catalog(this);

			/*
			// Catalog build phase 0
			catalog.generatePhase0(generator);

			// Catalog build phase 1
			catalog.generatePhase1(generator);
			*/

			// Prepare for battle.
			reset();

			if (!quiet)
				System.out.println("Datasheet " + envHome + " is open.");

		} catch (DatabaseException db) {
			String msg = "Unable to open database: " + db.getMessage();
			outputStream.println(msg);
			db.printStackTrace(System.out);
			throw new ExceptionFatal("RS0325: " + msg);
		}
	}

	/** Return the directory that contains the database. */
	public String getHomeDir() {
		return homeDir;
	}

	public void registerTupleIterator(RegisteredTupleIterator registeredTupleIterator) {
		registeredTupleIterators.add(registeredTupleIterator);
	}

	public void unregisterTupleIterator(RegisteredTupleIterator registeredTupleIterator) {
		registeredTupleIterators.remove(registeredTupleIterator);
	}

	public boolean isOpen() {
		return environment != null;
	}

	// Close the environment
	public void close() {
		if (environment == null) {
			System.out.println("WARNING: Attempting to re-close a closed database!");
			System.out
					.println("It's not a problem, but we'll tell you where it's coming from so maybe you can fix it:");
			(new Throwable()).printStackTrace();
			return;
		}
		if (!quiet) {
			System.out.println("Closing database in " + homeDir);
			System.out.println("\tClosing active tuple iterators in " + homeDir);
		}
		int activeTupleIterators = 0;
		try {
			for (RegisteredTupleIterator tupleIterator : registeredTupleIterators)
				if (tupleIterator.forceClose())
					activeTupleIterators++;
		} catch (Exception e) {
			System.err.println("\tError closing active tuple iterators: " + homeDir + ": " + e.toString());
		}
		if (activeTupleIterators == 1)
			System.err.println("\t" + activeTupleIterators + " active tuple iterator was closed.");
		else if (activeTupleIterators > 1)
			System.err.println("\t" + activeTupleIterators + " active tuple iterators were closed.");
		if (!quiet)
			System.out.println("\tCommitting open transactions in " + homeDir);
		int openTransactions = 0;
		for (LocalTransaction transaction : transactions.values())
			while (transaction.getReferenceCount() > 0)
				try {
					commitTransaction(transaction);
					openTransactions++;
				} catch (DatabaseException dbe) {
					System.err.println("\tError committing active transactions " + homeDir + ": " + dbe.toString());
				} catch (Exception e) {
					System.err.println("\tUnknown shutdown error 1: " + e);
				}
		if (openTransactions == 1)
			System.err.println("\t" + openTransactions + " open transaction was closed.");
		else if (openTransactions > 1)
			System.err.println("\t" + openTransactions + " open transactions were closed.");
		if (!quiet)
			System.out.println("\tClosing relvars in " + homeDir);
		try {
			for (Database table : openStorage.values())
				table.close();
		} catch (DatabaseException dbe) {
			System.err.println("\tError closing internal tables " + homeDir + ": " + dbe.toString());
		} catch (Exception e) {
			System.err.println("\tUnknown shutdown error 2: " + e);
		}
		/*
		if (!quiet)
			System.out.println("\tPurging temporary data in " + homeDir);
		try {
			for (String tableName : tempStorageNames)
				environment.removeDatabase(null, tableName);
		} catch (DatabaseException dbe) {
			System.err.println("\tError removing temporary data storage " + homeDir + ": " + dbe.toString());
		} catch (Exception e) {
			System.err.println("\tUnknown shutdown error 3: " + e);
		}
		if (!quiet)
			System.out.println("\tTemporary data purged in " + homeDir);
		*/
		try {
			relvarDb.close();
		} catch (DatabaseException dbe) {
			System.err.println("\tError closing the relvarDb " + homeDir + ": " + dbe.toString());
		} catch (Exception e) {
			System.err.println("\tUnknown shutdown error 4: " + e);
		}
		if (!quiet)
			System.out.println("\tClosing environment in " + homeDir);
		try {
			environment.close();
		} catch (DatabaseException dbe) {
			System.err.println("\tError closing the environment " + homeDir + ": " + dbe.toString());
		} catch (Exception e) {
			System.err.println("\tUnknown shutdown error 5: " + e);
		}
		environment = null;
		try {
			classCatalog.close();
		} catch (DatabaseException dbe) {
			System.err.println("\tError closing the ClassCatalog in " + homeDir + ": " + dbe.toString());
		} catch (Exception e) {
			System.err.println("\tUnknown shutdown error 6: " + e);
		}
		if (!quiet)
			System.out.println("Database " + homeDir + " is closed.");
	}

	public void reset() {
	}

	/** Get the user Java code definition directory. */
	public String getJavaUserSourcePath() {
		return userCodeHome;
	}

	public static String getRelUserCodePackage() {
		return relUserCodePackage;
	}

	private Database openDatabaseRaw(Transaction txn, String tabName, DatabaseConfig configuration)
			throws DatabaseException {
		Database db = environment.openDatabase(txn, tabName, configuration);
		openStorage.put(tabName, db);
		return db;
	}

	// This gnarly bit of code ensures a Berkeley Database is open and valid. If it
	// was opened in a transaction
	// that was rolled back, it will throw an exception when an attempt is made to
	// open a cursor. In that
	// case, it needs to be re-opened in the current transaction.
	private Database openDatabase(Transaction txn, String tabName, DatabaseConfig configuration) {
		Database db = openStorage.get(tabName);
		try {
			if (db == null)
				db = openDatabaseRaw(txn, tabName, configuration);
			else
				db.openCursor(txn, null).close();
		} catch (IllegalStateException de) {
			try {
				db.close();
				db = openDatabaseRaw(txn, tabName, configuration);
			} catch (IllegalStateException de2) {
				de2.printStackTrace();
				throw new ExceptionFatal("RS0326: openDatabase: re-open failed: " + de2);
			}
		}
		return db;
	}

	private void closeDatabase(String tabName) throws DatabaseException {
		Database table = openStorage.get(tabName);
		if (table != null) {
			table.close();
			openStorage.remove(tabName);
		}
	}

	public String getNativeDBVersion() {
		return "Oracle Berkeley DB Java Edition version " + getBerkeleyJavaDBVersion();
	}

	private final File getUniqueIDFile() {
		return new File(databaseHome + java.io.File.separatorChar + "unique.id");
	}

	// Set the next unique ID. No-op if the value is less than what the next ID
	// would have been.
	public synchronized void setUniqueID(long newid) {
		File uniquidFile = getUniqueIDFile();
		long id = 0, nextid = 1;
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(uniquidFile));
			id = dis.readLong();
			nextid = id + 1;
			dis.close();
		} catch (Throwable t) {
			if (!quiet)
				System.out.println("Creating new ID file.");
		}
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(uniquidFile));
			if (newid < nextid)
				dos.writeLong(nextid);
			else
				dos.writeLong(newid);
			dos.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0331: " + t.toString());
		}
	}

	// Obtain a unique ID
	public synchronized long getUniqueID() {
		File uniquidFile = getUniqueIDFile();
		long id = 0, nextid = 1;
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(uniquidFile));
			id = dis.readLong();
			nextid = id + 1;
			dis.close();
		} catch (Throwable t) {
			if (!quiet)
				System.out.println("Creating new ID file.");
		}
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(uniquidFile));
			dos.writeLong(nextid);
			dos.close();
			return id;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0331: " + t.toString());
		}
	}

	// Obtain the current thread ID
	private Long getThreadID() {
		return Long.valueOf(Thread.currentThread().getId());
	}

	public EntryBinding<Tuple> getTupleBinding() {
		return classCatalog.getTupleBinding();
	}

	public DatabaseEntry getKeyTableEntry() {
		return keyTableEntry;
	}

	StoredClassCatalog getClassCatalog() {
		return classCatalog.getStoredClassCatalog();
	}

	/** Begin transaction. */
	public synchronized LocalTransaction beginTransaction() {
		Long threadID = getThreadID();
		LocalTransaction currentTransaction = transactions.get(threadID);
		if (currentTransaction == null) {
			try {
				Transaction txn = environment.beginTransaction(null, null);
				// TODO - parameterise setLockTimeout value somewhere
				txn.setLockTimeout(10, TimeUnit.SECONDS);
				currentTransaction = new LocalTransaction(txn);
				transactions.put(threadID, currentTransaction);
			} catch (DatabaseException dbe) {
				dbe.printStackTrace();
				throw new ExceptionFatal("RS0332: unable to begin new transaction: " + dbe);
			}
		} else
			currentTransaction.addReference();
		return currentTransaction;
	}

	// Get current transaction in this thread. Return null if there isn't one.
	private LocalTransaction getCurrentTransaction() {
		Long threadID = getThreadID();
		return transactions.get(threadID);
	}

	/** Commit specified transaction */
	private void commitTransactionUnsynchronized(LocalTransaction txn) {
		txn.commit();
		if (txn.getReferenceCount() == 0)
			transactions.remove(getThreadID());
	}

	/** Commit specified transaction. */
	public synchronized void commitTransaction(LocalTransaction txn) {
		commitTransactionUnsynchronized(txn);
	}

	/** Commit current transaction. */
	public synchronized void commitTransaction() {
		LocalTransaction currentTransaction = getCurrentTransaction();
		if (currentTransaction == null)
			throw new ExceptionSemantic("RS0208: No transaction is active.");
		commitTransactionUnsynchronized(currentTransaction);
	}

	/** Roll back specified transaction. */
	private void rollbackTransactionUnsynchronized(LocalTransaction txn) {
		txn.abort();
		if (txn.getReferenceCount() == 0)
			transactions.remove(getThreadID());
	}

	/** Roll back specified transaction. */
	synchronized void rollbackTransaction(LocalTransaction txn) {
		rollbackTransactionUnsynchronized(txn);
	}

	/**
	 * Roll back current transaction if there is one. Silently return if there isn't
	 * one.
	 */
	public synchronized void rollbackTransactionIfThereIsOne() {
		reset();
		LocalTransaction currentTransaction = getCurrentTransaction();
		if (currentTransaction == null)
			return;
		while (currentTransaction.getReferenceCount() > 0)
			rollbackTransactionUnsynchronized(currentTransaction);
	}

	/** Roll back current transaction. Throw an exception if there isn't one. */
	public synchronized void rollbackTransaction() {
		LocalTransaction currentTransaction = getCurrentTransaction();
		if (currentTransaction == null)
			throw new ExceptionSemantic("RS0209: No transaction is active.");
		rollbackTransactionUnsynchronized(currentTransaction);
	}

	private String getUniqueTableName() {
		return "relvar_" + getUniqueID();
	}

	/** Get the storage for a given real relvar name. */
	/*
	public synchronized Storage getStorage(Transaction txn, String name) throws DatabaseException {
		RelvarMetadata metadata = getRelvarMetadata(txn, name);
		if (metadata == null)
			return null;
		if (!(metadata instanceof RelvarRealMetadata))
			throw new ExceptionFatal("RS0354: VAR " + name + " is not a REAL relvar.");
		StorageNames storageNames = ((RelvarRealMetadata) metadata).getStorageNames();
		if (storageNames == null)
			return null;
		Storage storage = new Storage(storageNames.size());
		for (int i = 0; i < storageNames.size(); i++) {
			String tabName = storageNames.getName(i);
			Database db = openDatabase(txn, tabName, dbConfigurationNormal);
			storage.setDatabase(i, db);
		}
		return storage;
	}
	*/

	/** Create a real relvar with specified metadata. */
	/*
	public synchronized void createRealRelvar(final Generator generator, final RelvarDefinition relvarInfo) {
		try {
			(new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					if (isRelvarExists(txn, relvarInfo.getName()))
						throw new ExceptionSemantic("RS0218: VAR " + relvarInfo.getName() + " already exists.");
					RelvarMetadata metadata = getRelvarMetadata(txn, relvarInfo.getName());
					if (metadata != null && metadata instanceof RelvarRealMetadata) {
						StorageNames storageNames = ((RelvarRealMetadata) metadata).getStorageNames();
						for (int i = 0; i < storageNames.size(); i++) {
							String tabName = storageNames.getName(i);
							closeDatabase(tabName);
							try {
								environment.removeDatabase(txn, tabName);
							} catch (DatabaseException dbe) {
								dbe.printStackTrace();
								throw new ExceptionFatal("RS0356: unable to remove table " + storageNames);
							}
						}
					}
					RelvarRealMetadata newMetadata = (RelvarRealMetadata) relvarInfo.getRelvarMetadata();
					StorageNames storageNames = new StorageNames(
							newMetadata.getHeadingDefinition(RelDatabase.this).getKeyCount());
					for (int i = 0; i < storageNames.size(); i++) {
						String tabName = getUniqueTableName();
						storageNames.setName(i, tabName);
						openDatabase(txn, tabName, dbConfigurationAllowCreate).close();
						openStorage.remove(tabName);
					}
					newMetadata.setStorageNames(storageNames);
					putRelvarMetadata(txn, relvarInfo.getName(), newMetadata);
					addDependencies(generator, relvarInfo.getName(), Catalog.relvarDependenciesRelvarType,
							relvarInfo.getReferences().getReferencedTypes());
					recordDDL(generator, txn, relvarInfo.toString());
					return null;
				}
			}).execute(this);
		} catch (ExceptionSemantic es) {
			throw es;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0357: createRealRelvar failed: " + t);
		}
	}

	public void createExternalRelvar(final Generator generator, final RelvarDefinition relvarInfo) {
		try {
			(new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					if (isRelvarExists(txn, relvarInfo.getName()))
						throw new ExceptionSemantic("RS0220: VAR " + relvarInfo.getName() + " already exists.");
					putRelvarMetadata(txn, relvarInfo.getName(), relvarInfo.getRelvarMetadata());
					addDependencies(generator, relvarInfo.getName(), Catalog.relvarDependenciesRelvarType,
							relvarInfo.getReferences().getReferencedTypes());
					recordDDL(generator, txn, relvarInfo.toString());
					return null;
				}
			}).execute(this);
		} catch (ExceptionSemantic es) {
			throw es;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0359: createExternalRelvar failed: " + t);
		}
	}
	*/

	/** Open a global relvar. Return null if it doesn't exist. */
	/*
	public synchronized RelvarGlobal openGlobalRelvar(final String name) {
		try {
			return ((RelvarGlobal) (new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					RelvarMetadata metadata = getRelvarMetadata(txn, name);
					if (metadata == null)
						return null;
					return metadata.getRelvar(name, RelDatabase.this);
				}
			}).execute(this));
		} catch (ExceptionSemantic es) {
			throw es;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0361: openRelvar failed: " + t);
		}
	}
	*/

	/** Drop a relvar. Throw an exception if it doesn't exist. */
	/*
	public synchronized void dropRelvar(final String name) {
		try {
			(new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
					RelvarMetadata metadata = getRelvarMetadata(txn, name);
					if (metadata == null)
						throw new ExceptionSemantic("RS0221: VAR " + name + " does not exist.");
					Generator generator = new Generator(RelDatabase.this, System.out);
					StringBuffer dependencies = new StringBuffer();
					obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesRelvarRelvar, "VAR");
					obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesOperatorRelvar,
							"OPERATOR");
					obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesConstraintRelvar,
							"CONSTRAINT");
					obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesTypeRelvar, "TYPE");
					if (dependencies.length() > 0)
						throw new ExceptionSemantic(
								"RS0222: VAR " + name + " may not be dropped due to dependencies:" + dependencies);
					metadata.dropRelvar(RelDatabase.this);
					if (metadata instanceof RelvarRealMetadata) {
						StorageNames storageNames = ((RelvarRealMetadata) metadata).getStorageNames();
						for (int i = 0; i < storageNames.size(); i++) {
							String tabName = storageNames.getName(i);
							closeDatabase(tabName);
							environment.removeDatabase(txn, tabName);
						}
					}
					dropRelvarMetadata(txn, name);
					removeDependencies(generator, name, Catalog.relvarDependenciesRelvarOperator);
					removeDependencies(generator, name, Catalog.relvarDependenciesRelvarRelvar);
					removeDependencies(generator, name, Catalog.relvarDependenciesRelvarType);
					recordDDL(generator, txn, "DROP VAR " + name + ";");
					return null;
				}
			}).execute(this);
		} catch (ExceptionSemantic es) {
			throw es;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal("RS0362: dropRelvar failed: " + t);
		}
	}
	*/

}
