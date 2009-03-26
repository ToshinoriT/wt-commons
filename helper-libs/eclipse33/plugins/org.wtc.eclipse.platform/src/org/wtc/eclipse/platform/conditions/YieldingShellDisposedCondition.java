/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.swt.condition.WidgetDisposedCondition;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.wtc.eclipse.platform.PlatformActivator;

/**
 * Yields if the active shell does not match the widget on which we are waiting.
 */
public class YieldingShellDisposedCondition extends WidgetDisposedCondition {
    private final Shell _shell; //cache the shell

    /**
     * Save the data members.
     */
    public YieldingShellDisposedCondition(Shell shell) {
        super(shell);
        _shell = shell;
    }

    /**
     * @return  Shell - Get the active shell from the workspace
     */
    private Shell getActiveShell() {
        final Display display = Display.getDefault();

        if (display.isDisposed())
            return null;

        final Shell[] shell = new Shell[1];
        display.syncExec(new Runnable() {
                public void run() {
                    shell[0] = display.getActiveShell();
                }
            });

        return shell[0];
    }

    /**
     * Is the known shell NOT active?
     */
    private boolean nestedDialogCondition() {
        //is the active shell NOT the shell on which we're waiting?
        Shell active = getActiveShell();
        boolean yield = active != _shell;

        if (yield) {
            PlatformActivator.logDebug("active shell is not condition shell --- yielding"); //$NON-NLS-1$
        }

        return yield;
    }

    /**
     * @see  com.windowtester.swt.condition.ICondition#test()
     */
    @Override
    public boolean test() {
        return super.test() || nestedDialogCondition();
    }

}
