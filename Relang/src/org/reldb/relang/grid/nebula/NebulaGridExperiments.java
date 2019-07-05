package org.reldb.relang.grid.nebula;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.nebula.widgets.grid.AdaptedDataVisualizer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class NebulaGridExperiments {
	
	static class MyOwnDataVisualizer extends AdaptedDataVisualizer {
	    FontRegistry registry = new FontRegistry();
	    
	    public MyOwnDataVisualizer() {
	    }
	 
	    @Override
	    public Image getImage(GridItem item, int columnIndex) {
	        return null;
	    }
	 
	    @Override
	    public String getText(GridItem item, int columnIndex) {
	    	return item.getText();
	    }
	 
	    @Override
	    public Font getFont(GridItem item, int columnIndex) {
	        return SWTResourceManager.getFont("Arial", 12, SWT.BOLD);
	    }
	 
	    @Override
	    public Color getBackground(GridItem item, int columnIndex) {
	        return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	    }
	 
	    @Override
	    public Color getForeground(GridItem item, int columnIndex) {
	        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	    }
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		var visualiser = new MyOwnDataVisualizer();
		var grid = new Grid(visualiser, shell, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
			
		grid.setLinesVisible(true);
		grid.setHeaderVisible(true);
//		grid.setRowHeaderVisible(true);
		

		int columnCount = 5;
		for (int i = 0; i < columnCount; i++) {
			var group = new GridColumnGroup(grid, SWT.NONE);
			group.setText("Group" + i);
			var column = new GridColumn(group, SWT.NONE);
			column.setFooterText("Column" + i);
			column.setWidth(150);
			column.setText("Column" + i);
		}
		
		var gridItem = new GridItem(grid, SWT.NONE);
		gridItem.setText("Item1");
		
/*
 * 
		GridItem item1 = new GridItem(grid, SWT.NONE);
		item1.setText("Item 0");
		GridItem item2 = new GridItem(grid, SWT.NONE);
		item2.setText("Item 1");
		GridItem item3 = new GridItem(grid, SWT.NONE);
		item3.setText("Item 2");

		for (int i = 0; i < 3; i++) {
			var item = new GridItem(grid, SWT.NONE);
			var editor = new GridEditor(grid);
			var text = new Text(grid, SWT.NONE);
			text.setText("This is text");
			editor.grabHorizontal = true;
			editor.setEditor(text, item, 1);
			
			GridEditor editor = new GridEditor(grid);
			CCombo combo = new CCombo(grid, SWT.NONE);
			combo.setText("CCombo Widget " + i);
			combo.add("item 1");
			combo.add("item 2");
			combo.add("item 3");
			editor.minimumWidth = 50;
			editor.grabHorizontal = true;
			editor.setEditor(combo, item, 0);

			item = new GridItem(grid, SWT.NONE);
			editor = new GridEditor(grid);
			Text text = new Text(grid, SWT.NONE);
			text.setText("Text " + i);
			editor.grabHorizontal = true;
			editor.setEditor(text, item, 1);
			
			editor = new GridEditor(grid);
			
			item = new GridItem(grid, SWT.NONE);
			Button button = new Button(grid, SWT.CHECK);
			button.setText("Check me");
			button.pack();
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor(button, item, 2);
		}
			*/

		grid.refreshData();

		shell.setSize(800, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}