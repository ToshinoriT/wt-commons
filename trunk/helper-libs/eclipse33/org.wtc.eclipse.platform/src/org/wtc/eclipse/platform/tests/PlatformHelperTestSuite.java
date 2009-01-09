/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.wtc.eclipse.core.tests.ManagedTestSuite;
import org.wtc.eclipse.platform.tests.helpers.AntHelperTest;
import org.wtc.eclipse.platform.tests.helpers.EditorHelperFindReplaceAllTest;
import org.wtc.eclipse.platform.tests.helpers.EditorHelperGoToLineTest;
import org.wtc.eclipse.platform.tests.helpers.FileBlockDifferTest;
import org.wtc.eclipse.platform.tests.helpers.JavaHelperAddClassFolderTest;
import org.wtc.eclipse.platform.tests.helpers.JavaHelperBuildPathJAROpsTest;
import org.wtc.eclipse.platform.tests.helpers.JavaHelperCreateClassTest;
import org.wtc.eclipse.platform.tests.helpers.JavaHelperCreatePackageTest;
import org.wtc.eclipse.platform.tests.helpers.JavaHelperCreateSourceFolderTest;
import org.wtc.eclipse.platform.tests.helpers.LineByLineRegexDifferTest;
import org.wtc.eclipse.platform.tests.helpers.LineByLineSetDifferTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperDeleteTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperExportTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperExportToArchiveTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperImportFromArchiveTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperImportFromSourceTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperJavaTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperOpenCloseTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperProjectDependencyTest;
import org.wtc.eclipse.platform.tests.helpers.ProjectHelperSimpleTest;
import org.wtc.eclipse.platform.tests.helpers.ResourceHelperCreateSimpleFileTest;
import org.wtc.eclipse.platform.tests.helpers.ResourceHelperDeleteFileTest;
import org.wtc.eclipse.platform.tests.helpers.ResourceHelperGetFileFormPluginTest;
import org.wtc.eclipse.platform.tests.helpers.ResourceHelperMoveFileTest;
import org.wtc.eclipse.platform.tests.helpers.ResourceHelperOpenCloseFileTest;
import org.wtc.eclipse.platform.tests.helpers.ResourceHelperVerifyFileUpdatedTest;
import org.wtc.eclipse.platform.tests.helpers.SEBIJavaBracesTest;
import org.wtc.eclipse.platform.tests.helpers.StringExistsDifferTest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperCleanAllProjTest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperCloseActivePerspectiveTest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperCloseEditorsTest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperOpenViewPerspectiveTest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperSwitchPerspectiveDialogTest;
import org.wtc.eclipse.platform.tests.helpers.WorkbenchHelperVerifyMarkersTest;

/**
 * Test all helpers.
 */
public class PlatformHelperTestSuite extends EclipseUITest {
    /**
     * Test all helpers.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(new ManagedTestSuite(AntHelperTest.class));
        suite.addTest(new ManagedTestSuite(EditorHelperFindReplaceAllTest.class));
        suite.addTest(new ManagedTestSuite(EditorHelperGoToLineTest.class));
        suite.addTest(new ManagedTestSuite(FileBlockDifferTest.class));
        suite.addTest(new ManagedTestSuite(JavaHelperAddClassFolderTest.class));
        suite.addTest(new ManagedTestSuite(JavaHelperBuildPathJAROpsTest.class));
        suite.addTest(new ManagedTestSuite(JavaHelperCreateClassTest.class));
        suite.addTest(new ManagedTestSuite(JavaHelperCreatePackageTest.class));
        suite.addTest(new ManagedTestSuite(JavaHelperCreateSourceFolderTest.class));
        suite.addTest(new ManagedTestSuite(LineByLineRegexDifferTest.class));
        suite.addTest(new ManagedTestSuite(LineByLineSetDifferTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperDeleteTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperExportTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperExportToArchiveTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperImportFromArchiveTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperImportFromSourceTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperJavaTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperOpenCloseTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperProjectDependencyTest.class));
        suite.addTest(new ManagedTestSuite(ProjectHelperSimpleTest.class));
        suite.addTest(new ManagedTestSuite(ResourceHelperCreateSimpleFileTest.class));
        suite.addTest(new ManagedTestSuite(ResourceHelperDeleteFileTest.class));
        suite.addTest(new ManagedTestSuite(ResourceHelperGetFileFormPluginTest.class));
        suite.addTest(new ManagedTestSuite(ResourceHelperMoveFileTest.class));
        suite.addTest(new ManagedTestSuite(ResourceHelperOpenCloseFileTest.class));
        suite.addTest(new ManagedTestSuite(ResourceHelperVerifyFileUpdatedTest.class));
        suite.addTest(new ManagedTestSuite(SEBIJavaBracesTest.class));
        suite.addTest(new ManagedTestSuite(StringExistsDifferTest.class));
        suite.addTest(new ManagedTestSuite(WorkbenchHelperCleanAllProjTest.class));
        suite.addTest(new ManagedTestSuite(WorkbenchHelperCloseActivePerspectiveTest.class));
        suite.addTest(new ManagedTestSuite(WorkbenchHelperCloseEditorsTest.class));
        suite.addTest(new ManagedTestSuite(WorkbenchHelperOpenViewPerspectiveTest.class));
        suite.addTest(new ManagedTestSuite(WorkbenchHelperSwitchPerspectiveDialogTest.class));
        suite.addTest(new ManagedTestSuite(WorkbenchHelperVerifyMarkersTest.class));

        return suite;
    }

}
