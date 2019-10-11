package org.reldb.relang.datasheet;

import java.util.Vector;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.relang.commands.CommandActivator;
import org.reldb.relang.commands.Commands;
import org.reldb.relang.datasheet.dialogs.DialogRenameData;
import org.reldb.relang.datasheet.tabs.GridTab;
import org.reldb.relang.dengine.data.CatalogEntry;
import org.reldb.relang.dengine.data.Data;
import org.reldb.relang.dengine.data.bdbje.BDBJEBase;
import org.reldb.relang.dengine.data.bdbje.BDBJEData;
import org.reldb.relang.platform.IconMenuItem;
import org.reldb.relang.platform.MessageDialog;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;

/** Present a collection of data sources and tools for manipulating them. */
public class Datasheet extends Composite {
	private BDBJEBase base = null;
	
	private Composite toolbarPanel;
	private ToolBar datasheetToolbar;
	private ToolBar tabToolbar;
	private CTabFolder tabFolder;
	private CTabItem lastSelection;
	private Tree catalogTree;
	private TreeItem catalogTreeCategoryData;
	
	private CTabItem itemSelectedByMenu;
	private int itemSelectedByMenuIndex;
	
	private MenuItem renameItem;
	private MenuItem dropItem;
	
	/**
	 * @wbp.parser.constructor
	 */
	public Datasheet(Shell newShell, int style) {
		super(newShell, style);
		setLayout(new FormLayout());
		
		toolbarPanel = new Composite(this, SWT.NONE);

		var fd_toolbarPanel = new FormData();
		fd_toolbarPanel.top = new FormAttachment(0, 0);
		fd_toolbarPanel.left = new FormAttachment(0, 0);
		fd_toolbarPanel.right = new FormAttachment(100, 0);
		toolbarPanel.setLayoutData(fd_toolbarPanel);
		
		var layout = new GridLayout(2, false);
		toolbarPanel.setLayout(layout);
		
		datasheetToolbar = new ToolBar(toolbarPanel, SWT.NONE);

		buildDatasheetToolbar();
		buildTabToolbar();
		
		var sashForm = new SashForm(this, SWT.NONE);
		
		var fd_sashForm = new FormData();
		fd_sashForm.top = new FormAttachment(toolbarPanel);
		fd_sashForm.bottom = new FormAttachment(100);
		fd_sashForm.left = new FormAttachment(0, 0);
		fd_sashForm.right = new FormAttachment(100, 0);
		sashForm.setLayoutData(fd_sashForm);
	
		var treeFolder = new CTabFolder(sashForm, SWT.BORDER);
		treeFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		var tbtmCatalog = new CTabItem(treeFolder, SWT.NONE);
		tbtmCatalog.setText("Catalog");
		
		catalogTree = new Tree(treeFolder, SWT.BORDER);
		tbtmCatalog.setControl(catalogTree);
		treeFolder.setSelection(tbtmCatalog);
		
		catalogTreeCategoryData = new TreeItem(catalogTree, SWT.NONE);
		catalogTreeCategoryData.setText("Data");
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addListener(SWT.Selection, e -> fireContentTabSelectionChange());
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent evt) {
				fireContentTabSelectionChange((CTabItem)evt.item);
			}
			@Override
			public void maximize(CTabFolderEvent evt) {
				sashForm.setMaximizedControl(tabFolder);
				tabFolder.setMaximized(true);
			}
			@Override
			public void restore(CTabFolderEvent evt) {
				sashForm.setMaximizedControl(null);
				tabFolder.setMaximized(false);
			}
		});
		
		sashForm.setWeights(new int[] {1, 3});
		
		newShell.layout();
		
		newShell.addListener(SWT.Close, evt -> closeAll());		
		newShell.addListener(SWT.Activate, evt -> enableToolbar());
		newShell.addListener(SWT.Deactivate, evt -> disableToolbar());

		buildTabMenu();
		
		enableToolbar();
		
		catalogTree.addListener(SWT.Selection, evt -> {
			TreeItem selection = getTreeSelection();
			if (selection != null)
				selectTab(selection.getText());
		});
		
		catalogTree.addListener(SWT.KeyUp, evt -> {
			if (evt.character == 13) {
				TreeItem items[] = catalogTree.getSelection();
				for (TreeItem item: items)
					item.setExpanded(!item.getExpanded());
				openTreeSelection();
			}
		});
		
		catalogTree.addListener(SWT.MouseDoubleClick, evt -> {
			String osName = System.getProperty("os.name").toLowerCase();
			if (!osName.startsWith("win")) {		// don't do this under Windows!
				TreeItem items[] = catalogTree.getSelection();
				for (TreeItem item: items)
					item.setExpanded(!item.getExpanded());
			}
			openTreeSelection();
		});
		
		var menu = new Menu(catalogTree);
		catalogTree.setMenu(menu);		
		catalogTree.addListener(SWT.MenuDetect, evt -> {
			var selection = getTreeSelection();
			evt.doit = (selection != null && selection.getItems().length == 0);
			dropItem.setEnabled(selection != null && base.isRemovable(selection.getText()));
			renameItem.setEnabled(selection != null && base.isRenameable(selection.getText()));
		});
		
		dropItem = new MenuItem(menu, SWT.PUSH);
		dropItem.setText("Drop");
		dropItem.addListener(SWT.Selection, evt -> {
			var selection = getTreeSelection();
			if (selection == null)
				return;
			String name = selection.getText();
			if (MessageDialog.openQuestion(getShell(), "Drop?", "Do you wish to permanently drop data source '" + name + "'?")) {
				base.remove(name);
				var tab = getTab(name);
				if (tab != null)
					tab.dispose();
				updateCatalogTree();
			}
		});
		
		renameItem = new MenuItem(menu, SWT.PUSH);
		renameItem.setText("Rename");
		renameItem.addListener(SWT.Selection, evt -> {
			var selection = getTreeSelection();
			if (selection == null)
				return;
			final String name = selection.getText();
			var renameDialog = new DialogRenameData(getShell(), name) {
				public void closed(String newName) {
					System.out.println("Datasheet: rename to " + newName);
					if (newName != null && newName.length() > 0 && !newName.equals(name)) {
						try {
							base.rename(name, newName);
						} catch (Throwable e) {
							MessageDialog.openError(getShell(), "Unable to Rename", "Unable to rename: " + e.getMessage());
							return;
						}
						var tab = getTab(name);
						if (tab != null)
							tab.setText(newName);
						updateCatalogTree();
						setTreeSelection(newName);
					}					
				}
			};
			renameDialog.open();
		});
	}

	public Datasheet(Shell newShell, BDBJEBase base) {
		this(newShell, SWT.NONE);
		this.base = base;
		updateCatalogTree();
	}

	private void openTab(String name, boolean create) {
		if (!create && selectTab(name))
			return;
		var base = getBase();
		Data<?, ?> data = null;
		try {
			data = base.open(name, create);
		} catch (Throwable e) {
			MessageDialog.openError(getShell(), "Problem Opening Tab", "Unable to open tab due to: " + e.getMessage());
			return;
		}
		var tab = new GridTab(tabFolder, data, SWT.CLOSE);
		selectTab(tab);
	}
	
	private void openTab(TreeItem selection) {
		if (selection != null)
			openTab(selection.getText(), false);
	}
	
	private void openTreeSelection() {
		var selection = getTreeSelection();
		if (selection != null) {
			var items = selection.getItems();
			if (items == null || items.length == 0)
				openTab(selection);
		}
	}

	private TreeItem searchTree(TreeItem[] items, String text) {
		for (var item: items) {
			TreeItem found = searchTree(item, text);
			if (found != null)
				return found;
		}
		return null;		
	}
	
	private TreeItem searchTree(TreeItem treeItem, String text) {
		if (treeItem.getText().equals(text))
			return treeItem;
		return searchTree(treeItem.getItems(), text);
	}
	
	private TreeItem searchTree(String text) {
		return searchTree(catalogTree.getItems(), text);
	}

	private void setTreeSelection(String name) {
		var foundItem = searchTree(name);
		if (foundItem != null)
			catalogTree.setSelection(foundItem);
	}
	
	@SuppressWarnings("unchecked")
	public void updateCatalogTree() {
		boolean catalogIsOpen = catalogTreeCategoryData.getExpanded();
		var topItem = catalogTree.getTopItem();
		var topItemText = topItem.getText();
		var selectedItem = getTreeSelection();
		String selectedItemText = (selectedItem != null) ? selectedItem.getText() : null;
		catalogTreeCategoryData.removeAll();
		var catalogData = (BDBJEData<String, CatalogEntry>)base.open(BDBJEBase.catalogName);
		catalogData.access(catalog -> catalog.forEach((key, value) -> {
			var item = new TreeItem(catalogTreeCategoryData, SWT.NONE);
			item.setText(key.toString());
		}));
		var newTopItem = searchTree(topItemText);
		catalogTree.setTopItem(newTopItem);
		catalogTreeCategoryData.setExpanded(catalogIsOpen);
		if (selectedItemText != null) {
			var newSelectedItem = searchTree(selectedItemText);
			if (newSelectedItem != null)
				catalogTree.setSelection(newSelectedItem);
		}
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}

	public BDBJEBase getBase() {
		return base;
	}

	protected void fireContentTabSelectionChange(CTabItem closing) {
		var selection = tabFolder.getSelection();
		if (selection != lastSelection)
			lastSelection = selection;
		if (closing == selection)
			lastSelection = null;
		buildTabToolbar();
	}
	
	protected void fireContentTabSelectionChange() {
		fireContentTabSelectionChange(null);
	}
	
	private void buildTabMenu() {
		Menu tabControlMenu = new Menu(tabFolder);
		tabFolder.setMenu(tabControlMenu);
		
		IconMenuItem closer = new IconMenuItem(tabControlMenu, "Close", null, SWT.NONE, e -> closeCurrentTab());
		IconMenuItem closeOthers = new IconMenuItem(tabControlMenu, "Close others", null, SWT.NONE, e -> closeOtherTabs());
		IconMenuItem closeLeft = new IconMenuItem(tabControlMenu, "Close left tabs", null, SWT.NONE, e -> closeLeftTabs());
		IconMenuItem closeRight = new IconMenuItem(tabControlMenu, "Close right tabs", null, SWT.NONE, e -> closeRightTabs());
		new MenuItem(tabControlMenu, SWT.SEPARATOR);
		IconMenuItem closeAll = new IconMenuItem(tabControlMenu, "Close all", null, SWT.NONE, e -> closeAllTabs());
		
		tabFolder.addListener(SWT.MenuDetect, e -> {
			Point clickPosition = Display.getDefault().map(null, tabFolder, new Point(e.x, e.y));
			if (clickPosition.y > tabFolder.getTabHeight())
				e.doit = false;
			else {
				itemSelectedByMenu = tabFolder.getItem(clickPosition);
				itemSelectedByMenuIndex = getTabIndex(tabFolder, itemSelectedByMenu);
				closer.setEnabled(itemSelectedByMenuIndex >= 0);
				closeOthers.setEnabled(tabFolder.getItemCount() > 1 && itemSelectedByMenuIndex >= 0);
				closeLeft.setEnabled(itemSelectedByMenuIndex > 0);
				closeRight.setEnabled(itemSelectedByMenuIndex >= 0 && itemSelectedByMenuIndex < tabFolder.getItemCount() - 1);
				closeAll.setEnabled(tabFolder.getItemCount() > 0);
			}
		});
	}

	private void closeRightTabs() {
		if (itemSelectedByMenuIndex < tabFolder.getItemCount() - 1) {
			var closers = new Vector<CTabItem>();
			for (int i = itemSelectedByMenuIndex + 1; i<tabFolder.getItemCount(); i++)
				closers.add(tabFolder.getItem(i));
			selectTab(itemSelectedByMenuIndex);
			for (CTabItem close: closers)
				close.dispose();
		}
	}

	private void closeLeftTabs() {
		if (itemSelectedByMenuIndex > 0) {
			var closers = new Vector<CTabItem>();
			for (int i=0; i<itemSelectedByMenuIndex; i++)
				closers.add(tabFolder.getItem(i));
			selectTab(itemSelectedByMenuIndex);
			for (CTabItem close: closers)
				close.dispose();
		}
	}

	private void closeOtherTabs() {
		selectTab(itemSelectedByMenuIndex);
		for (CTabItem tab: tabFolder.getItems())
			if (tab != itemSelectedByMenu)
				tab.dispose();
	}

	private void closeCurrentTab() {
		if (itemSelectedByMenu != null)
			itemSelectedByMenu.dispose();
		fireContentTabSelectionChange();
	}

	private void selectTab(CTabItem tab) {
		tabFolder.setSelection(tab);
		fireContentTabSelectionChange();		
	}
	
	private void selectTab(int tabIndex) {
		tabFolder.setSelection(tabIndex);
		fireContentTabSelectionChange();
	}
	
	private boolean selectTab(String name) {
		CTabItem tab = getTab(name);
		if (tab != null) {
			selectTab(tab);
			return true;
		}
		return false;
	}
	
	private CTabItem getTab(String name) {
		for (CTabItem tab: tabFolder.getItems())
			if (tab.getText().equals(name))
				return tab;
		return null;
	}
	
	private static int getTabIndex(CTabFolder tabFolder, CTabItem item) {
		if (item == null)
			return -1;
		for (int index = 0; index < tabFolder.getItemCount(); index++)
			if (tabFolder.getItem(index) == item)
				return index;
		return -1;
	}
		
	private TreeItem getTreeSelection() {
		TreeItem items[] = catalogTree.getSelection();
		if (items == null || items.length == 0)
			return null;
		return items[0];
	}
	
	private void buildDatasheetToolbar() {
		Commands.addCommandActivator(new CommandActivator(Commands.Do.NewGrid, datasheetToolbar, "newgrid", SWT.NONE, "New grid", e -> {
			openTab(base.getNewName(), true);
			updateCatalogTree();
		}));
		
		Commands.addCommandActivator(new CommandActivator(Commands.Do.Link, datasheetToolbar, "link", SWT.NONE, "Link...", e -> {
			// TODO - link
			updateCatalogTree();
		}));
		
		Commands.addCommandActivator(new CommandActivator(Commands.Do.Import, datasheetToolbar, "import", SWT.NONE, "Import...", e -> {
			// TODO - import
			updateCatalogTree();
		}));		
	}

	private void buildTabToolbar() {
		if (tabToolbar != null)
			tabToolbar.dispose();
		if (toolbarPanel.isDisposed())
			return;
		tabToolbar = new ToolBar(toolbarPanel, SWT.NONE);
		tabToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (lastSelection != null && !lastSelection.isDisposed() && lastSelection instanceof Tab)
			((Tab)lastSelection).populateToolbar(tabToolbar);
		toolbarPanel.layout(true);
	}
	
	private void setToolbarState(ToolBar toolBar, boolean enabled) {
		Stream.of(toolBar.getItems()).forEach(toolItem -> toolItem.setEnabled(enabled));				
	}
	
	private void setToolbarState(boolean enabled) {
		setToolbarState(datasheetToolbar, enabled);		
		if (tabToolbar != null)
			setToolbarState(tabToolbar, enabled);		
	}
	
	private void closeAllTabs() {
		while (tabFolder.getItemCount() > 0)
			tabFolder.getItem(0).dispose();
		fireContentTabSelectionChange();
	}

	private void closeAll() {
		disableToolbar();
		closeAllTabs();
	}
	
	private void disableToolbar() {
		setToolbarState(false);
	}

	private void enableToolbar() {
		setToolbarState(true);
	}
	
}
