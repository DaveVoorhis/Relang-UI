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

public class GridSnippet9 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

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

		
		final Grid grid = new Grid(new MyOwnDataVisualizer(), shell, SWT.BORDER | SWT.VIRTUAL);
/*		
		grid.setLinesVisible(true);
		for (int i = 0; i < 3; i++) {
			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setWidth(150);
		}

		for (int i = 1; i < 2; i++) {
			GridItem item1 = new GridItem(grid, SWT.NONE);
			item1.setText("Item " + i);
			GridItem item2 = new GridItem(grid, SWT.NONE);
			item2.setText("This cell spans both columns");
			item2.setColumnSpan(0, 1);
			GridItem item3 = new GridItem(grid, SWT.NONE);
			item3.setText("Item " + (i + 1));
		}

		GridItem[] items = grid.getItems();
		for (int i = 0; i < items.length; i++) {
			GridEditor editor = new GridEditor(grid);
			CCombo combo = new CCombo(grid, SWT.NONE);
			combo.setText("CCombo Widget " + i);
			combo.add("item 1");
			combo.add("item 2");
			combo.add("item 3");
			editor.minimumWidth = 50;
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 0);

			editor = new GridEditor(grid);
			Text text = new Text(grid, SWT.NONE);
			text.setText("Text " + i);
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 1);
			editor = new GridEditor(grid);
			
			Button button = new Button(grid, SWT.CHECK);
			button.setText("Check me");
			button.pack();
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor(button, items[i], 2);
		}
*/

		shell.setSize(500, 500);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}