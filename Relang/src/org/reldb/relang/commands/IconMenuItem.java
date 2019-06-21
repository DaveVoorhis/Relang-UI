package org.reldb.relang.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.relang.preferences.PreferenceChangeAdapter;
import org.reldb.relang.preferences.PreferenceChangeEvent;
import org.reldb.relang.preferences.PreferenceChangeListener;
import org.reldb.relang.preferences.PreferencePageGeneral;
import org.reldb.relang.preferences.Preferences;
import org.reldb.relang.utilities.IconLoader;

// A MenuItem with an optional icon that can change size in response to change in Preferences.
public class IconMenuItem extends MenuItem {
	String imageName;
	PreferenceChangeListener preferenceChangeListener;
	
	public IconMenuItem(Menu parentMenu, String text, String imageName, int style, Listener listener) {
		super(parentMenu, style);
		if (text != null)
			setText(text);
		this.imageName = imageName;
		if (listener != null)
			addListener(SWT.Selection, listener);
		reloadImage();
		preferenceChangeListener = new PreferenceChangeAdapter("IconMenuItem" + text) {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				reloadImage();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}
	
	public IconMenuItem(Menu parentMenu, String text, int style) {
		this(parentMenu, text, null, style, null);
	}

	public void reloadImage() {
		if (imageName != null)
			setImage(IconLoader.loadIcon(imageName));
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}
	
	public void checkSubclass() {}
}
