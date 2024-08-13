package com.vulinh.utils;

/**
 * A universal interface for class that supports identification (by providing an ID value to be read
 * by using <code>getId()</code> method).
 *
 * @param <I> The data type of ID
 */
public interface Identifiable<I> {

  I getId();
}
