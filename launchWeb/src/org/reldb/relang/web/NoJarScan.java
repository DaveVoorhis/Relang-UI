package org.reldb.relang.web;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;

class NoJarScan implements JarScanner {

	@Override
	public JarScanFilter getJarScanFilter() {
		return null;
	}

	@Override
	public void scan(JarScanType arg0, ServletContext arg1, JarScannerCallback arg2) {
	}

	@Override
	public void setJarScanFilter(JarScanFilter arg0) {
	}
	
}