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
public class ResourceHelperMoveFileTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        String PROJECT_NAME = "ResourceHelperMoveFileTest"; //$NON-NLS-1$
        IPath FOLDER1 = new Path(PROJECT_NAME + "/folder1"); //$NON-NLS-1$
        IPath FOLDER2 = new Path(PROJECT_NAME + "/folder2"); //$NON-NLS-1$
        IPath FOLDER3 = new Path(PROJECT_NAME + "/folder3"); //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, PROJECT_NAME);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createFolder(ui, FOLDER1);
        resources.createFolder(ui, FOLDER2);
        resources.createFolder(ui, FOLDER3);

        IPath SIMPLE_FILE = resources.createSimpleFile(ui, FOLDER1, "moveTest.txt"); //$NON-NLS-1$
        resources.createSimpleFile(ui, FOLDER3, "moveTest.txt"); //$NON-NLS-1$

        IPath NEW_SIMPLE_FILE = resources.moveFile(ui, SIMPLE_FILE, FOLDER2);

        resources.moveFile(ui, NEW_SIMPLE_FILE, FOLDER3);
    }
}
