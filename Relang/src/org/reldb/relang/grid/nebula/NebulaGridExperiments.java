package org.reldb.relang.grid.nebula;

import java.util.HashMap;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NebulaGridExperiments {

	private int focusRow = 0;
	private int focusColumn = 0;
	
	private HashMap<Integer, HashMap<Integer, Control>> controls = new HashMap<Integer, HashMap<Integer, Control>>();
	
	private Grid grid;
	
	private void focusOnCell(int rowNumber, int columnNumber) {
		grid.setFocusColumn(grid.getColumn(columnNumber));
		var gridItem = grid.getItem(rowNumber);
		grid.setFocusItem(gridItem);
		grid.setCellSelection(new Point(columnNumber, rowNumber));
		var control = controls.get(rowNumber).get(columnNumber);
		if (!control.setFocus() && !control.forceFocus()) {
			grid.getParent().forceFocus();
			System.out.println("focusOnCell: can't focus on unfocusable control " + control);
		}
		focusRow = rowNumber;
		focusColumn = columnNumber;
	}

	private void setupControl(Control control, int rowNumber, int columnNumber) {
		var row = controls.get(rowNumber);
		if (row == null) {
			row = new HashMap<Integer, Control>();
			controls.put(rowNumber, row);
		}
		row.put(columnNumber, control);
		control.addListener(SWT.MouseDown, evt -> {
			if (rowNumber == focusRow && columnNumber == focusColumn)
				return;
			System.out.println("Focus on cell.");
			focusOnCell(rowNumber, columnNumber);
		});
		control.addListener(SWT.KeyDown, evt -> {
			if (evt.keyCode == SWT.TAB) {
				if ((evt.stateMask & SWT.SHIFT) == 0)
					traverseNext();
				else
					traversePrevious();
			} else if (evt.keyCode == SWT.ARROW_DOWN) {
				focusRow++;
				if (focusRow >= grid.getItemCount())
					focusRow = grid.getItemCount() - 1;
				evt.doit = false;
			} else if (evt.keyCode == SWT.ARROW_UP) {
				focusRow--;
				if (focusRow < 0)
					focusRow = 0;
				evt.doit = false;
			}
			focusOnCell(focusRow, focusColumn);
		});
		control.addListener(SWT.Traverse, evt -> {
			// disable standard TAB key traversal
			if (evt.detail == SWT.TRAVERSE_TAB_NEXT || evt.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				evt.doit = false;
		});
	}
	
	public void traverseNext() {
		System.out.println("TraverseNext invoked.");
		focusColumn++;
		if (focusColumn >= grid.getColumnCount())
			focusColumn = 0;
		focusOnCell(focusRow, focusColumn);		
	}
	
	public void traversePrevious() {
		System.out.println("TraversePrevious invoked.");
		focusColumn--;
		if (focusColumn < 0)
			focusColumn = grid.getColumnCount() - 1;		
		focusOnCell(focusRow, focusColumn);		
	}
	
	public void go() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		grid = new Grid(shell, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);

// to set up virtual retrieval
//		grid.setItemCount(count);
//		grid.addListener(SWT.SetData, evt -> { ... });
		
		grid.setLinesVisible(true);
		grid.setHeaderVisible(true);
		grid.setCellSelectionEnabled(true);
		
		int columnCount = 5;
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			var group = new GridColumnGroup(grid, SWT.NONE);
			group.setText("Group" + columnIndex);
			var column = new GridColumn(group, SWT.NONE);
			column.setFooterText("Column" + columnIndex);
			column.setWidth(150);
			column.setText("Column" + columnIndex);
		}
		
		for (int rowIndex = 0; rowIndex < 20; rowIndex++) {
			var row = new GridItem(grid, SWT.NONE);
			
			// column 0
			int columnIndex = 0;
			var editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			var label = new Text(grid, SWT.NONE);
			label.setText(Integer.toString(rowIndex));
			setupControl(label, rowIndex, columnIndex);
			editor.setEditor(label, row, columnIndex);
			
			// column 1
			columnIndex = 1;
			editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			var selector = new GridCheckButton(grid, SWT.NONE);
			setupControl(selector, rowIndex, columnIndex);
			editor.setEditor(selector, row, columnIndex);
			
			// column 2
			columnIndex = 2;
			editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			var text = new Text(grid, SWT.NONE);
			text.setText("Cell_Row" + rowIndex + "_Col" + columnIndex);
			setupControl(text, rowIndex, columnIndex);
			editor.setEditor(text, row, columnIndex);
			
			// column 3
			columnIndex = 3;
			editor = new GridEditor(grid);
			editor.minimumWidth = 50;
			editor.grabHorizontal = true;
			CCombo combo = new CCombo(grid, SWT.NONE);
			combo.setText("CCombo Widget " + columnIndex);
			for (int i = 0; i < 100; i++)
				combo.add("item " + i);
			setupControl(combo, rowIndex, columnIndex);
			editor.setEditor(combo, row, columnIndex);
			
			// column 4
			columnIndex = 4;
			editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			text = new Text(grid, SWT.NONE);
			text.setText("Row" + rowIndex + "_Col" + columnIndex);
			setupControl(text, rowIndex, columnIndex);
			editor.setEditor(text, row, columnIndex);
		}
		
		focusOnCell(0, 0);

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