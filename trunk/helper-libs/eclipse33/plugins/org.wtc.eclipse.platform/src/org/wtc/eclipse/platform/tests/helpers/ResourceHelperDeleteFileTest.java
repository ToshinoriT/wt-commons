/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class ResourceHelperDeleteFileTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        String PROJECT_NAME = "DeleteFileTest"; //$NON-NLS-1$
        IPath FOLDER_PATH = new Path(PROJECT_NAME + "/newFolder"); //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, PROJECT_NAME);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createFolder(ui, FOLDER_PATH);

        IPath FILE_PATH = resources.createSimpleFile(ui, FOLDER_PATH, "fileToDelete.txt"); //$NON-NLS-1$
        resources.deleteFileOrFolder(ui, FILE_PATH);
    }
}
