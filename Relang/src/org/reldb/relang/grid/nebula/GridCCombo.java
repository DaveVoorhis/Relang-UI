package org.reldb.relang.grid.nebula;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class GridCCombo extends CellComposite {
	
	private CCombo combo;
	private Text text;
	private StackLayout layout;
	
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
		updateLastFocused();
		layout.topControl = combo;
		layout();
		combo.setFocus();
	}
	
	public GridCCombo(Datagrid grid, int style) {
		super(grid.getGrid(), SWT.NONE);
		
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
		
		text.addListener(SWT.MouseDown, evt -> {
			System.out.println("text MouseDown");
			showCombo();
		});
		text.addListener(SWT.KeyDown, evt -> {
			System.out.println("text KeyDown " + evt.keyCode);
			if (evt.keyCode == 13)
				showCombo();
			evt.doit = false;
		});
		text.addListener(SWT.Traverse, evt -> {
			// redefine standard key traversal
			switch (evt.detail) {
			case SWT.TRAVERSE_TAB_NEXT:
				grid.traverseNext();
				evt.doit = false;
				break;
			case SWT.TRAVERSE_ARROW_NEXT:
				grid.traverseDown();
				evt.doit = false;
				break;
			case SWT.TRAVERSE_TAB_PREVIOUS:
				grid.traversePrevious();
				evt.doit = false;
				break;
			case SWT.TRAVERSE_ARROW_PREVIOUS:
				grid.traverseUp();
				evt.doit = false;
				break;
			case SWT.TRAVERSE_RETURN:
				System.out.print("text TRAVERSE_RETURN ");
				if (layout.topControl != combo) {
					System.out.println("text");
				//	grid.traverseDown();
				} else
					System.out.println("combo");
				evt.doit = false;
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
		
	public Control[] getAllChildren() {
		return new Control[] {text, combo};
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
