package org.reldb.relang.grid.nebula.experiments;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.grid.nebula.Datagrid;
import org.reldb.relang.grid.nebula.GridCCombo;
import org.reldb.relang.grid.nebula.GridCheckbutton;
import org.reldb.relang.grid.nebula.GridWidgetWrapper;

public class NebulaGridExperiments {
	
	public void go() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		var grid = new Datagrid(shell, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);

// to set up virtual retrieval
//		grid.setItemCount(count);
//		grid.addListener(SWT.SetData, evt -> { ... });
		
		grid.getGrid().setLinesVisible(true);
		grid.getGrid().setHeaderVisible(true);
		grid.getGrid().setCellSelectionEnabled(true);
		
		int columnCount = 5;
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			var group = new GridColumnGroup(grid.getGrid(), SWT.NONE);
			group.setText("Group" + columnIndex);
			var column = new GridColumn(group, SWT.NONE);
			column.setFooterText("Column" + columnIndex);
			column.setWidth(150);
			column.setText("Column" + columnIndex);
		}
		
		for (int rowIndex = 0; rowIndex < 20; rowIndex++) {
			var row = new GridItem(grid.getGrid(), SWT.NONE);
			
			// column 0
			int columnIndex = 0;
			var editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			var label = new Text(grid.getGrid(), SWT.NONE);
			label.setText(Integer.toString(rowIndex));
			grid.setupControl(new GridWidgetWrapper(grid, label, rowIndex, columnIndex), rowIndex, columnIndex);
			editor.setEditor(label, row, columnIndex);
			
			// column 1
			columnIndex = 1;
			editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			var selector = new GridCheckbutton(grid, SWT.NONE);
			grid.setupControl(selector, rowIndex, columnIndex);
			editor.setEditor(selector, row, columnIndex);
			
			// column 2
			columnIndex = 2;
			editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			var text = new Text(grid.getGrid(), SWT.NONE);
			text.setText("Cell_Row" + rowIndex + "_Col" + columnIndex);
			grid.setupControl(new GridWidgetWrapper(grid, text, rowIndex, columnIndex), rowIndex, columnIndex);
			editor.setEditor(text, row, columnIndex);
			
			// column 3
			columnIndex = 3;
			editor = new GridEditor(grid.getGrid());
			editor.minimumWidth = 50;
			editor.grabHorizontal = true;
			GridCCombo combo = new GridCCombo(grid, SWT.NONE);
			combo.setText("GridCCombo Widget " + columnIndex);
			for (int i = 0; i < 100; i++)
				combo.add("item " + i);
			grid.setupControl(combo, rowIndex, columnIndex);
			editor.setEditor(combo, row, columnIndex);
			
			// column 4
			columnIndex = 4;
			editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			text = new Text(grid.getGrid(), SWT.NONE);
			text.setText("Row" + rowIndex + "_Col" + columnIndex);
			grid.setupControl(new GridWidgetWrapper(grid, text, rowIndex, columnIndex), rowIndex, columnIndex);
			editor.setEditor(text, row, columnIndex);
		}
		
		grid.focusOnCell(0, 0);

		shell.setSize(800, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();		
	}
	
	public static void main(String[] args) {
		(new NebulaGridExperiments()).go();
	}

}