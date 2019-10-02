package org.reldb.relang.datagrid.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.datagrid.CellComposite;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetInterface;
import org.reldb.relang.platform.TraversalHandler;

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

		Listener focuser = evt -> {
			grid.focusOnCell(getRow(), getColumn());
			focus();
		};
		
		addListener(SWT.MouseDown, focuser);

		button.addListener(SWT.MouseDown,  focuser);
		button.addListener(SWT.Modify, evt -> getNotifier().changed(this, button.getSelection(), GridWidgetInterface.SpecialInstructions.NONE));
		
		text.addListener(SWT.MouseDown, focuser);
		text.addListener(SWT.KeyDown, evt -> {
			if (evt.character == ' ')
				button.setSelection(!button.getSelection());
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
					grid.traverseDown();
					evt.doit = false;
					break;
				}
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
