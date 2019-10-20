package org.reldb.relang.rwt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.reldb.relang.feedback.CrashDialog;
import org.reldb.relang.version.Version;

public class RelangApplicationConfiguration implements ApplicationConfiguration {

	/** Override to register application resources and entry points. */
	protected void register(Application application) {}
	
	/** Override to obtain specified RapidEntryPoint class or subclass. */
	protected Class<? extends RelangEntryPoint> getEntryPoint() {
		return RelangEntryPoint.class;
	}
	
	protected RegisteredImageResource registerImage(Application application, String imageFileName) {
		// This rather laborious machinery is necessary because Display.getCurrent() -- normally
		// needed to instantiate an Image so that we can obtain ImageData -- will return null
		// during app configuration. So this is the workaround. Note that it will not work on
		// .ico files, but .png files work as favicon images.
		var resourceStream = getClass().getClassLoader().getResourceAsStream(imageFileName);
		if (resourceStream == null) {
			System.out.println("RelangApplicationConfiguration: Unable to obtain stream for " + imageFileName);
			return null;
		}
		BufferedImage imageData;
		try {
			imageData = ImageIO.read(resourceStream);
		} catch (IOException e) {
			System.out.println("RelangApplicationConfiguration: Unable to read image file " + imageFileName);
			return null;
		}
		if (imageData == null) {
			System.out.println("RelangApplicationConfiguration: BufferedImage is null for " + imageFileName);
			return null;
		}
	    return new RegisteredImageResource(application, imageFileName, imageData.getWidth(), imageData.getHeight());
	}
	
	private void registerResources(Application application) {
		if (Version.getFavIconImage() != null)
			registerImage(application, Version.getFavIconImage());
        register(application);
	}
	
    public void configure(Application application) {
		// Set up exception handler per http://www.eclipse.org/rap/developers-guide/devguide.php?topic=application-configuration.html&version=3.2
		application.setExceptionHandler(exception -> {
			exception.printStackTrace();
			CrashDialog.launch(exception);
		});
		// Set up page attributes.
        Map<String, String> properties = new HashMap<String, String>();
		properties.put(WebClient.PAGE_TITLE, Version.getAppName());
		properties.put(WebClient.PAGE_OVERFLOW, "scrollY" );
		properties.put(WebClient.BODY_HTML, "<br/>&nbsp;&nbsp;Loading...");
		if (Version.getFavIconImage() != null)
			properties.put(WebClient.FAVICON, Version.getFavIconImage());
		// properties.put(WebClient.THEME_ID, "MyCustomTheme");
		// Set up entry point.
        application.addEntryPoint("/", getEntryPoint(), properties);
        registerResources(application);
    }

}
