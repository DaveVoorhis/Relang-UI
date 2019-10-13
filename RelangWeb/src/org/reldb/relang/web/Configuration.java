package org.reldb.relang.web;

import org.apache.catalina.webresources.StandardRoot;

@FunctionalInterface
public interface Configuration {
	void configure(StandardRoot root);
}
