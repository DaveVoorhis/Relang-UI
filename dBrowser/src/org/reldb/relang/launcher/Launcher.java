package org.reldb.relang.launcher;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.about.AboutDialog;
import org.reldb.relang.commands.AcceleratedMenuItem;
import org.reldb.relang.commands.Commands;
import org.reldb.relang.platform.IconLoader;
import org.reldb.relang.platform.Platform;
import org.reldb.relang.platform.UniversalClipboard;
import org.reldb.relang.preferences.Preferences;
import org.reldb.relang.updates.UpdatesCheckDialog;
import org.reldb.relang.utilities.MessageDialog;
import org.reldb.relang.utilities.PlatformDetect;
import org.reldb.relang.version.Version;
import org.reldb.swt.os_specific.OSSpecific;
import org.reldb.relang.datasheet.Datasheets;
import org.reldb.relang.dengine.data.bdbje.BDBJEEnvironment;
import org.reldb.relang.feedback.BugReportDialog;
import org.reldb.relang.feedback.CrashDialog;
import org.reldb.relang.feedback.SuggestionboxDialog;
import org.reldb.relang.log.LogWin;

public class Launcher {

	// Set to true to enable a Tools menu item to deliberately cause a crash for testing exception handling.
	static final boolean showKaboomMenuItem = true;
	
	static boolean createdScreenBar = false;
	
	static Shell shell = null;
	
	private static Queue<Runnable> tasks = new LinkedList<Runnable>();

	private static Datasheets datasheets;
	
	private static synchronized void quit() {
		System.out.println("Shutting down...");
		try {
			Display display = Display.getCurrent();
			if (display != null) {
				Shell[] shells = display.getShells();
				try {
				for (Shell shell: shells)
					if (!shell.isDisposed())
						shell.close();
				} catch (Throwable t) {
					System.out.println("Launcher: Error trying to close shells: " + t);
					t.printStackTrace();
				}
			}
			SWTResourceManager.dispose();
			System.out.println("Shutdown.");
			Platform.exit(0); 
		} catch (Throwable t) {
			System.out.println("Launcher: Error trying to free resources: " + t);
			t.printStackTrace();
		}
	}
	
