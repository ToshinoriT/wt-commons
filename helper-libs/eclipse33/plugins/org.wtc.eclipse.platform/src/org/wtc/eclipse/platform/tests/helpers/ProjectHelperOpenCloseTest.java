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
 * DESCRIPTION:
 */
public class ProjectHelperOpenCloseTest extends EclipseUITest {
    /**
     * Open and close a project a bunch of times.
     */
    public void test() {
        IUIContext ui = getUI();

        java.lang.String PROJECT_NAME = "OpenCloseProject"; //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, PROJECT_NAME);

        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        projectHelper.closeProject(ui, PROJECT_NAME);
        projectHelper.openProject(ui, PROJECT_NAME);
        projectHelper.closeProject(ui, PROJECT_NAME);
        projectHelper.closeProject(ui, PROJECT_NAME);
        projectHelper.openProject(ui, PROJECT_NAME);
        projectHelper.openProject(ui, PROJECT_NAME);
    }
}
