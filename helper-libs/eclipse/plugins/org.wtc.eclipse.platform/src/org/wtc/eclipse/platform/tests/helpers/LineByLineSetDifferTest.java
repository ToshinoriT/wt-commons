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
import org.wtc.eclipse.platform.util.diff.LineByLineSetDiffer;
import java.io.File;

/**
 * LineByLineSetDifferTest - Unit tests for the LineByLineSetDiffer.
 */
public class LineByLineSetDifferTest extends EclipseUITest {
    private static final String RESOURCES_PATH = "resources/testfiles/lineSetDiffer/"; //$NON-NLS-1$

    /**
     * diffFiles - Execute a LineByLineSet diff on the given file.
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI generated input
     * @param  testFile  - RESOURCES_PATH-relative path to the test file
     */
    private void diffFiles(IUIContext ui, String testFile) throws DifferenceException {
        IPath expectedPath = new Path(RESOURCES_PATH + testFile);
        IPath actualPath = new Path(RESOURCES_PATH + "MyFile.txt"); //$NON-NLS-1$

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();

        File file1 = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), expectedPath);
        File file2 = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), actualPath);

        new LineByLineSetDiffer().compare(ui, file1, file2);
    }

    /**
     * testDifferentElementsFile - Files with the same length but different lines should
     * throw an exception.
     */
    public void testDifferentElementsFile() {
        try {
            IUIContext ui = getUI();
            diffFiles(ui, "DiffElementsFile.txt"); //$NON-NLS-1$
            fail("We expected an exception because the files contain different elements."); //$NON-NLS-1$
        } catch (DifferenceException de) {
            // Do nothing. We expected an exception
        }
    }

    /**
     * testDifferentLengthFile - Files of different lengths should throw an exception.
     */
    public void testDifferentLengthFile() {
        try {
            IUIContext ui = getUI();
            diffFiles(ui, "DiffLengthFile.txt"); //$NON-NLS-1$

            fail("We expected an exception because the files are different lengths."); //$NON-NLS-1$
        } catch (DifferenceException de) {
            // Do nothing. We expected an exception
        }
    }

    /**
     * testIdenticalFile - Identical files should not throw an exception.
     */
    public void testIdenticalFile() {
        try {
            IUIContext ui = getUI();
            diffFiles(ui, "IdenticalFile.txt"); //$NON-NLS-1$
        } catch (DifferenceException lde) {
            PlatformActivator.logException(lde);
            fail(lde.getLocalizedMessage());
        }
    }

    /**
     * testNoCharactersFile - A empty file should throw an exception.
     */
    public void testNoCharactersFile() {
        try {
            IUIContext ui = getUI();
            diffFiles(ui, "NoCharFile.txt"); //$NON-NLS-1$
            fail("We expected an exception because one file has no elements and the other does."); //$NON-NLS-1$
        } catch (DifferenceException de) {
            // Do nothing. We expected an exception
        }
    }

}
