package org.reldb.relang.datasheet;

import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.commands.CommandActivator;
import org.reldb.relang.commands.Commands;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;

public class Datasheet extends Composite {
	private BDBJEBase base = null;
	
	private Composite toolbarPanel;
	private ToolBar datasheetToolbar;
	private ToolBar tabToolbar;
	private CTabFolder tabFolder;
	private CTabItem lastSelection;
	private Tree catalogTree;
	
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
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addListener(SWT.Selection, e -> fireContentTabSelectionChange());
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
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
		
		newShell.addListener(SWT.Close, evt -> disableToolbar());		
		newShell.addListener(SWT.Activate, evt -> enableToolbar());
		newShell.addListener(SWT.Deactivate, evt -> disableToolbar());
		enableToolbar();
	}
	
	public Datasheet(Shell newShell, BDBJEBase base) {
		this(newShell, SWT.NONE);
		this.base = base;
		
		updateCatalogTree();
	}
	
	public void updateCatalogTree() {
		catalogTree.clearAll(true);
		assert(base != null);
		assert(BDBJEBase.catalogName != null);
		try (var catalog = base.open(BDBJEBase.catalogName)) {
			// TODO - this should be the first to be replaced by a transactional tuple/row iterator mechanism
			long rowCount = catalog.getRowCount();
			for (long row = 0; row < rowCount; row++) {
				var item = new TreeItem(catalogTree, SWT.NONE);
				item.setText(catalog.getValue(0, row).toString());
			}
		}
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}

	protected void fireContentTabSelectionChange() {
		var selection = tabFolder.getSelection();
		if (selection != lastSelection)
			lastSelection = selection;
		buildTabToolbar();
	}
	
	private void buildDatasheetToolbar() {
		Commands.addCommandActivator(new CommandActivator(Commands.Do.NewGrid, datasheetToolbar, "newgrid", SWT.NONE, "New grid", e -> {
			
			Tab tbtmNewItem = new Tab(this, SWT.CLOSE);
			tbtmNewItem.setText("Tab" + tabFolder.getItemCount());
			tbtmNewItem.addListener(SWT.Dispose, evt -> fireContentTabSelectionChange());
			tabFolder.setSelection(tbtmNewItem);
			tabFolder.showSelection();
			fireContentTabSelectionChange();

			updateCatalogTree();
			
			var blah = new Button(tabFolder, SWT.BORDER);
			blah.setText("This is some sample content for " + tbtmNewItem.getText());
			
			tbtmNewItem.setControl(blah);
		}));
		
		Commands.addCommandActivator(new CommandActivator(Commands.Do.Link, datasheetToolbar, "link", SWT.NONE, "Link...", e -> {
			updateCatalogTree();
		}));
		
		Commands.addCommandActivator(new CommandActivator(Commands.Do.Import, datasheetToolbar, "import", SWT.NONE, "Import...", e -> {
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
		if (lastSelection != null && lastSelection instanceof Tab)
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
	
	private void disableToolbar() {
		setToolbarState(false);
	}

	private void enableToolbar() {
		setToolbarState(true);
	}
	
}
