import org.reldb.relang.core.utilities.MessageDialog;
import org.reldb.relang.core.main.Launcher;

public class Main {
	public static void main(String args[]) {
		try {
			Launcher.launch(args);
		} catch (Throwable t) {
			t.printStackTrace();
			MessageDialog.openError(null, "Launch Failure", "Check the system log for details about:\n\n" + t.toString());
		} finally {
			System.exit(0);
		}
	}
}
