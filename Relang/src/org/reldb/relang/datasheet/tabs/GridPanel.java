package org.reldb.relang.datasheet.tabs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.reldb.relang.core.data.Data;
import org.reldb.relang.core.tuples.Tuple;
import org.reldb.relang.core.tuples.TupleTypeGenerator;
import org.reldb.relang.core.utilities.DialogBase;
import org.reldb.relang.datagrid.Datagrid;
import org.reldb.relang.datagrid.GridWidgetInterface;
import org.reldb.relang.datagrid.GridWidgetInterface.Notifier;
import org.reldb.relang.datagrid.widgets.GridText;
import org.reldb.relang.main.Launcher;

/** A Sheet (controller?) connects a Data (model) to a Datagrid (viewer).
 * 
 * @author dave
 */
public class GridPanel extends Composite {

	private Datagrid grid;
	private Data<?, ?> data;
	
	public GridPanel(Composite parent, Data<?, ?> data) {
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
		column.addListener(SWT.Selection, evt -> Launcher.addTask(() -> addColumn()));		
	}
	
	private void load(int focusRow, int focusColumn) {
		if (grid != null)
			grid.dispose();

		grid = new Datagrid(this, SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		
		grid.getGrid().setHeaderVisible(true);
		
		var tupleType = data.getType();
		var fields = TupleTypeGenerator.getDataFields(tupleType).collect(Collectors.toList());
		
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
		
		Integer rowCount = (Integer)data.query(container -> container.entrySet().size());
		grid.getGrid().setItemCount(rowCount + (data.isReadonly() ? 0 : 1));
		
		grid.getGrid().addListener(SWT.SetData, setDataEvt -> {
			GridItem row = (GridItem)setDataEvt.item;
			int rowIndex = row.getRowIndex();
			
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				var editor = new GridEditor(grid.getGrid());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				var text = new Text(grid.getGrid(), SWT.NONE);
				
				text.setEditable(!data.isReadonly());

				// Horrible hack to deal with the fact that most containers are indexed
				// by a row number but sys_Catalog is indexed by name. This really needs
				// generalisation of keys.
				// TODO - generalise Data keys
				var field = fields.get(columnIndex);
				if (data.getName().equals("sys_Catalog"))
					text.setText((String)data.query(container -> {
						try {
							var entry = container.values().toArray()[rowIndex];
							var fieldValue = field.get(entry);
							return (fieldValue == null) ? "" : fieldValue.toString();
						} catch (IllegalArgumentException | IllegalAccessException e) {
							return "<???>";
						}
					}));	
				else 
					text.setText((String)data.query(container -> {
						try {
							if (rowIndex < container.size()) {
								var entry = container.get((long)rowIndex);
								var fieldValue = field.get(entry);
								return (fieldValue == null) ? "" : fieldValue.toString();
							} else
								return "";
						} catch (IllegalArgumentException | IllegalAccessException e) {
							return "<???>";
						}
					}));
				
				var cell = new GridText(grid, text, rowIndex, columnIndex);
				grid.setupControl(cell);
				cell.setNotifier(new Notifier() {
					@SuppressWarnings("unchecked")
					@Override
					public void changed(GridWidgetInterface gridWidget, Object newContent, GridWidgetInterface.SpecialInstructions specialInstruction) {
						data.access(container -> {
							var field = fields.get(gridWidget.getColumn());
							if (gridWidget.getRow() < container.size()) {
								var tuple = container.values().toArray()[rowIndex];
								try {
									field.set(tuple, newContent);
									container.put((long)gridWidget.getRow(), tuple);
								} catch (IllegalArgumentException | IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								Constructor<? extends Tuple> constructor;
								try {
									constructor = tupleType.getConstructor();
									var tuple = constructor.newInstance();
									field.set(tuple, newContent);
									container.put((long)container.size(), tuple);
								} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Launcher.addTask(() -> {
									if (specialInstruction == GridWidgetInterface.SpecialInstructions.MOVE_DOWN)
										reload(gridWidget.getRow() + 1, gridWidget.getColumn());
									else
										reload(gridWidget.getRow(), gridWidget.getColumn());
								});
							}
						});
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
		var dialog = new DialogBase<Integer>(parentShell) {
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
