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
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class ProjectHelperImportFromSourceTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        String PROJECT_NAME = "ImportFromSourceTest"; //$NON-NLS-1$
        IPath EDIT_FILE_PATH = new Path(PROJECT_NAME + "/projectroot.txt"); //$NON-NLS-1$

        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        projectHelper.importExistingProjectFromSource(ui, PlatformActivator.getDefault(), new Path("resources/testfiles/" + PROJECT_NAME), PROJECT_NAME); //$NON-NLS-1$

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.openFile(ui, EDIT_FILE_PATH);

        IEditorHelper editorHelper = EclipseHelperFactory.getEditorHelper();
        editorHelper.insertString(ui,
                                  "EDITED", //$NON-NLS-1$
                                  EDIT_FILE_PATH,
                                  "root.txt", //$NON-NLS-1$
                                  IEditorHelper.Placement.AFTER);

        resources.verifyFileByLine(ui, PlatformActivator.getDefault(), new Path("resources/expected/ImportFromSourceTest/projectroot.txt"), //$NON-NLS-1$
                                   EDIT_FILE_PATH);
    }
}
