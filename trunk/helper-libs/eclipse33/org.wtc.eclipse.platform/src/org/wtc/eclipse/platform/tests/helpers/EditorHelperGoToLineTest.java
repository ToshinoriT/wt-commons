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
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Quick smoke test of the editor helper "Go to line" support.
 */
public class EditorHelperGoToLineTest extends EclipseUITest {
    /**
     * Create a project, create a file, go to some lines.
     */
    public void testGoToLine() {
        IUIContext ui = getUI();

        String projectName = "EditorHelperGoToLineTest"; //$NON-NLS-1$

        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, projectName);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.importFiles(ui, PlatformActivator.getDefault(), new Path("resources/testfiles/FindReplaceAllTest"), //$NON-NLS-1$
                              new Path(projectName));

        IPath fullFilePath = new Path(projectName + "/testFindReplaceAll.txt"); //$NON-NLS-1$
        resources.openFile(ui, fullFilePath);

        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.gotoLine(ui, 4);
        editor.gotoLine(ui, 1);
    }
}
