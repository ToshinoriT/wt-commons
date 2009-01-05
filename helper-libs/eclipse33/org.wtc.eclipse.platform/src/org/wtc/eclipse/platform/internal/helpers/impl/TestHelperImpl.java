/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import junit.framework.TestCase;
import org.wtc.eclipse.platform.helpers.ITestHelper;

/**
 * Helper for injecting JUnit behavior into a test directly.
 */
public class TestHelperImpl extends HelperImplAdapter implements ITestHelper {
    /**
     * @see  org.wtc.eclipse.platform.helpers.ITestHelper#failTest(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void failTest(IUIContext ui, String message) {
        TestCase.fail(message);
    }
}
