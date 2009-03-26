/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import org.wtc.eclipse.platform.util.diff.DifferenceException;
import org.wtc.eclipse.platform.util.diff.FileBlockDiffer;
import org.wtc.eclipse.platform.util.diff.FileBlockDiffer.BlockDifferenceFoundException;
import org.wtc.eclipse.platform.util.diff.FileBlockDiffer.BlockNotFoundException;
import java.io.File;

/**
 * FileBlockDifferTest - Unit tests for the FileBlockDiffer.
 */
public class FileBlockDifferTest extends EclipseUITest {
    private static final String FILEPATH_TESTFILES = "resources/testfiles/FileBlockDifferTest/"; //$NON-NLS-1$
    private static final String FILEPATH_BLOCK = FILEPATH_TESTFILES + "blockDiffer.block.txt"; //$NON-NLS-1$
    private static final String FILEPATH_DIFFERENCE = FILEPATH_TESTFILES
        + "blockDiffer.difference.txt"; //$NON-NLS-1$
    private static final String FILEPATH_FOUND = FILEPATH_TESTFILES + "blockDiffer.found.txt"; //$NON-NLS-1$
    private static final String FILEPATH_NOTFOUND = FILEPATH_TESTFILES + "blockDiffer.notFound.txt"; //$NON-NLS-1$

    /**
     * testBlockDifference - A block that is found but contains differences should throw
     * an exception.
     */
    public void testBlockDifference() {
        IUIContext ui = getUI();

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File blockFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), new Path(FILEPATH_BLOCK));

        File testFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), new Path(FILEPATH_DIFFERENCE));

        try {
            new FileBlockDiffer().compare(ui, blockFile, testFile);

            // If we got to here, then the differ said that the block
            // exists but we expected a not found exception
            fail("Expected the block to not be found"); //$NON-NLS-1$
        }
        // The block is supposed to have differences
        catch (DifferenceException de) {
            if (!(de instanceof BlockDifferenceFoundException)) {
                PlatformActivator.logException(de);
                fail(de.getMessage());
            }
        }
    }

    /**
     * testBlockFound - A block in a file should not throw exceptions.
     */
    public void testBlockFound() {
        IUIContext ui = getUI();

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File blockFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), new Path(FILEPATH_BLOCK));

        File testFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), new Path(FILEPATH_FOUND));

        try {
            new FileBlockDiffer().compare(ui, blockFile, testFile);
        }
        // Any exception is a failure
        catch (Exception e) {
            PlatformActivator.logException(e);
            fail(e.getMessage());
        }
    }

    /**
     * testBlockNotFound - A block that is not found in a file should throw an exception.
     */
    public void testBlockNotFound() {
        IUIContext ui = getUI();

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File blockFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), new Path(FILEPATH_BLOCK));

        File testFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), new Path(FILEPATH_NOTFOUND));

        try {
            new FileBlockDiffer().compare(ui, blockFile, testFile);

            // If we got to here, then the differ said that the block
            // exists but we expected a not found exception
            fail("Expected the block to not be found"); //$NON-NLS-1$
        }
        // The block is supposed to be not found
        catch (DifferenceException de) {
            if (!(de instanceof BlockNotFoundException)) {
                PlatformActivator.logException(de);
                fail(de.getMessage());
            }
        }

    }
}
