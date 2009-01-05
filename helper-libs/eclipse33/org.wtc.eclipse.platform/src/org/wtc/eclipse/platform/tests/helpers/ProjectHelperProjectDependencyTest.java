/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

import com.windowtester.runtime.IUIContext;

/**
 * Smoke test for setting project build paths.
 */
public class ProjectHelperProjectDependencyTest extends EclipseUITest {
    /**
     * Test that a java project can depend on another project.
     */
    public void testSetProjectBuildPathDependency() {
        IUIContext ui = getUI();

        IJavaProjectHelper java = EclipseHelperFactory.getJavaProjectHelper();

        String project1 = "project1"; //$NON-NLS-1$
        String project2 = "project2"; //$NON-NLS-1$

        java.createProject(ui, project1);
        java.createProject(ui, project2);

        // import files such that project 1 files depend on project 2
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();

        String importsRoot = "resources/testfiles/ProjectHelperProjectDependencyTest/"; //$NON-NLS-1$
        resources.importFiles(ui, PlatformActivator.getDefault(), new Path(importsRoot + project1), new Path(project1 + "/src")); //$NON-NLS-1$

        resources.importFiles(ui, PlatformActivator.getDefault(), new Path(importsRoot + project2), new Path(project2 + "/src")); //$NON-NLS-1$

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        java.addProjectBuildDependency(ui,
                                       project1,
                                       project2);

        workbench.saveAndWait(ui);

        workbench.verifyBuild(ui, project1);
    }
}
