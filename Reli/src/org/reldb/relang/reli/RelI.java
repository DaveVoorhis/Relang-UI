package org.reldb.relang.reli;

/*
 * Based on snippet from http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.reldb.relang.reli.macos.CocoaUIEnhancer;

public class RelI {

	static boolean createdScreenBar = false;

	static void createMenuBar(Shell s) {
		Menu bar = Display.getCurrent().getMenuBar();
		boolean hasAppMenuBar = (bar != null);
		
		if (bar == null)
			bar = new Menu(s, SWT.BAR);

		// Populate the menu bar once if this is a screen menu bar.
		// Otherwise, we need to make a new menu bar for each shell.
		if (!createdScreenBar || !hasAppMenuBar) {
			
			MenuItem item = new MenuItem(bar, SWT.CASCADE);			
			item.setText("File");
			Menu menu = new Menu(item);
			item.setMenu(menu);

			MenuItem newWindow = new MenuItem(menu, SWT.PUSH);
			newWindow.setText("New Window");
			newWindow.setAccelerator(SWT.MOD1 | 'N');
			newWindow.addListener(SWT.Selection, event -> {
				Shell s1 = createShell();
				s1.open();
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
			if (!hasAppMenuBar) {
				shell.getMenuBar().dispose();
				Shell[] shells = display.getShells();
				if ((shells.length == 1) && (shells[0] == shell)) {
					if (!display.isDisposed()) 
						display.dispose();
				}
			}
		});

		return shell;
	}
	
	private static String APP_NAME = "RelI";

	public static void main(String[] args) {
		Display.setAppName(APP_NAME);
		final Display display = new Display();
		
		if (SWT.getPlatform().equals("cocoa")) {
			new CocoaUIEnhancer(APP_NAME).earlyStartup();
		}

		Shell shell = createShell();
		shell.open();

		while (!display.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}