/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.reset;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.wtc.eclipse.core.reset.IResetDaemon;
import org.wtc.eclipse.platform.PlatformActivator;

/**
 * Reset daemon that makes sure that all breakpoints have been removed and suspended
 * processes have been restarted.
 */
public class DebugResetDaemon implements IResetDaemon {
    /**
     * @see  org.wtc.eclipse.core.reset.IResetDaemon#resetWorkspace(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.core.reset.IResetDaemon.ResetContext)
     */
    public void resetWorkspace(IUIContext ui, ResetContext context) {
        new SWTIdleCondition().waitForIdle();

        // First, remove all breakpoints
        IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
        IBreakpoint[] allBreakpoints = breakpointManager.getBreakpoints();

        try {
            breakpointManager.removeBreakpoints(allBreakpoints, true);
        } catch (CoreException e) {
            PlatformActivator.logException(e);
        }

        // Next, resume any running processes (the server can't terminate if
        // it is suspended)

        final boolean[] shouldWait = new boolean[1];

        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunch[] allLaunches = launchManager.getLaunches();

        for (ILaunch nextLaunch : allLaunches) {
            if (!nextLaunch.isTerminated()) {
                shouldWait[0] = true;

                IDebugTarget[] debugTargets = nextLaunch.getDebugTargets();

                for (IDebugTarget nextTarget : debugTargets) {
                    try {
                        nextTarget.resume();

                        IThread[] allThreads = nextTarget.getThreads();

                        for (IThread nextThread : allThreads) {
                            nextThread.resume();
                        }
                    } catch (DebugException e) {
                        PlatformActivator.logException(e);
                    }
                }
            }
        }

        // Not much we can safely wait on here
        if (shouldWait[0]) {
            ui.pause(20000);
        }
    }
}
