package org.reldb.relang.sheet;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.data.Data;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetWrapper;
import org.reldb.relang.utilities.DialogBase;

public class Sheet {

	public Sheet(Composite parent, Data data) {
		
		var grid = new Datagrid(parent, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
		grid.getGrid().setHeaderVisible(true);
//		grid.getGrid().get
		
		int columnCount = data.getColumnCount();
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			var column = new GridColumn(grid.getGrid(), SWT.NONE);
			column.setHeaderTooltip(data.getColumnTypeAt(columnIndex).getName());
			column.setWidth(150);
			column.setText(data.getColumnNameAt(columnIndex));
			column.addListener(SWT.Selection, evt -> showColumnDialog(evt));
			column.setWordWrap(true);
			column.setMoveable(true);
		}
		
		grid.getGrid().setLinesVisible(true);
		grid.getGrid().setCellSelectionEnabled(true);
		grid.getGrid().setRowsResizeable(true);
		grid.getGrid().setRowHeaderVisible(true);
		grid.getGrid().setColumnScrolling(true);
		grid.getGrid().setRowHeaderVisible(true);

		grid.getGrid().setItemCount(data.getRowCount());
		
		grid.getGrid().addListener(SWT.SetData, setDataEvt -> {
			GridItem row = (GridItem)setDataEvt.item;
			int rowIndex = row.getRowIndex();

			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				var editor = new GridEditor(grid.getGrid());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				var text = new Text(grid.getGrid(), SWT.NONE);
				text.setText(Integer.toString(rowIndex));
				grid.setupControl(new GridWidgetWrapper(grid, text, rowIndex, columnIndex));
				editor.setEditor(text, row, columnIndex);				
			}
		});
		
		grid.focusOnCell(0, 0);		
	}

	private void showColumnDialog(Event evt) {
		var column = (GridColumn)evt.item;
		var parentShell = column.getParent().getShell();
		var dialog = new DialogBase(parentShell, SWT.NONE) {
			@Override
			public void create(Shell shell) {
				shell.setText("Properties for " + column.getText());
			}
		};
		dialog.open();
	}
}
