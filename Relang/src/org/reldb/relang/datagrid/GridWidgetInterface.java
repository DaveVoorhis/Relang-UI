package org.reldb.relang.datagrid;

import org.eclipse.swt.widgets.Control;

public interface GridWidgetInterface {
	
	public static interface Notifier {
		void changed(GridWidgetInterface gridWidget, Object newContent);
	}
	
	/** Return the specific widget that will be displayed in the grid. */
	public Control getControl();
	
	/** Focus on the widget. */
	public boolean focus();
	
	/** Return the widget's row. */
	public int getRow();
	
	/** Return the widget's column. */
	public int getColumn();
	
	/** This will be invoked by the environment to tell the control what it should call to update the underlying Data.
	 * 
	 * In other words, whenever this widget changes, it should notify the Grid by invoking something like...
	 * 
	 *   notifier.changed(this, this.getText());
	 *  
	 *  ...where the Notifier has been set via setNotifier(...)
	 */
	public void setNotifier(Notifier notifier);

	/** Obtain the Notifier for this widget. */
	public Notifier getNotifier();
}
