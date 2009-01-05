/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.reset;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.core.reset.IResetDaemon;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;

/**
 * Close all editors making sure all editors are saved and closed.
 */
public class CloseAllEditorsResetDaemon implements IResetDaemon {
    /**
     * @see  org.wtc.eclipse.core.reset.IResetDaemon#resetWorkspace(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.core.reset.IResetDaemon.ResetContext)
     */
    public void resetWorkspace(IUIContext ui, ResetContext context) {
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.closeAllEditors(ui, true);
    }

}
