/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Test the cleanAllProjects method.
 */
public class WorkbenchHelperCleanAllProjTest extends EclipseUITest {
    /**
     * Create a few projects. Clean them
     */
    public void testCleanAllProjects() {
        IUIContext ui = getUI();

        IJavaProjectHelper javaProject = EclipseHelperFactory.getJavaProjectHelper();
        javaProject.createProject(ui, "JavaProject1"); //$NON-NLS-1$
        javaProject.createProject(ui, "JavaProject2"); //$NON-NLS-1$

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        workbench.cleanAllProjects(ui);
    }
}
