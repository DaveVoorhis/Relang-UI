package org.reldb.relang.reli.preferences;

public abstract class PreferenceChangeAdapter implements PreferenceChangeListener {
	private String id;
	public PreferenceChangeAdapter(String id) {this.id = id;}
	public String toString() {return "PreferenceChangeAdapter: " + id;}
}
