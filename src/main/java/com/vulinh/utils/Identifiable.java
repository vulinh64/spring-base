package com.vulinh.utils;

import java.io.Serializable;

/**
 * A universal interface for class that supports identification (by providing an ID value to be read
 * by using <code>getId()</code> method).
 *
 * @param <I> The data type of ID
 */
public interface Identifiable<I> extends Serializable {

  I getId();
}
