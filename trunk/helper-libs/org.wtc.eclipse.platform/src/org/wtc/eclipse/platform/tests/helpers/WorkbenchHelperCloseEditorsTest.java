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
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class WorkbenchHelperCloseEditorsTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        java.lang.String PROJECT_NAME = "CloseEditorsProject"; //$NON-NLS-1$
        java.lang.String INSERT = "This is a value that makes the editor dirty"; //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, PROJECT_NAME);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IPath file1 = resources.createSimpleFile(ui, new Path(PROJECT_NAME), "File1.txt"); //$NON-NLS-1$

        IEditorHelper editorHelper = EclipseHelperFactory.getEditorHelper();
        editorHelper.insertString(ui, INSERT, file1, 1, 1, false);

        IWorkbenchHelper workbenchHelper = EclipseHelperFactory.getWorkbenchHelper();
        workbenchHelper.closeActiveEditor(ui);

        IPath file2 = resources.createSimpleFile(ui, new Path(PROJECT_NAME), "File2.txt"); //$NON-NLS-1$
        editorHelper.insertString(ui, INSERT, file2, 1, 1, false);

        IPath file3 = resources.createSimpleFile(ui, new Path(PROJECT_NAME), "File3.txt"); //$NON-NLS-1$
        editorHelper.insertString(ui, INSERT, file3, 1, 1, false);

        workbenchHelper.closeAllEditors(ui);
    }
}
