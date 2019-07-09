package org.reldb.relang.grid.nebula;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class GridCCombo extends CellComposite {

	private CCombo combo;
	private Text text;
	
	public void checkSubclass() {}
	
	public GridCCombo(Datagrid grid, int style) {
		super(grid, SWT.NONE);
		
		var layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		setLayout(layout);

		setBackground(grid.getBackground());
		
		combo = new CCombo(this, style);
		text = new Text(this, SWT.NONE);

		combo.setEnabled(false);
		
		combo.addListener(SWT.FocusOut, evt -> combo.setEnabled(false));
		
		text.addListener(SWT.KeyDown, evt -> {
			if (evt.keyCode == 13 && !combo.isEnabled()) {
				combo.setEnabled(true);
				combo.setFocus();
				combo.addListener(SWT.KeyDown, evtInner -> {
					if (evtInner.keyCode == 27) {
						text.setFocus();
						combo.setEnabled(false);
					}
				});
			}
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
				grid.traverseDown();
				evt.doit = false;
				break;
			}
		});
	}
	
	public void setText(String text) {
		combo.setText(text);
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
}
