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
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Smoke test of the helper methods that verify a file's updated state.
 */
public class ResourceHelperVerifyFileUpdatedTest extends EclipseUITest {
    /**
     * Create a file, check the timestamp, do something, verify no updates, edit the file,
     * verify an update occurred.
     */
    public void testVerifyFileUpdated() {
        IUIContext ui = getUI();

        String projectName = "TestVerifyFileUpdated"; //$NON-NLS-1$
        String folderName = "testfolder"; //$NON-NLS-1$
        String fileName = "testfile.txt"; //$NON-NLS-1$

        ISimpleProjectHelper simpleProject = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProject.createProject(ui, projectName);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IPath folderPath = new Path(projectName + "/" + folderName); //$NON-NLS-1$
        resources.createFolder(ui, folderPath);

        IPath filePath = resources.createSimpleFile(ui, folderPath, fileName);

        long ts1 = resources.getFileTimestamp(ui, filePath);

        // Just here to verify that some time passes
        ui.pause(1000);

        resources.verifyFileUpdated(ui, filePath, ts1, false);

        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.insertString(ui, "edit1", filePath, 1, 1, true); //$NON-NLS-1$

        resources.verifyFileUpdated(ui, filePath, ts1, true);
    }
}
