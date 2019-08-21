package org.reldb.relang.datasheet.tabs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.reldb.relang.data.Data;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetInterface;
import org.reldb.relang.datagrid.GridWidgetInterface.Notifier;
import org.reldb.relang.datagrid.widgets.GridText;
import org.reldb.relang.main.Main;
import org.reldb.relang.utilities.DialogBase;

/** A Sheet (controller?) connects a Data (model) to a Datagrid (viewer).
 * 
 * @author dave
 */
public class GridPanel extends Composite {

	private Datagrid grid;
	private Data<?> data;
	
	private Iterator<?> iterator = null;
	private int iteratorIndex = -1;
	
	public GridPanel(Composite parent, Data<?> data) {
		super(parent, SWT.NONE);
		this.data = data;
		
		setLayout(new FillLayout());
		
		load(0, 0);
	}

	public void refresh() {
		reload(grid.getFocusRow(), grid.getFocusColumn());
	}
	
	private void addColumn() {
		var type = data.getType();
		var fields = Stream.of(type.getFields())
				.map(entry -> entry.getName())
				.collect(Collectors.toSet());
		var serialNumber = fields.size();
		var columnName = "";
		do
			columnName = "COL" + serialNumber++;
		while (fields.contains(columnName));
		data.extend(columnName, String.class);
		refresh();
	}

	private void addColumnAdderColumn() {
		var column = new GridColumn(grid.getGrid(), SWT.NONE);
		column.setHeaderTooltip("Add column.");
		column.setWidth(75);
		column.setText("+");
		column.addListener(SWT.Selection, evt -> Main.addTask(() -> addColumn()));		
	}
	
	private void load(int focusRow, int focusColumn) {
		if (grid != null)
			grid.dispose();

		grid = new Datagrid(this, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
		grid.getGrid().setHeaderVisible(true);
		
		var type = data.getType();
		var fieldArray = type.getFields();
		var fields = Arrays.asList(fieldArray);
		
		int columnCount = fields.size();
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			var column = new GridColumn(grid.getGrid(), SWT.NONE);
			var columnAttribute = fields.get(columnIndex);
			var columnName = columnAttribute.getName();
			var columnType = columnAttribute.getType().getName();
			column.setHeaderTooltip(columnType);
			column.setWidth(150);
			column.setText(columnName);
			column.addListener(SWT.Selection, evt -> showColumnDialog(evt));
			column.setWordWrap(true);
			column.setMoveable(true);
		}
		
		if (data.isExtendable())
			addColumnAdderColumn();
		
		grid.getGrid().setLinesVisible(true);
		grid.getGrid().setCellSelectionEnabled(true);
		grid.getGrid().setRowsResizeable(true);
		grid.getGrid().setRowHeaderVisible(true);
		grid.getGrid().setColumnScrolling(true);
		grid.getGrid().setRowHeaderVisible(true);

		// TODO - fix potential long vs int issue here
//		long rowCount = data.getRowCount() + 1;
//		grid.getGrid().setItemCount((int)rowCount);

		Vector<Object> rowCache = new Vector<>();
		
		grid.getGrid().addListener(SWT.SetData, setDataEvt -> {
			GridItem row = (GridItem)setDataEvt.item;
			int rowIndex = row.getRowIndex();

			if (iterator == null)
				iterator = data.iterator();

			while (iteratorIndex < rowIndex && iterator.hasNext())
				rowCache.set(iteratorIndex++, iterator.next());
			
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				var editor = new GridEditor(grid.getGrid());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				var text = new Text(grid.getGrid(), SWT.NONE);

				try {
					String valueText = fieldArray[columnIndex].get(rowCache.get(rowIndex)).toString();
					text.setText(valueText);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					text.setText("<???>");
				}
				
				/*
				if (rowIndex < data.getRowCount())
					text.setText(data.getValue(columnIndex, rowIndex).toString());
				*/
				
				var cell = new GridText(grid, text, rowIndex, columnIndex);
				grid.setupControl(cell);
				cell.setNotifier(new Notifier() {
					@Override
					public void changed(GridWidgetInterface gridWidget, Object newContent, GridWidgetInterface.SpecialInstructions specialInstruction) {
						/*
						long getRowCount = data.getRowCount();
						data.setValue(gridWidget.getColumn(), gridWidget.getRow(), newContent);
						if (getRowCount != data.getRowCount())
							Main.addTask(() -> {
								if (specialInstruction == GridWidgetInterface.SpecialInstructions.MOVE_DOWN)
									reload(gridWidget.getRow() + 1, gridWidget.getColumn());
								else
									reload(gridWidget.getRow(), gridWidget.getColumn());
							});
						*/
					}
				});
				editor.setEditor(text, row, columnIndex);
				if (columnIndex == focusColumn && rowIndex == focusRow)
					grid.focusOnCell(focusRow, focusColumn);
			}
		});
	}
	
	private void reload(int focusRow, int focusColumn) {
		load(focusRow, focusColumn);
		grid.getGrid().getParent().layout();
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
