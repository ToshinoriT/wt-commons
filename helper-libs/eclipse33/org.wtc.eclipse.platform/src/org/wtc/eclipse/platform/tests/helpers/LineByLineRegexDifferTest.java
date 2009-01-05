/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import org.wtc.eclipse.platform.util.diff.DifferenceException;
import org.wtc.eclipse.platform.util.diff.LineByLineRegexDiffer;
import org.wtc.eclipse.platform.util.diff.LineDifferenceException;
import java.io.File;

/**
 * LineByLineRegexDifferTest - Unit tests for the LineByLineRegexDiffer.
 */
public class LineByLineRegexDifferTest extends EclipseUITest {
    private static final String RESOURCES_PATH = "resources/testfiles/regexDiffer/"; //$NON-NLS-1$

    /**
     * doTestFiles - Perform a diff constructing the actual and expected files from the
     * given path.
     *
     * @param   ui            - Driver for UI generated input
     * @param   expectedFile  - RESOURCES_PATH-relative path to test file
     * @throws  LineDifferenceException  - When at least one line in the actual file does
     *                                   not match the pattern of the same line (by line
     *                                   number) in the baseline file
     */
    private void doTestFiles(IUIContext ui, String expectedFile) throws DifferenceException {
        IPath expectedPath = new Path(RESOURCES_PATH + expectedFile);
        IPath actualPath = new Path(RESOURCES_PATH + "actual.txt"); //$NON-NLS-1$

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();

        File expected = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), expectedPath);
        File actual = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), actualPath);

        new LineByLineRegexDiffer().compare(ui, expected, actual);
    }

    /**
     * testDifferenceNoRegex - A difference in a line that has no regex should throw an
     * exception.
     */
    public void testDifferenceNoRegex() {
        try {
            IUIContext ui = getUI();
            doTestFiles(ui, "expected.difference.txt"); //$NON-NLS-1$

            fail("We expected an expection because the files are different"); //$NON-NLS-1$
        } catch (DifferenceException lde) {
            // Do nothing. We expected an exception
        }
    }

    /**
     * testDifferenceRegex - A difference in a line that has regex should throw an
     * exception.
     */
    public void testDifferenceRegex() {
        try {
            IUIContext ui = getUI();
            doTestFiles(ui, "expected.differenceRegex.txt"); //$NON-NLS-1$

            fail("We expected an expection because the files are different"); //$NON-NLS-1$
        } catch (DifferenceException lde) {
            // Do nothing. We expected an exception
        }
    }

    /**
     * testIdentical - Identical files should not throw an exception.
     */
    public void testIdentical() {
        try {
            IUIContext ui = getUI();
            doTestFiles(ui, "expected.identical.txt"); //$NON-NLS-1$
        } catch (DifferenceException lde) {
            PlatformActivator.logException(lde);
            fail(lde.getLocalizedMessage());
        }
    }

    /**
     * testMatchesRegex - Regex matches should not throw an exception.
     */
    public void testMatchesRegex() {
        try {
            IUIContext ui = getUI();
            doTestFiles(ui, "expected.matchesRegex.txt"); //$NON-NLS-1$
        } catch (DifferenceException lde) {
            PlatformActivator.logException(lde);
            fail(lde.getLocalizedMessage());
        }
    }
}
