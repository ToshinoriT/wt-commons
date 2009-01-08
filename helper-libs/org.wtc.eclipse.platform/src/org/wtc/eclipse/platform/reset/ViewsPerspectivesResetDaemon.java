/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.reset;

import com.windowtester.runtime.IUIContext;
import com.windowtester.swt.util.WaitForIdle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.wtc.eclipse.core.reset.IResetDaemon;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.PerspectiveActiveCondition;
import org.wtc.eclipse.platform.helpers.IPerspective;
import org.wtc.eclipse.platform.internal.reset.ViewsPerspectiveResetRegistry;

/**
 * Reset daemon that makes sure all perspectives (and therefore views) have been reset.
 */
public class ViewsPerspectivesResetDaemon implements IResetDaemon {
    /**
     * @see  org.wtc.eclipse.core.reset.IResetDaemon#resetWorkspace(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.core.reset.IResetDaemon.ResetContext)
     */
    public void resetWorkspace(IUIContext ui, ResetContext context) {
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    // Close everything
                    if (window != null) {
                        IWorkbenchPage[] pages = window.getPages();

                        if (pages != null) {
                            for (IWorkbenchPage nextPage : pages) {
                                nextPage.closeAllPerspectives(true, true);
                            }
                        }
                    }
                }
            });

        final IPerspective defaultPerspective =
            ViewsPerspectiveResetRegistry.getDefaultPerspective();
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    // Now re-open the default perspective
                    try {
                        PlatformUI.getWorkbench().showPerspective(defaultPerspective.getID(), window);
                    } catch (WorkbenchException we) {
                        PlatformActivator.logException(we);
                    }
                }
            });

        // Clear the event queue
        new WaitForIdle().waitForIdle();
        ui.wait(new PerspectiveActiveCondition(defaultPerspective));
    }
}
