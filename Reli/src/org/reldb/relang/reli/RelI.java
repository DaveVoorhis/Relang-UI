package org.reldb.relang.reli;

import java.awt.SplashScreen;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*
 * Based on snippet from http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relang.reli.version.Version;
import org.reldb.swt.os_specific.OSSpecific;

public class RelI {

	static boolean createdScreenBar = false;
	
	static void createFileMenu(Menu bar) {
		
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);			
		fileItem.setText("File");
		
		Menu menu = new Menu(fileItem);
		fileItem.setMenu(menu);

		MenuItem newDatasheet = new MenuItem(menu, SWT.PUSH);
		newDatasheet.setText("New");
		newDatasheet.setImage(IconLoader.loadIcon("add-new-document"));
		newDatasheet.setAccelerator(SWT.MOD1 | 'N');
		newDatasheet.addListener(SWT.Selection, event -> {
			Shell s1 = createShell();
			s1.open();
		});
		
		MenuItem openDatasheet = new MenuItem(menu, SWT.PUSH);
		openDatasheet.setText("Open Datasheet...");
		openDatasheet.setImage(IconLoader.loadIcon("open-folder-outline"));
		openDatasheet.setAccelerator(SWT.MOD1 | 'O');
		openDatasheet.addListener(SWT.Selection, event -> {
			
		});
		
		if (!SWT.getPlatform().equals("cocoa")) {
			MenuItem exit = new MenuItem(menu, SWT.PUSH);
			exit.setText("Exit");
			exit.addListener(SWT.Selection, event -> {
				Display d = Display.getCurrent();
				Shell[] shells = d.getShells();
				for (Shell shell : shells) {
					shell.close();
				}
			});
		}
		
	}
	
	static void createEditMenu(Menu bar) {		
		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("Edit");
	}

	static void createDataMenu(Menu bar) {
		
		MenuItem dataItem = new MenuItem(bar, SWT.CASCADE);
		dataItem.setText("Data");
		
		Menu menu = new Menu(dataItem);
		dataItem.setMenu(menu);

		MenuItem newGrid = new MenuItem(menu, SWT.PUSH);
		newGrid.setText("New Grid...");
		newGrid.setAccelerator(SWT.MOD1 | 'G');
		newGrid.addListener(SWT.Selection, event -> {
			
		});
		
		MenuItem loadFile = new MenuItem(menu, SWT.PUSH);
		loadFile.setText("Load...");
		loadFile.setAccelerator(SWT.MOD1 | 'L');
		loadFile.addListener(SWT.Selection, event -> {
			
		});
		
		MenuItem importFile = new MenuItem(menu, SWT.PUSH);
		importFile.setText("Import...");
		importFile.setAccelerator(SWT.MOD1 | 'I');
		importFile.addListener(SWT.Selection, event -> {
			
		});
	}
	
	static void createMenuBar(Shell s) {
		Menu bar = Display.getCurrent().getMenuBar();
		boolean hasAppMenuBar = (bar != null);
		
		if (bar == null)
			bar = new Menu(s, SWT.BAR);

		// Populate the menu bar once if this is a screen menu bar.
		// Otherwise, we need to make a new menu bar for each shell.
		if (!createdScreenBar || !hasAppMenuBar) {
			
			createFileMenu(bar);
			createEditMenu(bar);
			createDataMenu(bar);
			
			if (!hasAppMenuBar) 
				s.setMenuBar(bar);
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
		if (!SWT.getPlatform().equals("cocoa")) {
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

		OSSpecific.launch(Version.getAppName());
		
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