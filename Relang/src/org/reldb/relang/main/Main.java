package org.reldb.relang.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.relang.about.AboutDialog;
import org.reldb.relang.commands.AcceleratedMenuItem;
import org.reldb.relang.core.Datasheet;
import org.reldb.relang.data.DataTemporary;
import org.reldb.relang.feedback.BugReportDialog;
import org.reldb.relang.feedback.CrashDialog;
import org.reldb.relang.feedback.SuggestionboxDialog;
import org.reldb.relang.log.LogWin;
import org.reldb.relang.preferences.Preferences;
import org.reldb.relang.sheet.Sheet;
import org.reldb.relang.updates.UpdatesCheckDialog;
import org.reldb.relang.utilities.IconLoader;
import org.reldb.relang.utilities.MessageDialog;
import org.reldb.relang.utilities.PlatformDetect;
import org.reldb.relang.version.Version;
import org.reldb.swt.os_specific.OSSpecific;

public class Main {

	// Set to true to enable a Tools menu item to deliberately cause a crash for testing exception handling.
	static final boolean showKaboomMenuItem = false;
	
	static boolean createdScreenBar = false;
	
	static Shell shell = null;
	
	private static Queue<Runnable> tasks = new LinkedList<Runnable>();
	
	private static synchronized void quit() {
		Display display = Display.getCurrent();
		Shell[] shells = display.getShells();
		try {
		for (Shell shell: shells)
			if (!shell.isDisposed())
				shell.close();
		} catch (Throwable t) {
			System.out.println("Error trying to close shells: " + t);
			t.printStackTrace();
		}
		try {
			if (!display.isDisposed()) 
				display.dispose();
		} catch (Throwable t) {
			System.out.println("Error trying to close display: " + t);
			t.printStackTrace();
		}
		try {
			SWTResourceManager.dispose();
		} catch (Throwable t) {
			System.out.println("Error trying to free resources: " + t);
		}
	}
	
	static void createFileMenu(Menu bar) {	
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);			
		fileItem.setText("File");
		
		Menu menu = new Menu(fileItem);
		fileItem.setMenu(menu);

		new AcceleratedMenuItem(menu, "&New datasheet\tCtrl-N", SWT.MOD1 | 'N', "add-new-document", event -> {
			Shell newDatasheet = createShell();
			newDatasheet.setText("New Datasheet");
			new Datasheet(newDatasheet);
			newDatasheet.open();
		});
		
		new AcceleratedMenuItem(menu, "Open datasheet...\tCtrl-O", SWT.MOD1 | 'O', "open-folder-outline", event -> {
			
		});

