/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import java.io.File;

/**
 * Tests for creating Simple Projects.
 */
public class ProjectHelperSimpleTest extends EclipseUITest {
    /**
     * Test that we can delete a project.
     */
    public void testSimpleCreateDelete() {
        IUIContext ui = getUI();

        String projectName = "SimpleProjectCrDelTest"; //$NON-NLS-1$
        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, projectName);

        // Not needed. Just want to see the project in the workspace
        // when watching
        ui.pause(1000);

        simple.deleteProject(ui, projectName);
    }

    /**
     * Test that we can delete a project.
     */
    public void testSimpleCreateDeleteContext() {
        IUIContext ui = getUI();

        String projectName = "SimpleProjectCrDelTest"; //$NON-NLS-1$
        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, projectName);

        // Not needed. Just want to see the project in the workspace
        // when watching
        ui.pause(1000);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IFile file = resources.getFileFromWorkspace(ui, new Path(projectName + "/.project")); //$NON-NLS-1$
        resources.verifyFileExists(ui, new Path(projectName + "/.project"), true); //$NON-NLS-1$
        final File rawFile = file.getLocation().toFile();
        assertTrue(rawFile.exists());

        simple.deleteProject(ui, projectName, true);

        ui.wait(new ICondition() {
                public boolean test() {
                    return !rawFile.exists();
                }

            });
    }

    /**
     * testSimpleCreation - Simple project creation with just a name.
     */
    public void testSimpleCreation() {
        IUIContext ui = getUI();

        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, "SimpleProjectCreateTest"); //$NON-NLS-1$
    }
}
