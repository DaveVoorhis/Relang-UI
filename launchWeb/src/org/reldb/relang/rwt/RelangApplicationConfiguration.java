package org.reldb.relang.rwt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.swt.widgets.Display;
import org.reldb.relang.feedback.CrashDialog;
import org.reldb.relang.version.Version;

public class RelangApplicationConfiguration implements ApplicationConfiguration {

	/** Override to register application resources and entry points. */
	protected void register(Application application) {}
	
	/** Override to obtain specified RapidEntryPoint class or subclass. */
	protected Class<? extends RelangEntryPoint> getEntryPoint() {
		return RelangEntryPoint.class;
	}
	
	private RegisteredImageResource registerImage(Application application, String imageFileName) {
		var stream = getClass().getClassLoader().getResourceAsStream(imageFileName);
		if (stream == null) {
			System.out.println("RelangApplicationConfiguration: Unable to obtain stream for " + imageFileName);
			return null;
		}
		BufferedImage readImage;
		try {
			readImage = ImageIO.read(stream);
		} catch (IOException e) {
			System.out.println("RelangApplicationConfiguration: Unable to read image file " + imageFileName);
			return null;
		}
		if (readImage == null) {
			System.out.println("RelangApplicationConfiguration: BufferedImage is null for " + imageFileName);
			return null;
		}
	    int h = readImage.getHeight();
	    int w = readImage.getWidth();
	    return new RegisteredImageResource(application, imageFileName, w, h);
	}
	
	private void registerResources(Application application) {
		if (Version.getFavIconImage() != null)
			registerImage(application, Version.getFavIconImage());
        register(application);
	}
	
    public void configure(Application application) {
		// Setup exception handler per http://www.eclipse.org/rap/developers-guide/devguide.php?topic=application-configuration.html&version=3.2
		application.setExceptionHandler(exception -> CrashDialog.launch(Display.getCurrent().getActiveShell(), exception));
        Map<String, String> properties = new HashMap<String, String>();
		properties.put(WebClient.PAGE_TITLE, Version.getAppName());
		properties.put(WebClient.PAGE_OVERFLOW, "scrollY" );
		properties.put(WebClient.BODY_HTML, "<br/>&nbsp;&nbsp;Loading...");
		if (Version.getFavIconImage() != null)
			properties.put(WebClient.FAVICON, Version.getFavIconImage());
		// properties.put(WebClient.THEME_ID, "MyCustomTheme");
        application.addEntryPoint("/", getEntryPoint(), properties);
        registerResources(application);
    }

}
