package org.reldb.relang.reli;

import org.eclipse.swt.widgets.Composite;

public class RvaEditor extends Grid {

	private String rvaValue;

	// Relvar attribute designer
	public RvaEditor(Composite parent) {
		super(parent);
		// askDeleteConfirm = false;
	}

	protected String getAttributeSource() {
		return rvaValue;
	}

	public String getRVAValue() {
		rvaValue = dataProvider.getLiteral();
		return rvaValue;
	}

	public void setRVAValue(String rvaValue) {
		this.rvaValue = rvaValue;
		tuples = obtainTuples();
		heading = tuples.getHeading().toArray();
		init();
	}

}