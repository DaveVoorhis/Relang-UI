package org.reldb.relang.core.tuples;

import java.io.Serializable;

/**
 * Any class that implements Tuple can be used as a static tuple. Attributes are set via
 * setters, obtained via getters. 
 * 
 * @author dave
 *
 */
public interface Tuple extends Serializable {}
