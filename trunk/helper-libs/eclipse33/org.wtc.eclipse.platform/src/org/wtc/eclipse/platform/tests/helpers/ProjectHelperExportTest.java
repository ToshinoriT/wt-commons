/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Smoke test the export as archive method.
 */
public class ProjectHelperExportTest extends EclipseUITest {
    /**
     * Smoke test the export as archive method.
     */
    public void testExport() {
        IUIContext ui = getUI();

        java.lang.String PROJECT_NAME_1 = "SimpleProject1"; //$NON-NLS-1$
        java.lang.String PROJECT_NAME_2 = "SimpleProject2"; //$NON-NLS-1$
        java.lang.String PROEJCT_NAME_3 = "JavaProject"; //$NON-NLS-1$
        java.lang.String[] EXPORT_LIST = {
                PROJECT_NAME_1,
                PROEJCT_NAME_3
            };

        ISimpleProjectHelper simpleProject = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProject.createProject(ui, PROJECT_NAME_1);
        simpleProject.createProject(ui, PROJECT_NAME_2);

        IJavaProjectHelper javaHelper = EclipseHelperFactory.getJavaProjectHelper();
        javaHelper.createProject(ui, PROEJCT_NAME_3);

        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        projectHelper.exportProjectsToArchive(ui, EXPORT_LIST, "exportTestArchive"); //$NON-NLS-1$
    }
}
