/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Create a project, delete it.
 */
public class ProjectHelperDeleteTest extends EclipseUITest {
    /**
     * test some stuff.
     */
    public void testProjectDelete() {
        IUIContext ui = getUI();

        String projectName = "DeleteProjectTest"; //$NON-NLS-1$

        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, projectName);

        // Not needed just want to see the project
        ui.pause(2000);

        simple.deleteProject(ui, projectName);

        // Not needed just want to see the project
        ui.pause(2000);
    }
}
