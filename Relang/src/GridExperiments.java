

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridCCombo;
import org.reldb.relang.datagrid.GridCheckbutton;
import org.reldb.relang.datagrid.GridWidgetWrapper;

public class GridExperiments {
	
	public void go() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		var grid = new Datagrid(shell, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
		grid.getGrid().setHeaderVisible(true);
		
		int columnCount = 5;
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			var group = new GridColumnGroup(grid.getGrid(), SWT.NONE);
			group.setText("Group" + columnIndex);
			var column = new GridColumn(group, SWT.NONE);
			column.setFooterText("Column" + columnIndex);
			column.setWidth(150);
			column.setText("Column" + columnIndex);
		}
		
		grid.getGrid().setLinesVisible(true);
		grid.getGrid().setCellSelectionEnabled(true);
		grid.getGrid().setRowsResizeable(true);
		grid.getGrid().setRowHeaderVisible(true);
		grid.getGrid().setColumnScrolling(true);
		grid.getGrid().setRowHeaderVisible(true);

		grid.getGrid().setItemCount(500);
		
		grid.getGrid().addListener(SWT.SetData, setDataEvt -> {
			GridItem row = (GridItem)setDataEvt.item;
			int rowIndex = row.getRowIndex();
			
			System.out.println("Fill in rowIndex = " + rowIndex);
			
			/*
			
			// column 0
			int columnIndex = 0;
			var editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			var label = new Text(grid.getGrid(), SWT.NONE);
			label.setText(Integer.toString(rowIndex));
			grid.setupControl(new GridWidgetWrapper(grid, label, rowIndex, columnIndex));
			editor.setEditor(label, row, columnIndex);
			
			// column 1
			columnIndex = 1;
			editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			var selector = new GridCheckbutton(grid, SWT.NONE, rowIndex, columnIndex);
			grid.setupControl(selector);
			editor.setEditor(selector, row, columnIndex);
			
			// column 2
			columnIndex = 2;
			editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			var text = new Text(grid.getGrid(), SWT.NONE);
			text.setText("Cell_Row" + rowIndex + "_Col" + columnIndex);
			grid.setupControl(new GridWidgetWrapper(grid, text, rowIndex, columnIndex));
			editor.setEditor(text, row, columnIndex);
	
			// column 3
			columnIndex = 3;
			editor = new GridEditor(grid.getGrid());
			editor.minimumWidth = 50;
			editor.grabHorizontal = true;
			GridCCombo combo = new GridCCombo(grid, SWT.NONE, rowIndex, columnIndex);
			combo.setText("GridCCombo Widget " + columnIndex);
			for (int i = 0; i < 100; i++)
				combo.add("item " + i);
			grid.setupControl(combo);
			editor.setEditor(combo, row, columnIndex);
			
			// column 4
			columnIndex = 4;
			editor = new GridEditor(grid.getGrid());
			editor.grabHorizontal = true;
			text = new Text(grid.getGrid(), SWT.NONE);
			text.setText("Row" + rowIndex + "_Col" + columnIndex);
			grid.setupControl(new GridWidgetWrapper(grid, text, rowIndex, columnIndex));
			editor.setEditor(text, row, columnIndex);			
			*/
		});
		
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
		(new GridExperiments()).go();
	}

}