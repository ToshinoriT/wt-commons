/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Create a project or two, export 'em.
 */
public class ProjectHelperExportToArchiveTest extends EclipseUITest {
    /**
     * Do some testing.
     */
    public void testProjectExport() {
        IUIContext ui = getUI();

        String[] projectNames = { "project1", "project2", "project3" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] projectsToExport = { projectNames[0], projectNames[2] };

        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();

        for (String nextProjectName : projectNames) {
            simple.createProject(ui, nextProjectName);
        }

        IProjectHelper project = EclipseHelperFactory.getProjectHelper();
        project.exportProjectsToArchive(ui, projectsToExport, "exportTest"); //$NON-NLS-1$

    }
}
