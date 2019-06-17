package org.reldb.relang.reli.core;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.edit.gui.AbstractDialogCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;

public class RvaCellEditor extends AbstractDialogCellEditor {	
    private boolean dialogIsClosed = false;

    private String defaultValue;
    
    public RvaCellEditor(Grid editor, String defaultValue) {
    	this.defaultValue = defaultValue;
    }
    
    @Override
    public int open() {
    	if (Window.OK == getDialogInstance().open()) {
            commit(MoveDirectionEnum.NONE);
            this.dialogIsClosed = true;
            return Window.OK;
        } else {
            this.dialogIsClosed = true;
            return Window.CANCEL;
        }
    }

    @Override
    public RvaEditorDialog createDialogInstance() {
        dialogIsClosed = false;
        return new RvaEditorDialog(parent.getShell());
    }

    @Override
    public RvaEditorDialog getDialogInstance() {
        return (RvaEditorDialog) dialog;
    }

    @Override
    public Object getEditorValue() {
    	return getDialogInstance().getRVAValue();
    }

    @Override
    public void setEditorValue(Object value) {
    	String editorValue;
    	if (value.toString().trim().length() == 0)
    		editorValue = defaultValue;
    	else
    		editorValue = value.toString();
    	getDialogInstance().setRVAValue(editorValue);
    }

    @Override
    public void close() {
    	getDialogInstance().close();
    }

    @Override
    public boolean isClosed() {
        return this.dialogIsClosed;
    }

}
