package org.reldb.relang.sheet;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.data.Data;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetWrapper;

public class Sheet {

	public Sheet(Composite parent, Data data) {
		
		var grid = new Datagrid(parent, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
		grid.getGrid().setHeaderVisible(true);
		
		int columnCount = data.getColumnCount();
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			var group = new GridColumnGroup(grid.getGrid(), SWT.NONE);
			group.setText(data.getColumnTypeAt(columnIndex).getName());
			var column = new GridColumn(group, SWT.NONE);
			column.setSort(1);
			column.setWidth(150);
			column.setText(data.getColumnNameAt(columnIndex));
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
}
