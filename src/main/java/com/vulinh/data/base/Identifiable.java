package com.vulinh.data.base;

import java.io.Serializable;

/**
 * A universal interface for class that supports identification (by providing an ID value to be read
 * by using <code>getId()</code> method).
 *
 * @param <I> The data type of ID
 */
@FunctionalInterface
public interface Identifiable<I> extends Serializable {

  I getId();
}
