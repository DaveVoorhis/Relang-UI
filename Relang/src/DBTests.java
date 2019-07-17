import com.sleepycat.je.JEVersion;

public class DBTests {

	public static void main(String[] args) {
		System.out.println("JEVersion: " + JEVersion.CURRENT_VERSION.getVersionString());
	}

}
