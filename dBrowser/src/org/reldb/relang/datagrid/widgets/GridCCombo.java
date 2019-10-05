package org.reldb.relang.datagrid.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.datagrid.CellComposite;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetInterface;
import org.reldb.relang.platform.TraversalHandler;

public class GridCCombo extends CellComposite {
	
	private CCombo combo;
	private Text text;
	private StackLayout layout;
	private Datagrid grid;
	
	private static GridCCombo lastFocused = null;

	private void updateLastFocused() {
		if (lastFocused != null && lastFocused != this)
			lastFocused.showText();
		lastFocused = this;		
	}
	
	private void showText() {
		updateLastFocused();
		layout.topControl = text;
		layout();
		text.setFocus();
	}
	
	private void showCombo() {
		grid.focusOnCell(getRow(), getColumn());
		updateLastFocused();
		layout.topControl = combo;
		layout();
		combo.setFocus();
	}
	
	public GridCCombo(Datagrid grid, int style, int rowNumber, int columnNumber) {
		super(grid.getGrid(), SWT.NONE, rowNumber, columnNumber);
		
		this.grid = grid;
		
		layout = new StackLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		setLayout(layout);

		setBackground(grid.getGrid().getBackground());
		
		combo = new CCombo(this, style);
		text = new Text(this, SWT.NONE);

		showText();
		
		combo.addListener(SWT.Selection, evt -> {
			text.setText(combo.getText());
			showText();
		});
		combo.addListener(SWT.KeyDown, evt -> {
			if (evt.keyCode == 27) {
				showText();
				evt.doit = false;
			}
		});
		combo.addListener(SWT.Traverse, evt -> {
			text.setText(combo.getText());
			showText();				
		});
		combo.addListener(SWT.Modify, evt -> getNotifier().changed(this, combo.getText(), GridWidgetInterface.SpecialInstructions.NONE));
		
		text.addListener(SWT.MouseDown, evt -> {
			showCombo();
		});
		text.addListener(SWT.KeyDown, evt -> {
			if (evt.keyCode == 13)
				showCombo();
			evt.doit = false;
		});
		text.addListener(SWT.Traverse, evt -> {
			// redefine standard key traversal
			switch (TraversalHandler.getTraversal(evt.detail)) {
			case Next:
				grid.traverseNext();
				evt.doit = false;
				break;
			case Down:
				grid.traverseDown();
				evt.doit = false;
				break;
			case Previous:
				grid.traversePrevious();
				evt.doit = false;
				break;
			case Up:
				grid.traverseUp();
				evt.doit = false;
				break;
			case None:
				if (evt.detail == SWT.TRAVERSE_RETURN) {
					if (layout.topControl == combo)
						grid.traverseDown();
					evt.doit = false;
				}
				break;			
			}
		});
	}
	
	public void setText(String txt) {
		combo.setText(txt);
		text.setText(txt);
	}
	
	public void add(String item) {
		combo.add(item);
	}

	public void add(String item, int index) {
		combo.add(item, index);
	}
	
	public boolean setFocus() {
		return text.setFocus();
	}
	
	public CCombo getCCombo() {
		return combo;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public boolean focus() {
		showText();
		return true;
	}
}
