package org.reldb.relang.datasheet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relang.data.bdbje.BDBJEBase;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.ToolItem;

public class Datasheet extends Composite {

	private BDBJEBase base = null;
	
	/**
	 * @wbp.parser.constructor
	 */
	public Datasheet(Shell newShell, int style) {
		super(newShell, style);
		setLayout(new FormLayout());
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0, 0);
		fd_toolBar.left = new FormAttachment(0, 0);
		fd_toolBar.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(fd_toolBar);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		FormData fd_sashForm = new FormData();
		fd_sashForm.top = new FormAttachment(toolBar, 0);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setText("New Item");
		
		ToolItem tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_1.setText("New Item");
		
		ToolItem tltmNewItem_2 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_2.setText("New Item");
		fd_sashForm.left = new FormAttachment(0, 0);
		fd_sashForm.right = new FormAttachment(100, 0);
		fd_sashForm.bottom = new FormAttachment(100, 0);
		sashForm.setLayoutData(fd_sashForm);
	
		CTabFolder treeFolder = new CTabFolder(sashForm, SWT.BORDER);
		treeFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmCatalog = new CTabItem(treeFolder, SWT.NONE);
		tbtmCatalog.setText("Catalog");
		
		Tree tree = new Tree(treeFolder, SWT.BORDER);
		tbtmCatalog.setControl(tree);
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("New Item");
		sashForm.setWeights(new int[] {1, 3});
		
		newShell.layout();
		
		newShell.addListener(SWT.FocusIn, e -> System.out.println("focus in on Datasheet " + this));		
		newShell.addListener(SWT.FocusOut, e -> System.out.println("focus out on Datasheet " + this));
	}
	
	public Datasheet(Shell newShell, BDBJEBase base) {
		this(newShell, SWT.NONE);
		this.base = base;
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
