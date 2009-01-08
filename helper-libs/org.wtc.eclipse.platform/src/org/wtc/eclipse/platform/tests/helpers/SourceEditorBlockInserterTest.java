/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import junit.framework.AssertionFailedError;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IEditorHelper.Placement;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import org.wtc.eclipse.platform.util.SourceEditorBlockInserter;

/**
 * SourceEditorBlockInserterTest - Unit tests for the SourceEditorBlockInserter.
 */
public class SourceEditorBlockInserterTest extends EclipseUITest {
    private static final String RESOURCES_PATH = "resources/"; //$NON-NLS-1$
    private static final String EXPECTED_PATH = RESOURCES_PATH + "expected/"; //$NON-NLS-1$
    private static final String TESTFILES_PATH = RESOURCES_PATH + "testfiles/"; //$NON-NLS-1$

    private static final String SEBI_PATH = "sourceEditorBlockInserter/"; //$NON-NLS-1$
    private static final String SEBI_TESTFILES_PATH = TESTFILES_PATH + SEBI_PATH;
    private static final String SEBI_EXPECTED_PATH = EXPECTED_PATH + SEBI_PATH;

    private static final String PROJECT_NAME = "SourceEditorBlockInserterTestProject"; //$NON-NLS-1$

    private static final String FOLDER_NAME = "src"; //$NON-NLS-1$

    private static final String INSERT_FROM_FILE_NAME_3 = "test3.insert"; //$NON-NLS-1$
    private static final String INSERT_FROM_FILE_NAME_4 = "test4.insert"; //$NON-NLS-1$
    private static final String INSERT_FROM_FILE_NAME_5 = "test5.insert"; //$NON-NLS-1$
    private static final String INSERT_FROM_FILE_NAME_6 = "test6.insert"; //$NON-NLS-1$
    private static final String INSERT_FROM_FILE_NAME_7 = "test7.insert"; //$NON-NLS-1$
    private static final String INSERT_FROM_FILE_NAME_8 = "test8.insert"; //$NON-NLS-1$

    private static final String INSERT_TO_FILE_NAME = "insertBlockWithInts.txt"; //$NON-NLS-1$

    private static final String EXPECTED_RESULTS_FILE_NAME = "expectedResults.txt"; //$NON-NLS-1$

    private static final String SEARCH_TEXT = "LINE ONE OF TEXT"; //$NON-NLS-1$

    /**
     * Test different insertBlock methods.
     */
    public void testInsertBlock() {
        IUIContext ui = getUI();

        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, PROJECT_NAME);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();

        IPath insertFromFilePath3 = new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME_3);
        IPath insertFromFilePath4 = new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME_4);
        IPath insertFromFilePath5 = new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME_5);
        IPath insertFromFilePath6 = new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME_6);
        IPath insertFromFilePath7 = new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME_7);
        IPath insertFromFilePath8 = new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME_8);

        Plugin plugin = PlatformActivator.getDefault();

        IPath toFileFolderPath = new Path(PROJECT_NAME + "/" + FOLDER_NAME); //$NON-NLS-1$
        IPath toFilePath = toFileFolderPath.append(new Path(INSERT_TO_FILE_NAME));

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createSimpleFile(ui, toFileFolderPath, INSERT_TO_FILE_NAME);

        IEditorHelper editorHelper = EclipseHelperFactory.getEditorHelper();

        // Test 1: Expected to fail: Test 0 as the line number
        boolean failFlag = false;

        try {
            sebi.insertBlock(ui,
                             editorHelper,
                             plugin,
                             insertFromFilePath3,
                             toFilePath,
                             0,
                             0);
            failFlag = true;
            fail("We expected an assert because the line number must be at least 1."); //$NON-NLS-1$
        } catch (AssertionFailedError ae) {
            if (failFlag == false) {
                // do nothing. the test passes
            } else {
                PlatformActivator.logException(ae);
                fail(ae.getLocalizedMessage());
            }
        }

        // Test 2: Expected to fail: Test 0 as the column number
        try {
            sebi.insertBlock(ui,
                             editorHelper,
                             plugin,
                             insertFromFilePath3,
                             toFilePath,
                             1,
                             0);
            failFlag = true;
            fail("We expected an assert because the column number must be at least 1."); //$NON-NLS-1$
        } catch (AssertionFailedError ae) {
            if (failFlag == false) {
                // do nothing. the test passes
            } else {
                PlatformActivator.logException(ae);
                fail(ae.getLocalizedMessage());
            }
        }

        // Test 3: Test line 1 column 1 on a new file
        sebi.insertBlock(ui,
                         editorHelper,
                         plugin,
                         insertFromFilePath3,
                         toFilePath,
                         1,
                         1);

        // Test 4: Start from the 4th line in the 10th column
        sebi.insertBlock(ui,
                         editorHelper,
                         plugin,
                         insertFromFilePath4,
                         toFilePath,
                         4,
                         10);

        // Test 5: Find a String and insert text before it
        sebi.insertBlock(ui,
                         editorHelper,
                         plugin,
                         insertFromFilePath5,
                         toFilePath,
                         SEARCH_TEXT,
                         Placement.BEFORE);

        // Test 6: Find a String and insert text before it
        sebi.insertBlock(ui,
                         editorHelper,
                         plugin,
                         insertFromFilePath6,
                         toFilePath,
                         SEARCH_TEXT,
                         Placement.AFTER);

        // Test 7: Find a String and insert text instead of it
        sebi.insertBlock(ui,
                         editorHelper,
                         plugin,
                         insertFromFilePath7,
                         toFilePath,
                         SEARCH_TEXT,
                         Placement.INSTEADOF);

        // Test 8: Add tab, braces, quotes, backslashes, end tags
        sebi.insertBlock(ui,
                         editorHelper,
                         plugin,
                         insertFromFilePath8,
                         toFilePath,
                         1,
                         1);

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        resources.verifyFileByLine(ui,
                                   plugin,
                                   new Path(SEBI_EXPECTED_PATH + EXPECTED_RESULTS_FILE_NAME),
                                   toFilePath);
    }

}
