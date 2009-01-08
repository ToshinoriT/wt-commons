/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.reset;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.wtc.eclipse.core.tests.ManagedTestSuite;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperOpenViewPerspectiveTest;

/**
 * Verify perspectives can be reset.
 */
public class ResetPerspectivesTest extends EclipseUITest {
    private static final int LOOP_COUNT = 2;

    /**
     * Open some views.
     */
    /**
     * Loopify this test.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        for (int i = 0; i < LOOP_COUNT; i++) {
            suite.addTest(new ManagedTestSuite(WorkbenchHelperOpenViewPerspectiveTest.class));
        }

        return suite;
    }

}
