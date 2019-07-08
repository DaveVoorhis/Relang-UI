package org.reldb.relang.grid.nebula;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class GridCheckButton extends Composite {

	private Button button;
	private Text text;
	
	public void checkSubclass() {}
	
	public GridCheckButton(Grid grid, int style) {
		super(grid, style | SWT.TRANSPARENT);
		
		var layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		setLayout(layout);

		setBackground(grid.getBackground());
		
		button = new Button(this, SWT.CHECK);
		text = new Text(this, SWT.NONE);
		
		text.addListener(SWT.KeyDown, evt -> {
			evt.doit = false;
			if (evt.character == ' ')
				button.setSelection(!button.getSelection());
		});
		text.addListener(SWT.Traverse, evt -> {
			System.out.println("GridCheckButton traverse invoked.");
			grid.traverse(evt.detail);
			evt.doit = false;
		});
	}

	public void setChecked(boolean checked) {
		button.setSelection(checked);
	}
	
	public boolean setFocus() {
		return text.setFocus();
	}
		
}
