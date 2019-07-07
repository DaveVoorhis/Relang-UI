package org.reldb.relang.grid.nebula;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NebulaGridExperiments {

	int focusRow = 0;
	int focusColumn = 0;
	
	private void focusOnCell(Grid grid, int rowNumber, int columnNumber, Control control) {
		grid.setFocusColumn(grid.getColumn(columnNumber));
		var gridItem = grid.getItem(rowNumber);
		grid.setFocusItem(gridItem);
		grid.setCellSelection(new Point(columnNumber, rowNumber));
		if (control != null) {
			System.out.println("focusOnCell force focus to " + control + " ");
			if (!control.forceFocus())
				System.out.print("focusOnCell can't force focus ");
		} else
			System.out.print("focusOnCell given a null control to focus on ");
		System.out.println("focusOnCell column=" + columnNumber + " row=" + rowNumber);
		focusRow = rowNumber;
		focusColumn = columnNumber;
	}

	private void setupControl(Grid grid, Control control, int rowNumber, int columnNumber) {
		System.out.println("setupControl control=" + System.identityHashCode(control));
		final Control ctl[] = new Control[] {control};
		control.addMouseListener(new MouseAdapter () {
			@Override
			public void mouseDown(MouseEvent arg0) {
				focusOnCell(grid, rowNumber, columnNumber, ctl[0]);
			}
		});
		control.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
			//	focusOnCell(grid, rowNumber, columnNumber, control);
				if (evt.keyCode == SWT.TAB) {
					if ((evt.stateMask & SWT.SHIFT) == 0) {
						System.out.println("NebulaGridExperiments: TAB");
						focusColumn++;
						if (focusColumn >= grid.getColumnCount())
							focusColumn = 0;
					} else {
						System.out.println("NebulaGridExperiments: Shift-TAB");
						focusColumn--;
						if (focusColumn < 0)
							focusColumn = 0;
					}
					System.out.println("setupControl keylistener focus on " + System.identityHashCode(ctl[0]) + " at " + columnNumber + ", " + rowNumber);
					focusOnCell(grid, focusRow, focusColumn, ctl[0]);
				}
			}
		});
		control.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent evt) {
				// disable standard TAB key traversal
				if (evt.detail == SWT.TRAVERSE_TAB_NEXT || evt.detail == SWT.TRAVERSE_TAB_PREVIOUS)
					evt.doit = false;
			}
		});
	}
	
	public void go() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		var grid = new Grid(shell, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
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
			setupControl(grid, label, rowIndex, columnIndex);
			editor.setEditor(label, row, columnIndex);
			
	//		if (rowIndex == 0 && columnIndex == 0) {				
	//			focusOnCell(grid, 0, 0, null);
	//			label.forceFocus();
	//		}
			
			// column 1
			columnIndex = 1;
			editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			var selector = new Button(grid, SWT.CHECK);
			setupControl(grid, selector, rowIndex, columnIndex);
			editor.setEditor(selector, row, columnIndex);
			
			// column 2
			columnIndex = 2;
			editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			var text = new Text(grid, SWT.NONE);
			text.setText("Cell_Row" + rowIndex + "_Col" + columnIndex);
			setupControl(grid, text, rowIndex, columnIndex);
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
			setupControl(grid, combo, rowIndex, columnIndex);
			editor.setEditor(combo, row, columnIndex);
			
			// column 4
			columnIndex = 4;
			editor = new GridEditor(grid);
			editor.grabHorizontal = true;
			text = new Text(grid, SWT.NONE);
			text.setText("Row" + rowIndex + "_Col" + columnIndex);
			setupControl(grid, text, rowIndex, columnIndex);
			editor.setEditor(text, row, columnIndex);
		}

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