	private static void addCoreFileMenuItems(Menu menu) {
		new AcceleratedMenuItem(menu, "&New datasheet\tCtrl-N", SWT.MOD1 | 'N', "add-new-document", event -> {
			addRecentlyUsedDatasheetItem(datasheets.create(createShell()));
		});
		
		new AcceleratedMenuItem(menu, "Open datasheet...\tCtrl-O", SWT.MOD1 | 'O', "open-folder-outline", event -> {
			addRecentlyUsedDatasheetItem(datasheets.open(createShell()));
		});

		MenuItem recentItem = new MenuItem(menu, SWT.CASCADE);
		recentItem.setImage(IconLoader.loadIcon("list"));
		recentItem.setText("Recently-opened datasheets");
		
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent arg0) {
				Menu recentDatabases = new Menu(menu);
				recentItem.setMenu(recentDatabases);				
				String[] dbURLs = getRecentlyUsedDatasheetList();
				if (dbURLs.length > 0) {
					int recentlyUsedCount = 0;
					for (String dbURL: dbURLs) {
						(new AcceleratedMenuItem(recentDatabases, "Open " + dbURL, 0, "OpenDBLocalIcon", e -> openDatasheet(dbURL))).setEnabled(datasheetMayExist(dbURL));
						if (++recentlyUsedCount >= 20)	// arbitrarily decide 20 is enough
							break;
					}
					new MenuItem(recentDatabases, SWT.SEPARATOR);
					new AcceleratedMenuItem(recentDatabases, "Clear this list...", 0, null, e -> clearRecentlyUsedDatasheetList());
				}
			}
		});
	}
	
	static void createFileMenu(Menu bar) {	
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);			
		fileItem.setText("File");
		
		Menu menu = new Menu(fileItem);
		fileItem.setMenu(menu);

		if (!PlatformDetect.isWeb())
			addCoreFileMenuItems(menu);
		
		OSSpecific.addFileMenuItems(menu);
	}
	
	protected static boolean datasheetMayExist(String dbURL) {
		return BDBJEEnvironment.exists(dbURL);
	}

	protected static void clearRecentlyUsedDatasheetList() {
		// TODO Auto-generated method stub
	}

	protected static void addRecentlyUsedDatasheetItem(String dbURL) {
		if (dbURL == null)
			return;
	}
	
	protected static void openDatasheet(String dbURL) {
		addRecentlyUsedDatasheetItem(datasheets.open(createShell(), dbURL));
	}

	protected static String[] getRecentlyUsedDatasheetList() {
		// TODO - get list of recently used things
		return new String[] {};
	}

	private static boolean isWebSite(Control control) {
		return control.getClass().getName().equals("org.eclipse.swt.browser.WebSite");		
	}
	
	private static boolean isThereSomethingToPaste() {
		UniversalClipboard clipboard = new UniversalClipboard(Display.getCurrent());
		try {
			TextTransfer textTransfer = TextTransfer.getInstance();
			HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
			String textData = (String)clipboard.getContents(textTransfer);
			String htmlData = (String)clipboard.getContents(htmlTransfer);
			return (textData != null && textData.length() > 0) || (htmlData != null && htmlData.length() > 0);
		} finally {
			clipboard.dispose();	
		}
	}
	
	private static Method getEditMethod(String methodName, Control control) {
		if (control == null)
			return null;
		if (isWebSite(control)) {
			// Browser browser = (Browser)getParent().getParent();
			Object webSite = control;
			Method getParent;
			try {
				getParent = webSite.getClass().getMethod("getParent");
				Object webSiteParent = getParent.invoke(webSite);
				Method getParentOfParent = webSiteParent.getClass().getMethod("getParent");
				Object browser = getParentOfParent.invoke(webSiteParent);
				return browser.getClass().getMethod(methodName);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		} else {		
			Class<?> controlClass = control.getClass();
			try {
				return (Method)controlClass.getMethod(methodName, new Class<?>[0]);
			} catch (NoSuchMethodException | SecurityException e) {
				return null;
			}
		}
	}
	
	private static AcceleratedMenuItem createEditMenuItem(String methodName, AcceleratedMenuItem menuItem) {
		class EditMenuAdapter extends MenuAdapter { 
			Control focusControl;
			LinkedList<Method> menuItemMethods = new LinkedList<Method>();
		};
		EditMenuAdapter menuAdapter = new EditMenuAdapter() {
			@Override
			public void menuShown(MenuEvent arg0) {
				focusControl = menuItem.getDisplay().getFocusControl();
				if (focusControl == null)
					return;
				if (!menuItem.canExecute()) {
					menuItem.setEnabled(false);
					return;
				}
				Method method = getEditMethod(methodName, focusControl);
				if (method != null) {
					menuItemMethods.add(method);
					menuItem.setEnabled(true);
				} else {
					if (methodName.equals("clear")) {
						Method selectAll = getEditMethod("selectAll", focusControl);
						Method cut = getEditMethod("cut", focusControl);
						if (selectAll != null && cut != null) {
							menuItemMethods.add(selectAll);
							menuItemMethods.add(cut);
							menuItem.setEnabled(true);
						} else
							menuItem.setEnabled(false);
					} else
						menuItem.setEnabled(false);
				}
			}
		};
		menuItem.getParent().addMenuListener(menuAdapter);
		menuItem.addListener(SWT.Selection, evt -> {
			try {
				for (Method method: menuAdapter.menuItemMethods)
					method.invoke(menuAdapter.focusControl, new Object[0]);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		});
		menuItem.setEnabled(false);
		return menuItem;
	}
	
	private static void createEditMenu(Menu bar) {		
		if (!UniversalClipboard.isClipboardAvailable())
			return;
		
		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("Edit");
		
		Menu menu = new Menu(editItem);
		editItem.setMenu(menu);
				
		createEditMenuItem("undo", new AcceleratedMenuItem(menu, "Undo\tCtrl-Z", SWT.MOD1 | 'Z', "undo"));
		
		int redoAccelerator = SWT.MOD1 | (PlatformDetect.isMac() ? SWT.SHIFT | 'Z' : 'Y');
		createEditMenuItem("redo", new AcceleratedMenuItem(menu, "Redo\tCtrl-Y", redoAccelerator, "redo"));
		
		new MenuItem(menu, SWT.SEPARATOR);

		createEditMenuItem("cut", new AcceleratedMenuItem(menu, "Cut\tCtrl-X", SWT.MOD1 | 'X', "cut"));
		createEditMenuItem("copy", new AcceleratedMenuItem(menu, "Copy\tCtrl-C", SWT.MOD1 | 'C', "copy"));
		createEditMenuItem("paste", new AcceleratedMenuItem(menu, "Paste\tCtrl-V", SWT.MOD1 | 'V', "paste") {
			public boolean canExecute() {
				return isThereSomethingToPaste();
			}
		});
		
		createEditMenuItem("delete", new AcceleratedMenuItem(menu, "Delete\tDel", SWT.DEL, "rubbish-bin"));
		createEditMenuItem("selectAll", new AcceleratedMenuItem(menu, "Select All\tCtrl-A", SWT.MOD1 | 'A', "select-all"));
	}
	
	static void createDataMenu(Menu bar) {
		MenuItem dataItem = new MenuItem(bar, SWT.CASCADE);
		dataItem.setText("Data");
		
		Menu menu = new Menu(dataItem);
		dataItem.setMenu(menu);

		Commands.linkCommand(Commands.Do.NewGrid, new AcceleratedMenuItem(menu, "New Grid...\tCtrl-G", SWT.MOD1 | 'G', "newgrid"));
		Commands.linkCommand(Commands.Do.Link, new AcceleratedMenuItem(menu, "Link...\tCtrl-L", SWT.MOD1 | 'L', "link"));
		Commands.linkCommand(Commands.Do.Import, new AcceleratedMenuItem(menu, "Import...\tCtrl-I", SWT.MOD1 | 'I', "import"));
	}

	@SuppressWarnings("null")
	private static void kaboom() {
		Object object = null; 
		object.toString();		
	}
	
	private static void createToolMenu(Menu bar) {
		MenuItem toolsItem = new MenuItem(bar, SWT.CASCADE);
		toolsItem.setText("Tools");
		
		Menu menu = new Menu(toolsItem);
		toolsItem.setMenu(menu);
		
		new AcceleratedMenuItem(menu, "View log", 0, "log", e -> LogWin.open());
		new MenuItem(menu, SWT.SEPARATOR);
		new AcceleratedMenuItem(menu, "Submit Feedback", 0, "idea", e -> new SuggestionboxDialog(shell).open());
		new AcceleratedMenuItem(menu, "Bug Report", 0, "bug_menu", e -> new BugReportDialog(shell).open());
		new AcceleratedMenuItem(menu, "Check for Updates", 0, "upgrade_menu", e -> new UpdatesCheckDialog(shell).open());
		
		if (showKaboomMenuItem) {
			new MenuItem(menu, SWT.SEPARATOR);
			new AcceleratedMenuItem(menu, "Force crash for testing purposes", 0, null, e -> kaboom());
		}
	}
	
	private static void createHelpMenu(Menu bar) {
		MenuItem helpItem = new MenuItem(bar, SWT.CASCADE);
		helpItem.setText("Help");
		
		Menu menu = new Menu(helpItem);
		helpItem.setMenu(menu);
		
		OSSpecific.addHelpMenuItems(menu);
	}
	
	static void createMenuBar(Shell shell) {
		Menu bar = Display.getCurrent().getMenuBar();
		boolean hasAppMenuBar = (bar != null);
		
		if (bar == null)
			bar = new Menu(shell, SWT.BAR);

		// Populate the menu bar once if this is a screen menu bar.
		// Otherwise, we need to make a new menu bar for each shell.
		if (!createdScreenBar || !hasAppMenuBar) {
			createFileMenu(bar);
			createEditMenu(bar);
			createDataMenu(bar);
			createToolMenu(bar);
			createHelpMenu(bar);
			
			if (!hasAppMenuBar) 
				shell.setMenuBar(bar);
			createdScreenBar = true;
		}
	}

	public static void openFile(String text) {
		// TODO Auto-generated method stub
		
	}
	
	private static boolean menubarCreated = false;
	
	private static Shell createShell() {
		final Shell shell = new Shell(SWT.SHELL_TRIM);
		if (!PlatformDetect.isWeb() && !menubarCreated) {
			createMenuBar(shell);
			menubarCreated = true;
		}
		shell.setImage(IconLoader.loadIcon("RelangIcon"));
		shell.setImages(loadIcons(Display.getCurrent()));
		shell.setText(Version.getAppID());
		return shell;
	}

	private static void closeSplash() {
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null)
			splash.close();
	}
	
	// If there is a splash screen, return true and execute splashInteraction.
	// If there is no splash screen, return false. Do not execute splashInteraction.
	//
	// Based on https://stackoverflow.com/questions/21022788/is-there-a-chance-to-get-splashimage-work-for-swt-applications-that-require
	private static boolean executeSplashInteractor(Runnable splashInteraction) {
		if (SplashScreen.getSplashScreen() == null)
			return false;
		
		// Non-MacOS
		if (!PlatformDetect.isMac()) {
			splashInteraction.run();
			closeSplash();
			return true;
		}

		// MacOS
		Display display = Display.getDefault();
		final Semaphore sem = new Semaphore(0);
		Thread splashInteractor = new Thread(() -> {
			splashInteraction.run();
			sem.release();
			display.asyncExec(() -> {});
			closeSplash();
		});
		splashInteractor.start();

		// Interact with splash screen
		while (!display.isDisposed() && !sem.tryAcquire())
			if (!display.readAndDispatch())
				display.sleep();
		
		return true;
	}

	private static Image[] loadIcons(Display display) {
		ClassLoader loader = Launcher.class.getClassLoader();
		LinkedList<Image> iconImages = new LinkedList<>();
		for (String resourceSpec: Version.getIconsPaths()) {
			InputStream inputStream = loader.getResourceAsStream(resourceSpec);
			if (inputStream != null) {
				iconImages.add(new Image(display, inputStream));
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Unable to load icon " + resourceSpec);
			}
		}
		return iconImages.toArray(new Image[0]);		
	}

	public static void addTask(Runnable task) {
		tasks.add(task);
	}
	
	private static void doWaitingTasks() {
		if (shell == null)
			return;
		shell.getDisplay().syncExec(() -> {
			while (!tasks.isEmpty())
				tasks.remove().run();
		});
	}
	
	/** Desktop launcher. */
	public static void launch(String[] args) {
		Display.setAppName(Version.getAppName());
		Display.setAppVersion(Version.getAppID());
		final Display display = new Display();
		
		OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor();
		display.addListener(SWT.OpenDocument, openDocProcessor);
		
		openDocProcessor.addFilesToOpen(args);		

		if (PlatformDetect.isMac())
			executeSplashInteractor(() -> {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
				}
			});
		
		OSSpecific.launch(Version.getAppName(),
			event -> quit(),
			event -> new AboutDialog(shell).open(),
			event -> new Preferences(shell).show()
		);

		if (!PlatformDetect.isMac()) {
			SplashScreen splash = SplashScreen.getSplashScreen();
			if (splash != null) {
				Graphics2D gc = splash.createGraphics();
				Rectangle rect = splash.getBounds();
				int barWidth = rect.width - 20;
				int barHeight = 10;
				Rectangle progressBarRect = new Rectangle(10, rect.height - 20, barWidth, barHeight);
				gc.draw3DRect(progressBarRect.x, progressBarRect.y, progressBarRect.width, progressBarRect.height, false);
				gc.setColor(Color.green);
				(new Thread(() -> {
					while (SplashScreen.getSplashScreen() != null) {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
						}
					}							
				})).start();
			}
		}
		
		shell = createShell();
		shell.addListener(SWT.Dispose, evt -> {
			LogWin.remove();
			quit();
		});

		LogWin.install(createShell());
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private int failureCount = 0;

			public void uncaughtException(Thread t, Throwable e) {
				if (failureCount > 1) {
					System.err
							.println("SYSTEM ERROR!  It's gotten even worse.  This is a last-ditch attempt to escape.");
					failureCount++;
					Thread.setDefaultUncaughtExceptionHandler(null);
					System.exit(1);
					return;
				}
				if (failureCount > 0) {
					System.err.println(
							"SYSTEM ERROR!  Things have gone so horribly wrong that we can't recover or even pop up a message.  I hope someone sees this...\nShutting down now, if we can.");
					failureCount++;
					System.exit(1);
					return;
				}
				failureCount++;
				if (e instanceof OutOfMemoryError) {
					System.err.println("Out of memory!");
					e.printStackTrace();
					MessageDialog.openError(shell, "OUT OF MEMORY", "Out of memory!  Shutting down NOW!");
					shell.dispose();
				} else {
					System.err.println("Unknown error: " + t);
					e.printStackTrace();
					MessageDialog.openError(shell, "Unexpected Error", e.toString());
					shell.dispose();
				}
				System.exit(1);
			}
		});

		String[] filesToOpen = openDocProcessor.retrieveFilesToOpen();
		for (String fname: filesToOpen)
			openFile(fname);
		
		if (!PlatformDetect.isMac())
			closeSplash();
		
		datasheets = new Datasheets();
		
		// Thunderbirds are go.
		shell.open();
		var baseDir = System.getProperty("user.home") + File.separator + "relangbase";
		try {
			datasheets.openOrCreate(shell, baseDir, true);
		} catch (com.sleepycat.je.EnvironmentLockedException ele) {
			MessageDialog.openError(shell, "Database in Use", "The database at " + baseDir + " appears to already be in use.");
		}
		
		while (display != null && !display.isDisposed()) {
			try {
				if (display != null && !display.readAndDispatch()) {
					doWaitingTasks();
					display.sleep();
				}
			} catch (Throwable exception) {
				CrashDialog.launch(exception);
			}
		}
	}

	/** RWT (Web) launcher. */
	public static void launch(Composite parent) {
		Display.setAppName(Version.getAppName());
		Display.setAppVersion(Version.getAppID());
		
		shell = (Shell) parent;
		
		Display.setAppName(Version.getAppName());
		Display.setAppVersion(Version.getAppID());
		
		OSSpecific.launch(Version.getAppName(),
			event -> quit(),
			event -> new AboutDialog(shell).open(),
			event -> new Preferences(shell).show()
		);
		
		shell.addListener(SWT.Dispose, evt -> {
			shutdownBackgroundRunner();
			LogWin.remove();
			quit();
		});
		
		LogWin.install(createShell());
				
		datasheets = new Datasheets();
		
		createMenuBar(shell);
		shell.setImage(IconLoader.loadIcon("RelangIcon"));
		shell.setImages(loadIcons(Display.getCurrent()));
		shell.setText(Version.getAppID());

		startBackgroundRunner();
		
		// Thunderbirds are go.
		var baseDir = System.getProperty("user.home") + File.separator + "relangbase";
		try {
			datasheets.openOrCreate(shell, baseDir, true);
		} catch (com.sleepycat.je.EnvironmentLockedException ele) {
			MessageDialog.openError(shell, "Database in Use", "The database at " + baseDir + " appears to already be in use.");
		}
	}

	private static boolean backgroundRunnerRunning = false;
	
	private static void startBackgroundRunner() {
		// Background task processor.
		var background = new Thread(() -> {
			 backgroundRunnerRunning = true;
			 while (backgroundRunnerRunning) {
				 doWaitingTasks();
				 try {
					 Thread.sleep(500);
				 } catch (InterruptedException e1) {}
			 }
		});
		background.setDaemon(true);
		background.start();
	}

	private static void shutdownBackgroundRunner() {
		backgroundRunnerRunning = false;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {}
	}
	
}