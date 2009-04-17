/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.IShellCondition;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.YieldingShellDisposedCondition;

/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically wait
 * until a popped progress dialog is closed
 * 
 * @since 3.8.0
 */
public class AbstractProgressDialogShellHandler extends AbstractShellHandler
    implements IShellConditionHandler {
    private IShellCondition _condition;

    // cached value for handling
    private Shell _shell;

    private final String _title;

    /**
     * Save the data members.
     */
    public AbstractProgressDialogShellHandler(IUIContext ui,
                                              String title) {
        super(ui, title, true);

        _title = title;
        _condition = new ProgressDialogShellCondition();
    }

    /**
     * @see  com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
     */
    public void handle(IUIContext ui) {
        PlatformActivator.logDebug(getClass().getSimpleName() + " handling shell"); //$NON-NLS-1$

        // Wait up to 10 minutes
        if ((_shell != null) && !_shell.isDisposed()) {
            ui.wait(new YieldingShellDisposedCondition(_shell), 600000);
        }
    }

    /**
     * @see  com.windowtester.swt.condition.shell.IShellCondition#test(org.eclipse.swt.widgets.Shell)
     */
    public boolean test(Shell shell) {
        boolean test = _condition.test(shell);

        if (test) {
            _shell = shell;
        }

        return test;
    }

    /**
     * Shell conditions identify shells for the shell monitor. In this case, test that the
     * given shell is an instance of the eclipse progress dialog
     */
    private class ProgressDialogShellCondition extends ShellCondition {
        /**
         * Save the data members. There is noo title we care about and this dialog is
         * modal
         */
        public ProgressDialogShellCondition() {
            super(null, true);
        }

        /**
         * @see  com.windowtester.swt.condition.shell.IShellCondition#test(org.eclipse.swt.widgets.Shell)
         */
        @Override
        public boolean test(Shell shell) {
            Object data = shell.getData();

            return ((data instanceof ProgressMonitorDialog) || (_title.equals(shell.getText())));

        }

    }
}
