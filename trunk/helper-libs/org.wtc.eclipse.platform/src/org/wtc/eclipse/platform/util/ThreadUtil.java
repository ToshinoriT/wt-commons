/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ThreadUtil {
    /**
     * Ensures that the provided runnable is run on the UI thread if the calling thread is
     * a non-UI thread.
     */
    public static void ensureRunOnUIThread(final Runnable runnable) {
        if (isMainDisplayThread()) {
            runnable.run();
        } else {
            PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
        }
    }

    /**
     * Determine if the current thread is the main SWT display thread. This can be called
     * in an assert to make sure things are happening on the main thread.
     *
     * @return  true if the current thread is the default display thread
     */
    public static boolean isMainDisplayThread() {
        return Display.findDisplay(Thread.currentThread()) == Display.getDefault();
    }

}
