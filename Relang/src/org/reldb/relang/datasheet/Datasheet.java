package org.reldb.relang.datasheet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.reldb.relang.utilities.EventHandler;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.ToolItem;

public class Datasheet extends Composite {
	private BDBJEBase base = null;
	
	private ToolBar toolBar;
	private CTabFolder tabFolder;
	private CTabItem lastSelection = null;
	
	public static class TabSelectionChangeEvent {
		public final Datasheet sheet;
		public final CTabItem tabItem;

		public TabSelectionChangeEvent(Datasheet sheet, CTabItem tabItem) {
			this.sheet = sheet;
			this.tabItem = tabItem;
		}
	}

	public final EventHandler<TabSelectionChangeEvent> tabSelectionChangeEventHandler = new EventHandler<TabSelectionChangeEvent>();
	
	protected void fireContentTabSelectionChange() {
		var selection = tabFolder.getSelection();
		if (selection != lastSelection) {
			tabSelectionChangeEventHandler.fire(new TabSelectionChangeEvent(this, selection));
			lastSelection = selection;
		}
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public Datasheet(Shell newShell, int style) {
		super(newShell, style);
		setLayout(new FormLayout());
		
		toolBar = new ToolBar(this, SWT.FLAT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0, 0);
		fd_toolBar.left = new FormAttachment(0, 0);
		fd_toolBar.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(fd_toolBar);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		FormData fd_sashForm = new FormData();
		fd_sashForm.top = new FormAttachment(toolBar, 0);		
		fd_sashForm.left = new FormAttachment(0, 0);
		fd_sashForm.right = new FormAttachment(100, 0);
		fd_sashForm.bottom = new FormAttachment(100, 0);
		sashForm.setLayoutData(fd_sashForm);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setText("New Tab");
		tltmNewItem.addListener(SWT.Selection, e -> {
			CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.CLOSE);
			tbtmNewItem.setText("Tab" + tabFolder.getItemCount());
			tbtmNewItem.addListener(SWT.Dispose, evt -> fireContentTabSelectionChange());
			tabFolder.setSelection(tbtmNewItem);
			tabFolder.showSelection();
			fireContentTabSelectionChange();
			
			var blah = new Button(tabFolder, SWT.BORDER);
			blah.setText("This is some sample content for " + tbtmNewItem.getText());
			
			tbtmNewItem.setControl(blah);
		});
		
		ToolItem tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_1.setText("Demo2");
		
		ToolItem tltmNewItem_2 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_2.setText("Demo3");
	
		CTabFolder treeFolder = new CTabFolder(sashForm, SWT.BORDER);
		treeFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmCatalog = new CTabItem(treeFolder, SWT.NONE);
		tbtmCatalog.setText("Catalog");
		
		Tree tree = new Tree(treeFolder, SWT.BORDER);
		tbtmCatalog.setControl(tree);
		
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
	}
	
	public Datasheet(Shell newShell, BDBJEBase base) {
		this(newShell, SWT.NONE);
		this.base = base;
		
		tabSelectionChangeEventHandler.addListener(evt -> {
			System.out.println("Datasheet tab changed to " + (evt.tabItem) != null ? evt.tabItem.getText() : "null");
		});
	}

	public void disableToolbar() {
		// TODO Auto-generated method stub
		
	}

	public Listener enableToolbar() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	Data gridData;
	if (base.exists(gridName)) {
		gridData = base.open(gridName);
	} else {
		gridData = base.create(gridName);
		gridData.setColumnName(0, "A");
		gridData.setColumnName(1, "B");
		gridData.setColumnName(2, "C");
		gridData.appendRow();
		gridData.appendRow();
		gridData.appendRow();
	}
	new SheetPanel(newShell, gridData);
	*/
	
	
}
