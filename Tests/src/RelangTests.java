import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.*;

public class RelangTests {
		
	public static void main(String args[]) {
	    final LauncherDiscoveryRequest request = 
		        LauncherDiscoveryRequestBuilder.request()
		                                   .selectors(
		                                		   selectPackage("org.reldb.relang.tests.main")
		                                		   )
		                                   .build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        long testFoundCount = summary.getTestsFoundCount();
        var failures = summary.getFailures();
        System.out.println("Tests found: " + testFoundCount);
        System.out.println("Tests succeeded: " + summary.getTestsSucceededCount());
        failures.forEach(failure -> System.out.println("Test failed: " + failure.getException()));		
	}
}
