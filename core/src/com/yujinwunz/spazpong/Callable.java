package com.yujinwunz.spazpong;

// Replaces java.concurrent.util.Callable for gwt to work.
public interface Callable<V> {
	/**
	 * Computes a result, or throws an exception if unable to do so.
	 *
	 * @return computed result
	 * @throws Exception if unable to compute a result
	 */
	V call() throws Exception;
}
