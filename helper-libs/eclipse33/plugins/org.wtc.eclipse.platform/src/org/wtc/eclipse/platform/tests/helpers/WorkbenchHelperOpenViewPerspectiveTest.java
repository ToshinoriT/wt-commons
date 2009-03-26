/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper.Perspective;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper.View;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Unit tests for WorkbenchHelperImpl methods.
 */
public class WorkbenchHelperOpenViewPerspectiveTest extends EclipseUITest {
    /**
     * testOpenView - Verify that opening each view does not throw test failures.
     */
    public void testOpenView() {
        IUIContext ui = getUI();
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();

        Perspective[] allPerspectives = Perspective.values();

        for (Perspective nextPerspective : allPerspectives) {
            workbench.openPerspective(ui, nextPerspective);
        }

        View[] allViews = View.values();

        for (View nextView : allViews) {
            workbench.openView(ui, nextView);

        }
    }
}
