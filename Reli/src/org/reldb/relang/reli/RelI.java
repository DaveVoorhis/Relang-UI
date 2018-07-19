package org.reldb.relang.reli;

import java.awt.SplashScreen;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relang.reli.version.Version;
import org.reldb.swt.os_specific.OSSpecific;

public class RelI {

	static boolean createdScreenBar = false;
	
	static boolean isMac() {
		return SWT.getPlatform().equals("cocoa");
	}
	
	static void quit() {
		Display d = Display.getCurrent();
		Shell[] shells = d.getShells();
		for (Shell shell: shells)
			shell.close();
	}
	
	static void about() {
		System.out.println("RelI: about");
	}
	
	static void preferences() {
		System.out.println("RelI: preferences");
	}
	
	static void createFileMenu(Menu bar) {	
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);			
		fileItem.setText("File");
		
		Menu menu = new Menu(fileItem);
		fileItem.setMenu(menu);

		new DecoratedMenuItem(menu, "&New Datasheet\tCtrl-N", SWT.MOD1 | 'N', IconLoader.loadIcon("add-new-document"), event -> {
			Shell newDatasheet = createShell();
			newDatasheet.setText("New Datasheet");
			new Datasheet(newDatasheet);
			newDatasheet.open();
		});
		
		new DecoratedMenuItem(menu, "Open Datasheet...\tCtrl-O", SWT.MOD1 | 'O', IconLoader.loadIcon("open-folder-outline"), event -> {
			
		});

		OSSpecific.addFileMenuItems(menu);
	}
	
	static void createEditMenuItem(String methodName, DecoratedMenuItem menuItem) {
		abstract class EditTimerTask extends TimerTask {
			Control focusControl = null;
			Method menuItemMethod = null;
		}
		Timer editTimer = new Timer();
		EditTimerTask editMenuItemTimer = new EditTimerTask() {
			public void run() {
				Display display = Display.getCurrent();
				if (display == null)
					return;
				focusControl = display.getFocusControl();
				if (focusControl == null)
					return;
				Class<?> focusControlClass = focusControl.getClass();
				try {
					menuItemMethod = (Method)focusControlClass.getMethod(methodName, new Class[] {null});
					menuItem.setEnabled(true);
				} catch (NoSuchMethodException | SecurityException e) {
					menuItem.setEnabled(false);
				}
			}
		};
		menuItem.setEnabled(false);
		menuItem.addListener(SWT.Selection, evt -> {
			try {
				editMenuItemTimer.menuItemMethod.invoke(editMenuItemTimer.focusControl, new Object[] {null});
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		});
		editTimer.schedule(editMenuItemTimer, 250);
	}
	
	static void createEditMenu(Menu bar) {		
		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("Edit");
		
		Menu menu = new Menu(editItem);
		editItem.setMenu(menu);
		
		createEditMenuItem("undo", new DecoratedMenuItem(menu, "Undo\tCtrl-Z", SWT.MOD1 | 'Z', IconLoader.loadIcon("undo")));
		
		int redoAccelerator = SWT.MOD1 | (isMac() ? SWT.SHIFT | 'Z' : 'Y');
		createEditMenuItem("redo", new DecoratedMenuItem(menu, "Redo\tCtrl-Y", redoAccelerator, IconLoader.loadIcon("redo")));
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		createEditMenuItem("cut", new DecoratedMenuItem(menu, "Cut\tCtrl-X", SWT.MOD1 | 'X', IconLoader.loadIcon("cut")));
		createEditMenuItem("copy", new DecoratedMenuItem(menu, "Copy\tCtrl-C", SWT.MOD1 | 'C', IconLoader.loadIcon("copy")));
		createEditMenuItem("paste", new DecoratedMenuItem(menu, "Paste\tCtrl-V", SWT.MOD1 | 'V', IconLoader.loadIcon("paste")));
		createEditMenuItem("delete", new DecoratedMenuItem(menu, "Delete\tDel", SWT.DEL, IconLoader.loadIcon("rubbish-bin")));
		createEditMenuItem("selectAll", new DecoratedMenuItem(menu, "Select All\tCtrl-A", SWT.MOD1 | 'A', IconLoader.loadIcon("select-all")));
	}

	static void createDataMenu(Menu bar) {
		MenuItem dataItem = new MenuItem(bar, SWT.CASCADE);
		dataItem.setText("Data");
		
		Menu menu = new Menu(dataItem);
		dataItem.setMenu(menu);

		new DecoratedMenuItem(menu, "Add Grid...\tCtrl-G", SWT.MOD1 | 'G', IconLoader.loadIcon("newgrid"), event -> {
			Shell newGridShell = createShell();
			newGridShell.setText("New Grid");
			newGridShell.setLayout(new FillLayout());
			
			Grid grid = new Grid(newGridShell);			
			grid.tuples = grid.obtainTuples();
			grid.init();
			grid.refresh();
			
			newGridShell.open();
		});
		
		new DecoratedMenuItem(menu, "Link...\tCtrl-L", SWT.MOD1 | 'L', IconLoader.loadIcon("link"), event -> {
			
		});
		
		new DecoratedMenuItem(menu, "Import...\tCtrl-I", SWT.MOD1 | 'I', IconLoader.loadIcon("import"), event -> {
			
		});
	}
	
	static void createHelpMenu(Menu bar) {
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
			createHelpMenu(bar);
			
			if (!hasAppMenuBar) 
				shell.setMenuBar(bar);
			createdScreenBar = true;
		}
	}
	
	static Shell createShell() {
		final Shell shell = new Shell(SWT.SHELL_TRIM);
		createMenuBar(shell);
		
		shell.addDisposeListener(e -> {
			Display display = Display.getCurrent();
			Menu bar = display.getMenuBar();
			boolean hasAppMenuBar = (bar != null);
			Shell[] shells = display.getShells();
			if (!hasAppMenuBar && shell != null && shell.getMenuBar() != null) {
				shell.getMenuBar().dispose();
				if ((shells.length == 1) && (shells[0] == shell)) {
					if (!display.isDisposed()) 
						display.dispose();
				}
			}
			if (shells.length <= 1)
				System.exit(0);		
		});
		
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
		if (!isMac()) {
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
		ClassLoader loader = RelI.class.getClassLoader();
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
	
	public static void main(String[] args) {
		Display.setAppName(Version.getAppName());
		final Display display = new Display();

		OSSpecific.launch(Version.getAppName(),
			event -> quit(),
			event -> about(),
			event -> preferences()
		);
		
		executeSplashInteractor(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		Shell shell = createShell();
		shell.setImages(loadIcons(display));
		shell.setText(Version.getAppID());
		shell.open();

		while (!display.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		
		ResourceManager.dispose();
	}
}