		MenuItem recentItem = new MenuItem(menu, SWT.CASCADE);
		recentItem.setImage(IconLoader.loadIcon("list"));
		recentItem.setText("Recently-opened datasheets");
		
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent arg0) {
				Menu recentDatabases = new Menu(menu);
				recentItem.setMenu(recentDatabases);				
				String[] dbURLs = getRecentlyUsedDatabaseList();
				if (dbURLs.length > 0) {
					int recentlyUsedCount = 0;
					for (String dbURL: dbURLs) {
						(new AcceleratedMenuItem(recentDatabases, "Open " + dbURL, 0, "OpenDBLocalIcon", e -> openDatabase(dbURL))).setEnabled(databaseMayExist(dbURL));
						if (++recentlyUsedCount >= 20)	// arbitrarily decide 20 is enough
							break;
					}
					new MenuItem(recentDatabases, SWT.SEPARATOR);
					new AcceleratedMenuItem(recentDatabases, "Clear this list...", 0, null, e -> clearRecentlyUsedDatabaseList());
				}
			}
		});
		
		OSSpecific.addFileMenuItems(menu);
	}
	
	protected static boolean databaseMayExist(String dbURL) {
		// TODO Auto-generated method stub
		return false;
	}

	protected static Object clearRecentlyUsedDatabaseList() {
		// TODO Auto-generated method stub
		return null;
	}

	protected static Object openDatabase(String dbURL) {
		// TODO Auto-generated method stub
		return null;
	}

	protected static String[] getRecentlyUsedDatabaseList() {
		// TODO - get list of recently used things
		return new String[] {};
	}

	private static boolean isWebSite(Control control) {
		return control.getClass().getName().equals("org.eclipse.swt.browser.WebSite");		
	}
	
	private static boolean isThereSomethingToPaste() {
		Clipboard clipboard = new Clipboard(Display.getCurrent());
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
	
	/*
	// Link a command (which implies a toolbar-accessible action) with a menu item.
	private static void linkCommand(Commands.Do command, AcceleratedMenuItem menuItem) {
		Commands.linkCommand(command, menuItem);
	}
	*/
	
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

		/*
		linkCommand(Commands.Do.FindReplace, new AcceleratedMenuItem(menu, "Find/Replace", 0, "edit_find_replace"));

		new MenuItem(menu, SWT.SEPARATOR);
		
		linkCommand(Commands.Do.SpecialCharacters, new AcceleratedMenuItem(menu, "Special characters", 0, "characters"));
		linkCommand(Commands.Do.PreviousHistory, new AcceleratedMenuItem(menu, "Previous history", 0, "previousIcon"));
		linkCommand(Commands.Do.NextHistory, new AcceleratedMenuItem(menu, "Next history", 0, "nextIcon"));
		linkCommand(Commands.Do.LoadFile, new AcceleratedMenuItem(menu, "Load file", 0, "loadIcon"));
		linkCommand(Commands.Do.InsertFile, new AcceleratedMenuItem(menu, "Insert file", 0, "loadInsertIcon"));
		linkCommand(Commands.Do.InsertFileName, new AcceleratedMenuItem(menu, "Insert file name", 0, "pathIcon"));
		linkCommand(Commands.Do.SaveFile, new AcceleratedMenuItem(menu, "Save file", 0, "saveIcon"));
		linkCommand(Commands.Do.SaveHistory, new AcceleratedMenuItem(menu, "Save history", 0, "saveHistoryIcon"));
 
 		new MenuItem(menu, SWT.SEPARATOR);
 
 		linkCommand(Commands.Do.CopyInputToOutput, new AcceleratedMenuItem(menu, "Copy input to output", 0, "copyToOutputIcon", SWT.CHECK));
 		linkCommand(Commands.Do.WrapText, new AcceleratedMenuItem(menu, "Wrap text", 0, "wrapIcon", SWT.CHECK));
 		*/
	}

	static long gridNumber = 0;
	
	static void createDataMenu(Menu bar) {
		MenuItem dataItem = new MenuItem(bar, SWT.CASCADE);
		dataItem.setText("Data");
		
		Menu menu = new Menu(dataItem);
		dataItem.setMenu(menu);

		new AcceleratedMenuItem(menu, "New Grid...\tCtrl-G", SWT.MOD1 | 'G', "newgrid", event -> {
			launchNewGrid();
		});
		
		new AcceleratedMenuItem(menu, "Link...\tCtrl-L", SWT.MOD1 | 'L', "link", event -> {
			
		});
		
		new AcceleratedMenuItem(menu, "Import...\tCtrl-I", SWT.MOD1 | 'I', "import", event -> {
			
		});
	}

	private static void launchNewGrid() {
		Shell newShell = createShell();
		newShell.setText("New Grid " + ++gridNumber);
		newShell.setLayout(new FillLayout());
		var gridData = new DataTemporary();
		gridData.setColumnName(0, "Col1");
		gridData.setColumnName(1, "Col2");
		gridData.setColumnName(2, "Col3");
		gridData.appendRow();
		gridData.appendRow();
		gridData.appendRow();
		new Sheet(newShell, gridData);
		newShell.open();
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
		new AcceleratedMenuItem(menu, "Submit Feedback", 0, "idea", e -> SuggestionboxDialog.launch(shell));
		new AcceleratedMenuItem(menu, "Bug Report", 0, "bug_menu", e -> BugReportDialog.launch(shell));
		new AcceleratedMenuItem(menu, "Check for Updates", 0, "upgrade_menu", e -> UpdatesCheckDialog.launch(shell));
		
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
	
	private static Shell createShell() {
		final Shell shell = new Shell(SWT.SHELL_TRIM);
		createMenuBar(shell);
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
		ClassLoader loader = Main.class.getClassLoader();
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
		while (!tasks.isEmpty())
			tasks.remove().run();
	}
	
	private static void launch(String[] args) {
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
						int percent = Loading.getPercentageOfExpectedMessages();
						int drawExtent = Math.min(barWidth * percent / 100, barWidth);
						gc.fillRect(progressBarRect.x, progressBarRect.y, drawExtent, barHeight);
						splash.update();					
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
						}
					}							
				})).start();
			}
		}
		
		shell = createShell();
		shell.setImage(IconLoader.loadIcon("RelangIcon"));
		shell.setImages(loadIcons(display));
		shell.setText(Version.getAppID());
		shell.addListener(SWT.Close, e -> {
			LogWin.remove();
			shell.dispose();
		});
		shell.addDisposeListener(e -> quit());
		shell.layout();

		LogWin.install(shell);
		
		Loading.start();
		
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
		
		// Thunderbirds are go.
		shell.open();
		launchNewGrid();
		
		while (display != null && !display.isDisposed()) {
			try {
				if (display != null && !display.readAndDispatch()) {
					doWaitingTasks();
					display.sleep();
				}
			} catch (Throwable t) {
				System.out.println(Version.getAppName() + ": Exception: " + t);
				t.printStackTrace();
				CrashDialog.launch(t, shell);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Throwable t) {
			t.printStackTrace();
			MessageDialog.openError(null, "Launch Failure", "Check the system log for details about:\n\n" + t.toString());
		} finally {
			System.exit(0);
		}
	}
	
}