package org.reldb.relang.dengine.preferences;

public class PreferenceChangeEvent {

	private String name;
	
	public PreferenceChangeEvent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
