package org.reldb.relang.grid.nebula;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.nebula.widgets.grid.AdaptedDataVisualizer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
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

public class NebulaGridExperiments {

	static class MyModel {
		public int counter;

		public MyModel(int n) {
			counter = n;
		}
		
		public String toString() {
			return "MODEL " + counter;
		}
		
	};
	
	static class MyOwnDataVisualizer extends AdaptedDataVisualizer {
	    FontRegistry registry = new FontRegistry();
	    private final MyModel models[];
	 
	    public MyOwnDataVisualizer(MyModel models[]) {
	        this.models = models;
	    }
	 
	    @Override
	    public Image getImage(GridItem item, int columnIndex) {
	        return null;
	    }
	 
	    @Override
	    public String getText(GridItem item, int columnIndex) {
	        return "Column " + columnIndex + " => " + models[item.getRowIndex()].toString();
	    }
	 
	    @Override
	    public Font getFont(GridItem item, int columnIndex) {
	        if ((models[item.getRowIndex()]).counter % 2 == 0) {
	            return registry.getBold(Display.getCurrent().getSystemFont()
	                    .getFontData()[0].getName());
	        }
	        return null;
	    }
	 
	    @Override
	    public Color getBackground(GridItem item, int columnIndex) {
	        if ((models[item.getRowIndex()]).counter % 2 == 0) {
	            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	        }
	        return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	    }
	 
	    @Override
	    public Color getForeground(GridItem item, int columnIndex) {
	        if ((models[item.getRowIndex()]).counter % 2 == 1) {
	            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	        }
	        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	    }
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		var models = new MyModel[10000];
		for (int i = 0; i < models.length; i++) {
			models[i] = new MyModel(i);
		}
		
		var visualiser = new MyOwnDataVisualizer(models);
		var grid = new Grid(visualiser, shell, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
			
		grid.setLinesVisible(true);
		grid.setHeaderVisible(true);
		grid.setRowHeaderVisible(true);
		
		for (int i = 0; i < 3; i++) {
			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setWidth(150);
		}

		GridItem item1 = new GridItem(grid, SWT.NONE);
		item1.setText("Item 0");
		GridItem item2 = new GridItem(grid, SWT.NONE);
		item2.setText("Item 1");
		GridItem item3 = new GridItem(grid, SWT.NONE);
		item3.setText("Item 2");

		for (int i = 3; i < 500; i++) {
			var item = new GridItem(grid, SWT.NONE);
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