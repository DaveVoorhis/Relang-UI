import java.io.File;
import java.util.SortedMap;

import org.reldb.relang.utilities.ExceptionFatal;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.JEVersion;

public class DBTest1 implements TransactionWorker {

	private static boolean create = true;

	private Environment env;
	private ClassCatalog catalog;
	private Database db;
	private SortedMap<Integer, String> map;

	public static void mkdir(String dir) {
		File dirf = new File(dir);
		if (!dirf.exists()) {
			if (!dirf.mkdirs()) {
				String msg = "Unable to create directory: " + dirf;
				throw new ExceptionFatal("RS0324: " + msg);
			}
		}
	}

	/** Creates the environment and runs a transaction */
	public static void main(String[] argv) throws Exception {

		System.out.println("JEVersion: " + JEVersion.CURRENT_VERSION.getVersionString());

		String dir = "./data/dbtest1";
		mkdir(dir);

		// environment is transactional
		var envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		if (create) {
			envConfig.setAllowCreate(true);
		}
		var env = new Environment(new File(dir), envConfig);

		// create the application and run a transaction
		var worker = new DBTest1(env);
		var runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			runner.run(worker);
		} finally {
			// close the database outside the transaction
			worker.close();
		}
	}

	/** Creates the database for this application */
	private DBTest1(Environment env) throws Exception {
		this.env = env;
		open();
	}

	/** Performs work within a transaction. */
	public void doWork() {
		writeAndRead();
	}

	/** Opens the database and creates the Map. */
	private void open() throws Exception {

		// use a generic database configuration
		var dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		if (create) {
			dbConfig.setAllowCreate(true);
		}

		// catalog is needed for serial bindings (java serialization)
		var catalogDb = env.openDatabase(null, "catalog", dbConfig);
		catalog = new StoredClassCatalog(catalogDb);

		// use Integer tuple binding for key entries
		var keyBinding = TupleBinding.getPrimitiveBinding(Integer.class);

		// use String serial binding for data entries
		var dataBinding = new SerialBinding<String>(catalog, String.class);

		this.db = env.openDatabase(null, "helloworld", dbConfig);

		// create a map view of the database
		this.map = new StoredSortedMap<Integer, String>(db, keyBinding, dataBinding, true);
	}

	/** Closes the database. */
	private void close() throws Exception {
		if (catalog != null) {
			catalog.close();
			catalog = null;
		}
		if (db != null) {
			db.close();
			db = null;
		}
		if (env != null) {
			env.close();
			env = null;
		}
	}

	/** Writes and reads the database via the Map. */
	private void writeAndRead() {
		// check for existing data
		var key = Integer.valueOf(0);
		var val = map.get(key);
		if (val == null) {
			System.out.println("Writing data");
			for (int i = 0; i < 10000000; i++) {
				if (i % 100000 == 0)
					System.out.println(i);
				map.put(Integer.valueOf(i), "Datum " + i);
			}
		}
		// get iterator over map entries
		var iter = map.entrySet().iterator();
		System.out.println("Reading data");
		while (iter.hasNext()) {
			var entry = iter.next();
			var keyvalue = entry.getKey();
			if (keyvalue % 100000 == 0)
				System.out.println(keyvalue + ' ' + entry.getValue());
		}
		System.out.println("Done");
	}

}
