package org.reldb.relang.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.relang.IconLoader;
import org.reldb.relang.preferences.PreferenceChangeAdapter;
import org.reldb.relang.preferences.PreferenceChangeEvent;
import org.reldb.relang.preferences.PreferenceChangeListener;
import org.reldb.relang.preferences.PreferencePageGeneral;
import org.reldb.relang.preferences.Preferences;

public class ManagedToolbar extends ToolBar {    
    private PreferenceChangeListener preferenceChangeListener;
    
	public ManagedToolbar(Composite parent) {
		super(parent, SWT.NONE);
		preferenceChangeListener = new PreferenceChangeAdapter("ManagedToolbar") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				for (ToolItem item: getItems()) {
					if (item instanceof CommandActivator) {
						CommandActivator activator = (CommandActivator)item;
						activator.setImage(IconLoader.loadIcon(activator.getIconName()));
					}
				}
				getShell().layout(new Control[] {ManagedToolbar.this}, SWT.DEFER);
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}
	
	public void addSeparator() {
		new ToolItem(this, SWT.SEPARATOR);
	}
	
	public void addSeparatorFill() {
		new ToolItem(this, SWT.SEPARATOR);
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}

	public void checkSubclass() {}
}
