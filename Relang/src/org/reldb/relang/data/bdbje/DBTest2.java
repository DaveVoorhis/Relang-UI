package org.reldb.relang.data.bdbje;
import java.io.File;
import java.io.Serializable;
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

public class DBTest2 implements TransactionWorker {

	private static boolean create = true;

	private Environment env;
	private ClassCatalog catalog;
	private Database db;
	private SortedMap<Key, Data> map;

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

		String dir = "./data/dbtest2";
		mkdir(dir);

		// environment is transactional
		var envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		if (create) {
			envConfig.setAllowCreate(true);
		}
		var env = new Environment(new File(dir), envConfig);

		// create the application and run a transaction
		var worker = new DBTest2(env);
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
	private DBTest2(Environment env) throws Exception {
		this.env = env;
		open();
	}

	/** Performs work within a transaction. */
	public void doWork() {
		writeAndRead();
	}
	
	private static class Data implements Serializable {
		private static final long serialVersionUID = 1L;
		private int a;
		private int b;
		private int c;
		public Data(int a, int b, int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
		public String toString() {
			return "Data(" + a + ", " + b + ", " + c + ")";
		}
		public int getA() {
			return a;
		}
		public int getB() {
			return b;
		}
	}

	private static class Key implements Serializable {
		private static final long serialVersionUID = 1L;
		private int a;
		private int b;
		public Key(Data data) {
			this.a = data.getA();
			this.b = data.getB();
		}
		public String toString() {
			return "Key(" + a + ", " + b + ")";
		}
		public int getA() {
			return a;
		}
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

		// use tuple binding for key entries
		var keyBinding = new SerialBinding<Key>(catalog, Key.class);
		
		// use serial binding for data entries
		var dataBinding = new SerialBinding<Data>(catalog, Data.class);

		this.db = env.openDatabase(null, "helloworld", dbConfig);

		// create a map view of the database
		this.map = new StoredSortedMap<Key, Data>(db, keyBinding, dataBinding, true);
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
		final int maxValue = 10000000;
		final int step = 100000;
		
		// check for existing data
		var idata = new Data(0, 0 + 12, 0 * 10);
		var ikey = new Key(idata);
		var val = map.get(ikey);
		if (val == null) {
			System.out.println("Writing data");
			for (int i = 0; i < maxValue; i++) {
				var data = new Data(i, i + 12, i * 10);
				if (i % step == 0)
					System.out.println(i);
				map.put(new Key(data), data);
			}
		}
		// get iterator over map entries
		var iter = map.entrySet().iterator();
		System.out.println("Reading data");
		while (iter.hasNext()) {
			var entry = iter.next();
			var key = entry.getKey();
			var keyvalue = key.getA();
			if (keyvalue % step == 0)
				System.out.println(key + " " + entry.getValue().toString());
		}
		System.out.println("Done");
	}

}
