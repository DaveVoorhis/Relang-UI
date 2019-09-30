package org.reldb.relang.rwt;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.Version;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

public class RelangApplicationConfiguration implements ApplicationConfiguration {
	
	public static RegisteredImageResource home = null;
	public static RegisteredImageResource logo = null;

	/** Override to register application resources and entry points. */
	protected void register(Application application) {}
	
	/** Override to obtain specified RapidEntryPoint class or subclass. */
	protected Class<? extends RelangEntryPoint> getEntryPoint() {
		return RelangEntryPoint.class;
	}
	
	private void registerResources(Application application) {
		/*
		if (VersionProxy.getVersion().getLogoImage() != null)
			logo = new RegisteredImageResource(application, VersionProxy.getVersion().getLogoImage(), VersionProxy.getVersion().getLogoImageWidth(), VersionProxy.getVersion().getLogoImageHeight());
		if (VersionProxy.getVersion().getHomeImage() != null)
			home = new RegisteredImageResource(application, VersionProxy.getVersion().getHomeImage(), VersionProxy.getVersion().getHomeImageWidth(), VersionProxy.getVersion().getHomeImageHeight());
		if (VersionProxy.getVersion().getFavIconImage() != null)
			new RegisteredImageResource(application, VersionProxy.getVersion().getFavIconImage(), VersionProxy.getVersion().getFavIconImageWidth(), VersionProxy.getVersion().getFavIconImageHeight());
		*/
        register(application);
	}
	
    public void configure(Application application) {
		/*
		 * Setup exception handler
		 * 
		 * From http://www.eclipse.org/rap/developers-guide/devguide.php?topic=application-configuration.html&version=3.2
		 * 
		application.setExceptionHandler(new ExceptionHandler() {
		@Override
		  public void handleException(Throwable exception) {
			new Popup(getShell(), );
		    // display error dialog, redirect to error page,
		    // write exception to log, ...
		  }
		});
		*/
    	// VersionProxy.setProxy(getVersion());
        Map<String, String> properties = new HashMap<String, String>();
		properties.put(WebClient.PAGE_TITLE, Version.getProduct());
		properties.put(WebClient.PAGE_OVERFLOW, "scrollY" );
		properties.put(WebClient.BODY_HTML, "<br/>&nbsp;&nbsp;Loading...");
		// if (VersionProxy.getVersion().getFavIconImage() != null)
		//	properties.put(WebClient.FAVICON, VersionProxy.getVersion().getFavIconImage());
		// properties.put(WebClient.THEME_ID, "MyCustomTheme");
        application.addEntryPoint("/", getEntryPoint(), properties);
        registerResources(application);
    }

}
