package org.wtc.eclipse.platform.util;

import junit.framework.TestCase;

import org.wtc.eclipse.platform.PlatformActivator;

/**
 * An exception handling service.
 * <p/>
 * This service should be used to handle exceptions that occur in helper method calls.
 * <p/>
 * Ultimately this may be made pluggable (to enable swapping exception policies
 * in and out w/o re-compiling.
 * 
 * @since 3.8.0
 */
public class ExceptionHandler {

	/**
	 * Handle the given exception using the current exception handling policy.
	 * @param t - the exception to handle
	 * @since 3.8.0
	 */
	public static void handle(Throwable t) {
        PlatformActivator.logException(t);
        TestCase.fail(t.getLocalizedMessage());
	}

	/**
	 * Handle the given exception using the current exception handling policy.
	 * @param t - the exception to handle
	 * @param msgPreamble - the preamble to the error message
	 * @since 3.8.0
	 */
	public static void handle(Throwable t, String msgPreamble) {
		PlatformActivator.logException(t);
        TestCase.fail(msgPreamble + t.getLocalizedMessage());
	}
	
	
}
