/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.reset;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Create a project, create a file, edit the file w/o the SEBI finish the test. The
 * project reset daemon should handle the saving of any dirty editors in the workspace
 */
public class SaveNeededResetDaemonTest extends EclipseUITest {
    /**
     * Do that stuff I said above.
     */
    public void testDirtyEditorOnComplete() {
        IUIContext ui = getUI();

        String projectName = "SaveNeededResetDaemonTest"; //$NON-NLS-1$

        ISimpleProjectHelper project = EclipseHelperFactory.getSimpleProjectHelper();
        project.createProject(ui, projectName);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createSimpleFile(ui, new Path(projectName), "testFile.txt"); //$NON-NLS-1$

        // Just start firing off some key strokes
        ui.enterText("This is some output to make put the editor into a dirty state"); //$NON-NLS-1$

        // OOPS! Quitting the test w/o saving! Make sure we can clean up...
    }
}
