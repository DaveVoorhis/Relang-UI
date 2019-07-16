package org.reldb.relang.sheet;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.data.Data;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridText;
import org.reldb.relang.datagrid.GridWidgetInterface;
import org.reldb.relang.datagrid.GridWidgetInterface.Notifier;
import org.reldb.relang.utilities.DialogBase;

/** A Sheet (controller?) connects a Data (model) to a Datagrid (viewer).
 * 
 * @author dave
 */
public class Sheet extends Composite {

	private StackLayout layout;
	private Datagrid grid;
	private Data data;
	
	private void addColumn() {
		data.appendDefaultColumn();
		reload();
	}

	private void addColumnAdderColumn() {
		var column = new GridColumn(grid.getGrid(), SWT.NONE);
		column.setHeaderTooltip("Add column.");
		column.setWidth(75);
		column.setText("+");
		column.addListener(SWT.Selection, evt-> addColumn());		
	}
	
	private void load() {
		grid = new Datagrid(this, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
		grid.getGrid().setHeaderVisible(true);
		
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
		addColumnAdderColumn();
		
		grid.getGrid().setLinesVisible(true);
		grid.getGrid().setCellSelectionEnabled(true);
		grid.getGrid().setRowsResizeable(true);
		grid.getGrid().setRowHeaderVisible(true);
		grid.getGrid().setColumnScrolling(true);
		grid.getGrid().setRowHeaderVisible(true);

		grid.getGrid().setItemCount(data.getRowCount() + 1);
		
		grid.getGrid().addListener(SWT.SetData, setDataEvt -> {
			GridItem row = (GridItem)setDataEvt.item;
			int rowIndex = row.getRowIndex();

			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				var editor = new GridEditor(grid.getGrid());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				var text = new Text(grid.getGrid(), SWT.NONE);
				if (rowIndex < data.getRowCount() - 1)
					text.setText(data.getValue(columnIndex, rowIndex).toString());
				var cell = new GridText(grid, text, rowIndex, columnIndex);
				grid.setupControl(cell);
				cell.setNotifier(new Notifier() {
					@Override
					public void changed(GridWidgetInterface gridWidget, Object newContent) {
						int getRowCount = data.getRowCount();
						data.setValue(gridWidget.getColumn(), gridWidget.getRow(), newContent);
				//		if (getRowCount != data.getRowCount())
				//			reload();
					}
				});
				editor.setEditor(text, row, columnIndex);
			}
		});
		
		layout.topControl = grid.getGrid();
	}
	
	private void reload() {
		int focusRow = grid.getFocusRow();
		int focusColumn = grid.getFocusColumn();
		load();
		grid.focusOnCell(focusRow, focusColumn);
		grid.getGrid().getParent().layout();
	}
	
	public Sheet(Composite parent, Data data) {
		super(parent, SWT.NONE);
		this.data = data;

		layout = new StackLayout();
		setLayout(layout);
		
		load();
		
		grid.focusOnCell(0, 0);		
	}

	private void showColumnDialog(Event evt) {
		var column = (GridColumn)evt.item;
		var parentShell = column.getParent().getShell();
		var dialog = new DialogBase(parentShell, SWT.NONE) {
			@Override
			public void create(Shell shell) {
				shell.setText("Properties for " + column.getText());
				shell.setLayout(new FillLayout());
				var label = new Label(shell, SWT.NONE);
				label.setText("Coming Soon..."); 
			}
		};
		dialog.open();
	}
}
