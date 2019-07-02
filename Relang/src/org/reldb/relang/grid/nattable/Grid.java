package org.reldb.relang.grid.nattable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.CellEditorCreatedEvent;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.MultiLineTextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.gui.ICellEditDialog;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.ITraversalStrategy;
import org.eclipse.nebula.widgets.nattable.selection.MoveCellSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.command.ClearAllSelectionsCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tickupdate.TickUpdateConfigAttributes;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowRowInViewportCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.reldb.relang.core.MenuConfiguration;
import org.reldb.relang.data.GridData;
import org.reldb.relang.utilities.IconLoader;

public class Grid {

	private NatTable table;
	
	protected DataProvider dataProvider;

	private HeadingProvider headingProvider;
	private DefaultGridLayer gridLayer;

	private boolean popupEdit = false;

	private int lastRowSelected = -1;

	private GridData data;
	
	private Composite parent;

	enum RowAction {
		UPDATE, INSERT
	};

	class HeadingProvider implements IDataProvider {
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			switch (rowIndex) {
			case 0:
				return data.getColumnNameAt(columnIndex);
			case 1:
				return data.getColumnTypeAt(columnIndex).getName();
			default:
				return "";
			}
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumnCount() {
			return data.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return 2;
		}
	};

	class DataProvider implements IDataProvider {
		public DataProvider() {
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return data.getValue(columnIndex, rowIndex);
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			data.setValue(columnIndex, rowIndex, newValue);
		}

		@Override
		public int getColumnCount() {
			return data.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return data.getRowCount();
		}
	};

