package org.reldb.relang.core;

import org.eclipse.swt.widgets.Composite;

public class RvaEditor extends Grid {

	private String rvaValue;

	// Relvar attribute designer
	public RvaEditor(Composite parent) {
		super(parent);
		askDeleteConfirm = false;
	}

	protected String getAttributeSource() {
		return rvaValue;
	}

	public String getRVAValue() {
		rvaValue = getLiteral();
		return rvaValue;
	}

	public void setRVAValue(String rvaValue) {
		this.rvaValue = rvaValue;
	//	tuples = obtainTuples();
	//	init();
	}

}