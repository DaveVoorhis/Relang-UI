package org.reldb.relang.grid.nebula;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class GridCheckbutton extends CellComposite {

	private Button button;
	private Text text;
	
	public GridCheckbutton(Datagrid grid, int style, int rowNumber, int columnNumber) {
		super(grid.getGrid(), style, rowNumber, columnNumber);
		
		var layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		setLayout(layout);

		setBackground(grid.getGrid().getBackground());
		
		button = new Button(this, SWT.CHECK);
		text = new Text(this, SWT.NONE);
		
		addListener(SWT.MouseDown, evt -> focus());
		
		text.addListener(SWT.KeyDown, evt -> {
			if (evt.character == ' ')
				button.setSelection(!button.getSelection());
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

	public void setChecked(boolean checked) {
		button.setSelection(checked);
	}
	
	public boolean setFocus() {
		return text.setFocus();
	}
	
	public Button getButton() {
		return button;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public boolean focus() {
		return setFocus();
	}
}
