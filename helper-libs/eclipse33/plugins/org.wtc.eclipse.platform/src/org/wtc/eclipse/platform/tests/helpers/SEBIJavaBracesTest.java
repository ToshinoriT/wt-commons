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
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IEditorHelper.Placement;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Use the source editor block inserter to verify that a java file's braces are correctly
 * inserted when inserting a block of code.
 */
public class SEBIJavaBracesTest extends EclipseUITest {
    private static final String RESOURCES_PATH = "resources/"; //$NON-NLS-1$
    private static final String EXPECTED_PATH = RESOURCES_PATH + "expected/"; //$NON-NLS-1$
    private static final String TESTFILES_PATH = RESOURCES_PATH + "testfiles/"; //$NON-NLS-1$

    private static final String SEBI_PATH = "sourceEditorBlockInserter/"; //$NON-NLS-1$
    private static final String SEBI_TESTFILES_PATH = TESTFILES_PATH + SEBI_PATH;
    private static final String SEBI_EXPECTED_PATH = EXPECTED_PATH + SEBI_PATH;

    private static final String INSERT_FROM_FILE_NAME = "javaBraces.insert"; //$NON-NLS-1$
    private static final String EXPECTED_RESULTS_FILE_NAME = "javaBraces.expected"; //$NON-NLS-1$

    /**
     * Create a project, create a java file, insert some code, diff the results.
     */
    public void testJavaBraces() {
        IUIContext ui = getUI();

        IJavaProjectHelper java = EclipseHelperFactory.getJavaProjectHelper();
        java.createProject(ui, "JavaBracesInserterTest"); //$NON-NLS-1$

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IPath newFile = resources.createSimpleFile(ui,
                                                   new Path("JavaBracesInserterTest"), //$NON-NLS-1$
                                                   "testJavaBraces.java"); //$NON-NLS-1$

        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.insertBlock(ui, PlatformActivator.getDefault(), new Path(SEBI_TESTFILES_PATH + INSERT_FROM_FILE_NAME), newFile, 1, 1);

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        editor.insertString(ui,
                            "\ntry\n{   System.out.println(\"hello\")\ncatch(RuntimeException e)\n{\n   // nothing\n}\n", //$NON-NLS-1$
                            newFile,
                            "setVisitNumber(1);", //$NON-NLS-1$
                            Placement.AFTER);

        resources.verifyFileByLine(ui, PlatformActivator.getDefault(), new Path(SEBI_EXPECTED_PATH + EXPECTED_RESULTS_FILE_NAME), newFile);
    }
}
