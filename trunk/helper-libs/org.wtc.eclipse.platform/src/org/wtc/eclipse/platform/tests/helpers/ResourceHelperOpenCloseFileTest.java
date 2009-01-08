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
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class ResourceHelperOpenCloseFileTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        String PROJECT_NAME = "OpenCloseFileTest"; //$NON-NLS-1$
        String FILENAME1 = "testfile1.txt"; //$NON-NLS-1$
        String FILENAME2 = "testfile2.txt"; //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, PROJECT_NAME);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IPath FILE1_PATH = resources.createSimpleFile(ui, new Path(PROJECT_NAME), FILENAME1);
        IPath FILE2_PATH = resources.createSimpleFile(ui, new Path(PROJECT_NAME), FILENAME2);

        resources.closeFile(ui, FILE1_PATH);
        resources.openFile(ui, FILE1_PATH);

        IWorkbenchHelper workbenchHelper = EclipseHelperFactory.getWorkbenchHelper();
        workbenchHelper.closeAllEditors(ui);

        resources.closeFile(ui, FILE2_PATH);
        resources.openFile(ui, FILE2_PATH);
    }
}
