/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class WorkbenchHelperCloseActivePerspectiveTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        IWorkbenchHelper workbenchHelper = EclipseHelperFactory.getWorkbenchHelper();
        workbenchHelper.openPerspective(ui, IWorkbenchHelper.Perspective.DEBUG);
        workbenchHelper.closeActivePerspective(ui);
    }
}
