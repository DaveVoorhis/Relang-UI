package org.reldb.relang.datasheet;

import java.util.Vector;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.commands.CommandActivator;
import org.reldb.relang.commands.Commands;
import org.reldb.relang.commands.IconMenuItem;
import org.reldb.relang.data.CatalogEntry;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.reldb.relang.data.bdbje.BDBJEData;
import org.reldb.relang.datasheet.tabs.GridTab;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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
	
	private CTabItem itemSelectedByMenu;
	private int itemSelectedByMenuIndex;
	
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
			if (selection != null) {
				String name = selection.getText();
				CTabItem tab = getTab(name);
				if (tab != null) {
					getTabFolder().setSelection(tab);
					fireContentTabSelectionChange();
				}
			}
		});
		
		catalogTree.addListener(SWT.KeyUp, evt -> {
			if (evt.character == 13) {
				TreeItem items[] = catalogTree.getSelection();
				for (TreeItem item: items)
					item.setExpanded(!item.getExpanded());
				TreeItem selection = getTreeSelection();
				if (selection != null) {
					tabFolder.setSelection(new GridTab(this, selection.getText(), false, SWT.CLOSE));
					fireContentTabSelectionChange();
				}
			}
		});
		
		catalogTree.addListener(SWT.MouseDoubleClick, evt -> {
			String osName = System.getProperty("os.name").toLowerCase();
			if (!osName.startsWith("win")) {		// don't do this under Windows!
				TreeItem items[] = catalogTree.getSelection();
				for (TreeItem item: items)
					item.setExpanded(!item.getExpanded());
			}
			TreeItem selection = getTreeSelection();
			if (selection != null) {
				tabFolder.setSelection(new GridTab(this, selection.getText(), false, SWT.CLOSE));
				fireContentTabSelectionChange();
			}
		});
	}

	public Datasheet(Shell newShell, BDBJEBase base) {
		this(newShell, SWT.NONE);
		this.base = base;
		updateCatalogTree();
	}
	
	public void updateCatalogTree() {
		catalogTree.removeAll();
		var categoryData = new TreeItem(catalogTree, SWT.NONE);
		categoryData.setText("Data");
		try (BDBJEData<String, CatalogEntry> catalog = (BDBJEData<String, CatalogEntry>)base.open(BDBJEBase.catalogName)) {
			// TODO - this should be the first to be replaced by a transactional tuple/row iterator mechanism
			catalog.iterator().forEachRemaining(entry -> {
				var item = new TreeItem(categoryData, SWT.NONE);
				item.setText(entry.name);
			});
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
			tabFolder.setSelection(itemSelectedByMenuIndex);
			for (CTabItem close: closers)
				close.dispose();
		}
	}

	private void closeLeftTabs() {
		if (itemSelectedByMenuIndex > 0) {
			var closers = new Vector<CTabItem>();
			for (int i=0; i<itemSelectedByMenuIndex; i++)
				closers.add(tabFolder.getItem(i));
			tabFolder.setSelection(itemSelectedByMenuIndex);
			for (CTabItem close: closers)
				close.dispose();
		}
	}

	private void closeOtherTabs() {
		tabFolder.setSelection(itemSelectedByMenuIndex);
		for (CTabItem tab: tabFolder.getItems())
			if (tab != itemSelectedByMenu)
				tab.dispose();
	}

	private void closeCurrentTab() {
		if (itemSelectedByMenu != null)
			itemSelectedByMenu.dispose();
		fireContentTabSelectionChange();
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
			var newName = base.getNewName();
			tabFolder.setSelection(new GridTab(this, newName, true, SWT.CLOSE));			
			fireContentTabSelectionChange();
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