	class HeaderConfiguration extends AbstractRegistryConfiguration {
		public void configureRegistry(IConfigRegistry configRegistry) {
			ImagePainter keyPainter = new ImagePainter(IconLoader.loadIconNormal("bullet_key"));
			CellPainterDecorator decorator = new CellPainterDecorator(new TextPainter(), CellEdgeEnum.RIGHT,
					keyPainter);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
					new BeveledBorderDecorator(decorator), DisplayMode.NORMAL, "keycolumnintegrated");
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
					new BeveledBorderDecorator(keyPainter), DisplayMode.NORMAL, "keycolumnalone");
			BorderStyle borderStyle = new BorderStyle();
			borderStyle.setColor(GUIHelper.COLOR_GRAY);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
					new LineBorderDecorator(new TextPainter(), borderStyle), DisplayMode.NORMAL, GridRegion.CORNER);
		}
	}

	class EditorConfiguration extends AbstractRegistryConfiguration {
		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {
			// editable
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
					IEditableRule.ALWAYS_EDITABLE);
			// style for "changed" cells
			Style changedStyle = new Style();
			changedStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
			changedStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, changedStyle, DisplayMode.NORMAL,
					"changed");
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, changedStyle, DisplayMode.SELECT,
					"changed");
			// style for "error" cells
			Style errorStyle = new Style();
			errorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_RED);
			errorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, errorStyle, DisplayMode.NORMAL,
					"error");
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, errorStyle, DisplayMode.SELECT,
					"error");
			// style for selected cells
			Style selectStyle = new Style();
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, selectStyle, DisplayMode.SELECT);
			// default text editor
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor(true, true) {
				protected Control activateCell(Composite parent, Object originalCanonicalValue) {
					editorBeenOpened(getRowIndex(), getColumnIndex());
					return super.activateCell(parent, originalCanonicalValue);
				}

				public void close() {
					editorBeenClosed(getRowIndex(), getColumnIndex());
					super.close();
				}
			}, DisplayMode.NORMAL);
			// open adjacent editor when we leave the current one during editing
			configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE,
					DisplayMode.EDIT);
			// for each column...
			for (int column = 0; column < data.getColumnCount(); column++) {
				String columnLabel = "column" + column;
				System.out.println("Grid:registerDefaultColumn: " + columnLabel);
				registerDefaultColumn(configRegistry, columnLabel);
			}
		}

		private void registerDefaultColumn(IConfigRegistry configRegistry, String columnLabel) {
			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
					columnLabel);
		}

		private void registerBooleanColumn(IConfigRegistry configRegistry, String columnLabel) {
			// register a CheckBoxCellEditor
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor() {
				protected Control activateCell(Composite parent, Object originalCanonicalValue) {
					editorBeenOpened(getRowIndex(), getColumnIndex());
					return super.activateCell(parent, originalCanonicalValue);
				}

				public void close() {
					editorBeenClosed(getRowIndex(), getColumnIndex());
					super.close();
				}
			}, DisplayMode.EDIT, columnLabel);

			// if you want to use the CheckBoxCellEditor, you should also consider
			// using the corresponding CheckBoxPainter to show the content like a
			// checkbox in your NatTable
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
					DisplayMode.NORMAL, columnLabel);

			// using a CheckBoxCellEditor also needs a Boolean conversion to work
			// correctly
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
					new DefaultDisplayConverter() {
						@Override
						public Object canonicalToDisplayValue(Object canonicalValue) {
							if (canonicalValue == null)
								return null;
							boolean isTrue = canonicalValue.toString().equalsIgnoreCase("True");
							return Boolean.valueOf(isTrue);
						}

						@Override
						public Object displayToCanonicalValue(Object destinationValue) {
							return ((Boolean) destinationValue).booleanValue() ? "True" : "False";
						}
					}, DisplayMode.NORMAL, columnLabel);
		}

		private void registerRationalColumn(IConfigRegistry configRegistry, String columnLabel) {
			// configure the tick update dialog to use the adjust mode
			configRegistry.registerConfigAttribute(TickUpdateConfigAttributes.USE_ADJUST_BY, Boolean.TRUE,
					DisplayMode.EDIT, columnLabel);
			// Use Double converter
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
					new DefaultDoubleDisplayConverter(), DisplayMode.NORMAL, columnLabel);
		}

		private void registerIntegerColumn(IConfigRegistry configRegistry, String columnLabel) {
			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
					columnLabel);
			// Use Integer converter
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
					new DefaultIntegerDisplayConverter(), DisplayMode.NORMAL, columnLabel);
		}

		private void registerMultiLineEditorColumn(IConfigRegistry configRegistry, String columnLabel) {
			// configure the multi line text editor
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
					new MultiLineTextCellEditor(false) {
						protected Control activateCell(Composite parent, Object originalCanonicalValue) {
							editorBeenOpened(getRowIndex(), getColumnIndex());
							return super.activateCell(parent, originalCanonicalValue);
						}

						public void close() {
							editorBeenClosed(getRowIndex(), getColumnIndex());
							super.close();
						}
					}, DisplayMode.NORMAL, columnLabel);

			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
					columnLabel);

			// configure custom dialog settings
			Display display = Display.getCurrent();
			Map<String, Object> editDialogSettings = new HashMap<String, Object>();
			editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_TITLE, "Edit");
			editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_ICON, display.getSystemImage(SWT.ICON_WARNING));
			editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_RESIZABLE, Boolean.TRUE);

			Point size = new Point(400, 300);
			editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_SIZE, size);

			int screenWidth = display.getBounds().width;
			int screenHeight = display.getBounds().height;
			Point location = new Point((screenWidth / (2 * display.getMonitors().length)) - (size.x / 2),
					(screenHeight / 2) - (size.y / 2));
			editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_LOCATION, location);

			configRegistry.registerConfigAttribute(EditConfigAttributes.EDIT_DIALOG_SETTINGS, editDialogSettings,
					DisplayMode.EDIT, columnLabel);
		}
	}

	class PopupEditorConfiguration extends AbstractRegistryConfiguration {
		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {
			// always/never open in a subdialog depending on popupEdit value
			configRegistry.unregisterConfigAttribute(EditConfigAttributes.OPEN_IN_DIALOG);
			configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_IN_DIALOG, popupEdit, DisplayMode.EDIT);
		}
	}

	public void refresh() {
		table.refresh();
	}

	public Grid(Composite parent, GridData data) {
		this.parent = parent;
		this.data = data;
		init();
		refresh();
	}

	private static class EmptyGridData implements IDataProvider {
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return "This datasheet is not editable because it has no attributes. Select Design from the toolbar to add attributes.";
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return 1;
		}
	}

	private static class EmptyGridHeading implements IDataProvider {
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return null;
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return 0;
		}
	}

	protected void init() {
		dataProvider = new DataProvider();
		headingProvider = new HeadingProvider();

		gridLayer = new DefaultGridLayer(dataProvider, headingProvider);

		// CellLabelAccumulator determines how cells will be displayed
		class CellLabelAccumulator implements IConfigLabelAccumulator {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				configLabels.addLabel("column" + columnPosition);
				/*
				// error?
				if (dataProvider.getError(rowPosition) != null)
					configLabels.addLabel("error");
				// changed?
				else if (dataProvider.isChanged(columnPosition, rowPosition))
					configLabels.addLabel("changed");
				else if (dataProvider.isRVA(columnPosition))
					configLabels.addLabel("RVAeditor");
				*/
			}
		}

		DataLayer bodyDataLayer = (DataLayer) gridLayer.getBodyDataLayer();
		CellLabelAccumulator cellLabelAccumulator = new CellLabelAccumulator();
		bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);

		class HeadingLabelAccumulator implements IConfigLabelAccumulator {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				/*
				if (keys != null && keys.size() > 0) {
					if (rowPosition == 0 && keys.get(0).contains(heading[columnPosition].getName()))
						configLabels.addLabel("keycolumnintegrated");
					else if (rowPosition >= 2 && keys.size() > 1
							&& keys.get(rowPosition - 1).contains(heading[columnPosition].getName()))
						configLabels.addLabel("keycolumnalone");
				}
				*/
			}
		}

		DataLayer headingDataLayer = (DataLayer) gridLayer.getColumnHeaderDataLayer();
		HeadingLabelAccumulator columnLabelAccumulator = new HeadingLabelAccumulator();
		headingDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);

		table = new NatTable(parent, gridLayer, false);
		
		// Put cursor in table when it's initialised.
		table.addPaintListener(new PaintListener() {
		    @Override
		    public void paintControl(PaintEvent e) {
		        table.removePaintListener(this);
		        goToInsertRow();
		    }
		});		

		DefaultNatTableStyleConfiguration defaultStyle = new DefaultNatTableStyleConfiguration();
		table.addConfiguration(defaultStyle);
		table.addConfiguration(new EditorConfiguration());
		table.addConfiguration(new HeaderConfiguration());

		ContributionItem columnMenuItems = new ContributionItem() {
			@Override
			public void fill(Menu menu, int index) {
				/*
				IconMenuItem doesPopupEdit = new IconMenuItem(menu, "Pop-up Edit Box", "popup", SWT.CHECK, e -> {
					popupEdit = !popupEdit;
					table.addConfiguration(new PopupEditorConfiguration());
					table.configure();
				});
				doesPopupEdit.setSelection(popupEdit);
				*/
			}
		};
		
		table.addConfiguration(new MenuConfiguration(GridRegion.COLUMN_HEADER,
				new PopupMenuBuilder(table).withContributionItem(columnMenuItems)));

		ContributionItem rowMenuItems = new ContributionItem() {
			@Override
			public void fill(Menu menu, int index) {
				// new IconMenuItem(menu, "Delete", "table_row_delete", SWT.PUSH, e -> askDeleteSelected());
			}
		};
		
		table.addConfiguration(new MenuConfiguration(GridRegion.ROW_HEADER,
				new PopupMenuBuilder(table).withContributionItem(rowMenuItems)));

		// Report row selection events, to help control updating
		table.addLayerListener(new ILayerListener() {
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof RowSelectionEvent) {
					rowBeenSelected(-1);
				} else if (event instanceof CellSelectionEvent) {
					CellSelectionEvent csEvent = (CellSelectionEvent) event;
					int row = LayerUtil.convertRowPosition(csEvent.getLayer(), csEvent.getRowPosition(),
							gridLayer.getBodyDataLayer());
					rowBeenSelected(row);
				} else if (event instanceof CellVisualChangeEvent) {
					CellVisualChangeEvent cvEvent = (CellVisualChangeEvent) event;
					int row = LayerUtil.convertRowPosition(cvEvent.getLayer(), cvEvent.getRowPosition(),
							gridLayer.getBodyDataLayer());
					rowBeenSelected(row);
				} else if (event instanceof CellEditorCreatedEvent) {
				} else {
					rowBeenSelected(-1);
				}
			}
		});

		// Tabbing wraps and moves up/down
		gridLayer.registerCommandHandler(new MoveCellSelectionCommandHandler(
				gridLayer.getBodyLayer().getSelectionLayer(), ITraversalStrategy.TABLE_CYCLE_TRAVERSAL_STRATEGY));

		table.configure();

		table.getDisplay().addFilter(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (table.isDisposed())
					return;
				if (!hasFocus(table))
					lostFocus();
			}
		});

		// Tooltip for row/column headings
		new NatTableContentTooltip(table, GridRegion.COLUMN_HEADER, GridRegion.ROW_HEADER) {
			protected String getText(Event event) {
				return "Right-click for options.";
			}
		};

		// Tooltip shows dataProvider update errors
		new DefaultToolTip(table, ToolTip.NO_RECREATE, false) {
			@Override
			protected Object getToolTipArea(Event event) {
				int x = table.getColumnPositionByX(event.x);
				int y = table.getRowPositionByY(event.y);
				return new Point(x, y);
			}

			@Override
			protected String getText(Event event) {
				/*
				int x = table.getColumnPositionByX(event.x);
				int y = table.getRowPositionByY(event.y);
				ILayerCell cell = table.getCellByPosition(x, y);
				if (cell == null)
					return null;
				int row = cell.getRowIndex();
				return dataProvider.getError(row);
				*/
				return "tooltip";
			}

			@Override
			protected boolean shouldCreateToolTip(Event event) {
				if (getText(event) != null)
					return super.shouldCreateToolTip(event);
				return false;
			}
		};
	}

	public void processDirtyRows() {
//		dataProvider.processDirtyRows();
		lastRowSelected = -1;
	}

	public int countDirtyRows() {
//		if (dataProvider == null)
			return 0;
//		return dataProvider.countDirtyRows();
	}

	private void editorBeenOpened(int row, int column) {
		lastRowSelected = row;
		processDirtyRows();
	}

	private void editorBeenClosed(int row, int column) {
		lastRowSelected = row;
		processDirtyRows();
	}

	private void rowBeenSelected(int row) {
		lastRowSelected = row;
		processDirtyRows();
	}

	private void lostFocus() {
		lastRowSelected = -1;
		processDirtyRows();
	}

	public Control getControl() {
		return table;
	}

	// Recursively determine if control or one of its children have the keyboard
	// focus.
	public static boolean hasFocus(Control control) {
		if (control.isFocusControl())
			return true;
		if (control instanceof Composite)
			for (Control child : ((Composite) control).getChildren())
				if (hasFocus(child))
					return true;
		return false;
	}

	public void goToInsertRow() {
		if (table.commitAndCloseActiveCellEditor()) {
			table.setFocus();
			table.doCommand(new ClearAllSelectionsCommand());
			if (gridLayer != null && dataProvider != null) {
				int lastRow = dataProvider.getRowCount() - 1;
				table.doCommand(new ShowRowInViewportCommand(gridLayer.getBodyLayer().getViewportLayer(), lastRow));
				table.doCommand(new SelectCellCommand(gridLayer.getBodyLayer().getSelectionLayer(), 0, lastRow, true, true));
			}
		}
	}

	private void doDeleteSelected() {
		Set<Range> selections = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowPositions();
		/*
		if (dataProvider != null)
			dataProvider.deleteRows(selections);
		*/
	}

	public void askDeleteSelected() {
		if (dataProvider == null)
			return;
		if (countDirtyRows() > 0 && !MessageDialog.openConfirm(table.getShell(), "Unsaved Changes",
				"There are unsaved changes. If you proceed with deletion, they will be cancelled.\n\nPress OK to cancel unsaved changes and proceed with deletion."))
			return;
		/*
		if (askDeleteConfirm) {
			int selectedRowCount = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowCount();
			DeleteConfirmDialog deleteConfirmDialog = new DeleteConfirmDialog(table.getShell(), selectedRowCount,
					"tuple");
			if (deleteConfirmDialog.open() == DeleteConfirmDialog.OK) {
				askDeleteConfirm = deleteConfirmDialog.getAskDeleteConfirm();
				doDeleteSelected();
			}
		} else
			doDeleteSelected();
		*/
	}

}