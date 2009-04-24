package org.wtc.eclipse.platform.util;

import junit.framework.TestCase;

/**
 * Utility for argument precondition testing.
 *
 * @since 3.8.0
 */
public class Arguments {

	/**
	 * Assert that all args are non-null.
	 * @since 3.8.0
	 */
	public static void assertNotNull(Object ... args) {
		for (Object arg : args) {
			TestCase.assertNotNull(arg);
		}
	}
	
	
}
