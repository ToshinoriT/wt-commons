/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.reset;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.core.reset.IResetDaemon;
import org.wtc.eclipse.platform.handlers.FocusFixingConditionHandler;

/**
 * Reset daemon to reset the focus fixing shell handler condition to the default
 * extension-registered shells.
 */
public class FocusFixingConditionResetDaemon implements IResetDaemon {
    /**
     * @see org.wtc.eclipse.core.reset.IResetDaemon#resetWorkspace(com.windowtester.runtime.IUIContext, org.wtc.eclipse.core.reset.IResetDaemon.ResetContext)
     */
    public void resetWorkspace(IUIContext ui, ResetContext context) {
        FocusFixingConditionHandler.reset();
    }
}
